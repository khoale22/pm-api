package com.heb.pm.util.controller;

/**
 * Converts a String to a List of Longs.
 *
 * @author s573181
 * @since 1.4.0
 */
public class LongListFromStringFormatter extends ListFromStringFormatter<Long> {

	/**
	 * Creates a Long from a String.
	 *
	 * @param parsedValue The String to convert to a Long.
	 * @return ParsedValue converted to a Long.
	 */
	@Override
	public Long convert(String parsedValue) {
		try {
			return Long.valueOf(parsedValue);
		} catch (NumberFormatException e) {
			throw new NumberConversionException(String.format("Cannot convert \"%s\" to the required type.", parsedValue));
		}
	}
}
