package com.heb.pm.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

/**
 * Exception to throw when you want to return a 404 from an endpoint.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {


	private static final long serialVersionUID = 6538463666272057346L;

	/**
	 * A Supplier function to construct a new NotFoundException.
	 */
	public static final Supplier<NotFoundException> NOT_FOUND_EXCEPTION_SUPPLIER = () -> new NotFoundException("Not Found");

	/**
	 * Creates a new NotFoundException.
	 *
	 * @param message The error message.
	 */
	public NotFoundException(String message) {
		super(message);
	}
}
