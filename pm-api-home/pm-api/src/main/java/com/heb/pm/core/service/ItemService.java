/*
 * ItemInfoService
 *
 *  Copyright (c) 2018 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.ContainedUpc;
import com.heb.pm.core.model.Item;
import com.heb.pm.core.model.Upc;
import com.heb.pm.core.repository.ItemMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Holds business logic related to Item info data retrieval.
 *
 * @author s573181
 * @since 1.0.0
 */
@Service
public class ItemService {

	private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

	@Autowired
	private transient ItemMasterRepository itemMasterRepository;

	@Autowired
	private transient UpcService upcService;

	/**
	 * Sets the UpcService for the class to use.
	 *
	 * @param upcService The UpcService for the class to use.
	 */
	public void setUpcService(UpcService upcService) {
		this.upcService = upcService;
	}

	/**
	 * Returns warehouse item information for a given item code.
	 *
	 * @param itemCode The item code to search for.
	 * @return Warehouse item information. Will return null if the item is not found.
	 */
	public Optional<Item> getItem(Long itemCode) {

		ItemMasterKey itemMasterKey = ItemMasterKey.of(ItemMasterKey.WAREHOUSE, itemCode);
		return this.itemMasterRepository.findById(itemMasterKey).map(this::itemMasterToItem);
	}

	/**
	 * Converts an ItemMaster entity to an Item model object.
	 *
	 * @param itemMaster The ItemMaster entity to convert.
	 * @return The ItemMaster converted to an Item. If itemMaster is null, will return null.
	 */
	public Item itemMasterToItem(ItemMaster itemMaster) {

		if (Objects.isNull(itemMaster)) {
			return null;
		}

		// The common information regardless of the type of item.
		logger.debug(String.format("Converting item %d.", itemMaster.getKey().getItemId()));

		Item item = this.itemMasterToSkinnyItem(itemMaster);

		// Make sure all the data we need is there before proceeding.
		if (this.itemIsGood(itemMaster)) {
			boolean fakeMrt = this.isFakeMrt(itemMaster);

			item.setContainedUpc(this.getContainedUpc(itemMaster))
					.setInnerUpcs(this.getInnerUpcs(itemMaster))
					.setMrt(this.isMrt(itemMaster))
					.setAltPack(this.isAltPack(itemMaster) || fakeMrt)
					.setFakeMrt(fakeMrt);
		}

		return item;
	}


	/**
	 * If the item is not an MRT, will return the UPC contained in the item. If it is an MRT, will return null.
	 *
	 * @param itemMaster The ItemMaster to look at.
	 * @return The ContainedUpc in that item or null.
	 */
	protected ContainedUpc getContainedUpc(ItemMaster itemMaster) {

		if (this.isOpenStock(itemMaster)) {
			logger.debug(String.format("Item %d is an open stock item.", itemMaster.getKey().getItemId()));

			return ContainedUpc.of().setUpc(this.upcService.primaryUpcToSkinnyUpc(itemMaster.getPrimaryUpc()))
					.setPack(itemMaster.getPack())
					.setAssociatedUpcs(this.getAssociatesFromPrimaryUpc(itemMaster.getPrimaryUpc()));

		}

		if (this.isAltPack(itemMaster)) {
			logger.debug(String.format("Item %d is an alt-pack.", itemMaster.getKey().getItemId()));

			return ContainedUpc.of().setUpc(this.upcService.primaryUpcToSkinnyUpc(itemMaster.getPrimaryUpc().getShipper().get(0).getInnerPrimaryUpc()))
					.setPack(itemMaster.getPack())
					.setAssociatedUpcs(this.getAssociatesFromPrimaryUpc(itemMaster.getPrimaryUpc().getShipper().get(0).getPrimaryUpc()));
		}

		if (this.isFakeMrt(itemMaster)) {
			logger.debug(String.format("Item %d is a fake MRT.", itemMaster.getKey().getItemId()));
			Shipper s = this.getNonFakeShipper(itemMaster.getPrimaryUpc().getShipper());
			if (Objects.nonNull(s)) {

				return ContainedUpc.of().setUpc(this.upcService.primaryUpcToSkinnyUpc(s.getInnerPrimaryUpc()))
						.setPack(s.getShipperQuantity())
						.setAssociatedUpcs(this.getAssociatesFromPrimaryUpc(s.getPrimaryUpc()));
			}
			// If this happens, i don't know what the data would look like. It says it was a fake MRT, but both of the UPCs are all 6's.
		}

		return null;
	}

	/**
	 * Returns a list of all associated UPCs tied to a primary.
	 *
	 * @param primaryUpc The primary UPC to pull associates from.
	 * @return The list of associates, or null if there are none.
	 */
	private List<Long> getAssociatesFromPrimaryUpc(PrimaryUpc primaryUpc) {

		if (Objects.isNull(primaryUpc) || Objects.isNull(primaryUpc.getAssociateUpcs())) {
			return null;
		}

		List<Long> associatedUpcs = primaryUpc.getAssociateUpcs().stream()
				.filter(associate -> !associate.getUpc().equals(primaryUpc.getUpc()))
				.map(AssociatedUpc::getUpc).collect(Collectors.toList());
		return associatedUpcs.isEmpty() ? null : associatedUpcs;
	}
	/**
	 * If an item is an MRT, will return the list of UPCs inside the item. If the item is not an MRT, will return null.
	 *
	 * @param itemMaster The item to look at.
	 * @return The list of UPCs inside an MRT or null.
	 */
	protected List<ContainedUpc> getInnerUpcs(ItemMaster itemMaster) {

		if (this.isMrt(itemMaster)) {
			logger.debug(String.format("Item %d is an MRT.", itemMaster.getKey().getItemId()));

			List<ContainedUpc> innerUpcs = new LinkedList<>();

			for (Shipper s : itemMaster.getPrimaryUpc().getShipper()) {

				// This is an error state. There's a record in pd_shipper that does not have a corresponding record
				// in pd_upc.
				if (Objects.isNull(s.getInnerPrimaryUpc())) {
					Upc upc = Upc.of().setScanCodeId(s.getKey().getShipperUpc()).setStatus("EITM-001: UPC missing from pd_upc.");
					innerUpcs.add(
							ContainedUpc.of().setUpc(upc).setPack(s.getShipperQuantity()))
					;
				} else {
					ItemMaster innerItemMaster = s.getInnerPrimaryUpc().getItemMasters().stream().filter(im -> im.getKey().isWarehouse()).findFirst().orElse(null);
					Upc upc = this.upcService.primaryUpcToSkinnyUpc(s.getInnerPrimaryUpc());
					upc.setItem(this.itemMasterToSkinnyItem(innerItemMaster));
					innerUpcs.add(
							ContainedUpc.of().setUpc(upc).setPack(s.getShipperQuantity()))
					;
				}
			}
			return innerUpcs;
		}
		return null;
	}

	/**
	 * Converts an ItemMaster to an Item but only sets a minimum amount of information.
	 *
	 * @param itemMaster The ItemMaster to convert.
	 * @return The converted Item. If ItemMaster is null, will return null.
	 */
	public Item itemMasterToSkinnyItem(ItemMaster itemMaster) {

		if (Objects.isNull(itemMaster)) {
			return null;
		}

		Assert.notNull(itemMaster.getKey(), "Item code is required.");
		Assert.notNull(itemMaster.getKey().getItemId(), "Item code is required.");

		// The common information regardless of the type of item.
		logger.debug(String.format("Converting item %d with a minimal amount of data.", itemMaster.getKey().getItemId()));

		return Item.of().setItemCode(itemMaster.getKey().getItemId())
				.setOrderingUpc(itemMaster.getOrderingUpc())
				.setDescription(itemMaster.getDescription())
				.setCommodity(itemMaster.getCommodityCode())
				.setSubCommodity(itemMaster.getSubCommodityCode());
	}

	/**
	 * Inspects an item to see if an item is an open-stock item.
	 *
	 * @param itemMaster The item to inspect.
	 * @return True if the item is open-stock and false otherwise.
	 */
	protected boolean isOpenStock(ItemMaster itemMaster) {
		return CollectionUtils.isEmpty(itemMaster.getPrimaryUpc().getShipper());
	}

	/**
	 * Inspects a list of shippers to see if an item is an alt-pack.
	 *
	 * @param itemMaster The item to inspect.
	 * @return True if the item is an alt-pack and false otherwise.
	 */
	protected boolean isAltPack(ItemMaster itemMaster) {

		return !CollectionUtils.isEmpty(itemMaster.getPrimaryUpc().getShipper()) &&
				itemMaster.getPrimaryUpc().getShipper().size() == 1;
	}

	/**
	 * Inspects a list of shippers to see if an item is an MRT.
	 *
	 * @param itemMaster The item to inspect.
	 * @return True if the item is an MRT and false otherwise.
	 */
	protected boolean isMrt(ItemMaster itemMaster) {
		return !(this.isFakeMrt(itemMaster) ||
				CollectionUtils.isEmpty(itemMaster.getPrimaryUpc().getShipper()) ||
				itemMaster.getPrimaryUpc().getShipper().size() == 1);
	}

	/**
	 * Inspects a list of shippers to see if an item is a fake MRT.
	 *
	 * @param itemMaster The item to inspect.
	 * @return True if the item is a fake MRT and false otherwise.
	 */
	protected boolean isFakeMrt(ItemMaster itemMaster) {

		if (CollectionUtils.isEmpty(itemMaster.getPrimaryUpc().getShipper()) || itemMaster.getPrimaryUpc().getShipper().size() != 2) {
			return false;
		}
		for (Shipper s : itemMaster.getPrimaryUpc().getShipper()) {
			if (s.getKey().getShipperUpc().equals(Shipper.FAKE_MRT_UPC)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Given a list of shippers for a fake MRT, returns the real inner part. For efficiency, this method assumes it is passed a fake MRT.
	 *
	 * @param shippers The list of shippers to look through.
	 * @return The non-fake UPC in the shipper.
	 */
	private Shipper getNonFakeShipper(List<? extends Shipper> shippers) {

		for (Shipper s : shippers) {
			if (!s.getKey().getShipperUpc().equals(Shipper.FAKE_MRT_UPC)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Checks an ItemMaster to determine if it has the data to go through the process without dying.
	 *
	 * @param itemMaster The ItemMaster to check.
	 * @return True if the item has the needed data and false otherwise.
	 */
	private boolean itemIsGood(ItemMaster itemMaster) {

		if (Objects.isNull(itemMaster.getPrimaryUpc())) {
			logger.warn(String.format("Item %d is not tied to a pd_upc record.", itemMaster.getKey().getItemId()));
			return false;
		}

		return true;
	}
}
