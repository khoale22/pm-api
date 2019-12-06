/*
 *  Nutrition2016Validator.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */
package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.model.updates.nutrition.NutrientAmount;
import com.heb.pm.core.model.updates.nutrition.NutrientPanelColumn;
import com.heb.pm.core.repository.NutrientRepository;
import com.heb.pm.core.repository.NutritionUnitOfMeasureRepository;
import com.heb.pm.dao.core.entity.Nutrient;
import com.heb.pm.dao.core.entity.NutrientPanelColumnHeader;
import com.heb.pm.dao.core.entity.NutritionUnitOfMeasure;
import com.heb.pm.dao.core.entity.codes.MetricOrImperialUomType;
import com.heb.pm.dao.core.entity.codes.NutrientQuantityType;

import com.heb.pm.util.ListUtils;
import com.heb.pm.util.ValidatorUtils;

import java.util.*;

/**
 * Provides functions to make sure the nutrient statement data has a valid set of parameters.
 *
 * @author vn70529
 * @since 1.9.0
 */
/* default */ class Nutrition2016Validator  extends BaseNutritionValidator<NutritionUnitOfMeasure> {

    private final transient NutrientRepository nutrientRepository;

    /**
     * Constructs a new Nutrition2016Validator.
     *
     * @param nutritionUnitOfMeasureRepository The repository to look up scale units of measure in.
     * @param nutrientRepository The repository to look up scale nutrients in.
     */
    public Nutrition2016Validator(NutritionUnitOfMeasureRepository nutritionUnitOfMeasureRepository, NutrientRepository nutrientRepository) {
        super(nutritionUnitOfMeasureRepository::findAllByUomDisplayName);
        this.nutrientRepository = nutrientRepository;
    }

    /**
     * Validates the column and detail level for a 2016 nutrient statement.
     *
     * @param nutrientPanelColumns This list of columns to validate.
     * @return A list of errors. The list will not be null, but may be empty if there are no errors.
     */
    @Override
    protected List<String> validateNutrientDetails(List<NutrientPanelColumn> nutrientPanelColumns) {

        if (Objects.isNull(nutrientPanelColumns) || nutrientPanelColumns.isEmpty()) {
            return ListUtils.of("For NLEA 2016 nutrition, at least one column is required.");
        }

        // Check to make sure there are either 1 or two columns. Later checks will assume this is done.
        if (nutrientPanelColumns.size() != 1 && nutrientPanelColumns.size() != 2) {
            return ListUtils.of("For NLEA 2106 nutrition, you may only provide one or two columns.");
        }

        List<String> errors = new LinkedList<>();

        int columnOneCount = 0;
        int columnTwoCount = 0;

        for (NutrientPanelColumn nutrientPanelColumn : nutrientPanelColumns) {

            // This bit is to make sure one of the columns is set to 1 and the other is set to two and you have,
            // at max, one of each.
            if (NutrientPanelColumnHeader.SINGLE_COLUMN_HEADER_ID.equals(nutrientPanelColumn.getColumnId())) {
                columnOneCount++;
            }
            if (NutrientPanelColumnHeader.DUAL_COLUMN_HEADER_ID.equals(nutrientPanelColumn.getColumnId())) {
                columnTwoCount++;
            }
            ValidatorUtils.validateFieldInList(nutrientPanelColumn::getColumnId, "Panel ID can only be a 1 or a 2.",
                    NutrientPanelColumnHeader.SINGLE_COLUMN_HEADER_ID, NutrientPanelColumnHeader.DUAL_COLUMN_HEADER_ID).ifPresent(errors::add);

            // Calories is not a nutrient in the new panel.
            ValidatorUtils.validateFieldExists(nutrientPanelColumn::getCalories, "Nutrient amount is requied for calories.").ifPresent(errors::add);

            // Check all the required nutrients.
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getSaturatedFat(), "saturatedFat"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getTransFat(), "transFat"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getCholesterol(), "cholesterol"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getSodium(), "sodium"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getTotalCarbohydrates(), "totalCarbohydrates"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getDietaryFiber(), "dietaryFiber"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getTotalSugars(), "totalSugars"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getSugarAlcohol(), "sugarAlcohol"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getIncludesAddedSugars(), "includesAddedSugars"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getProtein(), "protein"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getVitaminD(), "vitaminD"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getCalcium(), "calcium"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getIron(), "iron"));
            errors.addAll(this.validateNutrient(nutrientPanelColumn.getPotassium(), "potassium"));
        }

        // Use the count of each column ID from above to make sure the columns have the right IDs.
        this.validateColumnOneCount(columnOneCount).ifPresent(errors::add);
        this.validateColumnTwoCount(columnTwoCount, nutrientPanelColumns.size()).ifPresent(errors::add);

        return errors;
    }

    @Override
    protected boolean imperialIsImperial(NutritionUnitOfMeasure imperialMeasure) {
        return imperialMeasure.getMetricOrImperialUomSwitch().equals(MetricOrImperialUomType.IMPERIAL);
    }

    @Override
    protected boolean metricIsMetric(NutritionUnitOfMeasure metricMeasure) {
        return metricMeasure.getMetricOrImperialUomSwitch().equals(MetricOrImperialUomType.METRIC);
    }

    @Override
    protected boolean formTypesMatch(NutritionUnitOfMeasure imperialMeasure, NutritionUnitOfMeasure metricMeasure) {
        return Objects.equals(imperialMeasure.getFormCode(), metricMeasure.getFormCode());
    }

    /**
     * Check to make sure there's the right numer of columns with the ID of 1.
     *
     * @param columnOneCount The number of columns with the ID of 1.
     * @return An error message if incorrect and empty otherwise.
     */
    private Optional<String> validateColumnOneCount(int columnOneCount) {

        Optional<String> toReturn;

        // There should be exactly 1 with an ID of 1.
        switch (columnOneCount) {
            case 0:
                toReturn = Optional.of("Panel ID 1 is required.");
                break;
            case 1:
                toReturn = Optional.empty();
                break;
            default:
                toReturn = Optional.of("There can only be 1 panel with an ID of 1.");
                break;
        }
        return toReturn;
    }

    /**
     * Check to make sure the number of columns with the ID of 2 is correct.
     *
     * @param columnTwoCount The number of columns with the ID of 2.
     * @param totalColumnCount The total number of columns.
     * @return An error message if incorrect and empty otherwise.
     */
    private Optional<String> validateColumnTwoCount(int columnTwoCount, int totalColumnCount) {

        // If there's two columns, there should be 1 with a value of 2. If there is 1 column, there shold be 0.
        Optional<String> toReturn;
        switch (totalColumnCount) {
            case 1:
                toReturn = columnTwoCount != 0 ? Optional.of("Panel ID 2 should not be present when there is only one panel.") : Optional.empty();
                break;
            case 2:
                toReturn = columnTwoCount != 1 ? Optional.of("Panel ID 2 is required when there are two panels.") : Optional.empty();
                break;
            default:
                toReturn = Optional.of("Invalid panel count.");
                break;
        }
        return toReturn;
    }

    /**
     * Validates an individual nutrient.
     *
     * @param nutrientAmount The nutrient information passed in by the user.
     * @param nutrientName The name of the nutrient being validated.
     * @return A list of errors. The list will not be null, but may be empty if there are no errors.
     */
    private Collection<String> validateNutrient(NutrientAmount nutrientAmount, String nutrientName) {

        NutrientMap val = NutrientMap.of(nutrientName);
        Nutrient scaleNutrient = this.nutrientRepository.getOne(val.getId());

        // There's flag that says if a value is set by a number or PDV.
        boolean setByRda = NutrientQuantityType.PERCENT_DAILY_VALUE.equals(scaleNutrient.getPercentDailyValueOrWhole());

        // If an RDA is supplied, then the user should set the PDV.
        boolean includeRda = !NutritionUtils.isNoRda(scaleNutrient.getRecommendedDailyAmount());

        // Delegate validating the actual nutrients to the base class.
        return BaseNutritionValidator.validateNutrient(nutrientAmount, nutrientName, setByRda, includeRda);
    }
}
