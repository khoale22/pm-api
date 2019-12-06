package com.heb.pm.core.service.validators;

import com.heb.pm.pam.model.CandidateProduct;
import com.heb.pm.pam.model.Warehouse;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Tests ScaDataValidator.
 *
 * @author d116773
 * @since 1.0.0
 */
public class ScaDataValidatorTest {

	private ScaDataValidator scaDataValidator = new ScaDataValidator();

	@Test
	public void validateMaxShipDays_handlesNullMaxShip() {

		CandidateProduct candidateProduct = CandidateProduct.of();
		List<CandidateProduct> candidateProducts = new LinkedList<>();
		candidateProducts.add(candidateProduct);

		Optional<String> errorMessages = this.scaDataValidator.validateMaxShipDays(candidateProducts);
		Assert.assertFalse(errorMessages.isPresent());
	}

	@Test
	public void validateMaxShipDays_handlesSmallMaxShipDays() {

		List<CandidateProduct> candidateProducts = new LinkedList<>();
		candidateProducts.add(CandidateProduct.of().setWarehouses(new ArrayList<>(Collections.singleton(Warehouse.of().setWarehouseId(1L).setMaxShip(-1)))));

		Optional<String> errorMessages = this.scaDataValidator.validateMaxShipDays(candidateProducts);
		Assert.assertTrue(errorMessages.isPresent());
	}

	@Test
	public void validateMaxShipDays_handlesLargeMaxShipDays() {

		List<CandidateProduct> candidateProducts = new LinkedList<>();
		candidateProducts.add(CandidateProduct.of().setWarehouses(new ArrayList<>(Collections.singleton(Warehouse.of().setWarehouseId(1L).setMaxShip(100000)))));

		Optional<String> errorMessages = this.scaDataValidator.validateMaxShipDays(candidateProducts);
		Assert.assertTrue(errorMessages.isPresent());
	}


	@Test
	public void validateMaxShipDays_handlesValidMaxShipDays() {

		List<CandidateProduct> candidateProducts = new LinkedList<>();
		candidateProducts.add(CandidateProduct.of().setWarehouses(new ArrayList<>(Collections.singleton(Warehouse.of().setWarehouseId(1L).setMaxShip(0)))));
		candidateProducts.add(CandidateProduct.of().setWarehouses(new ArrayList<>(Collections.singleton(Warehouse.of().setWarehouseId(1L).setMaxShip(99999)))));
		candidateProducts.add(CandidateProduct.of().setWarehouses(new ArrayList<>(Collections.singleton(Warehouse.of().setWarehouseId(1L).setMaxShip(268)))));

		Optional<String> errorMessages = this.scaDataValidator.validateMaxShipDays(candidateProducts);
		Assert.assertFalse(errorMessages.isPresent());
	}
}
