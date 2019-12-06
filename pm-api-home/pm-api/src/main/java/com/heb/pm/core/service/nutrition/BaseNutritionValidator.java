/*
 *  BaseNutritionValidator.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */
package com.heb.pm.core.service.nutrition;


import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.core.model.updates.nutrition.NutrientAmount;
import com.heb.pm.core.model.updates.nutrition.NutrientPanelColumn;
import com.heb.pm.core.model.updates.nutrition.NutrientStatement;
import com.heb.pm.util.ListUtils;
import com.heb.pm.util.UpcUtils;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.heb.pm.util.ValidatorUtils.validateFieldBetween;
import static com.heb.pm.util.ValidatorUtils.validateFieldExists;

/**
 * Provides functions to make sure the nutrient statement data has a valid set of parameters.
 *
 * @param <T> The type of Unit of Measure this nutrition statement uses.
 * @author vn70529
 * @since 1.9.0
 */
/* default */ abstract class BaseNutritionValidator<T> {

    private static final int MAX_NUTRIENT_SCALE = 1;

    private final transient Function<String, List<? extends T>> uomProducer;

    /**
     * Constructs a new BaseNutritionValidator.
     *
     * @param uomProducer The function to call to generate a unit of measure.
     */
    public BaseNutritionValidator(Function<String, List<? extends T>> uomProducer) {
        this.uomProducer = uomProducer;
    }

    /**
     * Performs the validation. If there are errors, this method will throw an exception.
     *
     * @param upc The UPC the NutrientStatement should be tied to.
     * @param nutrientStatementNumber The ID of the nutrient statement. This should be equal to the "PLU" portion of the UPC.
     * @param nutrientStatement The NutrientStatement to validate.
     */
    public void validate(Long upc, Long nutrientStatementNumber, NutrientStatement nutrientStatement) {

        List<String> errors = new LinkedList<>();

        validateFieldExists(nutrientStatement::getUserId, "User ID is required").ifPresent(errors::add);

        if (NutritionUtils.isReservedNutrientStatementNumber(nutrientStatementNumber)) {
            errors.add(String.format("%d is a reserved statement number and cannot be modified.", nutrientStatementNumber));
        }

        validateUpc(upc).ifPresent(errors::add);

        validateFieldExists(nutrientStatement::getSourceSystemId, "Source system is required.").ifPresent(errors::add);

        errors.addAll(validateServingSize(nutrientStatement));

        validateServingsPerContainer(nutrientStatement.getServingsPerContainer()).ifPresent(errors::add);

        validateServingSizeUnitsOfMeasure(nutrientStatement.getImperialServingSize().getUom(),
                nutrientStatement.getMetricServingSize().getUom()).ifPresent(errors::add);

        errors.addAll(this.validateNutrientDetails(nutrientStatement.getColumns()));

        if (!errors.isEmpty()) {
            throw new ValidationException("Unable to validate scale nutrition update request.", errors);
        }
    }

    /**
     * Override this method to provide validation of the specific details of a nutrient statement. There is a helper method,
     * validateNutrient, below that can be used to check the specifics of a given nutrient, but it is up to classes
     * extending this one to determine what the columns look like, which nutrients are required, and how to get which
     * values are needed for a particular nutrient.
     *
     * @param nutrientPanelColumns The list of columns to validate.
     * @return A list of errors. The list should never be null, but, if there are no errors, it should be empty.
     */
    protected abstract List<String> validateNutrientDetails(List<NutrientPanelColumn> nutrientPanelColumns);

    /**
     * Determines if a supplied unit of measure is part of the imperial system.
     *
     * @param imperialMeasure The unit of measure to check.
     * @return True if the unit of measure is part of the imperial system and false otherwise.
     */
    protected abstract boolean imperialIsImperial(T imperialMeasure);

    /**
     * Determines if a supplied unit of measure is part of the metric system.
     *
     * @param metricMeasure The unit of measure to check.
     * @return True if the unit of measure is part of the metric system and false otherwise.
     */
    protected abstract boolean metricIsMetric(T metricMeasure);

    /**
     * Checks to units of measure to make sure they are of the same form (solid, liquid, etc.).
     *
     * @param imperialMeasure The imperial measure to check.
     * @param metricMeasure The metric measure to check.
     * @return True if they are of the same form and false otherwise.
     */
    protected abstract boolean formTypesMatch(T imperialMeasure, T metricMeasure);

    /**
     * Validates the serving size units of measure.
     *
     * @param imperialUnitOfMeasure The imperial unit of measure.
     * @param metricUnitOfMeasure The metric unit of measure.
     * @return An error message if validation fails and empty otherwise.
     */
    private Optional<String> validateServingSizeUnitsOfMeasure(String imperialUnitOfMeasure, String metricUnitOfMeasure) {

        try {
            // The types of the different measure have to match the type (imperial with imperial and metric with metric).
            // They also have to match form (solid, liquid, etc.).

            T imperialMeasure = this.getUomCodeByUomDes(imperialUnitOfMeasure);
            if (!this.imperialIsImperial(imperialMeasure)) {
                return Optional.of("Imperial serving size unit of measure is of incorrect type.");
            }

            T metricMeasure = this.getUomCodeByUomDes(metricUnitOfMeasure);
            if (!this.metricIsMetric(metricMeasure)) {
                return Optional.of("Metric serving size unit of measure is of incorrect type.");
            }

            if (!this.formTypesMatch(imperialMeasure, metricMeasure)) {
                return Optional.of("Metric and imperial serving sizes are not of the same form.");
            }
        } catch (Exception e) {
            return Optional.of(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    /**
     * Checks a nutrient to see if has the values properly set.
     *
     * @param nutrientAmount The nutrient values to check.
     * @param nutrientName The name of the nutrient. This is used to provide clear error messages.
     * @param setByPdv Should this nutrient be set by PDV? True if so and false otherwise.
     * @param includeRda Should this nutrient have a PDV provided? True if so and false otherwise.
     * @return A list of error messages. This list will not be null, but may be empty if there are no errors.
     */
    protected static List<String> validateNutrient(NutrientAmount nutrientAmount, String nutrientName, boolean setByPdv, boolean includeRda) {

        // The programs are designed to only pass in required nutrients, so, if it's not present, that's an error.
        if (Objects.isNull(nutrientAmount)) {
            return ListUtils.of(String.format("%s is a required nutrient.", nutrientName));
        }

        List<String> errors = new LinkedList<>();
        try {

            // If this field is flipped, then the PDV needs to be there.
            if (setByPdv) {
                validateFieldExists(nutrientAmount::getDailyPercent, String.format("Percent daily amount is required for %s.", nutrientName)).ifPresent(errors::add);
                return errors;
            }

            // If the RDA is not zero, then a PDV is required.
            if (includeRda) {
                validateFieldExists(nutrientAmount::getDailyPercent, String.format("Percent daily amount is required for %s.", nutrientName)).ifPresent(errors::add);
                if (nutrientAmount.getDailyPercent().scale() >= MAX_NUTRIENT_SCALE) {
                    errors.add(String.format("%s has a percent daily value has a fractional part.", nutrientName));
                }
            }

            // If there's no value, then that's an error.
            validateFieldExists(nutrientAmount::getAmount, String.format("Nutrient amount is required for %s.", nutrientName)).ifPresent(errors::add);
            if (nutrientAmount.getAmount().scale() > MAX_NUTRIENT_SCALE) {
                errors.add(String.format("%s has a scale greater than 1.", nutrientName));
            }
            validateFieldBetween(nutrientAmount::getAmount, String.format("%s is not between 0 an 9999.", nutrientName), BigDecimal.ZERO, new BigDecimal("9999.0"))
                    .ifPresent(errors::add);
        } catch (Exception e) {
            errors.add(e.getLocalizedMessage());
        }
        return errors;
    }

    /**
     * Performs a basic validation of servings per container.
     *
     * @param servingsPerContainer The servings per container to validate.
     * @return An error message if invalid and empty otherwise.
     */
    private Optional<String> validateServingsPerContainer(String servingsPerContainer) {

        if (Objects.isNull(servingsPerContainer)) {
            return Optional.of("Servings per container is required.");
        }

        try {
            return validateFieldBetween(() -> Long.valueOf(servingsPerContainer), "Servings per container must be between 1 and 999.", 1L, 999L);
        } catch (NumberFormatException e) {
            return Optional.of("Servings per container must be a number");
        }
    }

    /**
     * Validates the UPC.
     *
     * @param upc the UPC need to validate.
     * @return An optional error string. The optional error string will not be empty, but can be empty.
     */
    private Optional<String> validateUpc(Long upc) {

        if (Objects.isNull(upc)) {
            return Optional.of("UPC is required.");
        }

        if (!UpcUtils.isPreDigitTwo(upc)) {
            return Optional.of("Only pre-digit 2 UPCs are supported.");
        }

        return Optional.empty();
    }

    /**
     * Does a basic validation of the serving sizes.
     *
     * @param nutrientStatement The NutrientStatement containing serving sizes to validate.
     * @return A list of error messages. The list will always exist, but be empty if there are no errors.
     */
    private List<String> validateServingSize(NutrientStatement nutrientStatement) {

        List<String> errors = new LinkedList<>();

        validateFieldExists(nutrientStatement::getImperialServingSize, "Imperial serving size is required.").ifPresent(errors::add);
        validateFieldExists(nutrientStatement::getMetricServingSize, "Metric serving size is required.").ifPresent(errors::add);

        if (!errors.isEmpty()) {
            return errors;
        }

        validateFieldExists(nutrientStatement.getImperialServingSize()::getQuantity, "Imperial serving size is required.").ifPresent(errors::add);
        validateFieldExists(nutrientStatement.getImperialServingSize()::getUom, "Imperial serving size is required.").ifPresent(errors::add);
        validateFieldExists(nutrientStatement.getMetricServingSize()::getQuantity, "Metric serving size is required.").ifPresent(errors::add);
        validateFieldExists(nutrientStatement.getMetricServingSize()::getUom, "Metric serving size is required.").ifPresent(errors::add);

        return errors;
    }


    private T getUomCodeByUomDes(String uomDescription) {

        return NutritionUtils.getUomCodeByUomDes(uomDescription, this.uomProducer);
    }
}
