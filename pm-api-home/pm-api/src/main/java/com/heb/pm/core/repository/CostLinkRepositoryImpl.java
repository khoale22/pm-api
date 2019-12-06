package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.DcmCostLink;
import com.heb.pm.dao.core.entity.ItemMasterKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Concrete implementation of CostLinkRepository that goes against the SOAP service.
 *
 * @author d116773
 * @since 1.0.0
 */
@Repository
@Profile({"local", "cert", "prod"})
public class CostLinkRepositoryImpl implements CostLinkRepository {

	private static final Logger logger = LoggerFactory.getLogger(CostLinkRepositoryImpl.class);

	private static final String COST_LINK_QUERY = "select cl.cst_lnk_id, cl.cst_lnk_des, cl.cst_lnk_actv_sw from emd.cst_links cl WHERE cl.CST_LNK_ID = ?";

	private static final String COST_LINK_DETAIL_QUERY = "SELECT im.itm_id, loc.ap_nbr, loc.ap_typ_cd " +
			"FROM emd.ITEM_MASTER IM " +
			"INNER JOIN emd.VEND_LOC_ITM VD ON IM.ITM_KEY_TYP_CD = VD.ITM_KEY_TYP_CD AND IM.ITM_ID = VD.ITM_ID  " +
			"and im.itm_key_typ_cd = vd.itm_key_typ_cd " +
			"INNER JOIN emd.LOCATION loc on loc.LOC_NBR = vd.VEND_LOC_NBR and loc.LOC_TYP_CD = vd.VEND_LOC_TYP_CD " +
			"INNER JOIN emd.ITM_WHSE_VEND itmWhs on itmWhs.VEND_LOC_NBR = vd.VEND_LOC_NBR " +
			"and itmWhs.VEND_LOC_TYP_CD = vd.VEND_LOC_TYP_CD  and itmWhs.itm_id = vd.itm_id " +
			"and itmWhs.itm_key_typ_cd = vd.itm_key_typ_cd " +
			"INNER JOIN emd.WHSE_LOC_ITM whsLoc on whsLoc.ITM_ID = itmWhs.ITM_ID  " +
			"and whsLoc.itm_key_typ_cd = itmWhs.itm_key_typ_cd " +
			"and whsLoc.WHSE_LOC_TYP_CD = itmWhs.WHSE_LOC_TYP_CD " +
			"and whsLoc.WHSE_LOC_NBR = itmWhs.WHSE_LOC_NBR " +
			"WHERE whsLoc.prch_Stat_Cd <> 'D' and IM.ITM_KEY_TYP_CD = 'ITMCD' and whsLoc.whse_Loc_Nbr <> '101' " +
			"AND whsLoc.cst_lnk_id = ?";

	@Autowired
	private transient JdbcTemplate jdbcTemplate;

	@Autowired
	private transient ItemMasterRepository itemMasterRepository;

	/**
	 * Interim class to pull the list of items out of the cost link.
	 */
	private static class ItemAndVendor {

		private final Long itemId;
		private final Long apNumber;
		private final String apType;

		/**
		 * Constructs a new ItemAndVendor.
		 *
		 * @param itemId The item code.
		 * @param apNumber The AP number.
		 * @param apType The AP type.
		 */
		public ItemAndVendor(Long itemId, Long apNumber, String apType) {
			this.itemId = itemId;
			this.apNumber = apNumber;
			this.apType = apType;
		}


		public Long getItemId() {
			return itemId;
		}

		public Long getApNumber() {
			return apNumber;
		}

		public String getApType() {
			return apType;
		}
	}

	/**
	 * Uses DcmCostLink service to populate a DcmCostLink.
	 *
	 * Note, only costLinkNumber, description, active, vendor, and representativeItem are populated by this method.
	 *
	 * @param costLinkNumber The cost link number being searched for.
	 * @return A DcmCostLink for the supplied number or empty.
	 */
	@Override
	public Optional<DcmCostLink> findById(Long costLinkNumber) {

		logger.debug(String.format("Looking up cost link %d.", costLinkNumber));

		if (Objects.isNull(costLinkNumber)) {
			return Optional.empty();
		}

		Object[] costLinkIdArgs = new Object[1];
		costLinkIdArgs[0] = costLinkNumber;

		// Pull the cost link number, the description, and if the cost link is active.
		DcmCostLink dcmCostLink = this.jdbcTemplate.queryForObject(COST_LINK_QUERY, costLinkIdArgs, (rs, rowNum) -> {
			DcmCostLink dcmCostLink1 = new DcmCostLink();
			dcmCostLink1.setCostLinkNumber(rs.getLong(1)).setDescription(rs.getString(2))
					.setActive(rs.getString(3).equalsIgnoreCase("Y"))
					.setItems(new LinkedList<>());
			return dcmCostLink1;
		});

		if (Objects.isNull(dcmCostLink)) {
			return Optional.empty();
		}

		// Get the list of items
		List<ItemAndVendor> itemAndVendors = this.jdbcTemplate.query(COST_LINK_DETAIL_QUERY, costLinkIdArgs,
				(rs, rowNum) -> new ItemAndVendor(rs.getLong(1), rs.getLong(2), rs.getString(3)));


		// Convert the items to item masters.
		for (ItemAndVendor iv : itemAndVendors) {

			dcmCostLink.setApType(iv.getApType()).setApNumber(iv.getApNumber());
			ItemMasterKey itemMasterKey = ItemMasterKey.of(ItemMasterKey.WAREHOUSE, iv.getItemId());
			this.itemMasterRepository.findById(itemMasterKey).ifPresent(dcmCostLink.getItems()::add);
		}

		return Optional.of(dcmCostLink);
	}
}
