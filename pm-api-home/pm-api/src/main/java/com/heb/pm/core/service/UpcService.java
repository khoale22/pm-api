/*
 * UpcService
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.Upc;
import com.heb.pm.core.repository.AssociatedUpcRepository;
import com.heb.pm.core.repository.CandidateSellingUnitRepository;
import com.heb.pm.core.repository.RegularZoneRetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Holds business logic related to upc.
 *
 * @author m314029
 * @since 1.0.0
 */
@Service
public class UpcService {

	private static final Logger logger = LoggerFactory.getLogger(UpcService.class);

	@Autowired
	private transient CandidateSellingUnitRepository candidateRepository;

	@Autowired
	private transient AssociatedUpcRepository associatedUpcRepository;

	@Autowired
	private transient ItemService itemService;

	@Autowired
	private transient ProductService productService;

	@Autowired(required = false)
	private transient RegularZoneRetailRepository regularZoneRetailRepository;

	private static final long[] ZONES_IN_PRIORITY_ORDER = {210, 158, 646, 645};

	public static final String ACTIVE = "ACTIVE";
	public static final String CANDIDATE_ACTIVATED = "108";
	public static final String CANDIDATE_DELETED = "104";
	public static final String CANDIDATE_REJECTED = "105";
	public static final String CANDIDATE_IN_PROGRESS = "111";
	public static final String VENDOR_CANDIDATE = "103";
	public static final String CANDIDATE_FAILED = "109";

	/**
	 * Sets the ProductService for this class to use.
	 *
	 * @param productService The ProductService for this class to use.
	 */
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Sets the ItemService for this class to use.
	 *
	 * @param itemService The ItemService for this class to use.
	 */
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}


	/**
	 * Sets the upc info object data.
	 *
	 * @param upc the upc to get info on.
	 * @return the updated Upc object.
	 */
	public Optional<Upc> getUpc(Long upc) {

		logger.debug(String.format("Searching for UPC %d", upc));

		// First, check to see if there is an already set up UPC.
		Optional<Upc> toReturn = this.associatedUpcRepository.findById(upc).map(this::associateUpcToUpc);

		if (toReturn.isEmpty()) {
			// If not, check to see if there is a candidate for the UPC.
			logger.debug(String.format("UPC %d is not a live UPC, checking candidates", upc));
			List<CandidateSellingUnit> candidateSellingUnits = this.candidateRepository.findAllByUpcOrderByLastUpdatedOnDesc(upc);
			toReturn = this.candidateSellingUnitsToUpc(candidateSellingUnits);
			if (toReturn.isEmpty()) {
				logger.debug(String.format("UPC %d not found", upc));
			}
		}

		return toReturn;
	}

	/**
	 * Converts a list of CandidateSellingUnits to a UPC.
	 *
	 * @param candidateSellingUnits The list of CandidateSellingUnits.
	 * @return The first non-deleted candidate found. Will return null if none is found.
	 */
	private Optional<Upc> candidateSellingUnitsToUpc(List<? extends CandidateSellingUnit> candidateSellingUnits) {

		if (CollectionUtils.isEmpty(candidateSellingUnits)) {
			return Optional.empty();
		}

		for (CandidateSellingUnit csu : candidateSellingUnits) {
			if (!csu.getCandidateProductMaster().
					getCandidateWorkRequest().getStatus().equalsIgnoreCase(CANDIDATE_DELETED)) {
				return Optional.of(this.candidateSellingUnitToUpc(csu));
			}
		}
		return Optional.empty();
	}

	/**
	 * Converts a single CandidateSellingUnit to a UPC.
	 *
	 * @param candidateSellingUnit the CandidateSellingUnit to convert.
	 * @return The UPC from the CandidateSellingUnit. if candidateSellingUnit is null, will return null.
	 */
	private Upc candidateSellingUnitToUpc(CandidateSellingUnit candidateSellingUnit) {

		if (Objects.isNull(candidateSellingUnit)) {
			return null;
		}

		return Upc.of()
				.setStatus(this.getCandidateStatusByCode(candidateSellingUnit.getCandidateProductMaster().getCandidateWorkRequest().getStatus()))
				.setSearchedUpc(candidateSellingUnit.getUpc());
	}

	/**
	 * Converts an AssociatedUpc to a UPC.
	 *
	 * @param associatedUpc The AssociatedUpc to convert.
	 * @return The UPC from the AssociatedUpc. if associatedUpc is null, will return null.
	 */
	protected Upc associateUpcToUpc(AssociatedUpc associatedUpc) {

		if (Objects.isNull(associatedUpc)) {
			return null;
		}

		logger.debug(String.format("Converting UPC %d.", associatedUpc.getUpc()));

		Assert.notNull(associatedUpc.getPrimaryUpc(), "Associated UPC not tied to a primary UPC.");
		Assert.notNull(associatedUpc.getPrimaryUpc().getSellingUnit(), "Associated UPC not tied to a primary UPC.");

		// This should not happen in the tables as there has to be at least one UPC in pd_associated_upc to get here.
		Assert.notNull(associatedUpc.getPrimaryUpc().getAssociateUpcs(), "Primary UPC not tied to any associates.");
		// This should not happen, but just in case.
		Assert.notNull(associatedUpc.getPrimaryUpc().getUpc(), "Primary UPC badly structured.");

		Upc upcInfo = this.primaryUpcToSkinnyUpc(associatedUpc.getPrimaryUpc())
				.setScanCodeId(associatedUpc.getPrimaryUpc().getUpc())
				.setSearchedUpc(associatedUpc.getUpc())
				.setStatus(ACTIVE);

		// Grab the list of associated UPCs tied to the primary. If the list is not empty then there are associates,
		// so set them. Otherwise, leave it as null.
		List<Upc> associatedUpcs = associatedUpc.getPrimaryUpc().getAssociateUpcs().stream()
				.filter(associate -> !associate.getUpc().equals(associatedUpc.getPrimaryUpc().getUpc()))
				.map(this::associatedUpcToInnerUpc).collect(Collectors.toList());
		if (!associatedUpcs.isEmpty()) {
			upcInfo.setAssociatedUpcs(associatedUpcs);
		}

		if (Objects.nonNull(associatedUpc.getPrimaryUpc().getItemMasters())) {
			upcInfo.setItem(this.itemService.itemMasterToSkinnyItem(
					associatedUpc.getPrimaryUpc().getItemMasters().stream().filter(im -> im.getKey().isWarehouse()).findFirst().orElse(null)));
		}



		return upcInfo;
	}

	/**
	 * Converts a PrimaryUpc to a UPC only setting a minimal amount of information.
	 *
	 * @param primaryUpc The PrimaryUpc to convert.
	 * @return The converted UPC. If sellingUnit is null, returns null.
	 */
	public Upc primaryUpcToSkinnyUpc(PrimaryUpc primaryUpc) {

		if (Objects.isNull(primaryUpc)) {
			return null;
		}
		logger.debug(String.format("Converting UPC %d with a minimal amount of data.", primaryUpc.getUpc()));

		Upc upcInfo = Upc.of()
				.setScanCodeId(primaryUpc.getUpc());
		if (Objects.nonNull(primaryUpc.getSellingUnit())) {
			upcInfo.setSize(primaryUpc.getSellingUnit().getTagSizeDescription())
					.setProduct(this.productService.productMasterToSkinnyProduct(primaryUpc.getSellingUnit().getProductMaster()));
		}

		if (Objects.nonNull(primaryUpc.getRetailLink())) {
			upcInfo.setRetailLink(primaryUpc.getRetailLink().getRetailLinkCd());
		}

		RegularZoneRetail regularZoneRetail = this.getRetail(primaryUpc.getUpc());
		if (Objects.nonNull(regularZoneRetail)) {
			upcInfo.setRetailPrice(regularZoneRetail.getRetail())
					.setXFor(regularZoneRetail.getXFor().intValue())
					.setWeightSw("Y".equals(regularZoneRetail.getWeightSwitch()));
		}

		return upcInfo;
	}


	/**
	 * Converts and AssociatedUpc into a UPC that is meant for the associatedUpc lis in Upc.
	 *
	 * @param associatedUpc The AssociatedUpc to convert.
	 * @return That AssociatedUpc converted into a very small Upc.
	 */
	protected Upc associatedUpcToInnerUpc(AssociatedUpc associatedUpc) {

		Upc upc = Upc.of().setScanCodeId(associatedUpc.getUpc());
		if (Objects.nonNull(associatedUpc.getSellingUnit())) {
			upc.setSize(associatedUpc.getSellingUnit().getTagSizeDescription())
					.setStatus(ACTIVE);
		}
		return upc;
	}

	/**
	 * Returns the regular zone retail of a UPC based on a prioritized list of zones.
	 *
	 * @param upc The UPC to look up the retail for.
	 * @return The RegularZoneRetail or null if it doesn't exist.
	 */
	private RegularZoneRetail getRetail(Long upc) {

		if (Objects.isNull(this.regularZoneRetailRepository)) {
			logger.warn("Retail repository not set.");
			return null;
		}

		RegularZoneRetail regularZoneRetail = null;
		int i = 0;

		try {
			while (Objects.isNull(regularZoneRetail) && i < ZONES_IN_PRIORITY_ORDER.length) {

				regularZoneRetail = this.regularZoneRetailRepository.findCurrentByUpcAndZone(upc, ZONES_IN_PRIORITY_ORDER[i]).orElse(null);
				i++;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

		return regularZoneRetail;
	}

	private String getCandidateStatusByCode(String code) {

		String toReturn;

		switch (code) {
			case CANDIDATE_REJECTED: {
				toReturn = "REJECTED_CANDIDATE";
				break;
			}
			case CANDIDATE_DELETED: {
				toReturn = "DELETED_CANDIDATE";
				break;
			}
			case CANDIDATE_ACTIVATED: {
				toReturn = "ACTIVE_CANDIDATE";
				break;
			}
			case VENDOR_CANDIDATE: {
				toReturn = "VENDOR_CANDIDATE";
				break;
			}
			case CANDIDATE_IN_PROGRESS: {
				toReturn = "IN_PROGRESS_CANDIDATE";
				break;
			}
			case CANDIDATE_FAILED: {
				toReturn = "FAILED_CANDIDATE";
				break;
			}
			default: {
				toReturn = "UNKNOWN_CANDIDATE";
				break;
			}
		}

		return toReturn;
	}


}
