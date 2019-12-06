/*
 * ListFromStringFormatter
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.util.controller;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.format.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base class that can be used to convert between Strings that hold a list of things separated by a delimiter and
 * actual Java lists of those things. It is intended to be loaded into a Spring context to convert between Strings
 * needed on the front end and real objects needed by Controllers.
 *
 * @author d116773
 * @param <T> The type of data that you want to store in the list.
 * @since 1.4.0
 */
public abstract class ListFromStringFormatter<T> implements Formatter<List<T>> {

	// If no set of delimiters are passed to the constructor, the default set is used:
	// spaces, tabs, carriage returns, commas, and new lines.
	private static final String DEFAULT_INPUT_DELIMITER = "[\\s,]+";

	// The actual delimiter that gets used.
	private final transient String inputDelimiter;

	/**
	 * Constructs a new ListFromStringFormatter.
	 *
	 * @param inputDelimiter The delimiters to use to parse incoming Strings.
	 */
	public ListFromStringFormatter(String inputDelimiter) {
		this.inputDelimiter = inputDelimiter;
	}

	/**
	 * Constructs a new ListFromStringFormatter with the default delimiters.
	 */
	public ListFromStringFormatter() {
		this(ListFromStringFormatter.DEFAULT_INPUT_DELIMITER);
	}

	/**
	 * Parses the String passed in into a list. If any element cannot be converted to T, then an
	 * IllegalArgumentException is thrown.
	 *
	 * @param stringToParse The String to parse.
	 * @param locale Ignored.
	 * @return A list of T parsed from stringToParse.
	 */
	@Override
	public List<T> parse(String stringToParse, Locale locale) {

		String[] stringSplit = stringToParse.split(this.inputDelimiter);

		List<T> returnList = new ArrayList<>();
		for (String value : stringSplit) {
			returnList.add(this.convert(value));
		}

		return returnList;
	}

	/**
	 * Takes a List of T and turns it into a string.
	 *
	 * @param listToPrint The List to turn into a String.
	 * @param locale Ignored.
	 * @return A String representation of the list.
	 */
	@Override
	public String print(List<T> listToPrint, Locale locale) {
		return ArrayUtils.toString(listToPrint);
	}

	/**
	 * Override this to take an individual String and convert it to T. This was needed as Supplier does not
	 * support taking arguments.
	 *
	 * @param parsedValue The String to convert to T.
	 * @return ParsedValue converted to DataType.
	 */
	public abstract T convert(String parsedValue);
}
