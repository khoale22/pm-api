/*
 * AssociatedUpcRepository
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.AssociatedUpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to retrieve information about an associated upc.
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface AssociatedUpcRepository extends JpaRepository<AssociatedUpc, Long> {
}
