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

import static org.hamcrest.Matchers.*;
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
public class ProductControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void findProductById_hcfCornNoFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/127127"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(127127)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.ebm", is("Callahan,Cristina [c591918]")))

				.andExpect(jsonPath("$.items", hasSize(2)))

				.andExpect(jsonPath("$.items[0].itemCode", is(207877)))
				.andExpect(jsonPath("$.items[0].description", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[0].commodity", is(15)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(6662)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(false)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(4122074262L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(127127)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(12)))

				.andExpect(jsonPath("$.items[1].itemCode", is(742627)))
				.andExpect(jsonPath("$.items[1].description", is("HCF WKC 24PK")))
				.andExpect(jsonPath("$.items[1].commodity", is(15)))
				.andExpect(jsonPath("$.items[1].subCommodity", is(6662)))
				.andExpect(jsonPath("$.items[1].mrt", is(false)))
				.andExpect(jsonPath("$.items[1].altPack", is(true)))
				.andExpect(jsonPath("$.items[1].orderingUpc", is(40000153513L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.scanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productId", is(127127)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[1].containedUpc.pack", is(24)))
				;
	}

	@Test
	public void findProductById_hcfSupplyChainFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/127127?filters=SUPPLY-CHAIN"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(127127)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.productDescription", is("HCF WHOLE KERNEL CORN")))

				.andExpect(jsonPath("$.ebm").doesNotExist())

				.andExpect(jsonPath("$.items", hasSize(2)))

				.andExpect(jsonPath("$.items[0].itemCode", is(207877)))
				.andExpect(jsonPath("$.items[0].description", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[0].commodity", is(15)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(6662)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(false)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(4122074262L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(127127)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(12)))

				.andExpect(jsonPath("$.items[1].itemCode", is(742627)))
				.andExpect(jsonPath("$.items[1].description", is("HCF WKC 24PK")))
				.andExpect(jsonPath("$.items[1].commodity", is(15)))
				.andExpect(jsonPath("$.items[1].subCommodity", is(6662)))
				.andExpect(jsonPath("$.items[1].mrt", is(false)))
				.andExpect(jsonPath("$.items[1].altPack", is(true)))
				.andExpect(jsonPath("$.items[1].orderingUpc", is(40000153513L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.scanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productId", is(127127)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.items[1].containedUpc.pack", is(24)))
		;
	}

	@Test
	public void findProductById_hcfSimpleFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/127127?filters=NONE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(127127)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.productDescription", is("HCF WHOLE KERNEL CORN")))

				.andExpect(jsonPath("$.items").doesNotExist())
				.andExpect(jsonPath("$.ebm").doesNotExist())

		;
	}

	@Test
	public void findProductById_hcfEcommerceFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/127127?filters=ECOMMERCE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(127127)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4122074262L)))
				.andExpect(jsonPath("$.productDescription", is("HCF WHOLE KERNEL CORN")))
				.andExpect(jsonPath("$.ebm", is("Callahan,Cristina [c591918]")))

				.andExpect(jsonPath("$.items").doesNotExist())
		;
	}

	@Test
	public void findProductById_fakeMrtNoFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/98463"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(98463)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.ebm", is("Callahan,Cristina [c591918]")))

				.andExpect(jsonPath("$.items", hasSize(3)))

				.andExpect(jsonPath("$.items[0].itemCode", is(11250)))
				.andExpect(jsonPath("$.items[0].description", is("NESTLE LA LECHERA 1/4 PLT OTB")))
				.andExpect(jsonPath("$.items[0].commodity", is(6730)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(true)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(40000141074L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(528)))

				.andExpect(jsonPath("$.items[1].itemCode", is(185094)))
				.andExpect(jsonPath("$.items[1].description", is("LA LECHERA HALF PALLET 1152PCS")))
				.andExpect(jsonPath("$.items[1].commodity", is(6730)))
				.andExpect(jsonPath("$.items[1].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[1].mrt", is(false)))
				.andExpect(jsonPath("$.items[1].altPack", is(true)))
				.andExpect(jsonPath("$.items[1].orderingUpc", is(2800081670L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[1].containedUpc.pack", is(1152)))

				.andExpect(jsonPath("$.items[2].itemCode", is(209486)))
				.andExpect(jsonPath("$.items[2].description", is("NESTLE LA LECHERA-CNNED MILK-C")))
				.andExpect(jsonPath("$.items[2].commodity", is(6730)))
				.andExpect(jsonPath("$.items[2].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[2].mrt", is(false)))
				.andExpect(jsonPath("$.items[2].altPack", is(false)))
				.andExpect(jsonPath("$.items[2].orderingUpc", is(2800051780L)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[2].containedUpc.pack", is(24)))

		;
	}

	@Test
	public void findProductById_fakeMrtSupplyChainFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/98463?filters=SUPPLY-CHAIN"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(98463)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))

				.andExpect(jsonPath("$.ebm").doesNotExist())

				.andExpect(jsonPath("$.items", hasSize(3)))

				.andExpect(jsonPath("$.items[0].itemCode", is(11250)))
				.andExpect(jsonPath("$.items[0].description", is("NESTLE LA LECHERA 1/4 PLT OTB")))
				.andExpect(jsonPath("$.items[0].commodity", is(6730)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(true)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(40000141074L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(528)))

				.andExpect(jsonPath("$.items[1].itemCode", is(185094)))
				.andExpect(jsonPath("$.items[1].description", is("LA LECHERA HALF PALLET 1152PCS")))
				.andExpect(jsonPath("$.items[1].commodity", is(6730)))
				.andExpect(jsonPath("$.items[1].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[1].mrt", is(false)))
				.andExpect(jsonPath("$.items[1].altPack", is(true)))
				.andExpect(jsonPath("$.items[1].orderingUpc", is(2800081670L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[1].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[1].containedUpc.pack", is(1152)))

				.andExpect(jsonPath("$.items[2].itemCode", is(209486)))
				.andExpect(jsonPath("$.items[2].description", is("NESTLE LA LECHERA-CNNED MILK-C")))
				.andExpect(jsonPath("$.items[2].commodity", is(6730)))
				.andExpect(jsonPath("$.items[2].subCommodity", is(6820)))
				.andExpect(jsonPath("$.items[2].mrt", is(false)))
				.andExpect(jsonPath("$.items[2].altPack", is(false)))
				.andExpect(jsonPath("$.items[2].orderingUpc", is(2800051780L)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.scanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.product.productId", is(98463)))
				.andExpect(jsonPath("$.items[2].containedUpc.upc.product.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.items[2].containedUpc.pack", is(24)))

		;
	}

	@Test
	public void findProductById_fakeMrtSimpleFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/98463?filters=NONE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(98463)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))

				.andExpect(jsonPath("$.items").doesNotExist())
				.andExpect(jsonPath("$.ebm").doesNotExist())
		;
	}

	@Test
	public void findProductById_fakeEcommerceFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/98463?filters=ECOMMERCE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(98463)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(2800051780L)))
				.andExpect(jsonPath("$.productDescription", is("NESTLE LA LECHERA-CANNED MILK")))
				.andExpect(jsonPath("$.ebm", is("Callahan,Cristina [c591918]")))

				.andExpect(jsonPath("$.items").doesNotExist())
		;
	}

	@Test
	public void findProductById_bananasEcommerceFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/377497?filters=ECOMMERCE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(377497)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4011)))
				.andExpect(jsonPath("$.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.ebm", is("Hooks,Brandon [h361956]")))

				.andExpect(jsonPath("$.items").doesNotExist())
		;
	}

	@Test
	public void findProductById_bananasNoFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/377497"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(377497)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4011)))
				.andExpect(jsonPath("$.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.ebm", is("Hooks,Brandon [h361956]")))

				.andExpect(jsonPath("$.items", hasSize(11)))

				.andExpect(jsonPath("$.items[0].itemCode", is(56792)))
				.andExpect(jsonPath("$.items[0].description", is("PREM YELLOW BANANAS TEST")))
				.andExpect(jsonPath("$.items[0].commodity", is(7363)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(8528)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(false)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(40056792000L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(40056792000L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(377497)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(38)))

				// This one has associates, we don't need to check them all, as the above is doing that.
				.andExpect(jsonPath("$.items[6].itemCode", is(428557)))
				.andExpect(jsonPath("$.items[6].description", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[6].commodity", is(7363)))
				.andExpect(jsonPath("$.items[6].subCommodity", is(8528)))
				.andExpect(jsonPath("$.items[6].mrt", is(false)))
				.andExpect(jsonPath("$.items[6].altPack", is(false)))
				.andExpect(jsonPath("$.items[6].orderingUpc", is(4011)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.scanCodeId", is(4011)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.product.productId", is(377497)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.product.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[6].containedUpc.pack", is(40)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs", hasSize(5)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[0]", is(4186)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[1]", is(15963254)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[2]", is(20401100000L)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[3]", is(20418600000L)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[4]", is(71752411112L)))
		;
	}

	@Test
	public void findByProductId_shelfEdgeFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/1437149?filters=SHELF-EDGE"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(1437149)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(20302300000L)))
				.andExpect(jsonPath("$.productDescription", is("BERRY & KIWI BLEND")))
				.andExpect(jsonPath("$.signRomanceCopy", is("BERRY & KIWI BLEND Romance Copy")))
				.andExpect(jsonPath("$.serviceCaseCallout", is("Berry & Kiwi Blend Service Case")))
				.andExpect(jsonPath("$.receiptDescription", is("BERRY & KIWI BLEND POS")))
				.andExpect(jsonPath("$.customerFriendlyDescriptionOne", is("Berry & Kiwi Blend CFD1")))
				.andExpect(jsonPath("$.customerFriendlyDescriptionTwo", is("Berry & Kiwi Blend CFD2")))
				.andExpect(jsonPath("$.primoPickDescription", is("Berry & Kiwi Blend Primo Pick")))
				.andExpect(jsonPath("$.showCalories", is(true)));
	}
	@Test
	public void findProductById_bananasSupplyChainFilter() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/377497?filters=SUPPLY-CHAIN"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(377497)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(4011)))
				.andExpect(jsonPath("$.productDescription", is("PREMIUM BANANAS")))

				.andExpect(jsonPath("$.items", hasSize(11)))

				.andExpect(jsonPath("$.items[0].itemCode", is(56792)))
				.andExpect(jsonPath("$.items[0].description", is("PREM YELLOW BANANAS TEST")))
				.andExpect(jsonPath("$.items[0].commodity", is(7363)))
				.andExpect(jsonPath("$.items[0].subCommodity", is(8528)))
				.andExpect(jsonPath("$.items[0].mrt", is(false)))
				.andExpect(jsonPath("$.items[0].altPack", is(false)))
				.andExpect(jsonPath("$.items[0].orderingUpc", is(40056792000L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.scanCodeId", is(40056792000L)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productId", is(377497)))
				.andExpect(jsonPath("$.items[0].containedUpc.upc.product.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[0].containedUpc.pack", is(38)))

				// This one has associates, we don't need to check them all, as the above is doing that.
				.andExpect(jsonPath("$.items[6].itemCode", is(428557)))
				.andExpect(jsonPath("$.items[6].description", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[6].commodity", is(7363)))
				.andExpect(jsonPath("$.items[6].subCommodity", is(8528)))
				.andExpect(jsonPath("$.items[6].mrt", is(false)))
				.andExpect(jsonPath("$.items[6].altPack", is(false)))
				.andExpect(jsonPath("$.items[6].orderingUpc", is(4011)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.scanCodeId", is(4011)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.product.productId", is(377497)))
				.andExpect(jsonPath("$.items[6].containedUpc.upc.product.productDescription", is("PREMIUM BANANAS")))
				.andExpect(jsonPath("$.items[6].containedUpc.pack", is(40)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs", hasSize(5)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[0]", is(4186)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[1]", is(15963254)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[2]", is(20401100000L)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[3]", is(20418600000L)))
				.andExpect(jsonPath("$.items[6].containedUpc.associatedUpcs[4]", is(71752411112L)))

				.andExpect(jsonPath("$.ebm").doesNotExist())
		;
	}


	@Test
	public void findProductById_nlea2016NutritionOnPrimaryUpc() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/1819862?filters=NUTRITION"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(1819862)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(22864400000L)))
				.andExpect(jsonPath("$.productDescription", is("CAMPECHANA")))
				.andExpect(jsonPath("$.nutritionPanels", hasSize(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelId", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelType", is("N2016")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingSize", is("4 piece (8mg)")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingsPerContainer", is("5")))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains", hasSize(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns", hasSize(2)))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].calories", is(45)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail", hasSize(16)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].columnModifier").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].id", is(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].sequence", is(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].value", is("45")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].value", is("2g")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].percentDailyValue", is("3")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[15].id", is(14)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[15].sequence", is(15)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[15].description", is("Potassium")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[15].value").doesNotExist())
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[15].percentDailyValue", is("1")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].id", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].calories", is(200)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail", hasSize(16)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].columnModifier").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[0].id", is(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[0].sequence", is(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[0].value", is("200")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[0].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[1].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[1].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[1].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[1].value", is("8g")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[1].percentDailyValue", is("10")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[15].id", is(14)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[15].sequence", is(15)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[15].description", is("Potassium")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[15].value").doesNotExist())
				.andExpect(jsonPath("$.nutritionPanels[0].columns[1].detail[15].percentDailyValue", is("5")));
	}

	@Test
	public void findProductById_scaleNutritionOnNonPrimaryUpc() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/1437123?filters=NUTRITION"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(1437123)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(3024)))
				.andExpect(jsonPath("$.productDescription", is("ALL BERRIES BOWL")))
				.andExpect(jsonPath("$.ingredients", is("strawberries (sub-ingredient), blueberries, blackberries, raspberries")))
				.andExpect(jsonPath("$.nutritionPanels", hasSize(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelId", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelType", is("N1990")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingSize", is("5 oz (140g)")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingsPerContainer", is("Varies")))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains", hasSize(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns", hasSize(1)))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].calories", is(60)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail", hasSize(17)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].columnModifier").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].id", is(100)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].value", is("60")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].percentDailyValue", is("12")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].id", is(101)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].sequence", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].description", is("Calories From Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].value", is("5")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].id", is(102)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].sequence", is(3)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].value", is("0.5g")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].percentDailyValue", is("1")));
	}

	@Test
	public void findProductById_scaleNutritionOnPrimaryUpc() throws Exception {

		ResultActions resultActions = this.mockMvc.perform(get("/product/1437149?filters=NUTRITION"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(1437149)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(20302300000L)))
				.andExpect(jsonPath("$.productDescription", is("BERRY & KIWI BLEND")))
				.andExpect(jsonPath("$.ingredients", is("strawberries (sub-ingredient), blueberries, blackberries, kiwi")))
				.andExpect(jsonPath("$.nutritionPanels", hasSize(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelId", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelType", is("N1990")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingSize", is("1 cup (151g)")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingsPerContainer", is("6")))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains", hasSize(0)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns", hasSize(1)))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].calories", is(10)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail", hasSize(17)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].columnModifier").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].id", is(100)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].value", is("10")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].percentDailyValue", is("1")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].id", is(101)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].sequence", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].description", is("Calories From Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].value", is("10")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].id", is(102)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].sequence", is(3)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].value", is("25g")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].percentDailyValue", is("38")));
	}

	@Test
	public void findProductById_ecomNutrition1990() throws Exception {

		String ingredients = "Milled Corn, Sugar, Malt Flavor, Contains 2% or Less of Salt. BHT added to packaging for freshness. Vitamins and Minerals: Iron, " +
				"Vitamin C (Sodium Ascorbate, Ascorbic Acid), Niacinamide, Vitamin B6 (Pyridoxine Hydrochloride), Vitamin B2 (Riboflavin), Vitamin B1 (Thiamin Hydrochloride), " +
				"Vitamin A Palmitate, Folic Acid, Vitamin D, Vitamin B12.";

		ResultActions resultActions = this.mockMvc.perform(get("/product/118647?filters=NUTRITION"));

		resultActions.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId", is(118647)))
				.andExpect(jsonPath("$.primaryScanCodeId", is(3800000120L)))
				.andExpect(jsonPath("$.productDescription", is("KELLOGGS CORN FLAKES CEREAL")))
				.andExpect(jsonPath("$.ingredients", is(ingredients)))
				.andExpect(jsonPath("$.nutritionPanels", hasSize(2)))

				.andExpect(jsonPath("$.nutritionPanels[0].panelId", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].panelType", is("N1990")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingSize", is("1 cup (28g)")))
				.andExpect(jsonPath("$.nutritionPanels[0].servingsPerContainer", is("about 18")))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains", hasSize(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains[0]", is("Kosher")))
				.andExpect(jsonPath("$.nutritionPanels[0].productIsOrContains[1]", is("Fat Free")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns", hasSize(1)))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].calories", is(100)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail", hasSize(26)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].columnModifier", is("cereal")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[0].value", is("100")))

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].id", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].sequence", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].description", is("Calories from Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].value", is("0")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[1].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].id", is(4)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].sequence", is(4)))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].value", is("0g")))
				.andExpect(jsonPath("$.nutritionPanels[0].columns[0].detail[2].percentDailyValue", is("0")))

				.andExpect(jsonPath("$.nutritionPanels[1].panelId", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[1].panelType", is("N1990")))
				.andExpect(jsonPath("$.nutritionPanels[1].servingSize", is("0  ")))
				.andExpect(jsonPath("$.nutritionPanels[1].servingsPerContainer", is("0")))
				.andExpect(jsonPath("$.nutritionPanels[1].productIsOrContains", hasSize(3)))
				.andExpect(jsonPath("$.nutritionPanels[1].productIsOrContains[0]", is("Kosher")))
				.andExpect(jsonPath("$.nutritionPanels[1].productIsOrContains[1]", is("Fat Free")))
				.andExpect(jsonPath("$.nutritionPanels[1].productIsOrContains[2]", is("Milk")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns", hasSize(1)))

				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].calories", is(140)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].columnModifier", is("with 0.5 cup skim milk")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail", hasSize(26)))

				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[0].id", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[0].sequence", is(1)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[0].description", is("Calories")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[0].value", is("140")))

				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[1].id", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[1].sequence", is(2)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[1].description", is("Calories from Fat")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[1].value", is("0")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[1].percentDailyValue").doesNotExist())

				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[2].id", is(4)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[2].sequence", is(4)))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[2].description", is("Total Fat")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[2].value", is("0g")))
				.andExpect(jsonPath("$.nutritionPanels[1].columns[0].detail[2].percentDailyValue", is("0")));
	}
}
