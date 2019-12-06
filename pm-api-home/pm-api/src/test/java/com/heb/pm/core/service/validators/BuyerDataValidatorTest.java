package com.heb.pm.core.service.validators;

import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.Commodity;
import com.heb.pm.pam.model.RetailLink;
import com.heb.pm.pam.model.SubCommodity;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Tests BuyerDataValidator.
 *
 * @author d116773
 * @since 1.0.0
 */
public class BuyerDataValidatorTest {

	private BuyerDataValidator buyerDataValidator = new BuyerDataValidator();

	@Test
	public void validateRetailFields_returnsErrorNonSellableWrongRetail() {

		Candidate c = Candidate.of();
		c.setProductType(Candidate.NON_SELLABLE).setRetailXFor(2L)
				.setRetailPrice(BigDecimal.ZERO);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailXFor(1L).setRetailPrice(BigDecimal.ONE);
		errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_goodResultNonSellableCorrectRetail() {

		Candidate c = Candidate.of();
		c.setProductType(Candidate.NON_SELLABLE).setRetailXFor(1L)
				.setRetailPrice(BigDecimal.ZERO);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertFalse(errorMessage.isPresent());
	}


	@Test
	public void validateRetailFields_returnsErrorPriceRequiredWrongRetail() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.PRICE_REQUIRED)
				.setRetailXFor(2L).setRetailPrice(BigDecimal.ZERO);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailXFor(1L).setRetailPrice(BigDecimal.ONE);
		errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_goodResultPriceRequiredCorrectRetail() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.PRICE_REQUIRED)
				.setRetailXFor(1L).setRetailPrice(BigDecimal.ZERO);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_returnsErrorKeyRetailWrongRetail() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.KEY_RETAIL)
				.setRetailXFor(0L).setRetailPrice(BigDecimal.ONE);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailXFor(1L).setRetailPrice(BigDecimal.ZERO);
		errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_goodResultKeyRetailCorrectRetail() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.KEY_RETAIL)
				.setRetailXFor(1L).setRetailPrice(BigDecimal.ONE);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_returnsErrorRetailLinkNoLink() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.RETAIL_LINK);
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailLink(RetailLink.of());
		errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateRetailFields_goodResultRetailLinkHasLink() {
		Candidate c = Candidate.of();
		c.setProductType(Candidate.SELLABLE).setRetailType(Candidate.RETAIL_LINK)
				.setRetailLink(RetailLink.of().setRetailLinkNumber(1L));
		Optional<String> errorMessage = this.buyerDataValidator.validateRetailFields(c);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateRequiredBuyerFields_testAll() {

		Candidate c = Candidate.of();

		Optional<String> errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setCommodity(Commodity.of());
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setSubCommodity(SubCommodity.of());
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailType(Candidate.SELLABLE);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailXFor(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setRetailPrice(BigDecimal.ZERO);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setCodeDate(Boolean.TRUE);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setInboundSpecDays(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setWarehouseReactionDays(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.setGuaranteeToStoreDays(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.getCommodity().setCommodityId(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		c.getCommodity().setCommodityId(null);
		c.getSubCommodity().setSubCommodityId(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertTrue(errorMessage.isPresent());

		// The once the commodity is set, this candidate is now valid.
		c.getCommodity().setCommodityId(1L);
		errorMessage = this.buyerDataValidator.validateRequiredBuyerFields(c);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateCodeDays_validCodeDateNotSet() {

		Candidate c = Candidate.of();
		Optional<String> errorMessage = this.buyerDataValidator.validateCodeDateDays(c);
		Assert.assertFalse(errorMessage.isPresent());

		c.setCodeDate(Boolean.FALSE);
		errorMessage = this.buyerDataValidator.validateCodeDateDays(c);
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsEmptyGoodValuesNonItemWeightDept() {
		Optional<String> errorMessage = this.buyerDataValidator.validateItemWeightType(Candidate.of().
				setCommodity(Commodity.of().setDepartmentId("03")));
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsEmptyGoodValuesItemWeightDept() {
		Optional<String> errorMessage = this.buyerDataValidator.validateItemWeightType(Candidate.of().
				setCommodity(Commodity.of().setDepartmentId("02")).setItemWeightType("None"));
		Assert.assertFalse(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValuesNoCommodity() {
		Optional<String> errorMessage = this.buyerDataValidator.validateItemWeightType(Candidate.of());
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValuesNoItemType() {
		Optional<String> errorMessage = this.buyerDataValidator.validateItemWeightType(Candidate.of().
				setCommodity(Commodity.of().setDepartmentId("02")));
		Assert.assertTrue(errorMessage.isPresent());
	}

	@Test
	public void validateItemWeightType_returnsErrorBadValueBadItemType() {
		Optional<String> errorMessage = this.buyerDataValidator.validateItemWeightType(Candidate.of().
				setCommodity(Commodity.of().setDepartmentId("02")).
				setItemWeightType("BAD TYPE"));
		Assert.assertTrue(errorMessage.isPresent());
	}

	// TODO: Code Date testing.

}
