package com.heb.pm.core.service.nutrition;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.Product;
import com.heb.pm.core.model.nutrition.NutritionDetail;
import com.heb.pm.core.model.nutrition.NutritionPanel;
import com.heb.pm.core.model.nutrition.NutritionPanelColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Service to handle the legacy tables.
 *
 * @author d116773
 * @since 1.4.0
 */
@Service
/* default */ class ScaleAgPanelService extends BaseScalePanelService {

	private static final Long VARIES_SERVING_SIZE = 999L;
	private static final Long CALORIES_NUTRIENT_ID = 100L;
	private static final String CALORIES_UOM = "*";
	private static final String NUTRIENT_REQUIRED = "Y";
	private static final String SET_NUTRIENT_VALUE_BY_RDA = "Y";

	@Override
	public boolean populateScaleNutrition(Product product, ProductMaster productMaster) {

		// See if this product has a scale UPC.
		Optional<ScaleUpc> optionalScaleUpc = this.getPrimaryScaleUpcForProduct(productMaster);
		if (optionalScaleUpc.isEmpty()) {
			return false;
		}
		ScaleUpc scaleUpc = optionalScaleUpc.get();

		if (!BaseScalePanelService.scaleUpcHasNutrition(scaleUpc)) {
			return false;
		}

		this.populateSaleIngredients(product, scaleUpc);

		// It is supposed to have a nutrition statement, but there's no data tied to it, so return.
		ScaleNutritionStatementHeader nutritionStatementHeader = scaleUpc.getNlea1990NutritionStatement();
		if (Objects.isNull(nutritionStatementHeader)) {
			return false;
		}

		List<NutritionPanel> nutritionPanels = new LinkedList<>();

		// There's only one panel.
		NutritionPanel nutritionPanel = NutritionPanel.of()
				.setServingsPerContainer(VARIES_SERVING_SIZE.equals(nutritionStatementHeader.getServingsPerContainer()) ?
						"Varies" : nutritionStatementHeader.getServingsPerContainer().toString())
				.setServingSize(extractServingSize(nutritionStatementHeader))
				.setPanelId(1L).setPanelType("N1990");

		// There's only one column.
		NutritionPanelColumn nutritionPanelColumn = NutritionPanelColumn.of()
				.setId(1L);

		// Set the calories if we can find the record.
		extractCaloriesDetailRecord(nutritionStatementHeader).map(c -> c.getQuantity().longValue()).ifPresent(nutritionPanelColumn::setCalories);

		// Map the detail.
		nutritionStatementHeader.getDetails().stream()
				.map(ScaleAgPanelService::scaleNutritionStatementDetailToNutritionDetail)
				.forEach(d -> d.ifPresent(nutritionPanelColumn.getDetail()::add));

		// Sort the nutrients based on the sequence.
		nutritionPanelColumn.getDetail().sort(Comparator.comparing(NutritionDetail::getSequence));

		nutritionPanel.getColumns().add(nutritionPanelColumn);
		nutritionPanels.add(nutritionPanel);

		product.setNutritionPanels(nutritionPanels);
		return true;
	}

	private static Optional<NutritionDetail> scaleNutritionStatementDetailToNutritionDetail(ScaleNutritionStatementDetail scaleNutritionStatementDetail) {

		if (!NUTRIENT_REQUIRED.equals(scaleNutritionStatementDetail.getNutrient().getRequired())) {
			return Optional.empty();
		}

		// All the calories nutrients have * units of measure tied to them, remove those.
		String rawUom = scaleNutritionStatementDetail.getNutrient().getUnitOfMeasure().getDescription().trim();

		String uom = CALORIES_UOM.equals(rawUom) ? StringUtils.EMPTY : rawUom;

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		NutritionDetail nutritionDetail = NutritionDetail.of().setSequence(scaleNutritionStatementDetail.getNutrient().getSequence())
				.setId(scaleNutritionStatementDetail.getKey().getNutrientId())
				.setDescription(WordUtils.capitalizeFully(scaleNutritionStatementDetail.getNutrient().getDescription().trim().toLowerCase(Locale.US)));

		// If this flag is flipped, then the nutrient detail is set by percentage and not by value, so don't set the value.
		if (!SET_NUTRIENT_VALUE_BY_RDA.equals(scaleNutritionStatementDetail.getNutrient().getSetByPercentDailyValue())) {
			nutritionDetail.setValue(String.format("%s%s", decimalFormat.format(scaleNutritionStatementDetail.getQuantity()), uom));
		}

		NutritionUtils.rdaToFormattedRda(scaleNutritionStatementDetail.getNutrient().getRecommendedDailyValue(),
				BigDecimal.valueOf(scaleNutritionStatementDetail.getPercentDailyValue())).ifPresent(nutritionDetail::setPercentDailyValue);

		return Optional.of(nutritionDetail);
	}

	private static Optional<ScaleNutritionStatementDetail> extractCaloriesDetailRecord(ScaleNutritionStatementHeader nutritionStatementHeader) {

		return nutritionStatementHeader.getDetails()
				.stream()
				.filter(d -> CALORIES_NUTRIENT_ID.equals(d.getKey().getNutrientId()))
				.findFirst();
	}

	private static String extractServingSize(ScaleNutritionStatementHeader nutritionStatementHeader) {

		return String.format("%s %s (%d%s)", NutritionUtils.servingSizeToText(nutritionStatementHeader.getImperialServingSize()),
				nutritionStatementHeader.getImperialUnitOfMeasure().getDescription().trim(),
				nutritionStatementHeader.getMetricServingSize().longValue(),
				nutritionStatementHeader.getMetricUnitOfMeasure().getDescription().trim());
	}
}
