package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.NleaUpcRollout;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for NleaUpcRollout, the table with which UPCs are part of the NLEA2016 rollout.
 *
 * @author d116773
 * @since 1.9.0
 */
public interface NleaUpcRolloutRepository extends JpaRepository<NleaUpcRollout, Long> {
}
