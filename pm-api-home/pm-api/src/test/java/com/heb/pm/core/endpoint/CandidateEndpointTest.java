package com.heb.pm.core.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.pm.TestConfiguration;
import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.repository.CandidateActivationRequestRepository;
import com.heb.pm.core.service.CandidateUtils;
import com.heb.pm.pam.model.Candidate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests CostLinkEndpoint.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class CandidateEndpointTest {

	private static final String USER_ID = "hebUser";
	private static final String RESPONSE_URL = "http://www.heb.com/activationResponse";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private CandidateActivationRequestRepository candidateActivationRequestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${app.test.basicWarehouseCandidate}")
	private String basicWarehouseCandidate;
	@Value("${app.test.basicallyEmptyCandidate}")
	private String emptyCandidate;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void validate_handlesVendorValidation() throws Exception {

		String candidateAsString = this.basicWarehouseCandidate;

		ResultActions resultActions = this.mockMvc.perform(post("/candidates/validate?validator=VENDOR_DATA_VALIDATOR")
				.content(candidateAsString)
				.param("user", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		resultActions
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void validate_handlesVendorAndBuyerValidation() throws Exception {

		String candidateAsString = this.basicWarehouseCandidate;

		ResultActions resultActions = this.mockMvc.perform(post("/candidates/validate?validator=VENDOR_DATA_VALIDATOR,BUYER_DATA_VALIDATOR")
				.content(candidateAsString)
				.param("user", USER_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		resultActions
				.andExpect(status().is4xxClientError());
	}

	@Test
	@Transactional
	public void postCandidateActivation_emptyCandidateReturnsErrors() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(post("/candidates/activateCandidate")
				.content(this.emptyCandidate)
				.param("user", USER_ID)
				.param("responseURL", RESPONSE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		resultActions.andExpect(status().is4xxClientError());
	}

	@Test
	@Transactional
	public void postCandidateActivation_handlesBasicWarehouseCandidate() throws Exception {

		String candidateAsString = this.basicWarehouseCandidate;

		Candidate candidate = this.objectMapper.readValue(candidateAsString, Candidate.class);

		ResultActions resultActions = this.mockMvc.perform(post("/candidates/activateCandidate")
 				.content(candidateAsString)
				.param("user", USER_ID)
				.param("responseURL", RESPONSE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		resultActions
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("-")));

		String transactionId = resultActions.andReturn().getResponse().getContentAsString();

		// CandidateActivationRequest
		CandidateActivationRequest request = this.candidateActivationRequestRepository.getOne(transactionId);
		Assert.assertEquals(transactionId, request.getRequestId());
		Assert.assertEquals(USER_ID, request.getCreateId());
		Assert.assertEquals(RESPONSE_URL, request.getResponseURL());
		Assert.assertNull(request.getErrors());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, request.getStatus());
		Assert.assertEquals(candidateAsString, request.getCandidate());

		// CandidateTransaction
		Assert.assertEquals(transactionId, request.getCandidateTransaction().getFileNm());
		Assert.assertEquals(candidate.getDescription(), request.getCandidateTransaction().getFileDescription());
		Assert.assertEquals(SourceSystem.PAM_SOURCE_SYSTEM.toString(), request.getCandidateTransaction().getSource());
		Assert.assertEquals(USER_ID, request.getCandidateTransaction().getCreateUserId());
		Assert.assertEquals(CandidateTransaction.ROLE_USER, request.getCandidateTransaction().getUserRole());
		Assert.assertEquals(CandidateTransaction.STAT_CODE_NOT_COMPLETE, request.getCandidateTransaction().getTransactionStatus());

		// CandidateWorkRequest
		Assert.assertEquals(1, request.getCandidateTransaction().getCandidateWorkRequests().size());
		CandidateWorkRequest candidateWorkRequest = request.getCandidateTransaction().getCandidateWorkRequests().get(0);
		Assert.assertEquals(Long.valueOf(CandidateWorkRequest.INTENT_NEW_PRODUCT), candidateWorkRequest.getIntent());
		Assert.assertEquals(USER_ID, candidateWorkRequest.getCreateUserId());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateWorkRequest.getStatus());
		Assert.assertEquals(Long.valueOf(CandidateStatus.STATUS_CHANGE_REASON_WORKING), candidateWorkRequest.getStatusChangeReason());
		Assert.assertEquals(SourceSystem.PAM_SOURCE_SYSTEM, candidateWorkRequest.getSource());
		Assert.assertEquals(CandidateUtils.YES, candidateWorkRequest.getReadyToActivate());
		Assert.assertEquals("555", candidateWorkRequest.getVendorAreaCode());
		Assert.assertEquals("1115555", candidateWorkRequest.getVendorPhone());
		Assert.assertEquals("sanchez.rick@heb.com", candidateWorkRequest.getVendorEmail());
		Assert.assertEquals(Long.valueOf(4122099999L), candidateWorkRequest.getUpc());

		// CandidateProductMaster
		CandidateProductMaster candidateProductMaster = candidateWorkRequest.getCandidateProductMasters().get(0);
		Assert.assertEquals("NEW PROD DESC", candidateProductMaster.getDescription());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(Long.valueOf(80L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(1100L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(6475L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("09", candidateProductMaster.getBdmId());
		Assert.assertEquals(USER_ID, candidateProductMaster.getLastUpdateUserId());
		Assert.assertNull(candidateProductMaster.getRetailLinkId());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSoldByWeight());
		Assert.assertEquals(Long.valueOf(2L), candidateProductMaster.getXFor());
		Assert.assertEquals(new BigDecimal("5.0"), candidateProductMaster.getRetail());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMatrixMargin());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(Long.valueOf(CandidateProductMaster.TOBACCO_TYPE_NOT_TOBACCO), candidateProductMaster.getTobaccoType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTobaccoUnstamped());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMarkPriceInStore());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTobaccoProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScaleProduct());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFsa());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrePriced());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrePriced());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getStrip());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getForceTare());
		Assert.assertEquals(Long.valueOf(14L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(0), candidateProductMaster.getScaleActionCode());
		Assert.assertEquals(Long.valueOf(0), candidateProductMaster.getScaleGrahicsCode());
		Assert.assertEquals(Long.valueOf(0), candidateProductMaster.getScaleLabelFormatOne());
		Assert.assertEquals(Long.valueOf(0), candidateProductMaster.getScaleLabelFormatTwo());
		Assert.assertEquals(Long.valueOf(4122099999L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(Long.valueOf(92L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("NEW PROD DES", candidateProductMaster.getScanDescription());
		Assert.assertEquals("000003", candidateProductMaster.getVertexTaxCategory());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPriceRequired());

		Assert.assertEquals(3, candidateProductMaster.getCandidateProductDescriptions().size());
		for (CandidateProductDescription candidateProductDescription : candidateProductMaster.getCandidateProductDescriptions()) {
			Assert.assertEquals(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH, candidateProductDescription.getKey().getLanguageType());
			Assert.assertEquals(USER_ID, candidateProductDescription.getLastUpdateUserId());
			switch (candidateProductDescription.getKey().getDescriptionType()) {
				case PRODUCT_DESCRIPTION:
					Assert.assertEquals("NEW PROD DESC", candidateProductDescription.getDescription());
					break;
				case CFD_1:
					Assert.assertEquals("test CFD 1", candidateProductDescription.getDescription());
					break;
				case CFD_2:
					Assert.assertEquals("test  CFD 2", candidateProductDescription.getDescription());
					break;
				default:
					Assert.fail(String.format("Unknown description type: \"%s\".", candidateProductDescription.getKey().getDescriptionType()));
			}
		}

		// CandidateSellingUnit
		Assert.assertEquals(1, candidateProductMaster.getCandidateSellingUnits().size());
		CandidateSellingUnit candidateSellingUnit = candidateProductMaster.getCandidateSellingUnits().get(0);
		Assert.assertEquals(Long.valueOf(4122099999L), candidateSellingUnit.getUpc());
		Assert.assertEquals(SellingUnit.SCAN_TYPE_UPC, candidateSellingUnit.getScanCodeType());
		Assert.assertEquals(CandidateUtils.NO, candidateSellingUnit.getBonus());
		Assert.assertEquals(USER_ID, candidateSellingUnit.getLastUpdatedBy());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getTestScanOverride());
		Assert.assertEquals(Long.valueOf(41220999996L), candidateSellingUnit.getTestScanUpc());
		Assert.assertEquals(BigDecimal.valueOf(1.0), candidateSellingUnit.getSellingSizeOne());
		Assert.assertEquals("ea", candidateSellingUnit.getSellingSizeCodeOne());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getNewData());
		Assert.assertEquals(BigDecimal.valueOf(6.0), candidateSellingUnit.getHeight());
		Assert.assertEquals(BigDecimal.valueOf(8.0), candidateSellingUnit.getLength());
		Assert.assertEquals(BigDecimal.valueOf(7.0), candidateSellingUnit.getWidth());
		Assert.assertEquals(BigDecimal.valueOf(5.0), candidateSellingUnit.getWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateSellingUnit.getSampleProvided());
		Assert.assertEquals(CandidateUtils.UNIT_OF_MEASURE_EACH, candidateSellingUnit.getTagSize());

		// CandidateItemMaster
		Assert.assertEquals(1, candidateWorkRequest.getCandidateItemMasters().size());
		CandidateItemMaster candidateItemMaster = candidateWorkRequest.getCandidateItemMasters().get(0);
		Assert.assertEquals("NEW PROD DESC", candidateItemMaster.getItemDescription());
		Assert.assertEquals("NEW PROD DESC", candidateItemMaster.getShortItemDescription());
		Assert.assertEquals(CandidateUtils.UNIT_OF_MEASURE_EACH, candidateItemMaster.getItemSizeText());
		Assert.assertEquals(BigDecimal.valueOf(1.0), candidateItemMaster.getItemSizeQuantity());
		Assert.assertEquals(CandidateUtils.UNIT_OF_MEASURE_EACH, candidateItemMaster.getItemSizeUom());
		Assert.assertEquals(Long.valueOf(150L), candidateItemMaster.getSupplierCaseLifeDays());
		Assert.assertEquals(Long.valueOf(40L), candidateItemMaster.getWarehouseReactionDays());
		Assert.assertEquals(Long.valueOf(30L), candidateItemMaster.getGuaranteeToStoreDays());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getDateControlledItem());
		Assert.assertEquals(Long.valueOf(1100L), candidateItemMaster.getCommodityCode());
		Assert.assertEquals(Long.valueOf(6475L), candidateItemMaster.getSubCommodityCode());
		Assert.assertEquals(Long.valueOf(80L), candidateItemMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(4122899999L), candidateItemMaster.getCaseUpc());
		Assert.assertEquals(Long.valueOf(4122099999L), candidateItemMaster.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99999L), candidateItemMaster.getMaxShipQuantity());
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
		Assert.assertEquals(Long.valueOf(10L), candidateItemMaster.getPack());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDsdItem());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getUpcMap());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDeposit());
		Assert.assertEquals(Long.valueOf(12L), candidateItemMaster.getDepartmentIdOne());
		Assert.assertEquals("A", candidateItemMaster.getSubDepartmentIdOne());
		Assert.assertEquals(Long.valueOf(92L), candidateItemMaster.getPssDepartmentCodeOne());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getUnstampedTobacco());
		Assert.assertEquals(USER_ID, candidateItemMaster.getLastUpdateUserId());
		Assert.assertEquals(Long.valueOf(50L), candidateItemMaster.getInboundSpecDays());
		Assert.assertEquals(CandidateItemMaster.DISTRIBUTION_CHANNEL_WAREHOUSE, candidateItemMaster.getDcChannelType());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getMrt());
		Assert.assertEquals(Long.valueOf(100L), candidateItemMaster.getMasterPackQuantity());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getNewData());
		Assert.assertEquals(BigDecimal.valueOf(10.0), candidateItemMaster.getShipLength());
		Assert.assertEquals(BigDecimal.valueOf(20.0), candidateItemMaster.getShipWidth());
		Assert.assertEquals(BigDecimal.valueOf(30.0), candidateItemMaster.getShipHeight());
		Assert.assertEquals((new BigDecimal(3.472)).setScale(3, RoundingMode.HALF_UP), candidateItemMaster.getShipCube());
		Assert.assertEquals(BigDecimal.valueOf(4.0), candidateItemMaster.getShipWeight());
		Assert.assertEquals(Long.valueOf(10L), candidateItemMaster.getShipInrPack());
		Assert.assertEquals(BigDecimal.valueOf(100.0), candidateItemMaster.getLength());
		Assert.assertEquals(BigDecimal.valueOf(110.0), candidateItemMaster.getWidth());
		Assert.assertEquals(BigDecimal.valueOf(120.0), candidateItemMaster.getHeight());
		Assert.assertEquals(BigDecimal.valueOf(130.0), candidateItemMaster.getWeight());
		Assert.assertEquals((new BigDecimal(763.8889)).setScale(4, RoundingMode.HALF_UP), candidateItemMaster.getCube());
		Assert.assertEquals(WarehouseLocationItem.SUSPENDED, candidateItemMaster.getPurchaseStatus());
		Assert.assertEquals(CandidateUtils.NO, candidateItemMaster.getDisplayReadyUnit());
		Assert.assertEquals(CandidateUtils.YES, candidateItemMaster.getAlwaysSubWhenOut());

		// CandidateStatus
		Assert.assertEquals(1, candidateWorkRequest.getCandidateStatuses().size());
		CandidateStatus candidateStatus = candidateWorkRequest.getCandidateStatuses().get(0);
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateStatus.getKey().getStatus());
		Assert.assertEquals(Long.valueOf(CandidateStatus.STATUS_CHANGE_REASON_WORKING), candidateStatus.getStatusChangeReason());
		Assert.assertEquals(USER_ID, candidateStatus.getUpdateUserId());

		// CandidateWarehouseLocationItem
		Assert.assertEquals(1, candidateItemMaster.getCandidateWarehouseLocationItems().size());
		CandidateWarehouseLocationItem candidateWarehouseLocationItem = candidateItemMaster.getCandidateWarehouseLocationItems().get(0);
		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateWarehouseLocationItem.getKey().getWarehouseType());
		Assert.assertEquals(Long.valueOf(217L), candidateWarehouseLocationItem.getKey().getWarehouseNumber());
		Assert.assertEquals(" ", candidateWarehouseLocationItem.getShipFromType());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getDsconTrxSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMfgId());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getVariableWeight());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getCatchWeight());
		Assert.assertEquals(CandidateUtils.YES, candidateWarehouseLocationItem.getItemRemarkSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getDalyHistSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getTransferAvailSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getPoRcmdSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMandFlwThrgSwitch());
		Assert.assertEquals(CandidateUtils.NO, candidateWarehouseLocationItem.getMandFlwThruSwitch());
		Assert.assertEquals(USER_ID, candidateWarehouseLocationItem.getLastUpdateUser());
		Assert.assertEquals(USER_ID, candidateWarehouseLocationItem.getAddedUser());
		Assert.assertEquals(CandidateUtils.YES, candidateWarehouseLocationItem.getNewData());

		// CandidateItemWarehouseComment
		Assert.assertEquals(1, candidateWarehouseLocationItem.getCandidateItemWarehouseComments().size());
		CandidateItemWarehouseComment candidateItemWarehouseComment = candidateWarehouseLocationItem.getCandidateItemWarehouseComments().get(0);
		Assert.assertEquals(Long.valueOf(217L), candidateItemWarehouseComment.getKey().getWarehouseNumber());
		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateItemWarehouseComment.getKey().getWarehouseType());
		Assert.assertEquals(CandidateItemWarehouseCommentKey.COMMENT_TYPE_REMARK, candidateItemWarehouseComment.getKey().getItemCommentType());
		Assert.assertEquals("Remarks line 1 remark", candidateItemWarehouseComment.getComments());


		// CandidateItemWarehouseVendor
		Assert.assertEquals(1, candidateWarehouseLocationItem.getCandidateItemWarehouseVendors().size());
		CandidateItemWarehouseVendor candidateItemWarehouseVendor = candidateWarehouseLocationItem.getCandidateItemWarehouseVendors().get(0);
		Assert.assertEquals(LocationKey.LOCATION_TYPE_VENDOR, candidateItemWarehouseVendor.getKey().getVendorType());
		Assert.assertEquals(Long.valueOf(17061856L), candidateItemWarehouseVendor.getKey().getBicep());
		Assert.assertEquals(LocationKey.LOCATION_TYPE_WAREHOUSE, candidateItemWarehouseVendor.getKey().getWarehouseType());
		Assert.assertEquals(Long.valueOf(217L), candidateItemWarehouseVendor.getKey().getWarehouseNumber());
		Assert.assertEquals(USER_ID, candidateItemWarehouseVendor.getLastUpdateUserId());
		Assert.assertEquals(CandidateUtils.YES, candidateItemWarehouseVendor.getPrimaryVendor());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getFreightException());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getTermsException());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getFreightFree());
		Assert.assertEquals(CandidateUtils.NO, candidateItemWarehouseVendor.getPpaddException());

		Assert.assertEquals(1, candidateItemMaster.getCandidateVendorLocationItems().size());
		CandidateVendorLocationItem candidateVendorLocationItem = candidateItemMaster.getCandidateVendorLocationItems().get(0);
		Assert.assertEquals(LocationKey.LOCATION_TYPE_VENDOR, candidateVendorLocationItem.getKey().getVendorType());
		Assert.assertEquals(Long.valueOf(17061856L), candidateVendorLocationItem.getKey().getBicep());
		Assert.assertEquals("VendorCode13", candidateVendorLocationItem.getVendItemId());
		Assert.assertEquals(Long.valueOf(1L), candidateVendorLocationItem.getTie());
		Assert.assertEquals(Long.valueOf(2L), candidateVendorLocationItem.getTier());
		Assert.assertEquals(BigDecimal.valueOf(24.0), candidateVendorLocationItem.getListCost());
		Assert.assertEquals("P    ", candidateVendorLocationItem.getOrderQuantityRestrictionCode());
		Assert.assertEquals(USER_ID, candidateVendorLocationItem.getLastUpdateUserId());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getMinOrderQuantity());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getSampleProvided());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getGuaranteedSale());
		Assert.assertEquals(Long.valueOf(14L), candidateVendorLocationItem.getCountryOfOrigin());
		Assert.assertEquals(Long.valueOf(13L), candidateVendorLocationItem.getSeasonalityId());
		Assert.assertEquals(Long.valueOf(20803L), candidateVendorLocationItem.getCostOwner());
		Assert.assertEquals(Long.valueOf(12084L), candidateVendorLocationItem.getTopToTop());
		Assert.assertEquals(BigDecimal.ZERO, candidateVendorLocationItem.getDutyPercent());
		Assert.assertEquals(CandidateUtils.YES, candidateVendorLocationItem.getNewData());
		Assert.assertNull(candidateVendorLocationItem.getSeasonalityYear());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHtsNumber());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHts2Number());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getHts3Number());
		Assert.assertEquals(BigDecimal.ZERO, candidateVendorLocationItem.getAgentCommissionPercent());
		Assert.assertEquals(CandidateUtils.NO, candidateVendorLocationItem.getDealItem());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getCostLink());
		Assert.assertNull(candidateVendorLocationItem.getCostLinkItem());
		Assert.assertEquals("12", candidateVendorLocationItem.getDeptNbr());
		Assert.assertEquals("A", candidateVendorLocationItem.getSubDeptId());
		Assert.assertEquals("C", candidateVendorLocationItem.getOrderQuantityType());
		Assert.assertEquals(Long.valueOf(92L), candidateVendorLocationItem.getPssDepartment());
		Assert.assertEquals(Long.valueOf(0), candidateVendorLocationItem.getSellByYear());
		Assert.assertNull(candidateVendorLocationItem.getSeasonality());
	}
}
