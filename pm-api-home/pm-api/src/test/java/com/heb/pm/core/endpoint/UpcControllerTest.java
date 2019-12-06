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
 * Tests UpcController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class UpcControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void findUpcInfoByUpc_hcfCorn() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/upc/4122074262"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.scanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.searchedUpc", is(4122074262L)))
				.andExpect(jsonPath("$.status", is("ACTIVE")))
				.andExpect(jsonPath("$.retailLink", is(8574865)))
				.andExpect(jsonPath("$.size", is("15.25Z")))
				.andExpect(jsonPath("$.product.productId", is(127127)))
				.andExpect(jsonPath("$.product.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.product.primaryScanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.item.itemCode", is(207877)))
				.andExpect(jsonPath("$.item.description", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.item.orderingUpc", is(4122074262L)));
	}
}
