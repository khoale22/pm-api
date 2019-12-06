package com.heb.pm.core.service;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests CandidateUtils.
 *
 * @author d116773
 * @since 1.2.0
 */
public class CandidateUtilsTest {

	@Test
	public void convertToItemUom_handlesEach() {

		Assert.assertEquals("EACH", CandidateUtils.convertToItemUom("D "));
	}
}
