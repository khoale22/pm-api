package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.PhCommodity;
import com.heb.pm.dao.core.entity.PhItemClass;
import com.heb.pm.dao.core.entity.PhSubCommodity;
import com.heb.pm.pam.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Tests CandidateValidator.
 *
 * @author s573181
 * @since 1.0.0
 */
public class CandidateValidatorTest {

	private final CandidateValidator candidateValidator = new CandidateValidator();

	@Test
	public void validateKeyedRetail_handlesNull() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of());
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-052:"));
	}

	@Test
	public void validateKeyedRetail_handlesNullRetail() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailPrice(BigDecimal.ONE));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-052:"));
	}

	@Test
	public void validateKeyedRetail_handlesNullXFor() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(1L));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-052:"));
	}


	@Test
	public void validateKeyedRetail_handlesNegativeRetail() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(1L).setRetailPrice(new BigDecimal("-1.0")));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-002:"));
	}

	@Test
	public void validateKeyedRetail_handlesZeroRetail() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(1L).setRetailPrice(BigDecimal.ZERO));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-002:"));
	}

	@Test
	public void validateKeyedRetail_handlesZeroXFor() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(0L).setRetailPrice(BigDecimal.ONE));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-002:"));
	}

	@Test
	public void validateKeyedRetail_handlesNegativeXFor() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(-1L).setRetailPrice(BigDecimal.ONE));
		Assert.assertTrue(errorMessage.isPresent());
		Assert.assertTrue(errorMessage.get().startsWith("EACT-002:"));
	}

	@Test
	public void validateKeyedRetail_handlesGoodRetail() {

		Optional<String> errorMessage = this.candidateValidator.validateKeyedRetail(Candidate.of().setRetailXFor(1L).setRetailPrice(BigDecimal.ONE));
		Assert.assertFalse(errorMessage.isPresent());
	}


	@Test
	public void validateCubeSize_handlesWillFit() {

		Optional<String> shouldBeEmpty = candidateValidator.validateCubeSize(new BigDecimal("99.99"), new BigDecimal("100.00"), "TT");
		Assert.assertTrue(shouldBeEmpty.isEmpty());
	}

	@Test
	public void validateCubeSize_handlesExactSameScale() {

		Optional<String> shouldBeEmpty = candidateValidator.validateCubeSize(new BigDecimal("99.99"), new BigDecimal("99.99"), "TT");
		Assert.assertTrue(shouldBeEmpty.isEmpty());
	}

	@Test
	public void validateCubeSize_handlesExactDifferentScale() {

		Optional<String> shouldBeEmpty = candidateValidator.validateCubeSize(new BigDecimal("99.99"), new BigDecimal("99.990"), "TT");
		Assert.assertTrue(shouldBeEmpty.isEmpty());
	}

	@Test
	public void validateCubeSize_handlesError() {

		Optional<String> shouldHaveValue = candidateValidator.validateCubeSize(new BigDecimal("99.99"), new BigDecimal("9.99"), "TT");
		Assert.assertTrue(shouldHaveValue.isPresent());
	}

	@Test
	public void validateRetailSize_noErrors() {
		Optional<String> optional = candidateValidator.validateRetailSize(Candidate.of().setRetailSize("123456"));
		Assert.assertTrue(optional.isEmpty());
	}

	@Test
	public void validateRetailSize_hasErrors() {
		Optional<String> optional2 = candidateValidator.validateRetailSize(Candidate.of().setRetailSize("1234567"));
		Assert.assertTrue(optional2.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsEmptyGoodValuesNonItemWeightDept() {
		Optional<String> errorMessage = candidateValidator.validateItemWeightType(Candidate.of().
						setCommodity(Commodity.of().setDepartmentId("12")),
				new PhSubCommodity().setCommodity(new PhCommodity().setItemClass(new PhItemClass().setDepartmentId(12L))));
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsEmptyGoodValuesItemWeightDept() {
		Optional<String> errorMessage = candidateValidator.validateItemWeightType(Candidate.of().
						setCommodity(Commodity.of().setDepartmentId("02")).
						setItemWeightType(CandidateUtils.NONE_ITEM_WEIGHT_TYPE),
				new PhSubCommodity().setCommodity(new PhCommodity().setItemClass(new PhItemClass().setDepartmentId(2L))));
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValuesCommodityDepartmentMismatch() {

		Optional<String> errorMessage = candidateValidator.validateItemWeightType(Candidate.of().
						setCommodity(Commodity.of().setDepartmentId("01")).
						setItemWeightType(CandidateUtils.NONE_ITEM_WEIGHT_TYPE),
				new PhSubCommodity().setCommodity(new PhCommodity().setItemClass(new PhItemClass().setDepartmentId(2L))));
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValuesInvalidItemType() {
		Optional<String> errorMessage = candidateValidator.validateItemWeightType(Candidate.of().
						setCommodity(Commodity.of().setDepartmentId("01")).
						setItemWeightType("Bad Weight Type"),
				new PhSubCommodity().setCommodity(new PhCommodity().setItemClass(new PhItemClass().setDepartmentId(2L))));
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValueNoItemType() {
		Optional<String> errorMessage = candidateValidator.validateItemWeightType(Candidate.of().
						setCommodity(Commodity.of().setDepartmentId("01")),
				new PhSubCommodity().setCommodity(new PhCommodity().setItemClass(new PhItemClass().setDepartmentId(2L))));
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateDRU_none_specified() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);

		candidate.setDisplayReadyUnit(true);
		List<String> errorMessages = candidateValidator.validDRU(candidate);

		// [Rows Deep in Retail Units is not specified., Rows Facing in Retail Units is not specified.,
		// Rows High in Retail Units is not specified., DRU Orientation is not specified., DRU type is not specified.]
		Assert.assertTrue(errorMessages.size() == 5);
	}

	@Test
	public void validateDRU_calc_error() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);
		candidate
				.setDisplayReadyUnit(true)
				.setDisplayReadyUnitRowsDeep(5L)
				.setDisplayReadyUnitRowsFacing(1L)
				.setDisplayReadyUnitRowsHigh(1L)
				.setDisplayReadyUnitOrientation(CandidateDRUValidator.ORIENTATION_BY_DEPTH)
				.setDisplayReadyUnitType(CandidateDRUValidator.DRU_TYPE_DRP);

		List<String> errorMessages = candidateValidator.validDRU(candidate);

		// [The Rows Facing x Rows Deep x Rows High must be equal to the Inner Pack. Please update the values.]
		Assert.assertTrue(errorMessages.size() == 1);
	}

	@Test
	public void validateDRU_good() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);
		candidate
				.setDisplayReadyUnit(true)
				.setDisplayReadyUnitRowsDeep(1L)
				.setDisplayReadyUnitRowsFacing(1L)
				.setDisplayReadyUnitRowsHigh(1L)
				.setDisplayReadyUnitOrientation(CandidateDRUValidator.ORIENTATION_BY_DEPTH)
				.setDisplayReadyUnitType(CandidateDRUValidator.DRU_TYPE_DRP);

		List<String> errorMessages = candidateValidator.validDRU(candidate);

		Assert.assertTrue(errorMessages.isEmpty());
	}

	/**
	 * populateValidVendorCandidate.
	 * @param candidate to populate.
	 */
	private void populateValidVendorCandidate(Candidate candidate) {
		candidate.setLastUpdatedTime(Instant.now());

		candidate.setVendor(Vendor.of().setApNumber(1L).setDescription("vendor 1"))
				.setBuyer(Buyer.of().setBuyerId("AA").setBuyerName("Buyer 1"))
				.setBrand(Brand.of().setBrandId(1L).setDescription("Brand 1"))
				.setCostOwner(CostOwner.of().setCostOwnerId(1L).setCostOwnerName("Cost Owner 1").setTopToTopId(1L).setTopToTopName("T2T 1"))
				.setTotalVolume(new BigDecimal("100.0"))
				.setUnitOfMeasure(UnitOfMeasure.of().setUnitOfMeasureId("1").setDescription("UOM 1"))
				.setRetailSize("1")
				.setPackageType(PackageType.of().setPackageTypeId("1").setDescription("Package Type 1"))
				.setProductLength(new BigDecimal("1.0")).setProductWidth(new BigDecimal("1.0"))
				.setProductHeight(new BigDecimal("1.0")).setProductWeight(new BigDecimal("1.0"))
				.setMasterPack(1L).setMasterLength(new BigDecimal("1.0")).setMasterWidth(new BigDecimal("1.0"))
				.setMasterHeight(new BigDecimal("1.0")).setMasterWeight(new BigDecimal("1.0")).setMasterCube(new BigDecimal("1.0"))
				.setInnerPack(1L).setInnerLength(new BigDecimal("1.0")).setInnerHeight(new BigDecimal("1.0"))
				.setInnerWeight(new BigDecimal("1.0")).setVendorTie(1L).setVendorTier(1L)
				.setInnerListCost(new BigDecimal("1.00")).setProductType(Candidate.SELLABLE)
				.setInnerWidth(new BigDecimal("1.0")).setMasterListCost(new BigDecimal("2.00"));

		candidate.getCandidateProducts().add(CandidateProduct.of().setId("4122030796").setUpc(4122030796L)
				.setUpcCheckDigit(8L).setCaseUpc(4122030799L)
				.setCaseUpcCheckDigit(9L)
				.setDescription("test").setCustomerFriendlyDescription1("test")
				.setCountryOfOrigin(CountryOfOrigin.of().setCountryId(1L).setDescription("COO 1"))
				.setSubBrand(SubBrand.of().setSubBrandId("1").setDescription("Sub Brand 1"))
				.setVendorProductCode("4122030796"));
	}
}
