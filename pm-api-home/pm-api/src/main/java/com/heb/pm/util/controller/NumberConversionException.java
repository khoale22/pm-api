package com.heb.pm.util.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class to throw when you're expected to be able to convert a string sent from a client appplication in to a number.
 *
 * @author d116773
 * @since 1.4.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
/* default */ class NumberConversionException extends RuntimeException {

	private static final long serialVersionUID = -5508063002791630657L;

	/**
	 * Constructs a new NumberConversionException.
	 *
	 * @param message The message to return to the user.
	 */
	protected NumberConversionException(String message) {
		super(message);
	}
}
