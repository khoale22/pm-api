package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ScaleNutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for ScaleNutrients.
 *
 * @author d116773
 * @since 1.9.0
 */
public interface ScaleNutrientRepository extends JpaRepository<ScaleNutrient, Long> {

	/**
	 * Finds all ScaleNutrients with a sequence number greater than a supplied number. Passing 0 will return
	 * all required nutrients (the only real use for this function).
	 *
	 * @param sequenceNumber Pass in 0.
	 * @return A list of required scale nutrients.
	 */
	List<ScaleNutrient> findAllBySequenceGreaterThan(Long sequenceNumber);
}
