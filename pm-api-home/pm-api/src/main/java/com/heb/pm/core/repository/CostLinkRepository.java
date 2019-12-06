package com.heb.pm.core.repository;


import com.heb.pm.dao.core.entity.DcmCostLink;

import java.util.Optional;

/**
 * Interface for classes that return cost link information.
 *
 * @author d116773
 * @since 1.0.0
 */
public interface CostLinkRepository {

	/**
	 * Return a DcmCostLink by cost link number. Will return an empty optional if not found.
	 *
	 * @param costLinkNumber The cost link number to look for.
	 * @return The DcmCostLink with that ID or empty.
	 */
	Optional<DcmCostLink> findById(Long costLinkNumber);
}
