package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.PhSubCommodity;
import com.heb.pm.dao.core.entity.PhSubCommodityKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for pulling data about a sub-commodity.
 *
 * @author d116773
 * @since 1.1.0
 */
public interface PhSubCommodityRepository extends JpaRepository<PhSubCommodity, PhSubCommodityKey> {

	/**
	 * Returns a list of PhSubCommodities for a given sub-commodity code. If there is more than one
	 * entry in the list, that is truly an error state.
	 *
	 * @param subCommodityCode The sub-commodity to look up.
	 * @return A list of sub-ccommodities with that code. This should contain only one entry.
	 */
	List<PhSubCommodity> findByKeySubCommodityCode(Long subCommodityCode);
}
