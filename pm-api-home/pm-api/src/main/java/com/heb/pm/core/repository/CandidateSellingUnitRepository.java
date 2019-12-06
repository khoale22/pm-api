/*
 * CandidateRepository
 *
 *  Copyright (c) 2018 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.CandidateSellingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for candidate product scan codes (UPC).
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface CandidateSellingUnitRepository extends JpaRepository<CandidateSellingUnit, Long> {

    /**
     *  Returns all candidate selling units by upc.
     *
     * @param upc the upc.
     * @return All candidate selling units by upc.
     */
    List<CandidateSellingUnit> findAllByUpcOrderByLastUpdatedOnDesc(Long upc);
}
