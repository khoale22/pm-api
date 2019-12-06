/*
 *  ScaleNutritionStatementHeaderRepository.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ScaleNutritionStatementHeader;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for ScaleNutritionStatementHeader entity.
 *
 * @author vn73545
 * @since 1.9.0
 */
public interface ScaleNutritionStatementHeaderRepository extends JpaRepository<ScaleNutritionStatementHeader, Long> {
}
