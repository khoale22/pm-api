package com.heb.pm.core.service.nutrition;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.Product;
import com.heb.pm.core.model.nutrition.NutritionDetail;
import com.heb.pm.core.model.nutrition.NutritionPanel;
import com.heb.pm.core.model.nutrition.NutritionPanelColumn;
import com.heb.pm.core.repository.MasterDataExtendedAttributeRepository;
import com.heb.pm.core.repository.ProductPackVariationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base service to handle eCommerce nutrition data.
 *
 * @author d116773
 * @since 1.4.0
 */
/* default */ abstract class BaseEcommercePanelService {

	// These are the different IDs in the nutrient table that store metric serving size.
	private static final List<Long> ECOMMERCE_METRIC_SERVING_SIZE_NUTRIENTS = List.of(27_187L, 20_567L);
	private static final String NOT_A_CLAIM_CODE = "0";
	private static final Long ECOMMERCE_INGREDIENTS_ATTRIBUTE = 1674L;

	// This is the map for child classes to fill with the nutrients
	// that are supported by the specific panel and the order the nutrients
	// should show up in on the panel.
	protected final transient Map<String, NutrientMapEntry> nutrientMap = new ConcurrentHashMap<>();

	@Autowired
	protected transient MasterDataExtendedAttributeRepository masterDataExtendedAttributeRepository;

	@Autowired
	private transient ProductPackVariationRepository productPackVariationRepository;

	/**
	 * Class to hold the list of nutrients and their sequence.
	 *
	 * @author d116773
	 * @since 1.4.4
	 */
	@Data
	@Accessors(chain = true)
	@RequiredArgsConstructor
	/* default */ static class NutrientMapEntry {
		private final String nutrientDescription;
		private final Long sequenceNumber;
	}

	/**
	 * Returns the type of nutrition panel this is. Should be either N1990, N2016, or INGREDIENTS ONLY.
	 *
	 * @return The type of nutrition panel this is.
	 */
	protected abstract String getPanelType();

	/**
	 * Returns whether or not this object handles the panel for a particular UPC.
	 *
	 * @param productPackVariation The root nutrition panel for the UPC.
	 * @return True if this object handles this UPC and false otherwise.
	 */
	protected abstract boolean handlesPanel(ProductPackVariation productPackVariation);

	/**
	 * Populates the nutritional data for a product. Will return false if nutrition
	 * does not exist or if this object does not handle the nutrition data for the product.
	 *
	 * @param product The product to add nutrition to. This object may be modified.
	 * @param productMaster The ProductMaster for the product to get data for.
	 * @return True if nutrition was added and false otherwise.
	 */
	public boolean populatePublishedEcommerceNutritionPanels(Product product, ProductMaster productMaster) {


		List<ProductPackVariation> panels = this.productPackVariationRepository.findByKeyUpcAndKeySourceSystem(productMaster.getPrimaryScanCodeId(), SourceSystem.ECOMMERCE_SOURCE_SYSTEM);

		if (panels.isEmpty()) {
			return false;
		}

		ProductPackVariation productPackVariation = panels.get(0);
		if (!this.handlesPanel(productPackVariation)) {
			return false;
		}

		List<NutritionPanel> nutritionPanels = new LinkedList<>();

		long panelCount = 0L;
		for (ProductPackVariation ppv : panels) {

			panelCount++;
			NutritionPanel nutritionPanel = NutritionPanel.of()
					.setPanelId(panelCount)
					.setPanelType(this.getPanelType())
					.setServingSize(String.format("%s %s %s",
							NutritionUtils.servingSizeToText(ppv.getServingSize()),
							ppv.getServingSizeUnitOfMeasure().getDescription().trim(),
							extractEcommerceMetricServingSize(ppv)))
					.setServingsPerContainer(extractServingsPerContainer(ppv));

			NutritionPanelColumn nutritionPanelColumn = NutritionPanelColumn.of().setId(1L);

			extractEcommerceCalories(ppv).ifPresent(nutritionPanelColumn::setCalories);

			if (StringUtils.isNotBlank(ppv.getColumnModifier())) {
				nutritionPanelColumn.setColumnModifier(ppv.getColumnModifier().trim());
			}

			nutritionPanel.getColumns().add(nutritionPanelColumn);

			ppv.getNutritionDetail().stream()
					.map(this::ecommerceNutrientDetailToNutritionDetail)
					.forEach(r -> r.ifPresent(nutritionPanelColumn.getDetail()::add));
			ppv.getNutritionDetail().stream()
					.filter(BaseEcommercePanelService::ecommerceNutrientDetailIsClaim)
					.forEach(r -> nutritionPanel.getProductIsOrContains().add(r.getNutrient().getDescription().trim()));

			this.reorderNutrients(nutritionPanelColumn.getDetail());
			nutritionPanelColumn.getDetail().sort(Comparator.comparing(NutritionDetail::getSequence));

			nutritionPanels.add(nutritionPanel);
		}
		product.setNutritionPanels(nutritionPanels);

		this.extractIngredients(productMaster).ifPresent(product::setIngredients);
		return true;
	}



	/**
	 * Returns whether or not a nutrient record is a a claim (Kosher, Gluten Free, etc.).
	 *
	 * @param detail The detail record to check.
	 * @return True if the record is a claim and false otherwise.
	 */
	protected static boolean ecommerceNutrientDetailIsClaim(EcommerceNutritionDetail detail) {
		return !NOT_A_CLAIM_CODE.equals(detail.getDeclaredOnLabelSwitch());
	}

	/**
	 * Returns whether or not to exclude a nutrient detail record from the panel.
	 *
	 * @param detail The nutrient detail record to check.
	 * @return True if the record should be excluded and false otherwise.
	 */
	protected boolean excludeNutrientFromPanel(EcommerceNutritionDetail detail) {

		// Exclude claims.
		if (ecommerceNutrientDetailIsClaim(detail)) {
			return true;
		}

		// If it's not one of the standard nutrients and it has 0 value and daily percent, exclude it.
		if (!this.nutrientMap.containsKey(detail.getNutrient().getDescription().trim())) {
			return nutrientIsEmpty(detail);
		}

		return false;
	}

	/**
	 * Converts an EcommerceNutritionDetail to a NutritionDetail. It will return empty if the EcommerceNutritionDetail
	 * is a claim or is actually the metric serving size.
	 *
	 * @param detail The EcommerceNutritionDetail to convert.
	 * @return An optional NutritionDetail.
	 */
	protected Optional<NutritionDetail> ecommerceNutrientDetailToNutritionDetail(EcommerceNutritionDetail detail) {

		// Exclude metric serving size.
		if (ECOMMERCE_METRIC_SERVING_SIZE_NUTRIENTS.contains(detail.getKey().getNutrientId())) {
			return Optional.empty();
		}

		// See if this should be excluded.
		if (this.excludeNutrientFromPanel(detail)) {
			return Optional.empty();
		}

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		NutritionDetail nutritionDetail = NutritionDetail.of()
				.setId(detail.getKey().getNutrientId())
				.setDescription(detail.getNutrient().getDescription().trim())
				.setValue(String.format("%s%s", decimalFormat.format(detail.getQuantity()), detail.getQuantityUnitOfMeasure().getDescription().trim()));

		if (Objects.nonNull(detail.getDailyValuePercent())) {
			nutritionDetail.setPercentDailyValue(decimalFormat.format(detail.getDailyValuePercent()));
		}

		if (Objects.nonNull(detail.getNutrient().getSequenceNumber())) {
			nutritionDetail.setSequence(detail.getNutrient().getSequenceNumber());
		} else {
			nutritionDetail.setSequence(detail.getKey().getNutrientId());
		}
		return Optional.of(nutritionDetail);
	}

	/**
	 * Extracts the metric serving size from a nutrition panel.
	 *
	 * @param productPackVariation The nutrition panel to pull the serving size from.
	 * @return The formatted metric serving size of the panel.
	 */
	protected static String extractEcommerceMetricServingSize(ProductPackVariation productPackVariation) {

		EcommerceNutritionDetail metricServingSizeNutrientRecord = productPackVariation.getNutritionDetail().stream()
				.filter(r ->  ECOMMERCE_METRIC_SERVING_SIZE_NUTRIENTS.contains(r.getKey().getNutrientId()))
				.findFirst().orElse(null);

		if (Objects.isNull(metricServingSizeNutrientRecord)) {
			return StringUtils.EMPTY;
		}

		return String.format("(%d%s)", metricServingSizeNutrientRecord.getQuantity().longValue(),
				metricServingSizeNutrientRecord.getQuantityUnitOfMeasure().getDescription().trim());
	}

	/**
	 * Extracts the calories from a nutrition panel.
	 *
	 * @param productPackVariation The nutrition panel to pull calories from.
	 * @return The calories from the panel. Will return -1 if not found.
	 */
	protected static Optional<Long> extractEcommerceCalories(ProductPackVariation productPackVariation) {

		// First, check prod-pack variation.
		if (Objects.nonNull(productPackVariation.getCalories()) && productPackVariation.getCalories() > 0) {
			return Optional.of(productPackVariation.getCalories());
		}

		// Then try to get nutrient ID 1.
		Optional<EcommerceNutritionDetail> caloriesRecord = productPackVariation.getNutritionDetail().stream()
				.filter(r -> r.getKey().getNutrientId().equals(1L)).findFirst();

		return caloriesRecord.map(ecommerceNutritionDetail -> ecommerceNutritionDetail.getQuantity().longValue());
	}

	/**
	 * Will set the sequence number of the nutrients in the panel based on the order supplied by child classes. Any
	 * nutrient not explicitly defined will be appended to the end of the list.
	 *
	 * @param listOfNutrients The list of nutrients to set the sequence numbers for. The attributes in the objects
	 *                        in this list will be modified by this function.
	 */
	protected void reorderNutrients(List<NutritionDetail> listOfNutrients) {

		long currentMaxNutrient = nutrientMap.size() + 1;
		for (NutritionDetail nd : listOfNutrients) {
			NutrientMapEntry nutrientMapEntry = this.nutrientMap.get(nd.getDescription());

			if (Objects.isNull(nutrientMapEntry)) {
				nd.setSequence(currentMaxNutrient);
				currentMaxNutrient++;
			} else {
				nd.setSequence(nutrientMapEntry.sequenceNumber);
			}
		}
	}

	/**
	 * Returns the ingredients for a given product. Will return empty if they are not found.
	 *
	 * @param productMaster The product to look for ingredients for.
	 * @return An optional formatted ingredient statement.
	 */
	protected Optional<String> extractIngredients(ProductMaster productMaster) {

		List<MasterDataExtendedAttribute> ingredientStatements =
				this.masterDataExtendedAttributeRepository.findByKeyAttributeIdAndKeyKeyIdAndKeyKeyTypeAndKeySourceSystemId(ECOMMERCE_INGREDIENTS_ATTRIBUTE,
						productMaster.getProdId(), MasterDataExtendedAttributeKey.KEY_TYPE_PRODUCT, SourceSystem.ECOMMERCE_SOURCE_SYSTEM);


		if (ingredientStatements.isEmpty()) {
			return Optional.empty();
		}

		// We have to do it this way since there's no guarantee of which sequence number the ingredients
		// has. If we get more than one back, that is an error state, so just return the first one.

		// You should also note that the way that the eCommerce data is stored, the ingredients are attached
		// directly to the product. When there are multiple panels, the ingredients repeat.
		return Optional.of(ingredientStatements.get(0).getTextValue());
	}

	/**
	 * Returns whether or not a given nutrition panel follows the NLEA 2016 standard.
	 *
	 * @param productPackVariation The nutrition panel to look at.
	 * @return True if the nutrition panel follows the NLEA 2016 standard and false otherwise.
	 */
	protected static boolean ecommercePanelIs2016(ProductPackVariation productPackVariation) {

		return productPackVariation.getNutritionDetail().stream()
				.anyMatch(r -> StringUtils.containsIgnoreCase(r.getNutrient().getDescription(), "Added Sugars"));
	}

	/**
	 * Returns whether or not a panel is a supplement panel.
	 *
	 * @param productPackVariation The panel to look at.
	 * @return True if the panel is a supplement panel and false otherwise.
	 */
	protected static boolean ecommercePanelIsSuppliment(ProductPackVariation productPackVariation) {
		return productPackVariation.getPanelType().trim().equals("SUPLI");
	}

	private boolean nutrientIsEmpty(EcommerceNutritionDetail detail) {

		BigDecimal quantity = Objects.isNull(detail.getQuantity()) ? BigDecimal.ZERO : detail.getQuantity();
		BigDecimal percent = Objects.isNull(detail.getDailyValuePercent()) ? BigDecimal.ZERO : detail.getDailyValuePercent();

		return quantity.compareTo(BigDecimal.ZERO)  == 0 && percent.compareTo(BigDecimal.ZERO) == 0;
	}

	private String extractServingsPerContainer(ProductPackVariation productPackVariation) {

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		// If the servings per container text has text, and it's a number, then format it.
		if (StringUtils.isNotBlank(productPackVariation.getServingsPerContainerAsText())) {
			try {
				Double spcr = Double.valueOf(productPackVariation.getServingsPerContainerAsText());
				return decimalFormat.format(spcr);
			} catch (NumberFormatException e) {
				// If we get here, it's a mixture of text and numbers, so just return it.
				return productPackVariation.getServingsPerContainerAsText().trim();
			}
		}

		// Otherwise, return what's in the minimum servings per container field.
		return decimalFormat.format(productPackVariation.getMinimumServingsPerContainer());
	}
}
