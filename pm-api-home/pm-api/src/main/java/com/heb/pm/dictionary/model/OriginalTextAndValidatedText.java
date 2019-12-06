package com.heb.pm.dictionary.model;


/**
 * Custom object to hold the original text and the text after it was validated against the Dictionary rules.
 *
 * @author m314029
 * @since 1.2.0
 */
public class OriginalTextAndValidatedText {

	private String originalText;

	private String validatedText;

	/**
	 * Returns OriginalText.
	 *
	 * @return The OriginalText.
	 **/
	public String getOriginalText() {
		return originalText;
	}

	/**
	 * Sets the OriginalText.
	 *
	 * @param originalText The OriginalText.
	 **/
	public OriginalTextAndValidatedText setOriginalText(String originalText) {
		this.originalText = originalText;
		return this;
	}

	/**
	 * Returns ValidatedText.
	 *
	 * @return The ValidatedText.
	 **/
	public String getValidatedText() {
		return validatedText;
	}

	/**
	 * Sets the ValidatedText.
	 *
	 * @param validatedText The ValidatedText.
	 **/
	public OriginalTextAndValidatedText setValidatedText(String validatedText) {
		this.validatedText = validatedText;
		return this;
	}
}
