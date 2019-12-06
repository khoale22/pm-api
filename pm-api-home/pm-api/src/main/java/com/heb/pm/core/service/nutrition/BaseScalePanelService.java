package com.heb.pm.core.service.nutrition;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.Product;
import com.heb.pm.core.repository.SellingUnitRepository;
import com.heb.pm.util.UpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Holds common functions for the scale nutrition services.
 *
 * @author d116773
 * @since 1.4.0
 */
/* default */ abstract class BaseScalePanelService {

	private static final Logger logger = LoggerFactory.getLogger(BaseScalePanelService.class);

	private static final Long EXCLUDED_INGREDIENT_CATEGORY = 12L;

	private static final String SUB_INGREDIENT_BEGIN_DELIMITER = " (";
	private static final String SUB_INGREDIENT_END_DELIMITER = ")";
	private static final String INGREDIENT_DELIMITER = ", ";

	@Autowired
	private transient SellingUnitRepository sellingUnitRepository;

	/**
	 * Adds a nutrition statement to a Product. Will return false if this object is not supposed to process
	 * this given UPC.
	 *
	 * @param product The product to add nutrition to. This method may modify the product.
	 * @param productMaster The product to get scale nutrition for.
	 * @return An optional list of nutrition panels.
	 */
	public abstract boolean populateScaleNutrition(Product product, ProductMaster productMaster);

	/**
	 * Loads scale ingredient data into a Product object.
	 *
	 * @param product The product to load ingredient data into.
	 * @param scaleUpc The scale UPC to get ingredient information from.
	 */
	protected void populateSaleIngredients(Product product, ScaleUpc scaleUpc) {

		// Not all scale items have ingredient statements.
		if (ScaleUpc.UNDEFINED_INGREDIENT_STATEMENT.equals(scaleUpc.getIngredientStatementId()) ||
				Objects.isNull(scaleUpc.getIngredientStatement())) {
			return;
		}

		StringJoiner ingredientsJoiner = new StringJoiner(INGREDIENT_DELIMITER);
		for (ScaleIngredientStatementDetail scaleIngredient : scaleUpc.getIngredientStatement().getIngredientStatementDetails()) {
			this.getIngredientText(scaleIngredient.getIngredient()).ifPresent(ingredientsJoiner::add);
		}
		product.setIngredients(ingredientsJoiner.toString());
	}

	/**
	 * Returns the full text for an ingredient. If the ingredient contains sub-ingredients, those will be returned
	 * in a format appropriate for a scale label.
	 *
	 * @param scaleIngredient The ingredient to return the text for.
	 * @return The full text for the ingredient.
	 */
	private Optional<String> getIngredientText(ScaleIngredient scaleIngredient) {

		if (EXCLUDED_INGREDIENT_CATEGORY.equals(scaleIngredient.getIngredientCategory())) {
			return Optional.empty();
		}

		StringBuilder ingredientBuilder = new StringBuilder();

		ingredientBuilder.append(scaleIngredient.getDescription());

		if ("Y".equals(scaleIngredient.getStatementOfIngredientsSwitch())) {
			ingredientBuilder.append(SUB_INGREDIENT_BEGIN_DELIMITER);
			StringJoiner subIngredientJoiner = new StringJoiner(INGREDIENT_DELIMITER);
			for (ScaleStatementOfIngredient subIngredient : scaleIngredient.getStatementOfIngredients()) {
				this.getIngredientText(subIngredient.getIngredient()).ifPresent(subIngredientJoiner::add);
			}

			ingredientBuilder.append(subIngredientJoiner.toString()).append(SUB_INGREDIENT_END_DELIMITER);
		}

		return Optional.of(ingredientBuilder.toString());
	}

	/**
	 * Reconciles products that are tied to scale items but the primary is not a scale UPC. Will try the
	 * product primary UPC and associates of the product primary UPC. It will return empty if nothing is found.
	 *
	 * @param productMaster The Product to try and find a scale UPC for.
	 * @return The SellingUnit tied to a scale UPC that is an associate of the product's primary UPC if it exists
	 * and empty otherwise.
	 */
	public Optional<ScaleUpc> getPrimaryScaleUpcForProduct(ProductMaster productMaster) {

		// First, just check the primary UPC.
		if (hasScaleUpc(productMaster.getPrimaryUpc().getSellingUnit())) {
			return Optional.of(productMaster.getPrimaryUpc().getSellingUnit().getScaleUpc());
		}

		// First, see if this is a PLU that has a pre-digit-2 tied to it.
		if (UpcUtils.isPlu(productMaster.getPrimaryScanCodeId())) {
			try {
				Long preDigitTwo = UpcUtils.pluToPreDigitTwo(productMaster.getPrimaryScanCodeId());
				Optional<SellingUnit> preDigitTwoSellingUnit = this.sellingUnitRepository.findById(preDigitTwo);
				if (preDigitTwoSellingUnit.isPresent() &&
						preDigitTwoSellingUnit.get().getProductId().equals(productMaster.getProdId()) &&
						hasScaleUpc(preDigitTwoSellingUnit.get())) {
							return Optional.of(preDigitTwoSellingUnit.get().getScaleUpc());
				}
			} catch (NumberFormatException e) {
				logger.error(String.format("Unable to convert %d to a pre-digit-2 UPC.", productMaster.getPrimaryScanCodeId()));
			}
		}

		// Next, look for associates tied to the product's primary UPC.
		return productMaster.getPrimaryUpc()
				.getAssociateUpcs()
				.stream()
				.filter(a -> hasScaleUpc(a.getSellingUnit()))
				.findFirst()
				.map(a ->a.getSellingUnit().getScaleUpc());
	}

	/**
	 * Checks to see if a scale UPC has a nutrient statement tied to it.
	 *
	 * @param scaleUpc The scale UPC to check.
	 * @return True if it has nutrition and false otherwise.
	 */
	protected static boolean scaleUpcHasNutrition(ScaleUpc scaleUpc) {

		// If this is a scale UPC, but it doesn't have a nutrient statement, so return.
		return !ScaleUpc.LIST_OF_UNDEFINED_NUTRIENT_STATEMENTS.contains(scaleUpc.getNlea1990NutritionStatementId());
	}


	/**
	 * Returns whether or not a selling unit is attached to a scale UPC.
	 *
	 * @param sellingUnit The selling unit to check.
	 * @return True if that selling unit is attached to a scale UPC and false otherwise.
	 */
	private static boolean hasScaleUpc(SellingUnit sellingUnit) {
		return Objects.nonNull(sellingUnit.getScaleUpc());
	}
}
