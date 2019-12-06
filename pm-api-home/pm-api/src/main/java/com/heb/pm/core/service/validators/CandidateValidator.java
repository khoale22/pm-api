package com.heb.pm.core.service.validators;

import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.pam.model.Candidate;

/**
 * CandidateValidator interface.
 *
 * All validators will implement this interface.
 */
public interface CandidateValidator {

    /**
     * Validate the candidate.
     * @param candidate to validate.
     * @throws ValidationException in case there are errors with validation.
     */
    void validate(Candidate candidate) throws ValidationException;

    /**
     * Returns whether or not a validator handles a particular type of validation.
     *
     * @param validatorType The type of the validation.
     * @return True if this validator handles that type and false otherwise.
     */
    boolean handles(CandidateValidatorType validatorType);
}
