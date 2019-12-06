package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.RetailLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for upc link.
 *
 * @author s573181
 * @since 1.0.0
 */
@Repository
public interface RetailLinkRepository extends JpaRepository<RetailLink, Long> {
}
