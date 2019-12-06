package com.heb.pm.core.service.nutrition;

import com.heb.pm.dao.core.entity.ProductPackVariation;
import com.heb.pm.util.IncrementingId;
import org.springframework.stereotype.Service;

/**
 * Handles eCommerce nutrition panels that follow the FDA (NLEA 2016) standard.
 *
 * @author d116773
 * @since 1.4.0
 */
@Service
/* default */ class EcommerceFdaPanelService extends BaseEcommercePanelService {

	/**
	 * Constructs a new EcommerceFdaPanelService.
	 */
	public EcommerceFdaPanelService() {
		super();

		IncrementingId incrementingId = IncrementingId.of(1L);

		this.nutrientMap.put("Calories", new NutrientMapEntry("Calories", incrementingId.longValue()));
		this.nutrientMap.put("Total Fat", new NutrientMapEntry("Total Fat", incrementingId.longValue()));
		this.nutrientMap.put("Saturated Fat", new NutrientMapEntry("Saturated Fat", incrementingId.longValue()));
		this.nutrientMap.put("Trans Fat", new NutrientMapEntry("Trans Fat", incrementingId.longValue()));
		this.nutrientMap.put("Cholesterol", new NutrientMapEntry("Cholesterol", incrementingId.longValue()));
		this.nutrientMap.put("Sodium", new NutrientMapEntry("Sodium", incrementingId.longValue()));
		this.nutrientMap.put("Total Carbohydrate", new NutrientMapEntry("Total Carbohydrate", incrementingId.longValue()));
		this.nutrientMap.put("Dietary Fiber", new NutrientMapEntry("Dietary Fiber", incrementingId.longValue()));
		this.nutrientMap.put("Total Sugars", new NutrientMapEntry("Total Sugars", incrementingId.longValue()));
		this.nutrientMap.put("Includes Added Sugars", new NutrientMapEntry("Includes Added Sugars", incrementingId.longValue()));
		this.nutrientMap.put("Protein", new NutrientMapEntry("Protein", incrementingId.longValue()));
		this.nutrientMap.put("Vitamin D", new NutrientMapEntry("Vitamin D", incrementingId.longValue()));
		this.nutrientMap.put("Calcium", new NutrientMapEntry("Calcium", incrementingId.longValue()));
		this.nutrientMap.put("Iron", new NutrientMapEntry("Iron", incrementingId.longValue()));
		this.nutrientMap.put("Potassium", new NutrientMapEntry("Potassium", incrementingId.longValue()));
	}


	@Override
	protected String getPanelType() {
		return "N2016";
	}

	@Override
	protected boolean handlesPanel(ProductPackVariation productPackVariation) {
		return BaseEcommercePanelService.ecommercePanelIs2016(productPackVariation);
	}
}
