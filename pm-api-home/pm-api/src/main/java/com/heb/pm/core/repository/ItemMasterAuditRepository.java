package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ItemMasterAudit;
import com.heb.pm.dao.core.entity.ItemMasterAuditKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to retrieve information about an item audit.
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface ItemMasterAuditRepository extends JpaRepository<ItemMasterAudit, ItemMasterAuditKey> {

	/**
	 * Returns a list of ItemMasterAudits searching by item code and item type ordering by the change date.
	 *
	 * @param itemId The item code.
	 * @param itemType The type.
	 * @return A list of ItemMasterAudits.
	 */
	List<ItemMasterAudit> findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(Long itemId, String itemType);
}
