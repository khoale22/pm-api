package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.model.Product;
import com.heb.pm.core.model.nutrition.NutritionDetail;
import com.heb.pm.core.model.nutrition.NutritionPanel;
import com.heb.pm.core.model.nutrition.NutritionPanelColumn;
import com.heb.pm.core.repository.NleaUpcRolloutRepository;
import com.heb.pm.core.repository.NutrientStatementPanelHeaderRepository;
import com.heb.pm.dao.core.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Service to handle nutrition for the scale NLEA2016 data. Long-term, all nutrition data will reside here,
 * but, for now, this is focused on scale NLEA2016.
 *
 * @author d116773
 * @since 1.9.0
 */
@Service
public class ScaleFdaPanelService extends BaseScalePanelService {

	private static final String VARIES_SERVING_SIZE = "999";

	@Autowired
	private transient NleaUpcRolloutRepository nleaUpcRolloutRepository;

	@Autowired
	private transient NutrientStatementPanelHeaderRepository nutrientStatementPanelHeaderRepository;

	@Override
	public boolean populateScaleNutrition(Product product, ProductMaster productMaster) {

		// See if this product has a scale UPC.
		Optional<ScaleUpc> optionalScaleUpc = this.getPrimaryScaleUpcForProduct(productMaster);
		if (optionalScaleUpc.isEmpty()) {
			return false;
		}
		ScaleUpc scaleUpc = optionalScaleUpc.get();

		// Check to see if this UPC is configured to use NLEA2016.
		if (!this.scaleUpcHasNlea2016Nutrition(scaleUpc)) {
			return false;
		}

		// Load ingredients.
		this.populateSaleIngredients(product, scaleUpc);


		// If we get here, we should be using 2016 nutrition.
		List<NutrientStatementPanelHeader> optionalNutrientStatementPanelHeader =
				this.nutrientStatementPanelHeaderRepository.findAllBySourceSystemIdAndSourceSystemReferenceIdAndActiveSwitch(SourceSystem.SCALE_MANAGEMENT_SOURCE_SYSTEM,
						scaleUpc.getNlea1990NutritionStatementId().toString(), Boolean.TRUE);
		if (optionalNutrientStatementPanelHeader.isEmpty()) {
			// If it's not there, then just return.
			return false;
		}

		// It's there and we should use it, so translate to external.
		// There should only be one active panel, so just grab the first.
		NutrientStatementPanelHeader nutrientStatementPanelHeader = optionalNutrientStatementPanelHeader.get(0);

		List<NutritionPanel> nutritionPanels = new LinkedList<>();
		nutritionPanels.add(scaleNutrientStatementPanelToNutritionPanel(nutrientStatementPanelHeader));

		product.setNutritionPanels(nutritionPanels);
		return true;
	}

	/**
	 * Determines if a scale UPC has NLEA2016 nutrition.
	 *
	 * @param scaleUpc The scale UPC to check.
	 * @return True if the scale UPC has NLEA2016 nutrition and false otherwise.
	 */
	private boolean scaleUpcHasNlea2016Nutrition(ScaleUpc scaleUpc) {

		// See if it has nutrition at all.
		if (!BaseScalePanelService.scaleUpcHasNutrition(scaleUpc)) {
			return false;
		}

		// If it's not in the rollout table or it's marked as do not use 2016, move on.
		Optional<NleaUpcRollout> nleaUpcRollout = this.nleaUpcRolloutRepository.findById(scaleUpc.getUpc());

		return nleaUpcRollout.map(NleaUpcRollout::getUseNlea2016).orElse(false);
	}

	/**
	 * Translates a NLEA2016 scale nutrition statement to a panel.
	 *
	 * @param nutrientStatementPanelHeader The NLEA2016 scale nutrition statement.
	 * @return The translated full panel. This will include all columns and detail.
	 */
	private static NutritionPanel scaleNutrientStatementPanelToNutritionPanel(NutrientStatementPanelHeader nutrientStatementPanelHeader) {

		// There's only one panel for scale items.
		NutritionPanel nutritionPanel = NutritionPanel.of()
				.setServingsPerContainer(VARIES_SERVING_SIZE.equals(nutrientStatementPanelHeader.getServingsPerContainer())
						? "Varies" : nutrientStatementPanelHeader.getServingsPerContainer())
				.setServingSize(extractServingSize(nutrientStatementPanelHeader))
				.setPanelId(1L)
				.setPanelType("N2016");

		nutrientStatementPanelHeader.getNutrientPanelColumnHeaders()
				.stream()
				.map(ScaleFdaPanelService::scaleNutrientPanelColumnToNutritionPanelColumn).forEach(nutritionPanel.getColumns()::add);
		nutrientStatementPanelHeader.getNutrientPanelColumnHeaders().sort(Comparator.comparing(n -> n.getKey().getNutrientPanelHeaderId()));

		return nutritionPanel;
	}

	/**
	 * Translates a NLEA2016 scale column to a column for a nutrition panel.
	 *
	 * @param nutrientPanelColumnHeader The NLEA2016 scale column to translate.
	 * @return The translated column. This will include all detail.
	 */
	private static NutritionPanelColumn scaleNutrientPanelColumnToNutritionPanelColumn(NutrientPanelColumnHeader nutrientPanelColumnHeader) {

		// The column header information.
		NutritionPanelColumn nutritionPanelColumn = NutritionPanelColumn.of()
				.setId(nutrientPanelColumnHeader.getKey().getNutrientPanelColumnId());

		if (Objects.nonNull(nutrientPanelColumnHeader.getCaloriesQuantity())) {

			Long calories = nutrientPanelColumnHeader.getCaloriesQuantity().longValue();
			nutritionPanelColumn.setCalories(calories);

			// Because of legacy ways of looking at it, we need to add calories as a detail column.
			nutritionPanelColumn.getDetail().add(NutritionDetail.of()
					.setSequence(0L)
					.setDescription("Calories")
					.setId(0L)
					.setValue(calories.toString()));
		}

		// Add the column detail information.
		nutrientPanelColumnHeader.getNutrientPanelDetails()
				.stream()
				.map(ScaleFdaPanelService::scaleNutrientPanelDetailToNutritionDetail).forEach(nutritionPanelColumn.getDetail()::add);

		// Sort the nutrition detail by sequence.
		nutritionPanelColumn.getDetail().sort(Comparator.comparing(NutritionDetail::getSequence));

		return nutritionPanelColumn;
	}

	/**
	 * Translates a NLEA2106 scale detail record to a detail object.
	 *
	 * @param nutrientPanelDetail The NLEA2016 scale detail record.
	 * @return The formatted detail.
	 */
	private static NutritionDetail scaleNutrientPanelDetailToNutritionDetail(NutrientPanelDetail nutrientPanelDetail) {

		NutritionDetail nutritionDetail =
				NutritionDetail.of().setId(nutrientPanelDetail.getKey().getNutrientId())
						.setSequence(nutrientPanelDetail.getNutrient().getNutrientDisplaySequenceNumber())
						.setValue(nutrientQuantityToFormattedNutrientQuantity(nutrientPanelDetail))
						.setDescription(nutrientPanelDetail.getNutrient().getNutrientDescription());

		NutritionUtils.rdaToFormattedRda(nutrientPanelDetail.getNutrient().getRecommendedDailyAmount(),
				nutrientPanelDetail.getPercentDailyValue()).ifPresent(nutritionDetail::setPercentDailyValue);

		return nutritionDetail;
	}

	/**
	 * Generates the formatted nutrient quantity for a nutrient detail record.
	 *
	 * @param nutrientPanelDetail The detail record.
	 * @return The formatted nutrient detail information. May be null.
	 */
	private static String nutrientQuantityToFormattedNutrientQuantity(NutrientPanelDetail nutrientPanelDetail) {

		if (Objects.isNull(nutrientPanelDetail.getNutrientQuantity())) {
			return null;
		}

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return String.format("%s%s", decimalFormat.format(nutrientPanelDetail.getNutrientQuantity()),
				nutrientPanelDetail.getNutrient().getUnitOfMeasure().getUomDisplayName());
	}

	/**
	 * Generates the serving size for a NLEA2016 scale panel.
	 *
	 * @param nutrientStatementPanelHeader The NLEA2016 scale panel to extract the serving size from.
	 * @return The serving size formatted for the panel.
	 */
	private static String extractServingSize(NutrientStatementPanelHeader nutrientStatementPanelHeader) {

		return String.format("%s %s (%s%s)", NutritionUtils.servingSizeToText(new BigDecimal(nutrientStatementPanelHeader.getImperialServingSize())),
				nutrientStatementPanelHeader.getImperialUnitOfMeasure().getUomDisplayName(),
				nutrientStatementPanelHeader.getMetricServingSize(),
				nutrientStatementPanelHeader.getMetricUnitOfMeasure().getUomDisplayName());
	}
}
