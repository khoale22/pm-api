package com.heb.pm.core.service;

import com.heb.pm.core.model.nutrition.NutritionPanel;
import com.heb.pm.dao.core.entity.ProductMaster;
import com.heb.pm.core.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

/**
 * Tests ProductService.
 *
 * @author d116773
 * @since 1.0.0
 */
public class ProductServiceTest {

	private ProductService productService;

	@Before
	public void setup() {
		this.productService = new ProductService();
	}

	@Test
	public void productMasterToSkinnyProduct_handlesNull() {

		Assert.assertNull(this.productService.productMasterToSkinnyProduct(null));
	}

	@Test
	public void productMasterToSkinnyProduct_handlesBadProduct() {

		Assert.assertNotNull(this.productService.productMasterToSkinnyProduct(new ProductMaster()));
	}

	@Test
	public void productMasterToSkinnyProduct_handlesGoodProduct() {

		ProductMaster productMaster = new ProductMaster().setProdId(32523L).setDescription("TEST PRODUCT");
		Product product = this.productService.productMasterToSkinnyProduct(productMaster);

		Assert.assertEquals(Long.valueOf(32523), product.getProductId());
		Assert.assertEquals("TEST PRODUCT", product.getProductDescription());
	}

	@Test
	public void appyNutritionCriterion_handlesNull() {

		Product p = Product.of();
		Assert.assertTrue(ProductService.applyNutritionCriterion(p, null));
	}

	@Test
	public void appyNutritionCriterion_handlesFalseWithNutrition() {

		Product p = Product.of();
		p.setNutritionPanels(new LinkedList<>()).getNutritionPanels().add(NutritionPanel.of());
		Assert.assertFalse(ProductService.applyNutritionCriterion(p, Boolean.FALSE));
	}

	@Test
	public void appyNutritionCriterion_handlesFalseWithoutNutrition() {

		Product p = Product.of();
		Assert.assertTrue(ProductService.applyNutritionCriterion(p, Boolean.FALSE));
	}

	@Test
	public void appyNutritionCriterion_handlesTrueWithNutrition() {

		Product p = Product.of();
		p.setNutritionPanels(new LinkedList<>()).getNutritionPanels().add(NutritionPanel.of());
		Assert.assertTrue(ProductService.applyNutritionCriterion(p, Boolean.TRUE));
	}

	@Test
	public void appyNutritionCriterion_handlesTrueWithoutNutrition() {

		Product p = Product.of();
		Assert.assertFalse(ProductService.applyNutritionCriterion(p, Boolean.TRUE));
	}

}
