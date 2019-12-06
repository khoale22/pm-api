package com.heb.pm.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Objects;

/**
 * Represents an error validating a candidate for activation.
 *
 * @author d116773
 * @since 1.1.0
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final List<String> errors;

	/**
	 * Constructs a new ValidationException.
	 *
	 * @param message The message to send back.
	 * @param errors The detailed list of errors in the candidate.
	 */
	public ValidationException(String message, List<String> errors) {

		super(message);
		if (Objects.isNull(errors)) {
			this.errors = List.of();
		} else {
			this.errors = errors;
		}
	}

	/**
	 * Return the detailed list of errors.
	 *
	 * @return The detailed list of errors.
	 */
	public List<String> getErrors() {
		return errors;
	}
}
