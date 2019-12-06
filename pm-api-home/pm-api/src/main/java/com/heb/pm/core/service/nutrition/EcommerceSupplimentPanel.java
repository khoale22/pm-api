package com.heb.pm.core.service.nutrition;

import com.heb.pm.dao.core.entity.ProductPackVariation;
import com.heb.pm.util.IncrementingId;
import org.springframework.stereotype.Service;

/**
 * Handles suppliment panels for eCommerce.
 *
 * @author d116773
 * @since 1.4.0
 */
@Service
/* default */ class EcommerceSupplimentPanel extends BaseEcommercePanelService {

	/**
	 * Constructs an EcommerceSupplimentPanel.
	 */
	public EcommerceSupplimentPanel() {
		super();

		IncrementingId incrementingId = IncrementingId.of(1L);

		this.nutrientMap.put("Calories", new NutrientMapEntry("Calories", incrementingId.longValue()));
		this.nutrientMap.put("Calories from Fat", new NutrientMapEntry("Calories from Fat", incrementingId.longValue()));
		this.nutrientMap.put("Saturated Fat Calories", new NutrientMapEntry("Saturated Fat Calories", incrementingId.longValue()));
		this.nutrientMap.put("Total Fat", new NutrientMapEntry("Total Fat", incrementingId.longValue()));
		this.nutrientMap.put("Saturated Fat", new NutrientMapEntry("Saturated Fat", incrementingId.longValue()));
		this.nutrientMap.put("Trans Fat", new NutrientMapEntry("Trans Fat", incrementingId.longValue()));
		this.nutrientMap.put("Polyunsaturated Fat", new NutrientMapEntry("Polyunsaturated Fat", incrementingId.longValue()));
		this.nutrientMap.put("Monounsaturated Fat", new NutrientMapEntry("Monounsaturated Fat", incrementingId.longValue()));
		this.nutrientMap.put("Cholesterol", new NutrientMapEntry("Cholesterol", incrementingId.longValue()));
		this.nutrientMap.put("Sodium", new NutrientMapEntry("Sodium", incrementingId.longValue()));
		this.nutrientMap.put("Potassium", new NutrientMapEntry("Potassium", incrementingId.longValue()));
		this.nutrientMap.put("Total Carbohydrate", new NutrientMapEntry("Total Carbohydrate", incrementingId.longValue()));
		this.nutrientMap.put("Dietary Fiber", new NutrientMapEntry("Dietary Fiber", incrementingId.longValue()));
		this.nutrientMap.put("Sugars", new NutrientMapEntry("Sugars", incrementingId.longValue()));
		this.nutrientMap.put("Protein", new NutrientMapEntry("Protein", incrementingId.longValue()));
		this.nutrientMap.put("Vitamin A", new NutrientMapEntry("Vitamin A", incrementingId.longValue()));
		this.nutrientMap.put("Calcium", new NutrientMapEntry("Calcium", incrementingId.longValue()));
		this.nutrientMap.put("Vitamin C", new NutrientMapEntry("Vitamin C", incrementingId.longValue()));
		this.nutrientMap.put("Iron", new NutrientMapEntry("Iron", incrementingId.longValue()));
	}


	@Override
	protected String getPanelType() {
		return "Supplement";
	}

	@Override
	protected boolean handlesPanel(ProductPackVariation productPackVariation) {
		return BaseEcommercePanelService.ecommercePanelIsSuppliment(productPackVariation);
	}
}
