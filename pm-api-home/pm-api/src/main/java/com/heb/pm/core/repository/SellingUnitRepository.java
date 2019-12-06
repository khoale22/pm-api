/*
 * SellingUnitRepository
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.SellingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for product scan codes (UPC).
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface SellingUnitRepository extends JpaRepository<SellingUnit, Long> {
}
