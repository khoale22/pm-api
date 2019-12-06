package com.heb.pm.dictionary.service;

import com.heb.pm.dictionary.entity.CaseCode;
import com.heb.pm.dictionary.entity.Suggestion;
import com.heb.pm.dictionary.entity.Vocabulary;
import com.heb.pm.dictionary.entity.WordCode;
import com.heb.pm.dictionary.model.OriginalTextAndValidatedText;
import com.heb.pm.dictionary.repository.SuggestionRepository;
import com.heb.pm.dictionary.repository.VocabularyRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Contains business logic related to the dictionary.
 *
 * @author m314029
 * @since 1.2.0
 */
@Service
public class DictionaryService {

	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);

	private static final String ANY_SPACE_REGEX = "\\s+";
	private static final String NO_VOWEL_REGEX = "^[^aeiouyAEIOUY]+$";
	private static final int ONE_CHARACTER = 1;
	private static final int FIRST_RECORD_INDEX = 0;

	// log messages
	private static final String NO_SUGGESTIONS_FOUND_FOR_FIX_WORD_CODE_OR_MIX_CASE_CODE = "Dictionary.Vocabulary table" +
			"has %s as %s case code, but Dictionary.Suggestion table has no suggestions.";
	private static final String REMOVING_BANNED_WORD = "Text: %s is banned according to the dictionary rules and is" +
			" being removed from the original text.";
	private static final String MODIFIED_FIXED_WORD = "Original text: %s needs to be fixed according to the dictionary " +
			"rules and is being replaced with: %s.";
	private static final String APPLYING_SPELL_CHECK_RULES = "Applying spell check rules on: %s.";
	private static final String APPLIED_VALIDATION_LOG = "Original text: %s was transformed into: %s.";

	@Autowired
	private transient VocabularyRepository vocabularyRepository;

	@Autowired
	private transient SuggestionRepository suggestionRepository;

	/**
	 * Validate a string based on Vocabulary rules for customer friendly description.
	 *
	 * @param textToValidate The text to validate against the vocabulary rules.
	 * @return A String with the corrected text (if any).
	 */
	public OriginalTextAndValidatedText validateCustomerFriendlyDescription(String textToValidate) {
		// apply spell check rules
		List<String> spellCheckAppliedWords = this.applySpellCheckRules(textToValidate);
		// apply case code rules
		List<String> caseCorrectedText = new ArrayList<>();
		for (String spellCheckAppliedWord : spellCheckAppliedWords) {
			String caseCodeConversionValueForCustomerFriendlyDescription =
					this.getCaseCodeConversionValueForCustomerFriendlyDescription(spellCheckAppliedWord);
			caseCorrectedText.add(caseCodeConversionValueForCustomerFriendlyDescription);
		}
		String validText = String.join(StringUtils.SPACE, caseCorrectedText);
		OriginalTextAndValidatedText toReturn = new OriginalTextAndValidatedText()
				.setOriginalText(textToValidate)
				.setValidatedText(validText);
		logger.info(String.format(APPLIED_VALIDATION_LOG, toReturn.getOriginalText(), toReturn.getValidatedText()));
		return toReturn;
	}

	/**
	 * This method takes in one string, then splits it on 'spaces'. Each of these strings is then looked up in the
	 * vocabulary data for records with 'baned' or 'fix' word codes.
	 * 1. If banned, do not add to return list.
	 * 2. If fix, get suggested word and add suggested word to return list.
	 * 3. Else, add word to return list.
	 * After applying these word codes, return the list of strings as it is now spell checked.
	 *
	 * @param textToValidate Text to run vocabulary rules on.
	 * @return A list of strings after removing banned words and replacing fixed words from original textToValidate.
	 */
	private List<String> applySpellCheckRules(String textToValidate) {
		List<String> toReturn = new ArrayList<>();
		String[] originalTextSplit = textToValidate.split(DictionaryService.ANY_SPACE_REGEX);
		String currentValue;
		for (String text : originalTextSplit) {
			logger.debug(String.format(DictionaryService.APPLYING_SPELL_CHECK_RULES, text));

			// apply ban or fix rule (if applicable) to text
			currentValue = this.applyBanOrFixRules(text);

			if (StringUtils.isNotBlank(currentValue)) {
				// if the current value is not blank, add to return list
				toReturn.add(currentValue);
			}
		}
		return toReturn;
	}

	/**
	 * This method applies ban or fix rules to a given text (if any), and returns the output of those rules:
	 * 1. If word is banned, return empty string.
	 * 2. If word is marked as fix, and a suggested word is found, return the suggested word.
	 * 3. Else return text as is.
	 *
	 * @param word Text to apply any banned or fix rules for.
	 * @return Text after ban or fix applied to word. If banned, returns empty string. If fix and fix for word is
	 * found, return the fix. Else return the text as is.
	 */
	private String applyBanOrFixRules(String word) {

		// find all entries in vocabulary matching current text and is banned or fix
		List<Vocabulary> bannedOrFixedVocabularyRules = this.vocabularyRepository.
				findByKeyWordTextIgnoreCaseAndWordCodeAttributeIn(word,
						Arrays.asList(WordCode.Code.BANNED.getName(), WordCode.Code.FIX.getName()));

		// if no ban or fix rules are found, return text as is
		if (bannedOrFixedVocabularyRules.isEmpty()) {
			return word;
		}

		Vocabulary bannedRule = this.getVocabularyRuleForSpecificWordCode(
				bannedOrFixedVocabularyRules, WordCode.Code.BANNED.getName());

		// if there is a ban record, return null so it will not be returned in valid text
		if (bannedRule != null) {
			logger.info(String.format(DictionaryService.REMOVING_BANNED_WORD, word));
			return StringUtils.EMPTY;
		}

		// get fix rule
		Vocabulary fixedRule = this.getVocabularyRuleForSpecificWordCode(
				bannedOrFixedVocabularyRules, WordCode.Code.FIX.getName());

		// if there is a fix record, set current value = corrected text found in suggestion table
		if (fixedRule != null) {
			String currentValue = this.getCorrectedWordTextForFixWordCodeOrMixedCaseCode(
					word, WordCode.Code.FIX.getName());

			// if the text was replaced, log a message containing both values (otherwise replacement not found)
			if (StringUtils.isNotBlank(currentValue)) {
				logger.info(String.format(DictionaryService.MODIFIED_FIXED_WORD, word, currentValue));
				return currentValue;
			}
		}

		// if there was no fix in the dictionary, just return original word
		return word;

	}

	/**
	 * This method converts a word text into a case versioned word text by precedence for customer friendly description:
	 * 1. If no rule was found, get value to return from 'getNoCaseRuleForText'
	 * 2. If MIXED_CASE_CODE rule exists, return corrected word from suggested table
	 * 3. If UPPER_CASE_CODE rule exists, return word text in all capital letters.
	 * 4. If CAPITALIZE_FIRST_LETTER_CODE rule exists, return word text with first letter capitalized.
	 * 5. Else return word text converted to title case.
	 *
	 * @param textToValidate Text to convert based on highest precedence case code value.
	 * @return Converted text value of given textToValidate.
	 */
	private String getCaseCodeConversionValueForCustomerFriendlyDescription(String textToValidate) {

		// get all rules based on the textToValidate and the case codes used for customer friendly description
		List<Vocabulary> currentVocabularyCaseRules = this.getVocabularyRulesForCaseCodeList(textToValidate,
				Arrays.asList(
						CaseCode.Code.MIXED.getName(),
						CaseCode.Code.UPPER_CASE.getName(),
						CaseCode.Code.CAPITALIZE_FIRST_LETTER.getName(),
						CaseCode.Code.LOWER_CASE_CODE.getName(),
						CaseCode.Code.PREPOSITION.getName()));

		// if there are no rules, call getNoCaseRulForText to get the correct value
		if (currentVocabularyCaseRules.isEmpty()) {
			return this.getNoCaseRuleForText(textToValidate);
		}

		// get the vocabulary record by case code precedence
		// 1. MIXED_CASE_CODE: get the corrected text from getCorrectedWordTextForFixWordCodeOrMixedCaseCode()
		if (this.getVocabularyRuleForSpecificCaseCode(
				currentVocabularyCaseRules, CaseCode.Code.MIXED.getName()) != null) {
			return this.getCorrectedWordTextForFixWordCodeOrMixedCaseCode(
					textToValidate, CaseCode.Code.MIXED.getName());
			// 2. UPPER_CASE_CODE: return text.toUpperCase()
		} else if (this.getVocabularyRuleForSpecificCaseCode(
				currentVocabularyCaseRules, CaseCode.Code.UPPER_CASE.getName()) != null) {
			return textToValidate.toUpperCase(Locale.getDefault());

			// 3. CAPITALIZE_FIRST_LETTER_CODE: return convertToTitleCase()
		} else if (this.getVocabularyRuleForSpecificCaseCode(
				currentVocabularyCaseRules, CaseCode.Code.CAPITALIZE_FIRST_LETTER.getName()) != null) {
			return this.convertToTitleCase(textToValidate);
			// 4. else LOWER_CASE_CODE OR PREPOSITION_CASE_CODE: return convertToTitleCase()
		} else {
			return this.convertToTitleCase(textToValidate);
		}
	}

	/**
	 * Takes a string and converts to title case value (Capitalize first letter, lower case rest).
	 *
	 * @param text Text to convert to title case.
	 * @return Title case converted text value.
	 */
	private String convertToTitleCase(String text) {
		return StringUtils.capitalize(text.toLowerCase(Locale.getDefault()));
	}

	/**
	 * When no case rules are found for a text, return:
	 * 1. if(length of text is greater than one character, and does not contain a vowel or 'y') it is probably an
	 * acronym, so return text.toUpperCase()
	 * 2. else return convertToTitleCase(text)
	 *
	 * @param textWithNoCaseRule The text that did not have any case rules found.
	 * @return textWithNoCaseRule.toUpperCase() if string length > 1 and contains no vowels or 'y'; otherwise return
	 * convertToTitleCase(textWithNoCaseRule)
	 */
	private String getNoCaseRuleForText(String textWithNoCaseRule) {

		// if text length is > 1 AND does not contain a vowel or 'y', it is probably an acronym: return text.toUpperCase
		if (textWithNoCaseRule.length() > DictionaryService.ONE_CHARACTER &&
				this.doesStringNotContainVowel(textWithNoCaseRule)) {
			return textWithNoCaseRule.toUpperCase(Locale.getDefault());

			// else return convertToTitleCase(text)
		} else {
			return this.convertToTitleCase(textWithNoCaseRule);
		}
	}

	/**
	 * This method uses the String method matches to compare a given string to a regex containing any character besides
	 * a vowel. If no vowel is found in the string, return true, otherwise return false.
	 *
	 * @param stringToLookForNoVowel String to match against the no vowel regex.
	 * @return True if no vowel is found, false otherwise.
	 */
	private boolean doesStringNotContainVowel(String stringToLookForNoVowel) {
		return stringToLookForNoVowel.matches(DictionaryService.NO_VOWEL_REGEX);
	}

	/**
	 * This method returns all vocabulary rules matching a given word text, and a list of case codes.
	 *
	 * @param wordText Text to search for.
	 * @param caseCodes List of case codes to search for.
	 * @return List of all vocabulary records matching the search criteria.
	 */
	private List<Vocabulary> getVocabularyRulesForCaseCodeList(String wordText, List<String> caseCodes) {
		return this.vocabularyRepository.findByKeyWordTextIgnoreCaseAndKeyCaseCodeAttributeIn(wordText, caseCodes);
	}

	/**
	 * This method gets the corrected word from the suggestions table when a vocabulary case code is 'mixed' or
	 * case code is 'fix'. If the rule says it needs to be fixed or mixed, but no suggestions are available, log a
	 * message and return the original text. Otherwise return the first suggestion found.
	 *
	 * @param text The text that was searched for.
	 * @param caseOrWordCode The case or word code attached to the vocabulary record.
	 * @return The given text if there is no change, null if the word is banned, and the fixed text if it needs
	 * to change.
	 */
	private String getCorrectedWordTextForFixWordCodeOrMixedCaseCode(String text, String caseOrWordCode) {
		List<Suggestion> suggestions = this.suggestionRepository.findByKeyWordTextIgnoreCase(text);
		if (CollectionUtils.isEmpty(suggestions)) {
			// If the vocabulary rule says it needs to be fixed or mixed, but there are no suggestions, log a message
			// and return the original text
			logger.debug(String.format(
					DictionaryService.NO_SUGGESTIONS_FOUND_FOR_FIX_WORD_CODE_OR_MIX_CASE_CODE, text, caseOrWordCode));
			return text;
		} else {
			// There may be more than one record in the suggestion table for this word text. The reason is that the
			// suggestion table is also used for suggesting a word, and there may be more than one suggestion. In
			// the case of a fix or mix correction, we only want the first occurrence.
			return suggestions.get(FIRST_RECORD_INDEX).getKey().getCorrectedText().trim();
		}
	}

	/**
	 * This method returns the vocabulary value matching the case code specified if it exists; null otherwise.
	 *
	 * @param currentVocabularyRules List of all vocabulary rules matching a given text.
	 * @param caseCode The case code to find.
	 * @return The vocabulary with the given case code if found, null otherwise.
	 */
	private Vocabulary getVocabularyRuleForSpecificCaseCode(List<Vocabulary> currentVocabularyRules, String caseCode) {
		for (Vocabulary vocabulary : currentVocabularyRules) {
			if (vocabulary.getKey().getCaseCodeAttribute().trim().equals(caseCode) && vocabulary.getActive()) {
				return vocabulary;
			}
		}

		// if a rule for the given caseCode was not found, return null
		return null;
	}

	/**
	 * This method returns the vocabulary value matching the word code specified if it exists; null otherwise.
	 *
	 * @param currentVocabularyRules List of all vocabulary rules matching a given text.
	 * @param wordCode The word code to find.
	 * @return The vocabulary with the given word code if found, null otherwise.
	 */
	private Vocabulary getVocabularyRuleForSpecificWordCode(List<Vocabulary> currentVocabularyRules, String wordCode) {
		for (Vocabulary vocabulary : currentVocabularyRules) {
			if (vocabulary.getWordCodeAttribute().trim().equals(wordCode) && vocabulary.getActive()) {
				return vocabulary;
			}
		}

		// if a rule for the given wordCode was not found, return null
		return null;
	}
}
