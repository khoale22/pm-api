package com.heb.pm.core.endpoint;

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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests CostLinkEndpoint.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class CostLinkEndpointTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getCostLink_404OnMissing() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/costLink/3333"));

		resultActions.andExpect(status().is4xxClientError());
	}

	@Test
	public void getCostLink_handlesGoodCostLink() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/costLink/454354"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.costLinkNumber", is(454354)))
				.andExpect(jsonPath("$.description", is("TEST COST LINK")))
				.andExpect(jsonPath("$.active", is(true)))
				.andExpect(jsonPath("$.pack", is(24)))
				.andExpect(jsonPath("$.vendor.apNumber", is(23221)))
				.andExpect(jsonPath("$.vendor.supplierType", is("AP")))
				.andExpect(jsonPath("$.commodity", is(6730)))
				.andExpect(jsonPath("$.searchedItemCode").doesNotExist())
		;
	}

	@Test
	public void getCostLinkByItem_404OnMissingItem() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/costLink/item/22333"));

		resultActions.andExpect(status().is4xxClientError());
	}

	@Test
	public void getCostLinkByItem_404OnNoCostLink() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/costLink/item/185094"));

		resultActions.andExpect(status().is4xxClientError());
	}


	@Test
	public void getCostLinkByItem_handlesGoodCostLink() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/costLink/item/209486"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.costLinkNumber", is(2524)))
				.andExpect(jsonPath("$.description", is("")))
				.andExpect(jsonPath("$.active", is(false)))
				.andExpect(jsonPath("$.pack", is(24)))
				.andExpect(jsonPath("$.vendor.apNumber", is(23221)))
				.andExpect(jsonPath("$.vendor.supplierType", is("AP")))
				.andExpect(jsonPath("$.commodity", is(6730)))
				.andExpect(jsonPath("$.searchedItemCode", is(209486)))
				.andExpect(jsonPath("$.listCost", is(41.28)))
		;
	}

}
