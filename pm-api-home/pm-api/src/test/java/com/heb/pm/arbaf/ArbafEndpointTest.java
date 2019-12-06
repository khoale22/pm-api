package com.heb.pm.arbaf;

import com.heb.pm.TestConfiguration;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests ArbafEndpoint.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ArbafEndpointTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void lookupUser_MissingApplicationReturns404() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/arbaf/badApp/x33445"));

		resultActions.andExpect(status().is4xxClientError());
	}

	@Test
	public void lookupUser_MissingUserReturnsEmptySet() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/arbaf/Product+Management/badUser"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void lookupUser_returnsGoodDataD116773() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/arbaf/Product+Management/d116773"));

		Permission p1 = new Permission("SM_MENU_00", "VIEW");
		Permission p2 = new Permission("VN_LIST_01", "EDIT");

		containsInAnyOrder(p1, p2).matches(jsonPath("$"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)));
	}

	@Test
	public void lookupUser_returnsGoodDataVn94553() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/arbaf/Product+Management/vn94553"));

		Permission p1 = new Permission("SM_MENU_00", "VIEW");
		Permission p2 = new Permission("VN_LIST_01", "EDIT");
		Permission p3 = new Permission("SM_NTRN_01", "EDIT");

		containsInAnyOrder(p1, p2, p3).matches(jsonPath("$"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(8)));
	}

	@Test
	public void lookupUser_returnsGoodDataVn94553NoJobCode() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/arbaf/Product+Management/vn94553?includeJobCode=false"));

		Permission p1 = new Permission("SM_CODE_03", "VIEW");
		Permission p2 = new Permission("SM_CODE_00", "VIEW");

		containsInAnyOrder(p1, p2).matches(jsonPath("$"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}
}
