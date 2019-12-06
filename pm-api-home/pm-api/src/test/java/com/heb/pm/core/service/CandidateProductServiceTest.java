package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.dao.core.entity.codes.DescriptionType;
import com.heb.pm.pam.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Tests CandidateProductService.
 *
 * @author d116773
 * @since 1.1.0
 */
public class CandidateProductServiceTest {

	private static final String USER_ID = "dfsdfse";

	private final CandidateProductService candidateProductService = new CandidateProductService();

	@Test
	public void getUpcWithCheckDigit_returnsUpcWithCheckDigit() {

		Assert.assertEquals(Long.valueOf(235643489L), this.candidateProductService.getUpcWithCheckDigit(23564348L, 9L));
	}

	@Test
	public void handleTobaccoFields_handlesNonTobaccoProduct() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();

		this.candidateProductService.handleTobaccoFields(candidateProductMaster);

		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTobaccoProduct());
		Assert.assertEquals(Long.valueOf(CandidateProductMaster.TOBACCO_TYPE_NOT_TOBACCO), candidateProductMaster.getTobaccoType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTobaccoUnstamped());
	}

	@Test
	public void handleRetailFields_handlesKeyedRetail() {

		Candidate candidate = Candidate.of().setRetailType(Candidate.KEY_RETAIL).setRetailXFor(2L).setRetailPrice(new BigDecimal("4.60"));

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();

		this.candidateProductService.handleRetailFields(candidate, candidateProductMaster);
		Assert.assertNull(candidateProductMaster.getRetailLinkId());
		Assert.assertNull(candidateProductMaster.getRetailLinkUpc());
		Assert.assertEquals(Long.valueOf(2L), candidateProductMaster.getXFor());
		Assert.assertEquals(new BigDecimal("4.60"), candidateProductMaster.getRetail());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMarkPriceInStore());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPriceRequired());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getPrePriceXFor());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMatrixMargin());
	}

	@Test
	public void handleRetailFields_handlesRetailLink() {

		com.heb.pm.pam.model.RetailLink retailLink = com.heb.pm.pam.model.RetailLink.of().setRetailLinkNumber(223423L).setUnitUpc(435345344L);

		Candidate candidate = Candidate.of().setRetailType(Candidate.RETAIL_LINK).setRetailLink(retailLink);

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();

		this.candidateProductService.handleRetailFields(candidate, candidateProductMaster);
		Assert.assertEquals(Long.valueOf(223423L), candidateProductMaster.getRetailLinkId());
		Assert.assertEquals(Long.valueOf(435345344L), candidateProductMaster.getRetailLinkUpc());
		Assert.assertEquals(Long.valueOf(1L), candidateProductMaster.getXFor());
		Assert.assertNull(candidateProductMaster.getRetail());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMarkPriceInStore());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPriceRequired());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getPrePriceXFor());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMatrixMargin());
	}

	@Test
	public void handleRetailFields_handlesPriceRequired() {

		Candidate candidate = Candidate.of().setRetailType(Candidate.PRICE_REQUIRED);

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();

		this.candidateProductService.handleRetailFields(candidate, candidateProductMaster);
		Assert.assertNull(candidateProductMaster.getRetailLinkId());
		Assert.assertNull(candidateProductMaster.getRetailLinkUpc());
		Assert.assertEquals(Long.valueOf(1L), candidateProductMaster.getXFor());
		Assert.assertNull(candidateProductMaster.getRetail());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMarkPriceInStore());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getPriceRequired());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getPrePriceXFor());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMatrixMargin());
	}

	@Test
	public void handleScaleFields_handlesNonScaleProduct() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();

		this.candidateProductService.handleScaleFields(candidateProductMaster);

		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSoldByWeight());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getStrip());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getForceTare());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getScaleActionCode());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getScaleGrahicsCode());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getScaleLabelFormatOne());
		Assert.assertEquals(Long.valueOf(0L), candidateProductMaster.getScaleLabelFormatTwo());
	}

	@Test
	public void getStandardCandidateProductDescription_createsStandardDescription() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of().setDescription("teSt Description");

		CandidateProductDescription candidateProductDescription =
				this.candidateProductService.getStandardCandidateProductDescription(candidateProduct, candidateProductMaster);

		Assert.assertEquals(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH, candidateProductDescription.getKey().getLanguageType());
		Assert.assertEquals(DescriptionType.PRODUCT_DESCRIPTION, candidateProductDescription.getKey().getDescriptionType());
		Assert.assertEquals(USER_ID, candidateProductDescription.getLastUpdateUserId());
		Assert.assertEquals("TEST DESCRIPTION", candidateProductDescription.getDescription());
		Assert.assertEquals(candidateProductMaster, candidateProductDescription.getCandidateProductMaster());
	}

	@Test
	public void getCustomerFriendlyDescriptionOne_createsCFD1() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of().setCustomerFriendlyDescription1("teSt CFD1 Description");

		CandidateProductDescription candidateProductDescription =
				this.candidateProductService.getCustomerFriendlyDescriptionOne(candidateProduct, candidateProductMaster);

		Assert.assertEquals(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH, candidateProductDescription.getKey().getLanguageType());
		Assert.assertEquals(DescriptionType.CFD_1, candidateProductDescription.getKey().getDescriptionType());
		Assert.assertEquals(USER_ID, candidateProductDescription.getLastUpdateUserId());
		Assert.assertEquals("teSt CFD1 Description", candidateProductDescription.getDescription());
		Assert.assertEquals(candidateProductMaster, candidateProductDescription.getCandidateProductMaster());
	}

	@Test
	public void getCustomerFriendlyDescriptionTwo_createsCFD2() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of().setCustomerFriendlyDescription2("teSt CFD2 Description");

		Optional<CandidateProductDescription> wrappedCandidateProductDescription =
				this.candidateProductService.getCustomerFriendlyDescriptionTwo(candidateProduct, candidateProductMaster);

		Assert.assertFalse(wrappedCandidateProductDescription.isEmpty());
		CandidateProductDescription candidateProductDescription = wrappedCandidateProductDescription.get();
		Assert.assertEquals(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH, candidateProductDescription.getKey().getLanguageType());
		Assert.assertEquals(DescriptionType.CFD_2, candidateProductDescription.getKey().getDescriptionType());
		Assert.assertEquals(USER_ID, candidateProductDescription.getLastUpdateUserId());
		Assert.assertEquals("teSt CFD2 Description", candidateProductDescription.getDescription());
		Assert.assertEquals(candidateProductMaster, candidateProductDescription.getCandidateProductMaster());
	}

	@Test
	public void getCustomerFriendlyDescriptionTwo_handlesNullCfd2() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of();

		Optional<CandidateProductDescription> wrappedCandidateProductDescription =
				this.candidateProductService.getCustomerFriendlyDescriptionTwo(candidateProduct, candidateProductMaster);

		Assert.assertTrue(wrappedCandidateProductDescription.isEmpty());
	}

	@Test
	public void getCustomerFriendlyDescriptionTwo_handlesEmptyCfd2() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of().setCustomerFriendlyDescription2("");

		Optional<CandidateProductDescription> wrappedCandidateProductDescription =
				this.candidateProductService.getCustomerFriendlyDescriptionTwo(candidateProduct, candidateProductMaster);

		Assert.assertTrue(wrappedCandidateProductDescription.isEmpty());
	}

	@Test
	public void getCustomerFriendlyDescriptionTwo_handlesAllBlankCfd2() {

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateProduct candidateProduct = CandidateProduct.of().setCustomerFriendlyDescription2("      ");

		Optional<CandidateProductDescription> wrappedCandidateProductDescription =
				this.candidateProductService.getCustomerFriendlyDescriptionTwo(candidateProduct, candidateProductMaster);

		Assert.assertTrue(wrappedCandidateProductDescription.isEmpty());
	}

	@Test
	public void candidateProductMasterToCandidateSellingUnits_createsBasicSellingUnitNotEach() {

		Candidate candidate = Candidate.of().setRetailSize("EACH").setProductHeight(new BigDecimal("1.1")).setTotalVolume(new BigDecimal("5.7"))
				.setProductLength(new BigDecimal("2.2")).setProductWidth(new BigDecimal("3.3")).setProductWeight(new BigDecimal("4.4"))
				.setUnitOfMeasure(UnitOfMeasure.of().setUnitOfMeasureId("OZ"))
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));

		candidate.getCandidateProducts().add(CandidateProduct.of().setUpc(4234523444L).setUpcCheckDigit(1L));

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateSellingUnit candidateSellingUnit =
				this.candidateProductService.candidateProductMasterToCandidateSellingUnits(USER_ID, candidate,
						candidate.getCandidateProducts().get(0), candidateProductMaster);

		Assert.assertEquals(Long.valueOf(4234523444L), candidateSellingUnit.getUpc());
		Assert.assertEquals(SellingUnit.SCAN_TYPE_UPC, candidateSellingUnit.getScanCodeType());
		Assert.assertEquals(CandidateUtils.NO, candidateSellingUnit.getBonus());
		Assert.assertEquals(USER_ID, candidateSellingUnit.getLastUpdatedBy());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getTestScanOverride());
		Assert.assertEquals(Long.valueOf(42345234441L), candidateSellingUnit.getTestScanUpc());
		Assert.assertEquals(new BigDecimal("5.7"), candidateSellingUnit.getSellingSizeOne());
		Assert.assertEquals("OZ", candidateSellingUnit.getSellingSizeCodeOne());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getNewData());
		Assert.assertEquals(new BigDecimal("1.1"), candidateSellingUnit.getHeight());
		Assert.assertEquals(new BigDecimal("2.2"), candidateSellingUnit.getLength());
		Assert.assertEquals(new BigDecimal("3.3"), candidateSellingUnit.getWidth());
		Assert.assertEquals(new BigDecimal("4.4"), candidateSellingUnit.getWeight());
		Assert.assertEquals("EACH", candidateSellingUnit.getTagSize());
	}

	@Test
	public void candidateProductMasterToCandidateSellingUnits_createsBasicSellingUnitEach() {

		Candidate candidate = Candidate.of().setRetailSize("POUND").setProductHeight(new BigDecimal("1.1"))
				.setProductLength(new BigDecimal("2.2")).setProductWidth(new BigDecimal("3.3")).setProductWeight(new BigDecimal("4.4"))
				.setUnitOfMeasure(UnitOfMeasure.of().setUnitOfMeasureId("1")).setTotalVolume(new BigDecimal("1.0"));

		candidate.getCandidateProducts().add(CandidateProduct.of().setUpc(4234523444L).setUpcCheckDigit(1L));

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster()
				.setCandidateProductId(453453454L)
				.setLastUpdateUserId(USER_ID);

		CandidateSellingUnit candidateSellingUnit =
				this.candidateProductService.candidateProductMasterToCandidateSellingUnits(USER_ID, candidate,
						candidate.getCandidateProducts().get(0), candidateProductMaster);

		Assert.assertEquals(Long.valueOf(4234523444L), candidateSellingUnit.getUpc());
		Assert.assertEquals(SellingUnit.SCAN_TYPE_UPC, candidateSellingUnit.getScanCodeType());
		Assert.assertEquals(CandidateUtils.NO, candidateSellingUnit.getBonus());
		Assert.assertEquals(USER_ID, candidateSellingUnit.getLastUpdatedBy());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getTestScanOverride());
		Assert.assertEquals(Long.valueOf(42345234441L), candidateSellingUnit.getTestScanUpc());
		Assert.assertEquals(new BigDecimal("1.0"), candidateSellingUnit.getSellingSizeOne());
		Assert.assertEquals("1", candidateSellingUnit.getSellingSizeCodeOne());
		Assert.assertEquals(CandidateUtils.YES, candidateSellingUnit.getNewData());
		Assert.assertEquals(new BigDecimal("1.1"), candidateSellingUnit.getHeight());
		Assert.assertEquals(new BigDecimal("2.2"), candidateSellingUnit.getLength());
		Assert.assertEquals(new BigDecimal("3.3"), candidateSellingUnit.getWidth());
		Assert.assertEquals(new BigDecimal("4.4"), candidateSellingUnit.getWeight());
		Assert.assertEquals("POUND", candidateSellingUnit.getTagSize());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_ShortDescription_NotTaxable_FoodSampEligible_NotSeasonal_NotFSA() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.TRUE)
				.setTaxable(Boolean.FALSE).setBrand(Brand.of().setBrandId(44L))
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("shrT Desc").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);

		Assert.assertEquals("SHRT DESC", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals("TEST PACKAGE DESCRIPTION", candidateProductMaster.getPackageType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("SHRT DESC", candidateProductMaster.getScanDescription());
		Assert.assertEquals("8888", candidateProductMaster.getVertexTaxCategory());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_LongDescription_NotTaxable_FoodSampEligible_NotSeasonal_NotFSA() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.TRUE)
				.setTaxable(Boolean.FALSE).setBrand(Brand.of().setBrandId(44L))
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("desc Longer Than 12 chr").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);

		Assert.assertEquals("DESC LONGER THAN 12 CHR", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals("TEST PACKAGE DESCRIPTION", candidateProductMaster.getPackageType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("DESC LONGER", candidateProductMaster.getScanDescription());
		Assert.assertEquals("8888", candidateProductMaster.getVertexTaxCategory());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_LongDescription_Taxable_FoodSampEligible_NotSeasonal_NotFSA_LongPackageType() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.TRUE)
				.setTaxable(Boolean.TRUE).setBrand(Brand.of().setBrandId(44L))
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION THAT IS LONGER THAN 30 CHARACTERS"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("desc Longer Than 12 chr").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);

		Assert.assertEquals("DESC LONGER THAN 12 CHR", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals("TEST PACKAGE DESCRIPTION THAT", candidateProductMaster.getPackageType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("DESC LONGER", candidateProductMaster.getScanDescription());
		Assert.assertEquals("99999", candidateProductMaster.getVertexTaxCategory());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_LongDescription_Taxable_NotFoodSampEligible_NotSeasonal_NotFSA() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.FALSE)
				.setTaxable(Boolean.TRUE).setBrand(Brand.of().setBrandId(44L))
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("desc Longer Than 12 chr").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);

		Assert.assertEquals("DESC LONGER THAN 12 CHR", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals("TEST PACKAGE DESCRIPTION", candidateProductMaster.getPackageType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("DESC LONGER", candidateProductMaster.getScanDescription());
		Assert.assertEquals("99999", candidateProductMaster.getVertexTaxCategory());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_LongDescription_Taxable_NotFoodSampEligible_NotSeasonal_FSA() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.FALSE)
				.setTaxable(Boolean.TRUE).setBrand(Brand.of().setBrandId(44L)).setFlexibleSpendingAccount(Boolean.TRUE)
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("desc Longer Than 12 chr").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);

		Assert.assertEquals("DESC LONGER THAN 12 CHR", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());
		Assert.assertEquals("TEST PACKAGE DESCRIPTION", candidateProductMaster.getPackageType());

		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("DESC LONGER", candidateProductMaster.getScanDescription());
		Assert.assertEquals("99999", candidateProductMaster.getVertexTaxCategory());
	}

	@Test
	public void candidateProductToCandidateProductMaster_createsBasicProduct_LongDescription_Taxable_NotFoodSampEligible_Seasonal_FSA() {

		PhCommodity phCommodity = new PhCommodity().setPssDepartment(99L);
		phCommodity.getKey().setClassCode(4L).setCommodityCode(223L);
		PhSubCommodity phSubCommodity = new PhSubCommodity().setCommodity(phCommodity).setNonTaxCategoryCode("8888").setTaxCategoryCode("99999");
		phSubCommodity.getKey().setSubCommodityCode(2345L);

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(4563453L);

		Candidate candidate = Candidate.of().setBuyer(Buyer.of().setBuyerId("A4")).setFoodStamp(Boolean.FALSE)
				.setTaxable(Boolean.TRUE).setBrand(Brand.of().setBrandId(44L)).setFlexibleSpendingAccount(Boolean.TRUE)
				.setSeason(Season.of().setSeasonId(11111L)).setSeasonYear(2018L)
				.setPackageType(PackageType.of().setDescription("TEST PACKAGE DESCRIPTION"));
		candidate.getCandidateProducts().add(CandidateProduct.of().setDescription("desc Longer Than 12 chr").setUpc(2342323L));

		CandidateProductMaster candidateProductMaster =
				this.candidateProductService.candidateProductToCandidateProductMaster(USER_ID, candidateWorkRequest, candidate,
						candidate.getCandidateProducts().get(0), phSubCommodity);
		Assert.assertEquals("TEST PACKAGE DESCRIPTION", candidateProductMaster.getPackageType());
		Assert.assertEquals("DESC LONGER THAN 12 CHR", candidateProductMaster.getDescription());
		Assert.assertEquals(candidateWorkRequest, candidateProductMaster.getCandidateWorkRequest());
		Assert.assertEquals(Long.valueOf(4L), candidateProductMaster.getClassCode());
		Assert.assertEquals(Long.valueOf(223L), candidateProductMaster.getCommodityId());
		Assert.assertEquals(Long.valueOf(2345L), candidateProductMaster.getSubCommodityId());
		Assert.assertEquals("A4", candidateProductMaster.getBdmId());
		Assert.assertEquals(CandidateProductMaster.PRODUCT_TYPE_BASIC, candidateProductMaster.getProductType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getQuantityRestricted());

		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAvc());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getStk());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSpecSheetAttached());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getFoodStampEligible());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getTaxable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSeasonal());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPharmacyProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getAlcoholProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getGenericProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getPrivateLabelProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getMacEac());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getScannable());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getFsa());
		Assert.assertEquals(",", candidateProductMaster.getPseType());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getHealthyLivingProduct());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDeleted());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getActivate());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDsdDepartment());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getDrugFactPanel());
		Assert.assertNull(candidateProductMaster.getSeasonYear());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getEas());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getIngredient());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getWicable());
		Assert.assertEquals(CandidateUtils.NO, candidateProductMaster.getSelfManufactured());
		Assert.assertEquals(Long.valueOf(44L), candidateProductMaster.getBrand());
		Assert.assertEquals(Long.valueOf(2342323L), candidateProductMaster.getPrimaryUpc());
		Assert.assertEquals(CandidateUtils.YES, candidateProductMaster.getNewData());
		Assert.assertEquals(Long.valueOf(99L), candidateProductMaster.getPssDepartment());
		Assert.assertEquals("DESC LONGER", candidateProductMaster.getScanDescription());
		Assert.assertEquals("99999", candidateProductMaster.getVertexTaxCategory());
	}
}
