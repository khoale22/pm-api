package com.heb.pm.core.service.nutrition;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests NutritionUtils.
 *
 * @author d116773
 * @since 1.4.0
 */
public class NutritionUtilsTest {


	@Test
	public void servingSizeToText_handlesOneTwelfthNoWhole() {
		Assert.assertEquals("1/12", NutritionUtils.servingSizeToText(new BigDecimal("0.08")));
	}

	@Test
	public void servingSizeToText_handlesOneTwelfthWithWhole() {
		Assert.assertEquals("4 1/12", NutritionUtils.servingSizeToText(new BigDecimal("4.08")));
	}

	@Test
	public void servingSizeToText_handlesOneTenthNoWhole() {
		Assert.assertEquals("1/10", NutritionUtils.servingSizeToText(new BigDecimal("0.10")));
	}

	@Test
	public void servingSizeToText_handlesOneTenthSmallerScale() {
		Assert.assertEquals("1/10", NutritionUtils.servingSizeToText(new BigDecimal("0.1")));
	}
	@Test
	public void servingSizeToText_handlesOneTenthWithWhole() {
		Assert.assertEquals("423 1/10", NutritionUtils.servingSizeToText(new BigDecimal("423.10")));
	}

	@Test
	public void servingSizeToText_handlesOneEighthNoWhole() {
		Assert.assertEquals("1/8", NutritionUtils.servingSizeToText(new BigDecimal("0.12")));
	}

	@Test
	public void servingSizeToText_handlesOneEighthWithWhole() {
		Assert.assertEquals("98763 1/8", NutritionUtils.servingSizeToText(new BigDecimal("98763.12")));
	}

	@Test
	public void servingSizeToText_handlesOneSixthNoWhole() {
		Assert.assertEquals("1/6", NutritionUtils.servingSizeToText(new BigDecimal("0.16")));
	}

	@Test
	public void servingSizeToText_handlesOneSixthWithWhole() {
		Assert.assertEquals("65534 1/6", NutritionUtils.servingSizeToText(new BigDecimal("65534.16")));
	}

	@Test
	public void servingSizeToText_handlesOneFifthNoWhole() {
		Assert.assertEquals("1/5", NutritionUtils.servingSizeToText(new BigDecimal("0.20")));
	}

	@Test
	public void servingSizeToText_handlesOneFifthSmallerScale() {
		Assert.assertEquals("1/5", NutritionUtils.servingSizeToText(new BigDecimal("0.2")));
	}

	@Test
	public void servingSizeToText_handlesOneFifthWithWhole() {
		Assert.assertEquals("223423 1/5", NutritionUtils.servingSizeToText(new BigDecimal("223423.20")));
	}

	@Test
	public void servingSizeToText_handlesOneFourthNoWhole() {
		Assert.assertEquals("1/4", NutritionUtils.servingSizeToText(new BigDecimal("0.25")));
	}

	@Test
	public void servingSizeToText_handlesOneFourthWithWhole() {
		Assert.assertEquals("5534 1/4", NutritionUtils.servingSizeToText(new BigDecimal("5534.25")));
	}

	@Test
	public void servingSizeToText_handlesOneThirdNoWhole() {
		Assert.assertEquals("1/3", NutritionUtils.servingSizeToText(new BigDecimal("0.33")));
	}

	@Test
	public void servingSizeToText_handlesOneThirdWithWhole() {
		Assert.assertEquals("65189 1/3", NutritionUtils.servingSizeToText(new BigDecimal("65189.33")));
	}

	@Test
	public void servingSizeToText_handlesOneHalfNoWhole() {
		Assert.assertEquals("1/2", NutritionUtils.servingSizeToText(new BigDecimal("0.50")));
	}

	@Test
	public void servingSizeToText_handlesOneHalfSmallerScale() {
		Assert.assertEquals("1/2", NutritionUtils.servingSizeToText(new BigDecimal("0.5")));
	}

	@Test
	public void servingSizeToText_handlesOneHalfWithWhole() {
		Assert.assertEquals("98565 1/2", NutritionUtils.servingSizeToText(new BigDecimal("98565.50")));
	}

	@Test
	public void servingSizeToText_handlesTwoThirdsNoWhole() {
		Assert.assertEquals("2/3", NutritionUtils.servingSizeToText(new BigDecimal("0.66")));
	}

	@Test
	public void servingSizeToText_handlesTwoThirdsWithWhole() {
		Assert.assertEquals("87542 2/3", NutritionUtils.servingSizeToText(new BigDecimal("87542.66")));
	}

	@Test
	public void servingSizeToText_handlesThreeFourthsNoWhole() {
		Assert.assertEquals("3/4", NutritionUtils.servingSizeToText(new BigDecimal("0.75")));
	}

	@Test
	public void servingSizeToText_handlesThreeFourthsWithWhole() {
		Assert.assertEquals("65411 3/4", NutritionUtils.servingSizeToText(new BigDecimal("65411.75")));
	}

	@Test
	public void servingSizeToText_handlesOddFractionNoWhole() {
		Assert.assertEquals("0.34", NutritionUtils.servingSizeToText(new BigDecimal("0.3422")));
	}

	@Test
	public void servingSizeToText_handlesOddFractionWithWhole() {
		Assert.assertEquals("345.22", NutritionUtils.servingSizeToText(new BigDecimal("345.223")));
	}

	@Test
	public void servingSizeToText_handlesNoFractionalPart() {
		Assert.assertEquals("8742", NutritionUtils.servingSizeToText(new BigDecimal("8742.00")));
	}
}
