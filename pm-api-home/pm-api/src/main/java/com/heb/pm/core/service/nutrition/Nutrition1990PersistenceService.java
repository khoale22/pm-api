/*
 *  Nutrition1990PersistenceService.java
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
import com.heb.pm.core.repository.*;
import com.heb.pm.dao.core.entity.*;
import com.heb.pm.dao.core.entity.codes.ScaleMaintenanceFunction;

import com.heb.pm.util.ListUtils;
import com.heb.pm.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Service to handle saving scale nutrition data to the legacy tables.
 *
 * @author vn70529
 * @since 1.9.0
 */
@Service
public class Nutrition1990PersistenceService implements NutritionPersistenceService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Nutrition1990PersistenceService.class);

    private static final String SET_VALUE_BY_RDA = "Y";

    @Autowired
    private transient ScaleUnitOfMeasureRepository scaleUnitOfMeasureRepository;

    @Autowired
    private transient ScaleNutritionStatementHeaderRepository scaleNutritionStatementHeaderRepository;

    @Autowired
    private transient ScaleNutrientRepository scaleNutrientRepository;

    @Autowired
    private transient ScaleUpcRepository scaleUpcRepository;

    private transient Nutrition1990Validator validator;

    /**
     * Save the nutrient statement information.
     *
     * @param upc The UPC the nutrient statement should be tied to.
     * @param nutrientStatement The nutrient statement info need to save.
     * @return
     */
    @Transactional
    @Override
    public NutritionUpdateResponse save(Long upc, NutrientStatement nutrientStatement) {

        Long nutrientStatementNumber = NutritionUtils.generateNutrientStatementIdFromUpc(upc);

        this.validator.validate(upc, nutrientStatementNumber, nutrientStatement);

        // TODO: Create a transaction.

        // Get the nutrient statement header if it exists, and create a new one if not.
        ScaleNutritionStatementHeader scaleNutritionStatementHeader = this.scaleNutritionStatementHeaderRepository.findById(nutrientStatementNumber)
                .orElseGet(() -> this.getNewStatement(nutrientStatementNumber, nutrientStatement.getUserId()));

        // If this statement is set for delete, it can't be updated.
        if (scaleNutritionStatementHeader.getStatementMaintenanceFunction().equals(ScaleMaintenanceFunction.DELETE)) {
            throw new ValidationException("This nutrient statement is marked for deletion and cannot be updated.",
                    ListUtils.of("Nutrient statement marked for deletion."));
        }

        // Copy the data from the passed in object to the one we're going to save to the DB.
        this.applyNutrientStatementHeader(scaleNutritionStatementHeader, nutrientStatement);
        this.applyNutrientStatementDetail(scaleNutritionStatementHeader, nutrientStatement.getColumns().get(0));

        // Save it all.
        this.scaleNutritionStatementHeaderRepository.save(scaleNutritionStatementHeader);

        // Build the response.
        NutritionUpdateResponse nutritionUpdateResponse = new NutritionUpdateResponse().setNutrientStatementId(nutrientStatementNumber);

        // See if this UPC exists and if it's tied to the nutrient statement we're updating.
        this.scaleUpcRepository.findById(upc).ifPresent(u ->
            nutritionUpdateResponse.setUpc(u.getUpc()).setNutritionIsTiedToUpc(nutrientStatementNumber.equals(u.getNlea1990NutritionStatementId()))
        );
        return nutritionUpdateResponse;
    }

    /**
     * Called by Spring after all the dependencies have been injected.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        this.validator = new Nutrition1990Validator(this.scaleUnitOfMeasureRepository, this.scaleNutrientRepository);
    }

    /**
     * Constructs a new ScaleNutritionStatementHeader. It will populate the list of required nutrients.
     *
     * @param nutrientStatementId The ID to give the nutrient statement.
     * @param userId The ID of the user creating the statement.
     * @return A new ScaleNutritionStatementHeader.
     */
    private ScaleNutritionStatementHeader getNewStatement(Long nutrientStatementId, String userId) {

        ScaleNutritionStatementHeader scaleNutritionStatementHeader = ScaleNutritionStatementHeader.of(nutrientStatementId)
                .setStatementMaintenanceFunction(ScaleMaintenanceFunction.ADD);

        // Find all the required nutrients and add them to the header.
        List<ScaleNutrient> allNutrients = this.scaleNutrientRepository.findAllBySequenceGreaterThan(0L);
        for (ScaleNutrient scaleNutrient : allNutrients) {

            scaleNutritionStatementHeader.getDetails().add(
                    ScaleNutritionStatementDetail.of(scaleNutritionStatementHeader, scaleNutrient)
                            .setDefaultBehaviorOverrideQuantity(BigDecimal.ZERO)
                            .setLastUpdateUserId(userId)
            );
        }

        return scaleNutritionStatementHeader;
    }

    /**
     * Overwrites the header details of a ScaleNutritionStatementHeader from a NutrientStatement.
     *
     * @param scaleNutritionStatementHeader The ScaleNutritionStatementHeader to set the details of. This object will be modified.
     * @param nutrientStatement The NutrientStatement to pull the data from.
     */
    private void applyNutrientStatementHeader(ScaleNutritionStatementHeader scaleNutritionStatementHeader, NutrientStatement nutrientStatement) {

        // TODO: Issue media master trieggers.
        // TODO: Stage a product update alert.

        scaleNutritionStatementHeader.setEffectiveDate(Instant.now())
                .setImperialUnitOfMeasureCode(this.getUomCodeByUomDes(nutrientStatement.getImperialServingSize().getUom()).getUnitOfMeasureId())
                .setMetricUnitOfMeasureCode(this.getUomCodeByUomDes(nutrientStatement.getMetricServingSize().getUom()).getUnitOfMeasureId())
                .setImperialServingSize(nutrientStatement.getImperialServingSize().getQuantity())
                .setMetricServingSize(nutrientStatement.getMetricServingSize().getQuantity())
                .setServingsPerContainer(Long.valueOf(nutrientStatement.getServingsPerContainer()))
                .setLastUpdateTime(Instant.now())
                .setLastUpdateUserId(nutrientStatement.getUserId());

        // If it wasn't already marked as an add, mark it as a change.
        if (!scaleNutritionStatementHeader.getStatementMaintenanceFunction().equals(ScaleMaintenanceFunction.ADD)) {
            scaleNutritionStatementHeader.setStatementMaintenanceFunction(ScaleMaintenanceFunction.UPDATE);
        }
    }

    /**
     * Overwrites the detail information of a ScaleNutritionStatementHeader from a NutrientPanelColumn.
     *
     * @param scaleNutritionStatementHeader The ScaleNutritionStatementHeader to overwrite. This object will be modified.
     * @param nutrientPanelColumn The NutrientPanelColumn to pull data from.
     */
    private void applyNutrientStatementDetail(ScaleNutritionStatementHeader scaleNutritionStatementHeader, NutrientPanelColumn nutrientPanelColumn) {

        for (ScaleNutritionStatementDetail detail : scaleNutritionStatementHeader.getDetails()) {

            NutrientMap nutrientId = NutrientMap.ofLegacyId(detail.getNutrient().getNutrientId());

            try {
                NutrientAmount nutrientAmount = ObjectUtils.extractField(nutrientPanelColumn, NutrientAmount.class, nutrientId.getAttributeName());
               this.applyNutrientDetail(detail, nutrientAmount);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
                throw new RuntimeException("Unable to set nutrient amount.");
            }
        }
    }

    /**
     * Overwrites a specific ScaleNutritionStatementDetail object based on the data in a NutrientAmount.
     *
     * @param detail The ScaleNutritionStatementDetail to overwrite. This object will be modified.
     * @param nutrientAmount The NutrientAmount to pull the data from.
     */
    private void applyNutrientDetail(ScaleNutritionStatementDetail detail, NutrientAmount nutrientAmount) {

        if (SET_VALUE_BY_RDA.equals(detail.getNutrient().getSetByPercentDailyValue())) {

            // If this object is set by PDV, then set the PDV, but make the value as zero.
            detail.setPercentDailyValue(nutrientAmount.getDailyPercent().longValue());
            detail.setQuantity(BigDecimal.ZERO);
        } else {

            // If set by amount, then set the amount. The scale of the value has already been validated.
            detail.setQuantity(nutrientAmount.getAmount());

            // If the nutrient is configured to have an RDA, then set the value. Otherwise,set it to zero.
            // The scale of the PDV has already been validated.
            if (NutritionUtils.isNoRda(detail.getNutrient().getRecommendedDailyValue())) {
                detail.setPercentDailyValue(0L);
            } else {
                detail.setPercentDailyValue(nutrientAmount.getDailyPercent().longValue());
            }
        }
    }

    /**
     * Get uom code for nutrition from pd_unit_measure table based on input param is uom description.
     *
     * @param uomDescription The uom description.
     * @return The uom code if it's existing in the pd_unit_measure by description, otherwise return null.
     */
    private ScaleUnitOfMeasure getUomCodeByUomDes(String uomDescription) {

        return NutritionUtils.getUomCodeByUomDes(uomDescription, this.scaleUnitOfMeasureRepository::findAllByDescription);
    }
}
