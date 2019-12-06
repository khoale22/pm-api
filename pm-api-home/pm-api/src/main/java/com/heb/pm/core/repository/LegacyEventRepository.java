package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.LegacyEvent;
import com.heb.pm.dao.core.entity.LegacyEventKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for testing LegacyEvents.
 *
 * @author d116773
 * @since 2.3.0
 */
public interface LegacyEventRepository extends JpaRepository<LegacyEvent, LegacyEventKey> {
}
