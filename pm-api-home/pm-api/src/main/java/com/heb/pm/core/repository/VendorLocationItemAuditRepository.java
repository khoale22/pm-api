package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.VendorLocationItemAudit;
import com.heb.pm.dao.core.entity.VendorLocationItemAuditKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to retrieve information about a vendor location item audit.
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface VendorLocationItemAuditRepository extends JpaRepository<VendorLocationItemAudit, VendorLocationItemAuditKey> {

	/**
	 * Returns a list of VendorLocationItemAudit records searching by item code and type ordering by change date.
	 *
	 * @param itemId The item code to search for.
	 * @param itemType The type of item to search for.
	 * @return A list of VendorLocationItemAudits.
	 */
	List<VendorLocationItemAudit> findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(Long itemId, String itemType);
}
