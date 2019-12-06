package com.heb.pm.util.json;

/**
 * Exception thrown by a StreamingOutputWriter when it encounters an error writing to a stream.
 *
 * @author d116773
 * @since 1.5.0
 */
public class StreamingOutputException extends RuntimeException {

	private static final long serialVersionUID = 7716075716889513681L;

	/**
	 * Constructs a new StreamingOutputException.
	 *
	 * @param message The error message.
	 * @param t The root error.
	 */
	/* default */ StreamingOutputException(String message, Throwable t) {
		super(message, t);
	}
}
