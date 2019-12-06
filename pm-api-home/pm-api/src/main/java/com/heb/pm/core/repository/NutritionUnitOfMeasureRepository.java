/*
 *  NutritionUnitOfMeasure.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.NutritionUnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for NutritionUnitOfMeasure entity.
 *
 * @author vn73545
 * @since 1.9.0
 */
public interface NutritionUnitOfMeasureRepository extends JpaRepository<NutritionUnitOfMeasure, Long> {

    /**
     * Returns all the NutritonUnitsOfMeasure by description.
     *
     * @param uomDescription The description to search for.
     * @return A list of NutritionUnitsOfMeasure with a given description.
     */
    List<NutritionUnitOfMeasure> findAllByUomDisplayName(String uomDescription);
}
