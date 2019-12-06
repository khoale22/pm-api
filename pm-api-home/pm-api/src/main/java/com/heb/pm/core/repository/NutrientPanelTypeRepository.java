package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.NutrientPanelType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for NutrientPanelTypes.
 *
 * @author d116773
 * @since 1.9.0
 */
public interface NutrientPanelTypeRepository extends JpaRepository<NutrientPanelType, String> {
}
