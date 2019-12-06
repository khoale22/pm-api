/*
 *  ScaleUnitOfMeasureRepository.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ScaleUnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for ScaleUnitOfMeasure entity.
 *
 * @author vn73545
 * @since 1.9.0
 */
public interface ScaleUnitOfMeasureRepository extends JpaRepository<ScaleUnitOfMeasure, Long> {

    /**
     * Find all by description.
     *
     * @param description The description.
     * @return The list of ScaleUnitOfMeasure.
     */
    List<ScaleUnitOfMeasure> findAllByDescription(String description);
}
