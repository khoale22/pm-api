package com.heb.pm.dictionary.endpoint;

import com.heb.pm.dictionary.model.OriginalTextAndValidatedText;
import com.heb.pm.dictionary.service.DictionaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Rest endpoint for looking up dictionary information.
 *
 * @author m314029
 * @since 1.2.0
 */
@RestController
@RequestMapping(DictionaryController.BASE_URL)
public class DictionaryController {

	private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

	// endpoints
	protected static final String BASE_URL = "/dictionary";
	private static final String VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL = "validateText/customerFriendlyDescription";

	// logs
	private static final String LOG_VALIDATE_TEXT_MESSAGE =
			"IP %s has requested dictionary validation of text: %s.";

	@Autowired
	private transient DictionaryService dictionaryService;

	/**
	 * Validate a string based on Vocabulary rules.
	 *
	 * @param textToValidate The text to validate against the vocabulary rules.
	 * @param request The HTTP request that initiated this call.
	 * @return A String with the corrected text (if any).
	 */
	@RequestMapping(method = RequestMethod.POST, value = VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL)
	@ApiOperation("Validate a string based on Vocabulary rules, and returns given string plus validated string.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "An object containing both given text, and validated text",
					response = OriginalTextAndValidatedText.class)})
	public OriginalTextAndValidatedText validateCustomerFriendlyDescription(
			@ApiParam("The text to validate against the dictionary rules.")
			@RequestBody String textToValidate, HttpServletRequest request) {

		this.logValidateText(request.getRemoteAddr(), textToValidate);
		return this.dictionaryService.validateCustomerFriendlyDescription(textToValidate);
	}

	/**
	 * Logs a user's request of validating text against the vocabulary rules.
	 *
	 * @param ip The IP address the request is coming from.
	 * @param textToValidate The text to validate against the vocabulary rules.
	 */
	private void logValidateText(String ip, String textToValidate) {
		DictionaryController.logger.info(
				String.format(
						DictionaryController.LOG_VALIDATE_TEXT_MESSAGE, ip, textToValidate)
		);
	}
}
