package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.pam.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;


/**
 * Tests CandidateItemService.
 *
 * @author d116773
 * @since 1.1.0
 */
public class CandidateItemServiceTest {

	private static final String USER_ID = "dfji3jv";

	private static final Long UPC = 3245334233L;
	private static final Long CASE_UPC = 23453232423L;
	private static final String SHORT_DESCRIPTION = "shrt des";
	private static final String LONG_DESCRIPTION = "description longer than 20";
	private static final String VENDOR_PRODUCT_CODE = "324avjoasi32v";
	private static final Long COUNTRY_OF_ORIGIN = 433094L;

	private static final Long COMMODITY = 243L;
	private static final Long SUB_COMMODITY = 34523L;
	private static final Long CLASS = 3242L;
	private static final Long DEPARTMENT = 3L;
	private static final String SUB_DEPARTMENT = "G";
	private static final String FORMATTED_DEPT = "03";
	private static final Long PSS_DEPARTMENT = 849845L;

	private static final BigDecimal TOTAL_VOLUME = new BigDecimal("2.3");
	private static final String RETAIL_SIZE = "POUND";
	private static final String UOM = "1";
	private static final Long SHELF_LIFE = 1L;
	private static final Long INBOUND_SPEC_DAYS = 2L;
	private static final Long WAREHOUSE_REACTION_DAYS = 3L;
	private static final Long STORE_DAYS = 4L;
	private static final BigDecimal INNER_LENGTH = new BigDecimal("435.85");
	private static final BigDecimal INNER_WIDTH = new BigDecimal("256.99");
	private static final BigDecimal INNER_HEIGHT = new BigDecimal("478.05");
	private static final BigDecimal INNER_WEIGHT = new BigDecimal("434.20");
	private static final BigDecimal INNER_CUBE = new BigDecimal("30987.237");
	private static final BigDecimal MASTER_LENGTH = new BigDecimal("369.22");
	private static final BigDecimal MASTER_WIDTH = new BigDecimal("597.12");
	private static final BigDecimal MASTER_HEIGHT = new BigDecimal("639.88");
	private static final BigDecimal MASTER_WEIGHT = new BigDecimal("32423423.33");
	private static final BigDecimal MASTER_CUBE = new BigDecimal("81639.7439");
	private static final Long INNER_PACK = 42L;
	private static final Long MASTER_PACK = 4343L;
	private static final Long COST_OWNER = 5452L;
	private static final Long TOP_TO_TOP = 3423L;
	private static final Long SEASON_YEAR = 3432313L;
	private static final String ORDER_QTY_RESTRICTION = "R";
	private static final String ORDER_UNIT = "453ss";

	private static final Long VENDROR_TIE = 55L;
	private static final Long VENDROR_TIER = 453L;

	private static final Long WAREHOUSE_ID = 3423L;
	private static final String OMI_ID = "23";
	private static final String LANE_ID = "324234";
	private static final Long BICEP = 23324234L;
	private static final BigDecimal UNIT_FACTOR_ONE = BigDecimal.valueOf(1);

	private static final BigDecimal INNER_LIST_COST = new BigDecimal("3455.33");
	private static final BigDecimal MASTER_LIST_COST = new BigDecimal("546554.43");
	private static final Long COST_LINK = 234323L;
	private static final Long COST_LINK_ITEM = 23452098L;
	private static final BigDecimal COST_LINK_COST = new BigDecimal("3232.329");

	private final CandidateItemService candidateItemService = new CandidateItemService();

	@Test
	public void candidateProductToCandidateItemMaster_returnCandidateItemMaster_ShortDescription_CodeDated() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateWithCodeDate();

		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		Assert.assertEquals(candidateWorkRequest, candidateItemMaster.getCandidateWorkRequest());
		Assert.assertEquals(SHORT_DESCRIPTION.toUpperCase(Locale.US), candidateItemMaster.getItemDescription());
		Assert.assertEquals(SHORT_DESCRIPTION.toUpperCase(Locale.US), candidateItemMaster.getShortItemDescription());
		Assert.assertEquals(RETAIL_SIZE, candidateItemMaster.getItemSizeText());
		Assert.assertEquals(TOTAL_VOLUME, candidateItemMaster.getItemSizeQuantity());
		Assert.assertEquals("LB", candidateItemMaster.getItemSizeUom());
		Assert.assertEquals(USER_ID, candidateItemMaster.getAddedUserId());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getDateControlledItem());
		Assert.assertEquals(SHELF_LIFE, candidateItemMaster.getSupplierCaseLifeDays());
		Assert.assertEquals(INBOUND_SPEC_DAYS, candidateItemMaster.getInboundSpecDays());
		Assert.assertEquals(WAREHOUSE_REACTION_DAYS, candidateItemMaster.getWarehouseReactionDays());
		Assert.assertEquals(STORE_DAYS, candidateItemMaster.getGuaranteeToStoreDays());
		Assert.assertEquals(SUB_COMMODITY, candidateItemMaster.getSubCommodityCode());
		Assert.assertEquals(CASE_UPC, candidateItemMaster.getCaseUpc());
		Assert.assertEquals(UPC, candidateItemMaster.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99_999L), candidateItemMaster.getMaxShipQuantity());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getNewItem());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getRepack());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getCriticalItemIndicator());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getNeverOut());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getCatchWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getLowVelocity());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getShipper());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getCrossDockItem());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getReplaceOrderQuantity());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getLetterOfCredit());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getVariableWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getForwardBuyApproved());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getCattle());
		Assert.assertEquals(INNER_PACK, candidateItemMaster.getPack());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDsdItem());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getUpcMap());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDeposit());
		Assert.assertEquals(DEPARTMENT, candidateItemMaster.getDepartmentIdOne());
		Assert.assertEquals(SUB_DEPARTMENT, candidateItemMaster.getSubDepartmentIdOne());
		Assert.assertEquals(PSS_DEPARTMENT, candidateItemMaster.getPssDepartmentCodeOne());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getUnstampedTobacco());
		Assert.assertEquals(USER_ID, candidateItemMaster.getLastUpdateUserId());
		Assert.assertEquals(CandidateItemMaster.DISTRIBUTION_CHANNEL_WAREHOUSE, candidateItemMaster.getDcChannelType());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getMrt());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getNewData());
		Assert.assertEquals(INNER_LENGTH, candidateItemMaster.getShipLength());
		Assert.assertEquals(INNER_WIDTH, candidateItemMaster.getShipWidth());
		Assert.assertEquals(INNER_HEIGHT, candidateItemMaster.getShipHeight());
		Assert.assertEquals(INNER_WEIGHT, candidateItemMaster.getShipWeight());
		Assert.assertEquals(INNER_PACK, candidateItemMaster.getPack());
		Assert.assertEquals(INNER_CUBE, candidateItemMaster.getShipCube());
		Assert.assertEquals(MASTER_LENGTH, candidateItemMaster.getLength());
		Assert.assertEquals(MASTER_WIDTH, candidateItemMaster.getWidth());
		Assert.assertEquals(MASTER_HEIGHT, candidateItemMaster.getHeight());
		Assert.assertEquals(MASTER_WEIGHT, candidateItemMaster.getWeight());
		Assert.assertEquals(MASTER_PACK, candidateItemMaster.getMasterPackQuantity());
		Assert.assertEquals(MASTER_CUBE, candidateItemMaster.getCube());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDisplayReadyUnit());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getAlwaysSubWhenOut());
	}

	@Test
	public void candidateProductToCandidateItemMaster_returnCandidateItemMaster_LongDescription() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateWithCodeDate();

		candidate.getCandidateProducts().get(0).setDescription(LONG_DESCRIPTION);

		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		Assert.assertEquals(candidateWorkRequest, candidateItemMaster.getCandidateWorkRequest());
		Assert.assertEquals(LONG_DESCRIPTION.toUpperCase(Locale.US), candidateItemMaster.getItemDescription());
		Assert.assertEquals(LONG_DESCRIPTION.substring(0, 20).toUpperCase(Locale.US), candidateItemMaster.getShortItemDescription());
	}

	@Test
	public void candidateProductToCandidateItemMaster_returnCandidateItemMaster_UomEach() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateWithCodeDate();

		candidate.setRetailSize("EACH").getUnitOfMeasure().setUnitOfMeasureId("ea");

		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		Assert.assertEquals("EACH", candidateItemMaster.getItemSizeText());
		Assert.assertEquals(TOTAL_VOLUME, candidateItemMaster.getItemSizeQuantity());
		Assert.assertEquals("EACH", candidateItemMaster.getItemSizeUom());
	}

	@Test
	public void candidateProductToCandidateItemMaster_returnCandidateItemMaster_NotCodeDated() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();

		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDateControlledItem());
		Assert.assertNull(candidateItemMaster.getSupplierCaseLifeDays());
		Assert.assertNull(candidateItemMaster.getInboundSpecDays());
		Assert.assertNull(candidateItemMaster.getWarehouseReactionDays());
		Assert.assertNull(candidateItemMaster.getGuaranteeToStoreDays());
	}


	@Test
	public void candidateProductToCandidateItemMaster_returnCandidateItemMaster_NotCodeDated_IgnoresCodeDateProperties() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();

		candidate.setMaxShelfLife(SHELF_LIFE).setInboundSpecDays(INBOUND_SPEC_DAYS)
				.setWarehouseReactionDays(WAREHOUSE_REACTION_DAYS).setGuaranteeToStoreDays(STORE_DAYS);

		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDateControlledItem());
		Assert.assertNull(candidateItemMaster.getSupplierCaseLifeDays());
		Assert.assertNull(candidateItemMaster.getInboundSpecDays());
		Assert.assertNull(candidateItemMaster.getWarehouseReactionDays());
		Assert.assertNull(candidateItemMaster.getGuaranteeToStoreDays());
	}


	@Test
	public void candidateProductToCandidateItemScanCode_returnsCandidateItemScanCode() {

		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		CandidateItemScanCode candidateItemScanCode =
				this.candidateItemService.candidateProductToCandidateItemScanCode(candidate,
						candidate.getCandidateProducts().get(0), candidateItemMaster);

		Assert.assertEquals(candidateItemMaster, candidateItemScanCode.getCandidateItemMaster());
		Assert.assertEquals(UPC, candidateItemScanCode.getKey().getUpc());
		Assert.assertEquals(SellingUnit.SCAN_TYPE_UPC, candidateItemScanCode.getScanType());
		Assert.assertEquals(INNER_PACK, candidateItemScanCode.getRetailPackQuantity());
		Assert.assertEquals(CandidateUtils.YES, candidateItemScanCode.getNewData());
	}

	@Test
	public void warehouseToWarehouseLocationItem_returnsWarehouseLocationItem() {

		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		CandidateWarehouseLocationItem candidateWarehouseLocationItem =
				this.candidateItemService.warehouseToWarehouseLocationItem(USER_ID, candidateItemMaster,
						candidate.getCandidateProducts().get(0).getWarehouses().get(0), candidate);

		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateWarehouseLocationItem.getKey().getWarehouseType());
		Assert.assertEquals(WAREHOUSE_ID, candidateWarehouseLocationItem.getKey().getWarehouseNumber());
		Assert.assertEquals(candidateItemMaster, candidateWarehouseLocationItem.getCandidateItemMaster());
		Assert.assertEquals(" ", candidateWarehouseLocationItem.getShipFromType());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getDsconTrxSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMfgId());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getVariableWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getCatchWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getDalyHistSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getTransferAvailSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getPoRcmdSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMandFlwThruSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMandFlwThrgSwitch());
		Assert.assertEquals(USER_ID, candidateWarehouseLocationItem.getLastUpdateUser());
		Assert.assertEquals(USER_ID, candidateWarehouseLocationItem.getAddedUser());
		Assert.assertEquals(CandidateUtils.YES, candidateWarehouseLocationItem.getNewData());
		Assert.assertEquals(UNIT_FACTOR_ONE, candidateWarehouseLocationItem.getUnitFactor1());
	}


	@Test
	public void remarkToCandidateItemWarehouseComment_handlesEmptyRemark() {

		Assert.assertTrue(this.candidateItemService.remarkToCandidateItemWarehouseComment(null, null, "  ", null).isEmpty());
	}

	@Test
	public void remarkToCandidateItemWarehouseComment_handlesNullRemark() {

		Assert.assertTrue(this.candidateItemService.remarkToCandidateItemWarehouseComment(null, null, null, null).isEmpty());
	}

	@Test
	public void remarkToCandidateItemWarehouseComment_returnsCandidateItemWarehouseComment() {


		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidate.getCandidateProducts().get(0), this.getSubCommodity());

		CandidateWarehouseLocationItem candidateWarehouseLocationItem =
				this.candidateItemService.warehouseToWarehouseLocationItem(USER_ID, candidateItemMaster,
						candidate.getCandidateProducts().get(0).getWarehouses().get(0), candidate);

		Optional<CandidateItemWarehouseComment> wrappedCandidateItemWarehouseComment =
			 	this.candidateItemService.remarkToCandidateItemWarehouseComment(candidate.getCandidateProducts().get(0).getWarehouses().get(0),
						4534L, "test comment", candidateWarehouseLocationItem);

		Assert.assertFalse(wrappedCandidateItemWarehouseComment.isEmpty());
		CandidateItemWarehouseComment candidateItemWarehouseComment = wrappedCandidateItemWarehouseComment.get();
		Assert.assertEquals(WAREHOUSE_ID, candidateItemWarehouseComment.getKey().getWarehouseNumber());
		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateItemWarehouseComment.getKey().getWarehouseType());
		Assert.assertEquals(Long.valueOf(4534L), candidateItemWarehouseComment.getKey().getItemCommentNumber());
		Assert.assertEquals(CandidateItemWarehouseCommentKey.COMMENT_TYPE_REMARK, candidateItemWarehouseComment.getKey().getItemCommentType());
		Assert.assertEquals("test comment", candidateItemWarehouseComment.getComments());

	}


	@Test
	public void warehouseTotemWarehouseVendor_returnsCandidateItemWarehouseVendor() {

		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateProduct candidateProduct = candidate.getCandidateProducts().get(0);
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidateProduct, this.getSubCommodity());

		Warehouse warehouse = candidate.getCandidateProducts().get(0).getWarehouses().get(0);
		CandidateWarehouseLocationItem candidateWarehouseLocationItem =
				this.candidateItemService.warehouseToWarehouseLocationItem(USER_ID, candidateItemMaster,
						warehouse, candidate);

		CandidateItemWarehouseVendor candidateItemWarehouseVendor =
				this.candidateItemService.warehouseToItemWarehouseVendor(USER_ID, candidate,
						candidateWarehouseLocationItem, warehouse);

		Assert.assertEquals(LocationKey.LOCATION_TYPE_VENDOR, candidateItemWarehouseVendor.getKey().getVendorType());
		Assert.assertEquals(BICEP, candidateItemWarehouseVendor.getKey().getBicep());
		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateItemWarehouseVendor.getKey().getWarehouseType());
		Assert.assertEquals(WAREHOUSE_ID, candidateItemWarehouseVendor.getKey().getWarehouseNumber());
		Assert.assertEquals(candidateWarehouseLocationItem, candidateItemWarehouseVendor.getCandidateWarehouseLocationItem());
		Assert.assertEquals(CandidateUtils.YES, candidateItemWarehouseVendor.getPrimaryVendor());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getFreightException());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getTermsException());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getFreightFree());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getPpaddException());
		Assert.assertEquals(USER_ID, candidateItemWarehouseVendor.getLastUpdateUserId());
	}

	@Test
	public void warehouseToVendorLocationItem_returnsVendorLocationItem_noCostLink() {

		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateProduct candidateProduct = candidate.getCandidateProducts().get(0);
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidateProduct, this.getSubCommodity());
		Warehouse warehouse = candidate.getCandidateProducts().get(0).getWarehouses().get(0);

		CandidateVendorLocationItem candidateVendorLocationItem =
				this.candidateItemService.warehouseToVendorLocationItem(USER_ID, this.getSubCommodity(),
						candidate, candidateProduct, candidateItemMaster, warehouse);

		Assert.assertEquals(candidateItemMaster, candidateVendorLocationItem.getCandidateItemMaster());
		Assert.assertEquals(VENDOR_PRODUCT_CODE, candidateVendorLocationItem.getVendItemId());
		Assert.assertEquals(VENDROR_TIE, candidateVendorLocationItem.getTie());
		Assert.assertEquals(VENDROR_TIER, candidateVendorLocationItem.getTier());
		Assert.assertEquals(ORDER_QTY_RESTRICTION, candidateVendorLocationItem.getOrderQuantityRestrictionCode());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getSampleProvided());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getGuaranteedSale());
		Assert.assertEquals(COUNTRY_OF_ORIGIN, candidateVendorLocationItem.getCountryOfOrigin());
		Assert.assertEquals(Long.valueOf(CandidateVendorLocationItem.SEASONALITY_ALL_YEAR), candidateVendorLocationItem.getSeasonalityId());
		Assert.assertEquals(COST_OWNER, candidateVendorLocationItem.getCostOwner());
		Assert.assertEquals(TOP_TO_TOP, candidateVendorLocationItem.getTopToTop());
		Assert.assertEquals(BigDecimal.ZERO, candidateVendorLocationItem.getDutyPercent());
		Assert.assertEquals(CandidateUtils.YES, candidateVendorLocationItem.getNewData());
		Assert.assertNull(candidateVendorLocationItem.getSeasonalityYear());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHtsNumber());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHts2Number());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHts3Number());
		Assert.assertEquals(BigDecimal.ZERO, candidateVendorLocationItem.getAgentCommissionPercent());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getDealItem());
		Assert.assertEquals(CandidateUtils.YES, candidateVendorLocationItem.getAuthorized());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getImportItem());
		Assert.assertEquals(FORMATTED_DEPT, candidateVendorLocationItem.getDeptNbr());
		Assert.assertEquals(SUB_DEPARTMENT, candidateVendorLocationItem.getSubDeptId());
		Assert.assertEquals(ORDER_QTY_RESTRICTION, candidateVendorLocationItem.getOrderQuantityRestrictionCode());
		Assert.assertEquals(PSS_DEPARTMENT, candidateVendorLocationItem.getPssDepartment());
		Assert.assertEquals(Long.valueOf(0L), candidateVendorLocationItem.getSellByYear());
		Assert.assertEquals(ORDER_UNIT, candidateVendorLocationItem.getOrderQuantityType());
		Assert.assertEquals(USER_ID, candidateVendorLocationItem.getLastUpdateUserId());
		Assert.assertEquals(Long.valueOf(0L), candidateVendorLocationItem.getCostLink());
		Assert.assertNull(candidateVendorLocationItem.getCostLinkItem());
		Assert.assertEquals(INNER_LIST_COST, candidateVendorLocationItem.getListCost());
	}

	@Test
	public void warehouseToVendorLocationItem_returnsVendorLocationItem_costLink() {

		CandidateWorkRequest candidateWorkRequest = this.getCandidateWorkRequest();
		Candidate candidate = this.getTestCandidateNotCodeDated();
		CandidateProduct candidateProduct = candidate.getCandidateProducts().get(0);
		CandidateItemMaster candidateItemMaster =
				this.candidateItemService.candidateProductToCandidateItemMaster(USER_ID, candidateWorkRequest,
						candidate, candidateProduct, this.getSubCommodity());
		Warehouse warehouse = candidate.getCandidateProducts().get(0).getWarehouses().get(0);

		candidate.setCostLinkBy("ITEM").setCostLink(COST_LINK)
				.setCostLinkFromServer(CostLink.of().setRepresentativeItemCode(COST_LINK_ITEM).setListCost(COST_LINK_COST));

		CandidateVendorLocationItem candidateVendorLocationItem =
				this.candidateItemService.warehouseToVendorLocationItem(USER_ID, this.getSubCommodity(),
						candidate, candidateProduct, candidateItemMaster, warehouse);

		Assert.assertEquals(COST_LINK, candidateVendorLocationItem.getCostLink());
		Assert.assertEquals(COST_LINK_ITEM, candidateVendorLocationItem.getCostLinkItem());
		Assert.assertEquals(COST_LINK_COST, candidateVendorLocationItem.getListCost());
	}
	// ******************************
	// * Helper methods
	// ******************************

	private CandidateWorkRequest getCandidateWorkRequest() {
		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(244324L);
		return candidateWorkRequest;
	}

	private PhSubCommodity getSubCommodity() {

		PhItemClass phItemClass = new PhItemClass();
		phItemClass.setDepartmentId(DEPARTMENT).setSubDepartmentId(SUB_DEPARTMENT);

		PhCommodity phCommodity = new PhCommodity().setItemClass(phItemClass).setPssDepartment(PSS_DEPARTMENT);
		phCommodity.getKey().setCommodityCode(COMMODITY).setClassCode(CLASS);

		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity);

		phSubCommodity.getKey().setSubCommodityCode(SUB_COMMODITY);

		return phSubCommodity;
	}

	private Candidate getTestCandidateNotCodeDated() {

		Candidate candidate = Candidate.of().setRetailSize(RETAIL_SIZE)
				.setUnitOfMeasure(UnitOfMeasure.of().setUnitOfMeasureId(UOM))
				.setTotalVolume(TOTAL_VOLUME)
				.setCodeDate(Boolean.FALSE)
				.setInnerLength(INNER_LENGTH)
				.setInnerWidth(INNER_WIDTH)
				.setInnerHeight(INNER_HEIGHT)
				.setInnerWeight(INNER_WEIGHT)
				.setMasterLength(MASTER_LENGTH)
				.setMasterWidth(MASTER_WIDTH)
				.setMasterHeight(MASTER_HEIGHT)
				.setMasterWeight(MASTER_WEIGHT)
				.setInnerPack(INNER_PACK)
				.setMasterPack(MASTER_PACK)
				.setLane(this.getLane())
				.setInnerListCost(INNER_LIST_COST)
				.setMasterListCost(MASTER_LIST_COST)
				.setVendorTie(VENDROR_TIE)
				.setVendorTier(VENDROR_TIER)
				.setCostOwner(com.heb.pm.pam.model.CostOwner.of().setCostOwnerId(COST_OWNER).setTopToTopId(TOP_TO_TOP))
				.setSeasonYear(SEASON_YEAR)
				.setCubeAdjustedFactor(UNIT_FACTOR_ONE)
				;

		CandidateProduct candidateProduct = CandidateProduct.of().setDescription(SHORT_DESCRIPTION)
				.setCaseUpc(CASE_UPC).setUpc(UPC).setVendorProductCode(VENDOR_PRODUCT_CODE)
				.setCountryOfOrigin(CountryOfOrigin.of().setCountryId(COUNTRY_OF_ORIGIN));

		candidateProduct.getWarehouses().add(this.getWarehouse());

		candidate.getCandidateProducts().add(candidateProduct);
		return candidate;
	}

	private Candidate getTestCandidateWithCodeDate() {

		return this.getTestCandidateNotCodeDated().setCodeDate(Boolean.TRUE)
				.setMaxShelfLife(SHELF_LIFE).setInboundSpecDays(INBOUND_SPEC_DAYS)
				.setWarehouseReactionDays(WAREHOUSE_REACTION_DAYS)
				.setGuaranteeToStoreDays(STORE_DAYS);
	}

	private Warehouse getWarehouse() {

		return Warehouse.of().setWarehouseId(WAREHOUSE_ID)
				.setOmiId(OMI_ID)
				.setOrderRestriction(OrderRestriction.of().setId(ORDER_QTY_RESTRICTION))
				.setOrderUnit(OrderUnit.of().setId(ORDER_UNIT))
				;
	}

	private Lane getLane() {

		return Lane.of().setId(LANE_ID);
	}

}
