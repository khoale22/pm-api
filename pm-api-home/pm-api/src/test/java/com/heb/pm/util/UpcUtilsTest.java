/*
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the UpcUtils class.
 *
 * @author d116773
 * @since 2.6.1
 */
public class UpcUtilsTest {

	/*
	 * isPredigitTwo
	 */

	/**
	 * Tests that a pre-digit 2 returns true.
	 */
	@Test
	public void isPreDigitTwo_validPredigitTwo() {
		Assert.assertTrue(UpcUtils.isPreDigitTwo(20260600000L));
	}

	/**
	 * Tests that something that has a 2 in spot 11 but isn't a pre-digit 2 returns false.
	 */
	@Test
	public void isPreDigitTwo_twoInSpot() {
		Assert.assertFalse(UpcUtils.isPreDigitTwo(320260600000L));
	}

	/**
	 * Tests that something that is not a pre-digit 2 but starts with 2 returns false.
	 */
	@Test
	public void isPreDigitTwo_invalidPredigitTwoStartsWithTwo() {
		Assert.assertFalse(UpcUtils.isPreDigitTwo(202606L));
	}

	/**
	 * Tests that something that is not a pre-digit 2 but starts with 2 and is ten digits returns false.
	 */
	@Test
	public void isPreDigitTwo_invalidPredigitTwoTenDigits() {
		Assert.assertFalse(UpcUtils.isPreDigitTwo(2026060000L));
	}

	/**
	 * Tests that something that is not a pre-digit 2 returns false.
	 */
	@Test
	public void isPreDigitTwo_invalidPredigitTwoRegularUpc() {
		Assert.assertFalse(UpcUtils.isPreDigitTwo(4122074262L));
	}

	/**
	 * Tests that something that is an alt-pack UPC returns false.
	 */
	@Test
	public void isPreDigitTwo_invalidPredigitTwoAltPackUpc() {
		Assert.assertFalse(UpcUtils.isPreDigitTwo(48745612396L));
	}

/*
	 * isPredigitFour
	 */

	/**
	 * Tests that a pre-digit 4 returns true.
	 */
	@Test
	public void isPreDigitFour_validPredigitFour() {
		Assert.assertTrue(UpcUtils.isPreDigitFour(48745612396L));
	}

	/**
	 * Tests that something that has a 4 in spot 11 but is not a pre-digit 4 returns false.
	 */
	@Test
	public void  isPreDigitFour_fourInSpot() {
		Assert.assertFalse(UpcUtils.isPreDigitFour(948745612396L));
	}

	/**
	 * Tests that something that is not a pre-digit 4 but starts with 4 returns false.
	 */
	@Test
	public void isPreDigitFour_invalidPredigitTwoStartsWithFour() {
		Assert.assertFalse(UpcUtils.isPreDigitFour(4011));
	}

	/**
	 * Tests that something that is not a pre-digit 4 but starts with 4 and is ten digits returns false.
	 */
	@Test
	public void isPreDigitFour_invalidPredigitFourTenDigits() {
		Assert.assertFalse(UpcUtils.isPreDigitFour(4122074262L));
	}

	/**
	 * Tests that something that is not a pre-digit 2 returns false.
	 */
	@Test
	public void isPreDigitFour_invalidPredigitFourRegularUpc() {
		Assert.assertFalse(UpcUtils.isPreDigitFour(4122074262L));
	}

	/**
	 * Tests that something that is an pre-digit 2 returns false.
	 */
	@Test
	public void isPreDigitFour_invalidPredigitFourPredigitTwo() {
		Assert.assertFalse(UpcUtils.isPreDigitFour(1410007952L));
	}

	/*
	 * validateCheckDigit
	 */

	/**
	 * Tests validateCheckDigit with a valid check digit.
	 */
	@Test
	public void validateCheckDigit_valid() {
		Assert.assertTrue(UpcUtils.validateCheckDigit(1410007952L, 1));
	}

	/**
	 * Tests validateCheckDigit with an invalid check digit.
	 */
	@Test
	public void validateCheckDigit_invalid() {
		Assert.assertFalse(UpcUtils.validateCheckDigit(1410007952L, 2));
	}

	/*
	 * getG14Upc
	 */

	/**
	 * Checks getG14Upc when the number is less than 13 digits.
	 */
	@Test
	public void getG14Upc_smallerThan11() {
		Assert.assertEquals("0004122074262", UpcUtils.getG14Upc(4122074262L));
	}

	/**
	 * Checks getG12Upc when the number is equal to 13 digits.
	 */
	@Test
	public void getG14Upc_11() {
		Assert.assertEquals("3644122074262", UpcUtils.getG14Upc(3644122074262L));
	}

	/**
	 * Checks getG12Upc when the number is more than than 13 digits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getG12Upc_largerThan11() {
		UpcUtils.getG14Upc(26344122074262L);
	}

	/*
	 * isPlu
	 */

	/**
	 * Checks isPlu when the number is smaller than 5 digits.
	 */
	@Test
	public void isPlu_smallerThan5() {
		Assert.assertTrue(UpcUtils.isPlu(233L));
	}

	/**
	 * Checks isPlu when the number is equal to 5 digits.
	 */
	@Test
	public void isPlu_5() {
		Assert.assertTrue(UpcUtils.isPlu(94011L));
	}

	/**
	 * Checks isPlu when the number is more than 5 digits.
	 */
	@Test
	public void isPlu_moreThan5() {
		Assert.assertFalse(UpcUtils.isPlu(940115L));
	}

	/*
	 * calculateCheckDigit
	 */
	/**
	 * Tests calculateCheckDigit when the number is too big for a G14.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void calculateCheckDigit_tooBig() {
		UpcUtils.calculateCheckDigit(53441220742621L);
	}

	/**
	 * Tests calculateCheckDigit with 1410007952.
 	 */
	@Test
	public void calculateCheckDigit_1410007952() {
		Assert.assertEquals(1, UpcUtils.calculateCheckDigit(1410007952L));
	}

	/**
	 * Tests calculateCheckDigit with 4122074262.
	 */
	@Test
	public void calculateCheckDigit_4122074262() {
		Assert.assertEquals(2, UpcUtils.calculateCheckDigit(4122074262L));
	}

	/**
	 * Tests calculateCheckDigit with 24122074262.
	 */
	@Test
	public void calculateCheckDigit_24122074262() {
		Assert.assertEquals(6, UpcUtils.calculateCheckDigit(24122074262L));
	}

	/**
	 * Tests calculateCheckDigit with 24122074264.
	 */
	@Test
	public void calculateCheckDigit_24122074264() {
		Assert.assertEquals(0, UpcUtils.calculateCheckDigit(24122074264L));
	}

	@Test
	public void pluToPreDigitTwo_handlesFourDigits() {
		Assert.assertEquals(20401100000L, UpcUtils.pluToPreDigitTwo(4011L));
	}

	@Test
	public void pluToPreDigitTwo_handlesFiveDigits() {
		Assert.assertEquals(29401100000L, UpcUtils.pluToPreDigitTwo(94011L));
	}
}
