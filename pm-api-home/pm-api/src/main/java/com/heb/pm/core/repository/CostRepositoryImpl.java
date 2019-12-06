package com.heb.pm.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Concrete implementation of CostRepository.
 */
@Repository
public class CostRepositoryImpl implements CostRepository {

	private static final String COST_QUERY = "SELECT vd.VEND_LIST_CST " +
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
			"AND IM.ITM_ID= ? and loc.ap_nbr = ? fetch first 1 row only";

	@Autowired
	private transient JdbcTemplate jdbcTemplate;

	private static final RowMapper<Double> COST_MAPPER = (rs, rowNum) -> rs.getDouble(1);

	@Override
	public Double getCurrentListCost(Long itemCode, Long apNumber) {

		Object[] args = new Object[2];
		args[0] = itemCode;
		args[1] = apNumber;

		List<Double> costs = this.jdbcTemplate.query(COST_QUERY, args, CostRepositoryImpl.COST_MAPPER);

		return costs.isEmpty() ? null : costs.get(0);
	}
}
