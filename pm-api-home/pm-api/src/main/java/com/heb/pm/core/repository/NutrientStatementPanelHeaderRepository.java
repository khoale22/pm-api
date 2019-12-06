/*
 *  NutrientStatementPanelHeaderRepository.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */
package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.NutrientStatementPanelHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for NutrientStatementPanelHeaders.
 *
 * @author d116773
 * @since 1.9.0
 */
public interface NutrientStatementPanelHeaderRepository extends JpaRepository<NutrientStatementPanelHeader, Long> {

	/**
	 * Find latest nutrient panel header id in table NutrientStatementPanelHeader .
	 *
	 * @return The latest nutrient panel header id of NutrientStatementPanelHeader, otherwise return zero, if there is no data.
	 */
	@Query("select max(nsph.nutrientPanelHeaderId) from NutrientStatementPanelHeader nsph")
	Long getLatestId();

	/**
	 * Returns the nutrient statement header records for a given nutrient statement number and source system.
	 *
	 * @param sourceSystemId The source system to look for scale information for.
	 * @param sourceSystemReferenceId The ID of the statement header in that system.
	 * @param activeSwitch What value to use for active switch. Typically, use True.
	 * @return The list of nutrient statement header records for a given nutrient statement number and source system.
	 */
	List<NutrientStatementPanelHeader> findAllBySourceSystemIdAndSourceSystemReferenceIdAndActiveSwitch(Long sourceSystemId, String sourceSystemReferenceId, Boolean activeSwitch);

}
