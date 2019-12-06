package com.heb.pm.core.service;

import com.heb.pm.core.model.CostLink;
import com.heb.pm.core.model.Item;

import com.heb.pm.core.model.Supplier;
import com.heb.pm.core.repository.CostLinkRepository;
import com.heb.pm.core.repository.CostRepository;
import com.heb.pm.core.repository.ItemMasterRepository;
import com.heb.pm.dao.core.entity.DcmCostLink;
import com.heb.pm.dao.core.entity.ItemMaster;
import com.heb.pm.dao.core.entity.ItemMasterKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains business logic related to cost links.
 */
@Service
public class CostLinkService {

	private static final Logger logger = LoggerFactory.getLogger(CostLinkService.class);

	private static final Long NO_COST_LINK = 0L;

	@Autowired
	private transient CostLinkRepository costLinkRepository;

	@Autowired
	private transient ItemService itemService;

	@Autowired
	private transient ItemMasterRepository itemMasterRepository;

	@Autowired
	private transient CostRepository costRepository;

	/**
	 * Returns cost link information for a requested cost link number. Will return empty if that cost link is not found.
	 *
	 * @param costLinkNumber The cost link number to look for.
	 * @return The cost link for a given number.
	 */
	public Optional<CostLink> getCostLinkById(Long costLinkNumber) {

		logger.debug(String.format("Searching for cost link %d", costLinkNumber));

		if (Objects.isNull(costLinkNumber)) {
			return Optional.empty();
		}

		return this.costLinkRepository.findById(costLinkNumber).map(this::dcmCostLinkToCostLink);
	}

	/**
	 * Returns cost link information for a requested item code. Will return empty if that item does not exist
	 * or is not part of a cost link or the cost link it is part of is not found.
	 *
	 * @param itemCode The item code to search for.
	 * @return The cost link this item is part of.
	 */
	public Optional<CostLink> getCostLinkByItemCode(Long itemCode) {

		ItemMasterKey itemMasterKey = ItemMasterKey.of().setItemId(itemCode).setItemKeyTypeCode(ItemMasterKey.WAREHOUSE);

		Optional<ItemMaster> itemMaster = this.itemMasterRepository.findById(itemMasterKey);
		if (itemMaster.isEmpty()) {
			return Optional.empty();
		}
		Optional<CostLink> toReturn = this.itemMasterToCostLink(itemMaster.get());
		toReturn.ifPresent(costLink -> costLink.setSearchedItemCode(itemCode));
		return toReturn;
	}

	/**
	 * Converts an ItemMaster to a CostLink if possible. Will return empty if item is not tied to a cost link.
	 *
	 * @param im The ItemMaster to convert.
	 * @return The converted CostLink.
	 */
	private Optional<CostLink> itemMasterToCostLink(ItemMaster im) {

		// Get the cost link number from the warehouse location item records.
		if (CollectionUtils.isEmpty(im.getWarehouseLocationItems())) {
			logger.info(String.format("Item %d has not matching warehouse location item records.",
					im.getKey().getItemId()));
			return Optional.empty();
		}

		// See what the cost link number is of the item. If 0, then it's not part of a cost link.
		Long costLinkNumber = im.getWarehouseLocationItems().get(0).getCostLinkNumber();
		if (NO_COST_LINK.equals(costLinkNumber)) {
			logger.info(String.format("Item %d is not part of a cost link.", im.getKey().getItemId()));
			return Optional.empty();
		}

		// Since we have the number, we can use the other functions, just make sure to pull the info from the
		// item the user passed in rather than just selecting the first active item.
		return this.costLinkRepository.findById(costLinkNumber)
				.map((cl) -> this.overlayItem(cl, im))
				.map(this::dcmCostLinkToCostLink);
	}

	/**
	 * Adds a passed in item to the front of the list in a DcmCostLink. This is just so when they are searching by
	 * item code so we can try the user's item first before going through the rest of the list..
	 *
	 * @param dcmCostLink The DcmCostLink to add to.
	 * @param im The item to add.
	 * @return The modified DcmCostLink
	 */
	private DcmCostLink overlayItem(DcmCostLink dcmCostLink, ItemMaster im) {
		dcmCostLink.getItems().add(0, im);
		return dcmCostLink;
	}

	/**
	 * Converts a DcmCostLink to a CostLink.
	 *
	 * @param dcmCostLink The DcmCostLink to convert.
	 * @return The converted DcmCostLink.
	 */
	private CostLink dcmCostLinkToCostLink(DcmCostLink dcmCostLink) {

		Item representativeItem =
				this.getRepresentativeItem(dcmCostLink.getItems()).orElse(null);

		Supplier vendor = Supplier.of().setApNumber(dcmCostLink.getApNumber())
				.setSupplierType(dcmCostLink.getApType().equalsIgnoreCase("AP") ? Supplier.WAREHOUSE : Supplier.DSD);

		CostLink costLink = CostLink.of().setCostLinkNumber(dcmCostLink.getCostLinkNumber())
				.setDescription(dcmCostLink.getDescription())
				.setActive(dcmCostLink.getActive()).setVendor(vendor)
				.setListCost(BigDecimal.valueOf(this.costRepository.getCurrentListCost(representativeItem.getItemCode(), dcmCostLink.getApNumber())));
		if (Objects.nonNull(representativeItem)) {
			costLink.setCommodity(representativeItem.getCommodity())
					.setPack(representativeItem.getContainedUpc().getPack())
					.setRepresentativeItemCode(representativeItem.getItemCode());
		} else {
			logger.warn(String.format("Cost link %d has no active items.", dcmCostLink.getCostLinkNumber()));
		}

		return costLink;
	}

	/**
	 * Takes a list of ItemMasters and tries to find an active item to use to pull cost link information from.
	 *
	 * @param itemMasters The list of ItemMasters to look through.
	 * @return An EntireCostLink if an active warehouse item is found and an empty optional otherwise.
	 */
	private Optional<Item> getRepresentativeItem(List<ItemMaster> itemMasters) {

		for (ItemMaster im : itemMasters) {

			if (im.getKey().isDsd() || im.isDiscontinued()) {
				continue;
			}

			Item item = this.itemService.itemMasterToItem(im);
			if (item.getMrt()) {
				logger.debug(String.format("Link contains MRT %d", im.getKey().getItemId()));
			} else {
				return Optional.of(item);
			}
		}
		return Optional.empty();
	}
}
