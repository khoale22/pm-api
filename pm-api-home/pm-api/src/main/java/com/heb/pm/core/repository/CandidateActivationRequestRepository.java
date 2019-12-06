/*
 * CandidateActivationRequestRepository
 *
 *  Copyright (c) 2019 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.CandidateActivationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for candidate activation requests.
 *
 * @author a786878
 * @since 1.0.0
 */
@Repository
public interface CandidateActivationRequestRepository extends JpaRepository<CandidateActivationRequest, String> {
}
