/*
 *  Nutrition1990Validator.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */
package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.model.updates.nutrition.NutrientAmount;
import com.heb.pm.core.model.updates.nutrition.NutrientPanelColumn;
import com.heb.pm.core.repository.ScaleNutrientRepository;
import com.heb.pm.core.repository.ScaleUnitOfMeasureRepository;
import com.heb.pm.dao.core.entity.NutrientPanelColumnHeader;
import com.heb.pm.dao.core.entity.ScaleNutrient;
import com.heb.pm.dao.core.entity.ScaleUnitOfMeasure;
import com.heb.pm.dao.core.entity.codes.ScaleUomType;
import com.heb.pm.util.ListUtils;

import java.util.*;

/**
 * Provides functions to make sure the nutrient statement data has a valid set of parameters.
 *
 * @author nv70529
 * @since 1.9.0
 */
/* default */ class Nutrition1990Validator extends BaseNutritionValidator<ScaleUnitOfMeasure> {

	private static final String SET_VALUE_BY_RDA = "Y";
	private static final int SCALE_1990_COLUMN_COUNT = 1;

	private final transient ScaleNutrientRepository scaleNutrientRepository;


	/**
	 * Constructs a new Nutrition1990Validator.
	 *
	 * @param scaleUnitOfMeasureRepository The repository to pull units of measure from.
	 * @param scaleNutrientRepository The repository to pull nutrients from.
	 */
	public Nutrition1990Validator(ScaleUnitOfMeasureRepository scaleUnitOfMeasureRepository, ScaleNutrientRepository scaleNutrientRepository) {

		super(scaleUnitOfMeasureRepository::findAllByDescription);
		this.scaleNutrientRepository = scaleNutrientRepository;
	}

	/**
	 * Validates the column and detail level for a 1990 nutrient statement.
	 *
	 * @param nutrientPanelColumns This list of columns to validate.
	 * @return A list of errors. The list will not be null, but may be empty if there are no errors.
	 */
	@Override
	protected List<String> validateNutrientDetails(List<NutrientPanelColumn> nutrientPanelColumns) {

		if (Objects.isNull(nutrientPanelColumns) || nutrientPanelColumns.isEmpty()) {
			return ListUtils.of("For NLEA 1990 nutrition, one column is required.");
		}

		// Only one column is allowed.
		if (nutrientPanelColumns.size() != SCALE_1990_COLUMN_COUNT) {
			return ListUtils.of("For NLEA 1990 nutrition, only one column is allowed.");
		}

		NutrientPanelColumn nutrientPanelColumn = nutrientPanelColumns.get(0);

		List<String> errors = new LinkedList<>();

		// It has to have an ID of 1.
		if (!nutrientPanelColumn.getColumnId().equals(NutrientPanelColumnHeader.SINGLE_COLUMN_HEADER_ID)) {
			errors.add(String.format("Nutrient panel column must have an ID of %d.", NutrientPanelColumnHeader.SINGLE_COLUMN_HEADER_ID));
		}

		// Check all the required nutrients.
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getCalories(), "calories"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getCaloriesFromFat(), "caloriesFromFat"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getTotalFat(), "totalFat"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getSaturatedFat(), "saturatedFat"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getTransFat(), "transFat"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getCholesterol(), "cholesterol"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getSodium(), "sodium"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getTotalCarbohydrates(), "totalCarbohydrates"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getDietaryFiber(), "dietaryFiber"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getSugars(), "sugars"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getSugarAlcohol(), "sugarAlcohol"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getProtein(), "protein"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getVitaminA(), "vitaminA"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getVitaminC(), "vitaminC"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getCalcium(), "calcium"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getIron(), "iron"));
		errors.addAll(this.validateNutrient(nutrientPanelColumn.getPotassium(), "potassium"));

		return errors;
	}

	@Override
	protected boolean imperialIsImperial(ScaleUnitOfMeasure imperialMeasure) {
		return imperialMeasure.getSystemOfMeasure().equals(ScaleUomType.IMPERIAL);
	}

	@Override
	protected boolean metricIsMetric(ScaleUnitOfMeasure metricMeasure) {
		return metricMeasure.getSystemOfMeasure().equals(ScaleUomType.METRIC);
	}

	@Override
	protected boolean formTypesMatch(ScaleUnitOfMeasure imperialMeasure, ScaleUnitOfMeasure metricMeasure) {
		return Objects.equals(imperialMeasure.getForm(), metricMeasure.getForm());
	}

	/**
	 * Validates an individual nutrient.
	 *
	 * @param nutrientAmount The nutrient information passed in by the user.
	 * @param nutrientName The name of the nutrient being validated.
	 * @return A list of errors. The list will not be null, but may be empty if there are no errors.
	 */
	private List<String> validateNutrient(NutrientAmount nutrientAmount, String nutrientName) {

		NutrientMap val = NutrientMap.of(nutrientName);
		ScaleNutrient scaleNutrient = this.scaleNutrientRepository.getOne(val.getScaleNutrientId());

		// There's a flag in the table that says the nutrient should be set by PDV rather than value.
		boolean setByRda = SET_VALUE_BY_RDA.equals(scaleNutrient.getSetByPercentDailyValue());

		// If an RDA is supplied, then the user should set the PDV.
		boolean includeRda = !NutritionUtils.isNoRda(scaleNutrient.getRecommendedDailyValue());

		// Delegate validating the actual nutrients to the base class.
		return BaseNutritionValidator.validateNutrient(nutrientAmount, nutrientName, setByRda, includeRda);
	}
}
