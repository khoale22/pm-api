package com.heb.pm.core.service.validators;

import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import com.heb.pm.pam.model.Warehouse;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Validates the data supplier by an SCA.
 *
 * @author d116773
 * @since 1.0.0
 */
@Component
/* default */ class ScaDataValidator implements CandidateValidator {

	/**
	 * Validates the data supplier by an SCA.
	 *
	 * @param candidate The candidate to validate.
	 * @return The validated candidate.
	 */
	@Override
	public void validate(Candidate candidate) throws ValidationException {

		List<String> errorMessages = new LinkedList<>();

		this.validateWarehouses(candidate.getCandidateProducts()).ifPresent(errorMessages::add);
		this.validateMaxShipDays(candidate.getCandidateProducts()).ifPresent(errorMessages::add);

		if (!errorMessages.isEmpty()) {
			throw new ValidationException("Unable to validate supply chain data.", errorMessages);
		}
	}

	/**
	 * Validates items are assigned to reasonable warehouses.
	 *
	 * @param candidateProducts The candidate products to validate.
	 * @return  An error message if an error is detected.
	 */
	protected Optional<String> validateWarehouses(List<CandidateProduct> candidateProducts) {
		// Unimplemented until we define the rules of how to set the warehouse.
		return Optional.empty();
	}

	/**
	 * Validates Max Ship for each product if it is present.
	 *
	 * @param candidateProducts The candidate products to validate.
	 * @return An error message if an error is detected.
	 */
	protected Optional<String> validateMaxShipDays(List<CandidateProduct> candidateProducts) {

		for (CandidateProduct p : candidateProducts) {
			for (Warehouse w : p.getWarehouses()) {
				if (Objects.nonNull(w.getMaxShip()) && (w.getMaxShip() < Candidate.MINIMUM_MAX_SHIP_DAYS || w.getMaxShip() > Candidate.MAXIMUM_MAX_SHIP_DAYS)) {
					return Optional.of(String.format("Max Ship must be between %d and %d.", Candidate.MINIMUM_MAX_SHIP_DAYS, Candidate.MAXIMUM_MAX_SHIP_DAYS));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean handles(CandidateValidatorType validatorName) {
		return CandidateValidatorType.SCA_DATA_VALIDATOR.equals(validatorName);
	}
}

