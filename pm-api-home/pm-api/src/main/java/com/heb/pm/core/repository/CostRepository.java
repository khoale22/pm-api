package com.heb.pm.core.repository;

/**
 * Interface for DAOs that return cost information.
 */
public interface CostRepository {

	/**
	 * Returns a representative current list cost for a given item code and vendor.
	 *
	 * @param itemCode The item code to search for.
	 * @param apNumber The Vendor AP number to look for.
	 * @return A representative current list cost.
	 */
	Double getCurrentListCost(Long itemCode, Long apNumber);
}
