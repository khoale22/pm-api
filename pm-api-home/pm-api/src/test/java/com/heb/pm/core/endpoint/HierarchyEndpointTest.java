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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests the HierarchyEndpoint class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class HierarchyEndpointTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void get_filtersDepartment() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/hierarchy?level=DEPARTMENT"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)))
				.andExpect(jsonPath("$[0].departmentId", is("01")))
				.andExpect(jsonPath("$[0].description", is("TOTAL STORE")))
				.andExpect(jsonPath("$[0].subDepartments").doesNotExist())
				.andExpect(jsonPath("$[17].departmentId", is("21")))
				.andExpect(jsonPath("$[17].description", is("OPTICAL")))
		;
	}

	@Test
	public void get_filtersSubDepartment() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/hierarchy?level=SUB_DEPARTMENT"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)))
				.andExpect(jsonPath("$[0].departmentId", is("01")))
				.andExpect(jsonPath("$[0].description", is("TOTAL STORE")))
				.andExpect(jsonPath("$[17].departmentId", is("21")))
				.andExpect(jsonPath("$[17].description", is("OPTICAL")))
				.andExpect(jsonPath("$[1].departmentId", is("02")))
				.andExpect(jsonPath("$[1].description", is("MARKET")))
				.andExpect(jsonPath("$[1].subDepartments", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[0].subDepartmentId", is("021")))
				.andExpect(jsonPath("$[1].subDepartments[0].description", is("DEPARTMENT TWO 1")))
				.andExpect(jsonPath("$[1].subDepartments[0].classes").doesNotExist())
				.andExpect(jsonPath("$[1].subDepartments[4].subDepartmentId", is("02M")))
				.andExpect(jsonPath("$[1].subDepartments[4].description", is("FRESH MEAT - 2M")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes").doesNotExist())
		;
	}

	@Test
	public void get_filtersClass() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/hierarchy?level=CLASS"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)))
				.andExpect(jsonPath("$[0].departmentId", is("01")))
				.andExpect(jsonPath("$[0].description", is("TOTAL STORE")))
				.andExpect(jsonPath("$[17].departmentId", is("21")))
				.andExpect(jsonPath("$[17].description", is("OPTICAL")))
				.andExpect(jsonPath("$[1].departmentId", is("02")))
				.andExpect(jsonPath("$[1].description", is("MARKET")))
				.andExpect(jsonPath("$[1].subDepartments", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[0].subDepartmentId", is("021")))
				.andExpect(jsonPath("$[1].subDepartments[0].description", is("DEPARTMENT TWO 1")))
				.andExpect(jsonPath("$[1].subDepartments[4].subDepartmentId", is("02M")))
				.andExpect(jsonPath("$[1].subDepartments[4].description", is("FRESH MEAT - 2M")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].itemClassId", is(2)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].description", is("MEAT MERCHANDISE")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities").doesNotExist())
				.andExpect(jsonPath("$[1].subDepartments[4].classes[5].itemClassId", is(56)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[5].description", is("MARKET WRAP")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[5].commodities").doesNotExist())
		;
	}

	@Test
	public void get_filtersCommodity() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/hierarchy?level=COMMODITY"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)))
				.andExpect(jsonPath("$[0].departmentId", is("01")))
				.andExpect(jsonPath("$[0].description", is("TOTAL STORE")))
				.andExpect(jsonPath("$[17].departmentId", is("21")))
				.andExpect(jsonPath("$[17].description", is("OPTICAL")))
				.andExpect(jsonPath("$[1].departmentId", is("02")))
				.andExpect(jsonPath("$[1].description", is("MARKET")))
				.andExpect(jsonPath("$[1].subDepartments", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[0].subDepartmentId", is("021")))
				.andExpect(jsonPath("$[1].subDepartments[0].description", is("DEPARTMENT TWO 1")))
				.andExpect(jsonPath("$[1].subDepartments[4].subDepartmentId", is("02M")))
				.andExpect(jsonPath("$[1].subDepartments[4].description", is("FRESH MEAT - 2M")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].itemClassId", is(2)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].description", is("MEAT MERCHANDISE")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities", hasSize(30)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].commodityId", is(55)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].description", is("CASE READY K-BOBS")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.bdmId", is("20")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.firstName", is("KURT")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.lastName", is("MAYER")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.fullName", is("KURT MAYER")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.onePassId", is("M260739")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities").doesNotExist())

				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].itemClassId", is(17)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].description", is("BAKED GOODS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities", hasSize(8)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].commodityId", is(7439)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].description", is("MUFFINS, RTE")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.bdmId", is("36")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.firstName", is("RANIA")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.lastName", is("DANIELS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.fullName", is("RANIA DANIELS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.onePassId", is("D140740")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities").doesNotExist())
		;
	}

	@Test
	public void get_noFilter() throws Exception {
		ResultActions resultActions = this.mockMvc.perform(get("/hierarchy"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(18)))
				.andExpect(jsonPath("$[0].departmentId", is("01")))
				.andExpect(jsonPath("$[0].description", is("TOTAL STORE")))
				.andExpect(jsonPath("$[17].departmentId", is("21")))
				.andExpect(jsonPath("$[17].description", is("OPTICAL")))
				.andExpect(jsonPath("$[1].departmentId", is("02")))
				.andExpect(jsonPath("$[1].description", is("MARKET")))
				.andExpect(jsonPath("$[1].subDepartments", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[0].subDepartmentId", is("021")))
				.andExpect(jsonPath("$[1].subDepartments[0].description", is("DEPARTMENT TWO 1")))
				.andExpect(jsonPath("$[1].subDepartments[4].subDepartmentId", is("02M")))
				.andExpect(jsonPath("$[1].subDepartments[4].description", is("FRESH MEAT - 2M")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes", hasSize(6)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].itemClassId", is(2)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].description", is("MEAT MERCHANDISE")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities", hasSize(30)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].commodityId", is(55)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].description", is("CASE READY K-BOBS")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.bdmId", is("20")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.firstName", is("KURT")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.lastName", is("MAYER")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.fullName", is("KURT MAYER")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].bdm.onePassId", is("M260739")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities", hasSize(2)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities[0].subCommodityId", is(733)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities[0].description", is("CR BEEF-K-BOBS")))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities[0].category.categoryId", is(718)))
				.andExpect(jsonPath("$[1].subDepartments[4].classes[0].commodities[3].subCommodities[0].category.description", is("SELF MANUFACTURED BEEF")))

				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].itemClassId", is(17)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].description", is("BAKED GOODS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities", hasSize(8)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].commodityId", is(7439)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].description", is("MUFFINS, RTE")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.bdmId", is("36")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.firstName", is("RANIA")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.lastName", is("DANIELS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.fullName", is("RANIA DANIELS")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].bdm.onePassId", is("D140740")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities", hasSize(1)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities[0].subCommodityId", is(8975)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities[0].description", is("REDUCED FAT MUFFINS RTE")))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities[0].category.categoryId", is(26)))
				.andExpect(jsonPath("$[2].subDepartments[0].classes[1].commodities[6].subCommodities[0].category.description", is("BAKERY MUFFINS")))



		;
	}
}
