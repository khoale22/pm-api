package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.repository.PhSubCommodityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import com.heb.pm.pam.model.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles processing candidate items.
 *
 * @author d116773
 * @since 1.1.0
 */
@Service
public class CandidateItemService {

	private static final Logger logger = LoggerFactory.getLogger(CandidateItemService.class);

	private static final int SHORT_DESCRIPTION_MAX_SIZE = 20;

	private static final long MAXIMUM_SHIP_DEFAULT = 99_999;

	private static final long MINIMUM_ORDER_QUANTITY_DEFAULT = 0L;

	private static final long HTS_DEFAULT = 0L;

	private static final BigDecimal DUTY_PERCENT_DEFAULT = BigDecimal.ZERO;

	private static final BigDecimal AGENT_COMMISSION_PERCENT_DEFAULT = BigDecimal.ZERO;

	private static final long SELL_BY_YEAR_DEFAULT = 0L;

	private static final String PURCHASE_STATUS_DEFAULT = WarehouseLocationItem.SUSPENDED;

	private static final String SHIP_FROM_DEFAULT = " ";


	@Autowired
	private transient PhSubCommodityRepository phSubCommodityRepository;

	/**
	 * Builds the CandidateItemMaster for a candidate. This will load all of the ps_ tabels under ps_item_master.
	 *
	 * @param user The ID of the user who kicked off this request.
	 * @param candidateWorkRequest This candidate work request the CandidateItemMaster will be a part of
	 * @param candidate The Candidate that kicked off this request.
	 * @param candidateProduct The CandidateProduct being worked on.
	 * @return The CandidateItemMaster.
	 */
	public CandidateItemMaster buildCandidateItemMaster(String user, CandidateWorkRequest candidateWorkRequest,
														Candidate candidate, CandidateProduct candidateProduct) {

		// Get the sub-commodity, it'll be needed for setting some of these values.
		// The validation would have failed if the sub-commodity is not good.
		PhSubCommodity subCommodity = this.phSubCommodityRepository.findByKeySubCommodityCode(candidate.getSubCommodity().getSubCommodityId()).get(0);

		CandidateItemMaster candidateItemMaster = this.candidateProductToCandidateItemMaster(user, candidateWorkRequest, candidate, candidateProduct, subCommodity);

		candidateItemMaster.getCandidateItemScanCodes().add(this.candidateProductToCandidateItemScanCode(candidate, candidateProduct, candidateItemMaster));

		// Handle the warehouse level data.
		for (Warehouse warehouse : candidateProduct.getWarehouses()) {

			CandidateWarehouseLocationItem candidateWarehouseLocationItem = this.warehouseToWarehouseLocationItem(user, candidateItemMaster, warehouse, candidate);

			this.remarkToCandidateItemWarehouseComment(warehouse, 1L, candidateProduct.getRemark1(), candidateWarehouseLocationItem)
					.ifPresent(candidateWarehouseLocationItem.getCandidateItemWarehouseComments()::add);

			this.remarkToCandidateItemWarehouseComment(warehouse, 2L, candidateProduct.getRemark2(), candidateWarehouseLocationItem)
					.ifPresent(candidateWarehouseLocationItem.getCandidateItemWarehouseComments()::add);

			// Flip the item remark switch based on the presence of comments.
			candidateWarehouseLocationItem.setItemRemarkSwitch(CandidateUtils.booleanToSwitch(!candidateWarehouseLocationItem.getCandidateItemWarehouseComments().isEmpty()));

			candidateWarehouseLocationItem.getCandidateItemWarehouseVendors()
					.add(this.warehouseToItemWarehouseVendor(user, candidate, candidateWarehouseLocationItem, warehouse));

			candidateItemMaster.getCandidateWarehouseLocationItems()
					.add(candidateWarehouseLocationItem);

			candidateItemMaster.getCandidateVendorLocationItems()
					.add(this.warehouseToVendorLocationItem(user, subCommodity, candidate, candidateProduct, candidateItemMaster, warehouse));
		}

		return candidateItemMaster;
	}

	/**
	 * Returns the CandidateItemMaster for the candidate.
	 *
	 * @param user The ID of the user who kicked off this request.
	 * @param candidateWorkRequest This candidate work request the CandidateItemMaster will be a part of
	 * @param candidate The Candidate that kicked off this request.
	 * @param candidateProduct The CandidateProduct being worked on.
	 * @param subCommodity The sub-commodity to tie the item to.
	 * @return
	 */
	protected CandidateItemMaster candidateProductToCandidateItemMaster(String user, CandidateWorkRequest candidateWorkRequest,
																	 Candidate candidate, CandidateProduct candidateProduct,
																	 PhSubCommodity subCommodity) {

		String itemDescription = candidateProduct.getDescription().trim().toUpperCase(Locale.US);

		String shortItemDescription = org.apache.commons.lang3.StringUtils.truncate(itemDescription, SHORT_DESCRIPTION_MAX_SIZE);

		CandidateItemMaster candidateItemMaster = new CandidateItemMaster();
		candidateItemMaster.setCandidateWorkRequest(candidateWorkRequest)
				.setItemDescription(itemDescription)
				.setShortItemDescription(shortItemDescription)
				.setItemSizeText(candidate.getRetailSize())
				.setItemSizeQuantity(candidate.getTotalVolume())
				.setItemSizeUom(CandidateUtils.convertToItemUom(candidate.getUnitOfMeasure().getUnitOfMeasureId()))
				.setAddedDate(Instant.now())
				.setAddedUserId(user)
				.setDateControlledItem(CandidateUtils.booleanToSwitch(candidate.getCodeDate()))
				.setCommodityCode(subCommodity.getKey().getCommodityCode())
				.setSubCommodityCode(subCommodity.getKey().getSubCommodityCode())
				.setClassCode(subCommodity.getCommodity().getKey().getClassCode())
				.setCaseUpc(candidateProduct.getCaseUpc())
				.setOrderingUpc(candidateProduct.getUpc())
				.setMaxShipQuantity(MAXIMUM_SHIP_DEFAULT)
				.setNewItem(CandidateUtils.YES)
				.setRepack(CandidateUtils.NO)
				.setCriticalItemIndicator(CandidateUtils.NO)
				.setNeverOut(CandidateUtils.NO)
				.setCatchWeight(CandidateUtils.NO)
				.setLowVelocity(CandidateUtils.NO)
				.setShipper(CandidateUtils.NO)
				.setCrossDockItem(CandidateUtils.NO)
				.setReplaceOrderQuantity(CandidateUtils.NO)
				.setLetterOfCredit(CandidateUtils.NO)
				.setVariableWeight(CandidateUtils.NO)
				.setForwardBuyApproved(CandidateUtils.NO)
				.setCattle(CandidateUtils.NO)
				.setPack(candidate.getInnerPack())
				.setDsdItem(CandidateUtils.NO)
				.setUpcMap(CandidateUtils.NO)
				.setDeposit(CandidateUtils.NO)
				.setDepartmentIdOne(subCommodity.getCommodity().getItemClass().getDepartmentId())
				.setSubDepartmentIdOne(subCommodity.getCommodity().getItemClass().getSubDepartmentId())
				.setPssDepartmentCodeOne(subCommodity.getCommodity().getPssDepartment())
				.setUnstampedTobacco(CandidateUtils.NO)
				.setLastUpdateDate(Instant.now())
				.setLastUpdateUserId(user)
				.setDcChannelType(CandidateItemMaster.DISTRIBUTION_CHANNEL_WAREHOUSE)
				.setMrt(CandidateUtils.NO)
				.setNewData(CandidateUtils.YES)
				.setShipLength(candidate.getInnerLength())
				.setShipWidth(candidate.getInnerWidth())
				.setShipHeight(candidate.getInnerHeight())
				.setShipWeight(candidate.getInnerWeight())
				.setShipInrPack(candidate.getInnerPack())
				.setLength(candidate.getMasterLength())
				.setWidth(candidate.getMasterWidth())
				.setHeight(candidate.getMasterHeight())
				.setWeight(candidate.getMasterWeight())
				.setMasterPackQuantity(candidate.getMasterPack())
				.setPurchaseStatus(PURCHASE_STATUS_DEFAULT)
				.setDisplayReadyUnit(CandidateUtils.NO)
				.setAlwaysSubWhenOut(CandidateUtils.YES)
				.setCatchWeight(CandidateUtils.getCatchWeightSwitch(candidate.getItemWeightType()))
				.setVariableWeight(CandidateUtils.getVariableWeightSwitch(candidate.getItemWeightType()));


		BigDecimal innerCube = CandidateUtils.calculateCube(candidate.getInnerLength(), candidate.getInnerWidth(), candidate.getInnerHeight(), 3);
		BigDecimal masterCube = CandidateUtils.calculateCube(candidate.getMasterLength(), candidate.getMasterWidth(), candidate.getMasterHeight(), 4);

		candidateItemMaster.setCube(masterCube)
				.setShipCube(innerCube);

		if (candidate.getCodeDate()) {
			candidateItemMaster.setSupplierCaseLifeDays(candidate.getMaxShelfLife())
					.setInboundSpecDays(candidate.getInboundSpecDays())
					.setWarehouseReactionDays(candidate.getWarehouseReactionDays())
					.setGuaranteeToStoreDays(candidate.getGuaranteeToStoreDays());
		}

		logger.debug(String.format("DRU %s", Objects.isNull(candidate.getDisplayReadyUnit()) ? "null" : "not null"));
		if (Objects.nonNull(candidate.getDisplayReadyUnit()) && candidate.getDisplayReadyUnit()) {
			setDRU(candidate, candidateItemMaster);
		}

		return candidateItemMaster;
	}

	/**
	 * Set the DRU fields.
	 * @param candidate to get DRU fields from.
	 * @param candidateItemMaster to set DRU fields to.
	 */
	private void setDRU(Candidate candidate, CandidateItemMaster candidateItemMaster) {
		if (candidate.getDisplayReadyUnit()) {
			candidateItemMaster.setDisplayReadyUnit(CandidateUtils.YES);
			candidateItemMaster.setTypeOfDRU(candidate.getDisplayReadyUnitType().equalsIgnoreCase(CandidateDRUValidator.DRU_TYPE_DRP) ? "7" : "9");

			candidateItemMaster.setRowsFacing(candidate.getDisplayReadyUnitRowsFacing());

			candidateItemMaster.setRowsDeep(candidate.getDisplayReadyUnitRowsDeep());

			candidateItemMaster.setRowsHigh(candidate.getDisplayReadyUnitRowsHigh());

			candidateItemMaster.setOrientation(candidate.getDisplayReadyUnitOrientation().equalsIgnoreCase(CandidateDRUValidator.ORIENTATION_BY_DEPTH) ? 1L : 2L);
		}
	}

	/**
	 * Creates the tie between the item and the scan code.
	 *
	 * @param candidate The candidate being processed.
	 * @param candidateProduct The CandidateProduct being processed.
	 * @param candidateItemMaster The CandidateItemMaster being created.
	 * @return The CandidateItemScanCode to tie to the CandidateItemMaster.
	 */
	protected CandidateItemScanCode candidateProductToCandidateItemScanCode(Candidate candidate, CandidateProduct candidateProduct, CandidateItemMaster candidateItemMaster) {

		CandidateItemScanCode candidateItemScanCode = new CandidateItemScanCode();

		candidateItemScanCode.getKey().setUpc(candidateProduct.getUpc());

		candidateItemScanCode.setCandidateItemMaster(candidateItemMaster)
				.setScanType(SellingUnit.SCAN_TYPE_UPC)
				.setPrimary(CandidateUtils.YES)
				.setRetailPackQuantity(candidate.getInnerPack())
				.setNewData(CandidateUtils.YES);

		return candidateItemScanCode;
	}

	/**
	 * Processes the CandidateWarehouseLocationItem portion of the candidate.
	 *
	 *  @param user The ID of the user who kicked off this request.
	 * @param candidateItemMaster The CandidateItemMaster being worked on.
	 * @param warehouse The warehouse being processed.
	 * @param candidate The candidate being processed.
	 */
	protected CandidateWarehouseLocationItem warehouseToWarehouseLocationItem(String user, CandidateItemMaster candidateItemMaster,
																			  Warehouse warehouse, Candidate candidate) {

		CandidateWarehouseLocationItem candidateWarehouseLocationItem = new CandidateWarehouseLocationItem();
		candidateWarehouseLocationItem.getKey().setWarehouseType(LocationKey.LOCATION_TYPE_WAREHOUSE)
				.setWarehouseNumber(warehouse.getWarehouseId());

		candidateWarehouseLocationItem.setCandidateItemMaster(candidateItemMaster)
				.setShipFromType(SHIP_FROM_DEFAULT)
				.setDsconTrxSwitch(CandidateUtils.NO)
				.setMfgId(CandidateUtils.NO)
				.setVariableWeight(CandidateUtils.NO)
				.setCatchWeight(CandidateUtils.NO)
				.setDalyHistSwitch(CandidateUtils.NO)
				.setTransferAvailSwitch(CandidateUtils.NO)
				.setPoRcmdSwitch(CandidateUtils.NO)
				.setMandFlwThrgSwitch(CandidateUtils.NO)
				.setMandFlwThruSwitch(CandidateUtils.NO) // Yes, these are different.
				.setLastUpdateDate(Instant.now())
				.setLastUpdateUser(user)
				.setLastUpdateTime(Instant.now())
				.setAddedTime(Instant.now())
				.setAddedUser(user)
				.setNewData(CandidateUtils.YES)
				.setUnitFactor1(candidate.getCubeAdjustedFactor())
		;

		return candidateWarehouseLocationItem;
	}

	/**
	 * Constructs a CandidateItemWarehouseComment for a remark if the remark is present. Will return empty if the remark is empty.
	 *
	 * @param warehouse The warehouse the remark is for.
	 * @param commentNumber The sequence number of the remark.
	 * @param remark The remark.
	 * @param candidateWarehouseLocationItem The CandidateWarehouseLocationItem to tie the remark to.
	 * @return A CandidateItemWarehouseComment for the remark or empty.
	 */
	protected Optional<CandidateItemWarehouseComment> remarkToCandidateItemWarehouseComment(Warehouse warehouse, Long commentNumber, String remark,
													CandidateWarehouseLocationItem candidateWarehouseLocationItem) {
		if (StringUtils.hasText(remark)) {
			CandidateItemWarehouseComment candidateItemWarehouseComment = new CandidateItemWarehouseComment();
			candidateItemWarehouseComment.getKey().setWarehouseNumber(warehouse.getWarehouseId())
					.setWarehouseType(LocationKey.LOCATION_TYPE_WAREHOUSE)
					.setItemCommentNumber(commentNumber)
					.setItemCommentType(CandidateItemWarehouseCommentKey.COMMENT_TYPE_REMARK);
			candidateItemWarehouseComment.setComments(remark)
					.setCandidateWarehouseLocationItem(candidateWarehouseLocationItem);
			return Optional.of(candidateItemWarehouseComment);
		}

		return Optional.empty();
	}

	/**
	 * Converts warehouse level data to the CandidateItemWarehouseVendor portion of the candidate.
	 *
	 * @param user The ID of the user who kicked off this request.
	 * @param candidate The candidate being worked on.
	 * @param candidateWarehouseLocationItem The CandidateWarehouseLocationItem being worked on.
	 * @param warehouse The warehouse being processed.
	 */
	protected CandidateItemWarehouseVendor warehouseToItemWarehouseVendor(String user, Candidate candidate,
											CandidateWarehouseLocationItem candidateWarehouseLocationItem,
											Warehouse warehouse) {

		CandidateItemWarehouseVendor candidateItemWarehouseVendor = new CandidateItemWarehouseVendor();

		candidateItemWarehouseVendor.getKey().setVendorType(LocationKey.LOCATION_TYPE_VENDOR)
				.setBicep(CandidateUtils.bicepFromLaneAndWarehouse(candidate.getLane(), warehouse))
				.setWarehouseType(LocationKey.LOCATION_TYPE_WAREHOUSE)
				.setWarehouseNumber(warehouse.getWarehouseId());

		candidateItemWarehouseVendor.setCandidateWarehouseLocationItem(candidateWarehouseLocationItem)
				.setPrimaryVendor(CandidateUtils.YES)
				.setFreightException(CandidateUtils.NO)
				.setTermsException(CandidateUtils.NO)
				.setFreightFree(CandidateUtils.NO)
				.setPpaddException(CandidateUtils.NO)
				.setLastUpdateTime(Instant.now())
				.setLastUpdateUserId(user);

		return candidateItemWarehouseVendor;
	}

	/**
	 * Converts warehouse level data to the CandidateVendorLocationItem portion of the candidate.
	 *
	 * @param user The user who kicked off the request.
	 * @param subCommodity The DB entity for the sub-commoditied the candidate will be tied to.
	 * @param candidate The candidate being processed.
	 * @param candidateProduct The CandidateProduct being processed.
	 * @param candidateItemMaster The CandidateItemMaster being created.
	 * @param warehouse The warehouse being processed.
	 */
	protected CandidateVendorLocationItem warehouseToVendorLocationItem(String user, PhSubCommodity subCommodity, Candidate candidate,
																		 CandidateProduct candidateProduct,
																		 CandidateItemMaster candidateItemMaster,
																		 Warehouse warehouse) {

		String formattedDepartment = String.format("%02d", subCommodity.getCommodity().getItemClass().getDepartmentId());

		CandidateVendorLocationItem candidateVendorLocationItem = new CandidateVendorLocationItem();
		candidateVendorLocationItem.getKey().setVendorType(LocationKey.LOCATION_TYPE_VENDOR)
				.setBicep(CandidateUtils.bicepFromLaneAndWarehouse(candidate.getLane(), warehouse));

		candidateVendorLocationItem.setCandidateItemMaster(candidateItemMaster)
				.setVendItemId(candidateProduct.getVendorProductCode())
				.setTie(candidate.getVendorTie())
				.setTier(candidate.getVendorTier())
				.setOrderQuantityRestrictionCode(warehouse.getOrderRestriction().getId())
				.setLastUpdateTime(Instant.now())
				.setLastUpdateUserId(user)
				.setMinOrderQuantity(MINIMUM_ORDER_QUANTITY_DEFAULT)
				.setSampleProvided(CandidateUtils.NO)
				.setGuaranteedSale(CandidateUtils.NO)
				.setCountryOfOrigin(candidateProduct.getCountryOfOrigin().getCountryId())
				.setSeasonalityId(CandidateVendorLocationItem.SEASONALITY_ALL_YEAR)
				.setCostOwner(candidate.getCostOwner().getCostOwnerId())
				.setTopToTop(candidate.getCostOwner().getTopToTopId())
				.setDutyPercent(DUTY_PERCENT_DEFAULT)
				.setNewData(CandidateUtils.YES)
				.setHtsNumber(HTS_DEFAULT)
				.setHts2Number(HTS_DEFAULT)
				.setHts3Number(HTS_DEFAULT)
				.setAgentCommissionPercent(AGENT_COMMISSION_PERCENT_DEFAULT)
				.setDealItem(CandidateUtils.NO)
				.setAuthorized(CandidateUtils.YES)
				.setImportItem(CandidateUtils.NO)
				.setDeptNbr(formattedDepartment)
				.setSubDeptId(subCommodity.getCommodity().getItemClass().getSubDepartmentId())
				.setPssDepartment(subCommodity.getCommodity().getPssDepartment())
				.setSellByYear(SELL_BY_YEAR_DEFAULT)
				.setOrderQuantityType(warehouse.getOrderUnit().getId())
		;

		// Handle cost
		if (Objects.isNull(candidate.getCostLinkBy())) {
			candidateVendorLocationItem.setListCost(candidate.getInnerListCost())
					.setCostLink(WarehouseLocationItem.COST_LINK_NO_COST_LINK);
		} else {
			candidateVendorLocationItem.setCostLink(candidate.getCostLink())
					.setCostLinkItem(candidate.getCostLinkFromServer().getRepresentativeItemCode())
					.setListCost(candidate.getCostLinkFromServer().getListCost());
		}

		return candidateVendorLocationItem;
	}
}
