package com.heb.pm.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to throw when a request from the user is malformed.
 *
 * @author d116773
 * @since 1.8.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

	public static final long serialVersionUID = 1L;

	/**
	 * Creates a new InvalidRequestException.
	 *
	 * @param message The error message.
	 */
	public InvalidRequestException(String message) {
		super(message);
	}
}
