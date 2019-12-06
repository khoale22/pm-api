package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.CandidateTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for candidate transaction tracking.
 *
 * @author d116773
 * @since 1.1.0
 */
public interface CandidateTransactionRepository extends JpaRepository<CandidateTransaction, Long> {
}
