package com.heb.pm.core.service.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Class through which all clients should access all candidate validators.
 *
 * @author a786878
 * @since 1.8.0
 */
@Service
public class CandidateValidatorRegistry {

	@Autowired
	private transient List<CandidateValidator> validators;

	/**
	 * Returns a list of validators to apply for a list of validation types.
	 *
	 * @param validatorTypes The list of types the client is trying to apply.
	 *
	 * @return The list of validators to apply based on the list of types passed in.
	 */
	public List<CandidateValidator> getValidators(List<CandidateValidatorType> validatorTypes) {

		List<CandidateValidator> toReutrn = new LinkedList<>();

		this.validators.stream().filter(v -> handlesAny(v, validatorTypes)).forEach(toReutrn::add);
		return toReutrn;
	}

	/**
	 * Helper function that will check if a validator handles any of a list of validation types.
	 *
	 * @param candidateValidator The validator to check.
	 * @param validatorTypes The list of types to see if the validator handles it.
	 * @return True if the validator handles any of the types and false otherwise.
	 */
	private boolean handlesAny(CandidateValidator candidateValidator, List<CandidateValidatorType> validatorTypes) {

		for (CandidateValidatorType validatorType : validatorTypes) {
			if (candidateValidator.handles(validatorType)) {
				return true;
			}
		}
		return false;
	}
}
