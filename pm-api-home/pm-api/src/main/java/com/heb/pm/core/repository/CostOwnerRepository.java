package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.CostOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for CostOwner entities.
 *
 * @author d116773
 * @since 1.1.0
 */
@Repository
public interface CostOwnerRepository extends JpaRepository<CostOwner, Long> {
}
