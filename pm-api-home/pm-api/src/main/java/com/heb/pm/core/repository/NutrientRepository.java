package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Nutrients.
 *
 * @author d116773
 * @since 1.9.0
 */
public interface NutrientRepository extends JpaRepository<Nutrient, Long> {
}
