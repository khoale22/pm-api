package com.heb.pm.core.service.validators;

import com.heb.pm.pam.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Tests VendorDataValidator.
 *
 * @author d116773
 * @since 1.0.0
 */
public class VendorDataValidatorTest {

	private VendorDataValidator vendorDataValidator = new VendorDataValidator();

	@Test
	public void validateCheckDigit_returnsEmtpyNullValues() {

		Optional<String> errorMessage = this.vendorDataValidator.validateCheckDigit(null, null);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateCheckDigit_returnsEmptyGoodValues() {
		Optional<String> errorMessage = this.vendorDataValidator.validateCheckDigit(4122030799L, 9L);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateCheckDigit_returnsErrorBadValues() {
		Optional<String> errorMessage = this.vendorDataValidator.validateCheckDigit(4122030799L, 2L);
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateRetailSize_returnsErrorBadValues() {
		Optional<String> errorMessage = this.vendorDataValidator.validRetailSize(Candidate.of().setRetailSize("1234567"));
		Assert.assertTrue(errorMessage.isPresent());
	}
	@Test
	public void validateRetailSize_returnsEmptyGoodValues() {
		Optional<String> errorMessage = this.vendorDataValidator.validRetailSize(Candidate.of().setRetailSize("123456"));
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateProductHasRequiredFields_testAll() {

		CandidateProduct candidateProduct = CandidateProduct.of();
		Optional<String> errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setCaseUpc(1111L);
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setUpc(11111L);
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setCaseUpcCheckDigit(1L);
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setUpcCheckDigit(1L);
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setDescription("test");
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setVendorProductCode("fpc");
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		candidateProduct.setRomanceCopy("This is romance copy");
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertTrue(errorMessage.isPresent());

		// At this point, it's got all the information we need.
		candidateProduct.setCustomerFriendlyDescription1("test");
		errorMessage = this.vendorDataValidator.validateProductHasRequiredFields(candidateProduct, 0);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateProducts_testAll() {

		List<String> errorMessages = this.vendorDataValidator.validateProducts(null);
		Assert.assertFalse(errorMessages.isEmpty());

		List<CandidateProduct> candidateProducts = new LinkedList<>();
		errorMessages = this.vendorDataValidator.validateProducts(candidateProducts);
		Assert.assertFalse(errorMessages.isEmpty());

		CandidateProduct candidateProduct = CandidateProduct.of();
		candidateProduct.setCaseUpc(4122030799L).setCaseUpcCheckDigit(1L)
				.setUpc(4122030798L).setUpcCheckDigit(1L)
				.setDescription("test").setVendorProductCode("aaa")
				.setCustomerFriendlyDescription1("test desc");
		candidateProducts.add(candidateProduct);
		errorMessages = this.vendorDataValidator.validateProducts(candidateProducts);
		Assert.assertFalse(errorMessages.isEmpty());

		candidateProduct.setCaseUpcCheckDigit(9L);
		errorMessages = this.vendorDataValidator.validateProducts(candidateProducts);
		Assert.assertFalse(errorMessages.isEmpty());

		candidateProduct.setRomanceCopy("This is romance copy");
		errorMessages = this.vendorDataValidator.validateProducts(candidateProducts);
		Assert.assertFalse(errorMessages.isEmpty());

		// Fix UPC check digit error.
		candidateProduct.setUpcCheckDigit(2L);

		// At this point, the products should be good.
		errorMessages = this.vendorDataValidator.validateProducts(candidateProducts);
		Assert.assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void validateDRU_none_specified() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);

		candidate.setDisplayReadyUnit(true);
		List<String> errorMessages = this.vendorDataValidator.validDRU(candidate);

		// [Rows Deep in Retail Units is not specified., Rows Facing in Retail Units is not specified.,
		// Rows High in Retail Units is not specified., DRU Orientation is not specified., DRU type is not specified.]
		Assert.assertEquals(5, errorMessages.size());
	}

	@Test
	public void validateDRU_calc_error() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);
		candidate.setDisplayReadyUnit(true)
				.setDisplayReadyUnitRowsDeep(5L)
				.setDisplayReadyUnitRowsFacing(1L)
				.setDisplayReadyUnitRowsHigh(1L)
				.setDisplayReadyUnitOrientation(VendorDataValidator.ORIENTATION_BY_DEPTH)
				.setDisplayReadyUnitType(VendorDataValidator.DRU_TYPE_DRP);

		List<String> errorMessages = this.vendorDataValidator.validDRU(candidate);

		// [The Rows Facing x Rows Deep x Rows High must be equal to the Inner Pack. Please update the values.]
		Assert.assertEquals(1, errorMessages.size());
	}

	@Test
	public void validateDRU_good() {
		Candidate candidate = Candidate.of();
		populateValidVendorCandidate(candidate);
		candidate.setDisplayReadyUnit(true)
				.setDisplayReadyUnitRowsDeep(1L)
				.setDisplayReadyUnitRowsFacing(1L)
				.setDisplayReadyUnitRowsHigh(1L)
				.setDisplayReadyUnitOrientation(VendorDataValidator.ORIENTATION_BY_DEPTH)
				.setDisplayReadyUnitType(VendorDataValidator.DRU_TYPE_DRP);

		List<String> errorMessages = this.vendorDataValidator.validDRU(candidate);

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
				.setCostOwner(CostOwner.of().setCostOwnerId(1L).setCostOwnerName("Cost Owner 1")
						.setTopToTopId(1L).setTopToTopName("T2T 1"))
				.setTotalVolume(BigDecimal.valueOf(100L))
				.setUnitOfMeasure(UnitOfMeasure.of().setUnitOfMeasureId("1").setDescription("UOM 1"))
				.setRetailSize("1")
				.setPackageType(PackageType.of().setPackageTypeId("1").setDescription("Package Type 1"))
				.setProductLength(BigDecimal.ONE).setProductWidth(BigDecimal.ONE)
				.setProductHeight(BigDecimal.ONE).setProductWeight(BigDecimal.ONE)
				.setMasterPack(1L).setMasterLength(BigDecimal.ONE).setMasterWidth(BigDecimal.ONE)
				.setMasterHeight(BigDecimal.ONE).setMasterWeight(BigDecimal.ONE).setMasterCube(BigDecimal.ONE)
				.setInnerPack(1L).setInnerLength(BigDecimal.ONE).setInnerHeight(BigDecimal.ONE)
				.setInnerWeight(BigDecimal.ONE).setVendorTie(1L).setVendorTier(1L)
				.setInnerListCost(BigDecimal.ONE).setProductType(Candidate.SELLABLE)
				.setInnerWidth(BigDecimal.ONE).setMasterListCost(BigDecimal.valueOf(2.0d));

		candidate.getCandidateProducts().add(CandidateProduct.of().setId("4122030796").setUpc(4122030796L)
				.setUpcCheckDigit(8L).setCaseUpc(4122030799L)
				.setCaseUpcCheckDigit(9L)
				.setDescription("test").setCustomerFriendlyDescription1("test")
				.setCountryOfOrigin(CountryOfOrigin.of().setCountryId(1L).setDescription("COO 1"))
				.setSubBrand(SubBrand.of().setSubBrandId("1").setDescription("Sub Brand 1"))
				.setVendorProductCode("4122030796"));
	}
}
