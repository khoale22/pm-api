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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests StatusEndpoint.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class StatusEndpointTest {


	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * Validates the application is ready.
	 * @throws Exception
	 */
	@Test
	public void ready_isReady() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/status/ready"));
		resultActions.andExpect(status().isOk());
	}

	/**
	 * Validates that the application is healthy.
	 * @throws Exception
	 */
	@Test
	public void healthy_isHealthy() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/status/healthy"));
		resultActions.andExpect(status().isOk());
	}
}
