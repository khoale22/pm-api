package com.heb.pm.dictionary.endpoint;

import com.heb.pm.TestConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests DictionaryController.
 *
 * @author m314029
 * @since 1.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class DictionaryControllerTest {

	private MockMvc mockMvc;

	private static final String BANNED_WORD = "samarium";
	private static final String VALID_WORD = "tomaSo";
	private static final String VALID_WORD_WITH_CASE = "tomaSo";
	private static final String FIX_WORD = "savour";
	private static final String SUGGESTION_FOR_FIX_WORD_WITH_CASE = "Savor";
	private static final String VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL =
			"/dictionary/validateText/customerFriendlyDescription";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void validateText_removeBannedWord() throws Exception {
		String textToValidate = BANNED_WORD;

		ResultActions resultActions = this.mockMvc.perform(post(VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL)
				.content(textToValidate));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.originalText", is(textToValidate)))
				.andExpect(jsonPath("$.validatedText", is(StringUtils.EMPTY)))
		;
	}

	@Test
	public void validateText_removeBannedWordAndKeepValidWord() throws Exception {
		String textToValidate = this.generateSpaceSeparatedTextFromStringList(BANNED_WORD, VALID_WORD);

		ResultActions resultActions = this.mockMvc.perform(post(VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL)
				.content(textToValidate));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.originalText", is(textToValidate)))
				.andExpect(jsonPath("$.validatedText", is(VALID_WORD_WITH_CASE)))
		;
	}

	@Test
	public void validateText_FixedFixWord() throws Exception {
		String textToValidate = FIX_WORD;

		ResultActions resultActions = this.mockMvc.perform(post(VALIDATE_CUSTOMER_FRIENDLY_DESCRIPTION_URL)
				.content(textToValidate));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.originalText", is(textToValidate)))
				.andExpect(jsonPath("$.validatedText", not(FIX_WORD)))
				.andExpect(jsonPath("$.validatedText", is(SUGGESTION_FOR_FIX_WORD_WITH_CASE)))
		;
	}

	/**
	 * This method takes in a list of one or more strings, and returns one string with a space between each string.
	 *
	 * @param strings List of strings to join.
	 * @return One string that has the given list of strings joined with a space in between each.
	 */
	private String generateSpaceSeparatedTextFromStringList(String... strings) {
		return String.join(StringUtils.SPACE, strings);
	}
}
