/*
 *  Nutrition2016PersistenceService.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */

package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.model.updates.nutrition.NutrientAmount;
import com.heb.pm.core.model.updates.nutrition.NutrientPanelColumn;
import com.heb.pm.core.model.updates.nutrition.NutrientStatement;
import com.heb.pm.core.repository.*;
import com.heb.pm.dao.core.entity.*;
import com.heb.pm.dao.core.entity.codes.NutrientQuantityType;
import com.heb.pm.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * Service to handle pulling nutrition 2016.
 *
 * @author vn70529
 * @since 1.9.0
 */
@Service
public class Nutrition2016PersistenceService implements NutritionPersistenceService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Nutrition2016PersistenceService.class);

    @Autowired
    private transient NutrientStatementPanelHeaderRepository nutrientStatementPanelHeaderRepository;

    @Autowired
    private transient NutritionUnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    private transient NutrientRepository nutrientRepository;

    @Autowired
    private transient ScaleUpcRepository scaleUpcRepository;

    @Autowired
    private transient NutrientPanelTypeRepository nutrientPanelTypeRepository;

    private transient Nutrition2016Validator validator;

    /**
     * Save the nutrient statement information into the database.
     *
     * @param upc The UPC to tie the nutrient statement to.
     * @param nutrientStatement The nutrient statement object need to save.
     * @return A response with the ID of the statement, and, possibly, the UPC it is meant for and if it is tied to the UPC.
     */
    @Transactional
    @Override
    public NutritionUpdateResponse save(Long upc, NutrientStatement nutrientStatement) {

        Long nutrientStatementNumber = NutritionUtils.generateNutrientStatementIdFromUpc(upc);

        this.validator.validate(upc, nutrientStatementNumber, nutrientStatement);

        // Load nutrient statement panel header from database by upc, source system and active status.
        List<NutrientStatementPanelHeader> oldNutrientStatementPanelHeaders =
                this.nutrientStatementPanelHeaderRepository.findAllBySourceSystemIdAndSourceSystemReferenceIdAndActiveSwitch(
                        SourceSystem.SCALE_MANAGEMENT_SOURCE_SYSTEM, nutrientStatementNumber.toString(), Boolean.TRUE);

        // Grab the ScaleUpc for this UPC if it's present.
        Optional<ScaleUpc> scaleUpc = this.scaleUpcRepository.findById(upc);
        Long upcToUse = null;
        if (scaleUpc.isPresent()) {
            upcToUse = scaleUpc.get().getUpc();
        }

        if (!oldNutrientStatementPanelHeaders.isEmpty()) {

            //When saving, if there is an existing panel, pull it from the DB and set its active switch to N.
            oldNutrientStatementPanelHeaders.forEach(o -> o.setActiveSwitch(Boolean.FALSE));
            this.nutrientStatementPanelHeaderRepository.saveAll(oldNutrientStatementPanelHeaders);
        }

        // Get a new panel ID to use.
        long newPanelId = generateNutrientStatementId();

        // Generate the new statement and save it.
        NutrientStatementPanelHeader nutrientStatementPanelHeader = this.generateNewNutrientStatement(nutrientStatement, upcToUse, nutrientStatementNumber, newPanelId);
        this.nutrientStatementPanelHeaderRepository.save(nutrientStatementPanelHeader);

        // Build the response.
        NutritionUpdateResponse nutritionUpdateResponse = new NutritionUpdateResponse().setNutrientStatementId(nutrientStatementNumber);

        // See if this UPC exists and if it's tied to the nutrient statement we're updating.
       scaleUpc.ifPresent(u ->
                nutritionUpdateResponse.setUpc(u.getUpc())
                        .setNutritionIsTiedToUpc(nutrientStatementNumber.equals(u.getNlea1990NutritionStatementId()))
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

        this.validator = new Nutrition2016Validator(this.unitOfMeasureRepository, this.nutrientRepository);
    }

    /**
     * Generates the full NutrientStatementPanelHeader to save.
     *
     * @param nutrientStatement The NutrientStatement provided by the user.
     * @param upc The UPC to tie the statement to. This should be null if the UPC is not in prod_scn_codes.
     * @param nutrientStatementId The legacy ID of the nutrient statement (the PLU part of the UPC).
     * @param nutrientPanelHeaderId The ID of the new panel (the sequential one in the table).
     * @return The full NutrientStatementPanelHeader.
     */
    private NutrientStatementPanelHeader generateNewNutrientStatement (NutrientStatement nutrientStatement, Long upc, Long nutrientStatementId, Long nutrientPanelHeaderId) {

        NutrientStatementPanelHeader nutrientStatementPanelHeader = NutrientStatementPanelHeader.of(nutrientPanelHeaderId)
                .setUpc(upc)
                .setEffectiveDate(Instant.now())
                .setActiveSwitch(Boolean.TRUE)
                .setImperialUnitOfMeasure(this.getUomCodeByUomDes(nutrientStatement.getImperialServingSize().getUom()))
                .setImperialServingSize(nutrientStatement.getImperialServingSize().getQuantity().toString())
                .setMetricUnitOfMeasure(this.getUomCodeByUomDes(nutrientStatement.getMetricServingSize().getUom()))
                .setMetricServingSize(nutrientStatement.getMetricServingSize().getQuantity().toString())
                .setServingsPerContainer(nutrientStatement.getServingsPerContainer())
                .setSourceSystemId(SourceSystem.SCALE_MANAGEMENT_SOURCE_SYSTEM)
                .setSourceSystemReferenceId(nutrientStatementId.toString())
                .setNutrientPanelType(this.nutrientPanelTypeRepository.getOne(nutrientStatement.getPanelType()))
                .setPublished(Boolean.FALSE)
                .setSourceSystemLastUpdateTime(Instant.now())
                .setPanelNumber(1L);

        this.applyNutrientStatementColumns(nutrientStatementPanelHeader, nutrientStatement);

        return nutrientStatementPanelHeader;
    }

    /**
     * Adds the column and detail level data to the NutrientStatementPanelHeader.
     *
     * @param nutrientStatementPanelHeader The NutrientStatementPanelHeader to add the column and detail data to. This object will be modified.
     * @param nutrientStatement The NutrientStatement provided by the user.
     */
    private void applyNutrientStatementColumns(NutrientStatementPanelHeader nutrientStatementPanelHeader, NutrientStatement nutrientStatement) {

        List<Nutrient> allNutrients = this.nutrientRepository.findAll();

        for (NutrientPanelColumn nutrientPanelColumn : nutrientStatement.getColumns()) {

            // Create a new column.
            NutrientPanelColumnHeader nutrientPanelColumnHeader = NutrientPanelColumnHeader.of(nutrientStatementPanelHeader, nutrientPanelColumn.getColumnId())
                    .setCaloriesQuantity(nutrientPanelColumn.getCalories().getAmount());

            // Add a record for all the nutrients. These will be filled out in the next call.
            for (Nutrient n : allNutrients) {

                nutrientPanelColumnHeader.getNutrientPanelDetails().add(NutrientPanelDetail.of(nutrientPanelColumnHeader, n));
            }

            // Delegate populating the detail to this function.
            this.applyNutrientColumnDetails(nutrientPanelColumnHeader, nutrientPanelColumn);

            // Add the new column to the header.
            nutrientStatementPanelHeader.getNutrientPanelColumnHeaders().add(nutrientPanelColumnHeader);
        }
    }

    /**
     * Overwrites the detail data of a NutrientPanelColumnHeader.
     *
     * @param nutrientPanelColumnHeader The NutrientPanelColumnHeader to update. This object will be modified.
     * @param nutrientPanelColumn The NutrientStatement provided by the user.
     */
    private void applyNutrientColumnDetails(NutrientPanelColumnHeader nutrientPanelColumnHeader, NutrientPanelColumn nutrientPanelColumn) {

        for (NutrientPanelDetail detail : nutrientPanelColumnHeader.getNutrientPanelDetails()) {

            NutrientMap nutrientId = NutrientMap.ofId(detail.getNutrient().getNutrientId());

            try {
                NutrientAmount nutrientAmount = ObjectUtils.extractField(nutrientPanelColumn, NutrientAmount.class, nutrientId.getAttributeName());
                this.applyNutrientDetail(detail, nutrientAmount);

                // We don't yet support "less than" on the scale labels.
                detail.setLessThan(Boolean.FALSE);
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
    private void applyNutrientDetail(NutrientPanelDetail detail, NutrientAmount nutrientAmount) {

        if (NutrientQuantityType.PERCENT_DAILY_VALUE.equals(detail.getNutrient().getPercentDailyValueOrWhole())) {

            // If this object is set by PDV, then set the PDV, but make the value as zero.
            detail.setPercentDailyValue(nutrientAmount.getDailyPercent());
        } else {

            // If set by amount, then set the amount. The scale of the value has already been validated.
            detail.setNutrientQuantity(nutrientAmount.getAmount());

            // If the nutrient is configured to have an RDA, then set the value. Otherwise,set it to zero.
            // The scale of the PDV has already been validated.
            if (!NutritionUtils.isNoRda(detail.getNutrient().getRecommendedDailyAmount())) {

                detail.setPercentDailyValue(nutrientAmount.getDailyPercent());
            }
        }
    }

    /**
     * Get uom code for nutrition from pd_unit_measure table based on input param is uom description.
     *
     * @param uomDescription The uom description.
     * @return The uom code if it's existing in the pd_unit_measure by description, otherwise return null.
     */
    private NutritionUnitOfMeasure getUomCodeByUomDes(String uomDescription) {

        return NutritionUtils.getUomCodeByUomDes(uomDescription, this.unitOfMeasureRepository::findAllByUomDisplayName);
    }

    /**
     * Generate the new nutrient Statement id.
     *
     * @return the nutrient Statement id by sequence.
     */
    private long generateNutrientStatementId() {
        return this.nutrientStatementPanelHeaderRepository.getLatestId() + 1;
    }
}
