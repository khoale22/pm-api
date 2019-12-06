package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.LocationKey;
import com.heb.pm.dao.core.entity.VendorLocation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for vendor location entities.
 *
 * @author d116773
 * @since 1.1.0
 */
public interface VendorLocationRepository extends JpaRepository<VendorLocation, LocationKey> {
}
