package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.WarehouseLocationItemAudit;
import com.heb.pm.dao.core.entity.WarehouseLocationItemAuditKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to retrieve information about a warehouse location item audit.
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface WarehouseLocationItemAuditRepository extends JpaRepository<WarehouseLocationItemAudit, WarehouseLocationItemAuditKey> {

	/**
	 * Returns a list of WarehouseLocationItemAudits searching by item code and type ordering by changed on.
	 *
	 * @param itemId The item code.
	 * @param itemType The item type.
	 * @return A list of WarehouseLocationItemAudit.
	 */
	List<WarehouseLocationItemAudit> findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(Long itemId, String itemType);
}
