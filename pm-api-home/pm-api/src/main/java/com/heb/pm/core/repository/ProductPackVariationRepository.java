package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ProductPackVariation;
import com.heb.pm.dao.core.entity.ProductPackVariationKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Repository for ProductPackVariations.
 *
 * @author d116773
 * @since 1.4.0
 */
public interface ProductPackVariationRepository extends JpaRepository<ProductPackVariation, ProductPackVariationKey> {

	/**
	 * Returns a list of all ProductPackVariations for a given UPC and source system.
	 *
	 * @param upc The UPC to look for data for.
	 * @param sourceSystem The source system to look for data for.
	 * @return A list of all ProductPackVariations for a given UPC and source system.
	 */
	List<ProductPackVariation> findByKeyUpcAndKeySourceSystem(Long upc, Long sourceSystem);
}
