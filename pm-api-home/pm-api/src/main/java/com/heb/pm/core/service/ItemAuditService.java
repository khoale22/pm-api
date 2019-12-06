package com.heb.pm.core.service;

import com.heb.pm.core.model.audit.AuditRecord;
import com.heb.pm.core.model.audit.AuditRecordWithVendor;
import com.heb.pm.core.model.audit.AuditRecordWithWarehouse;
import com.heb.pm.core.model.audit.ItemAudit;
import com.heb.pm.core.repository.ItemMasterAuditRepository;
import com.heb.pm.core.repository.VendorLocationItemAuditRepository;
import com.heb.pm.core.repository.WarehouseLocationItemAuditRepository;
import com.heb.pm.dao.core.entity.ItemMasterAudit;
import com.heb.pm.dao.core.entity.VendorLocationItemAudit;
import com.heb.pm.dao.core.entity.WarehouseLocationItemAudit;
import com.heb.pm.util.audit.AuditComparisonDelegate;
import com.heb.pm.util.audit.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds all business logic related to Item level audits.
 *
 * @author m314029
 * @since 1.0.0
 */
@Service
public class ItemAuditService {

	@Autowired
	@Qualifier("auditComparisonImpl")
	private transient AuditComparisonDelegate auditComparisonImplementation;

	@Autowired
	private transient ItemMasterAuditRepository itemMasterAuditRepository;

	@Autowired
	private transient VendorLocationItemAuditRepository vendorLocationItemAuditRepository;

	@Autowired
	private transient WarehouseLocationItemAuditRepository warehouseLocationItemAuditRepository;

	/**
	 * Finds item master audits by item id and type. If there are no item master audits related to the given item id
	 * and type, this method returns null. Else return the processed item master audits in reverse order (most recent
	 * records first).
	 *
	 * @param itemId Item id to search for.
	 * @param itemType Item type to search for.
	 * @param filters What to filter on.
	 * @return Audit records related to given item id and type or null if no item master audits are found.
	 */
	private List<AuditRecord> findItemMasterAuditsByItemIdAndItemType(Long itemId, String itemType, List<String> filters) {

		List<ItemMasterAudit> tableAudits =
				this.itemMasterAuditRepository.findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(
						itemId, itemType);

		if (CollectionUtils.isEmpty(tableAudits)) {
			return List.of();
		}

		if (filters.isEmpty()) {
			return this.auditComparisonImplementation.processClassFromList(tableAudits);
		} else {
			return this.auditComparisonImplementation.processClassFromList(tableAudits, filters.toArray(new String[0]));
		}
	}

	/**
	 * Finds vendor location audits by item id and type. If there are audits related to the given item id and type,
	 * this method returns an empty list. Else return the processed vendor location audits tied to the given item id
	 * and type in reverse order (most recent records first).
	 *
	 * @param itemId Item id to search for.
	 * @param itemType Item type to search for.
	 * @param filters What to filter on.
	 * @return Audit records with vendor related to given item id and type.
	 */
	private List<AuditRecordWithVendor> findVendorLocationAuditsByItemIdAndItemType(Long itemId, String itemType, List<String> filters) {

		List<VendorLocationItemAudit> vendorAudits = this.vendorLocationItemAuditRepository
				.findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(
						itemId, itemType);

		if (CollectionUtils.isEmpty(vendorAudits)) {
			return new ArrayList<>();
		}

		List<AuditRecordWithVendor> toReturn = new ArrayList<>();

		Map<String, String> userNameMap = this.auditComparisonImplementation.mapUserIdToUserDisplayName(vendorAudits);
		Map<Pair<Long, String>, VendorLocationItemAudit> previousValues = new ConcurrentHashMap<>();

		VendorLocationItemAudit previousValue;
		List<AuditRecord> newAudits;
		// for each audit get the previous value and process the changes over time
		for (VendorLocationItemAudit audit: vendorAudits) {
			previousValue = previousValues.get(
					Pair.of(audit.getKey().getVendorNumber(), audit.getKey().getVendorType()));

			if (filters.isEmpty()) {
				newAudits = this.auditComparisonImplementation
						.processClass(audit, previousValue, userNameMap.get(audit.getChangedBy()));
			} else {
				newAudits = this.auditComparisonImplementation
						.processClass(audit, previousValue, userNameMap.get(audit.getChangedBy()), filters.toArray(new String[0]));
			}

			for (AuditRecord newAudit : newAudits) {
				toReturn.add(this.vendorLocationItemAuditToAuditRecord(audit, newAudit));
			}

			previousValues.put(Pair.of(audit.getKey().getVendorNumber(),
					audit.getKey().getVendorType()), audit);
		}

		toReturn.sort(Comparator.comparing(AuditRecord::getChangedOn).reversed());

		return toReturn;
	}

	/**
	 * Builds a new AuditRecordWithVendor from a VendorLocationItemAudit and AuditRecord.
	 *
	 * @param audit The VendorLocationItemAudit to include.
	 * @param newAudit The AuditRecord to include.
	 * @return The new AuditRecordWithVendor
	 */
	private AuditRecordWithVendor vendorLocationItemAuditToAuditRecord(final VendorLocationItemAudit audit,
															 final AuditRecord newAudit) {

		AuditRecordWithVendor auditRecordWithVendor = AuditRecordWithVendor.of()
				.setVendorId(audit.getKey().getVendorNumber())
				.setVendorType(audit.getKey().getVendorType());
		auditRecordWithVendor
				.setChangedOn(newAudit.getChangedOn())
				.setChangedBy(newAudit.getChangedBy())
				.setAction(newAudit.getAction())
				.setAttributeName(newAudit.getAttributeName())
				.setChangedFrom(newAudit.getChangedFrom())
				.setChangedTo(newAudit.getChangedTo());
		return auditRecordWithVendor;
	}

	/**
	 * Finds warehouse location audits by item id and type. If there are audits related to the given item id and type,
	 * this method returns an empty list. Else return the processed warehouse location audits tied to the given item id
	 * and type in reverse order (most recent records first).
	 *
	 * @param itemId Item id to search for.
	 * @param itemType Item type to search for.
	 * @param filters What to filter on.
	 * @return Audit records with warehouse related to given item id and type.
	 */
	private List<AuditRecordWithWarehouse> findWarehouseLocationAuditsByItemIdAndItemType(Long itemId, String itemType, List<String> filters) {

		List<WarehouseLocationItemAudit> warehouseAudits =
				this.warehouseLocationItemAuditRepository.findByKeyItemCodeAndKeyItemTypeOrderByKeyChangedOn(
						itemId, itemType);
		if (CollectionUtils.isEmpty(warehouseAudits)) {
			return new ArrayList<>();
		}

		List<AuditRecordWithWarehouse> toReturn = new ArrayList<>();

		Map<String, String> userNameMap =
				this.auditComparisonImplementation.mapUserIdToUserDisplayName(warehouseAudits);
		Map<Pair<Long, String>, WarehouseLocationItemAudit> previousValues = new ConcurrentHashMap<>();

		WarehouseLocationItemAudit previousValue;
		// for each audit get the previous value and process the changes over time
		for (WarehouseLocationItemAudit audit: warehouseAudits) {
			previousValue = previousValues.get(
					Pair.of(audit.getKey().getWarehouseNumber(), audit.getKey().getWarehouseType()));

			List<AuditRecord> records;
			if (filters.isEmpty()) {
				records = this.auditComparisonImplementation
						.processClass(audit, previousValue, userNameMap.get(audit.getChangedBy()));
			} else {
				records = this.auditComparisonImplementation
						.processClass(audit, previousValue, userNameMap.get(audit.getChangedBy()), filters.toArray(new String[0]));
			}

			for (AuditRecord record : records) {
				toReturn.add(this.warehouseLocationItemAuditToAuditRecordWithWarehouse(audit, record));
			}

			previousValues.put(Pair.of(audit.getKey().getWarehouseNumber(),
					audit.getKey().getWarehouseType()), audit);
		}

		toReturn.sort(Comparator.comparing(AuditRecord::getChangedOn).reversed());
		return toReturn;
	}

	/**
	 * Creates a AuditRecordWithWarehouse from a  WarehouseLocationItemAudit and an AuditRecord.
	 *
	 * @param audit The WarehouseLocationItemAudit to add.
	 * @param record The AuditRecord to add.
	 * @return The constructed AuditRecordWithWarehouse.
	 */
	private AuditRecordWithWarehouse warehouseLocationItemAuditToAuditRecordWithWarehouse(
			final WarehouseLocationItemAudit audit, final AuditRecord record) {

		AuditRecordWithWarehouse auditRecordWithWarehouse = AuditRecordWithWarehouse.of()
				.setWarehouseId(audit.getKey().getWarehouseNumber())
				.setWarehouseType(audit.getKey().getWarehouseType());

		auditRecordWithWarehouse.setChangedOn(record.getChangedOn())
				.setChangedBy(record.getChangedBy())
				.setAction(record.getAction())
				.setAttributeName(record.getAttributeName())
				.setChangedFrom(record.getChangedFrom())
				.setChangedTo(record.getChangedTo());

		return auditRecordWithWarehouse;
	}

	/**
	 * Finds item audit by item id and type. If there are no item master audits related to the given item id and type,
	 * this method returns null. Else return the processed item master audits, vendor location audits, and
	 * warehouse location audits tied to the given item id and type.
	 *
	 * @param itemId Item id to search for.
	 * @param itemType Item type to search for.
	 * @param filters What to filter on.
	 * @return Item Audit related to given id and type or null if no item master audits are found.
	 */
	public ItemAudit findByItemIdAndItemType(Long itemId, String itemType, List<String> filters) {

		List<String> myFilters = Objects.isNull(filters) ? List.of() : filters;

		List<AuditRecord> audits = this.findItemMasterAuditsByItemIdAndItemType(itemId, itemType, myFilters);
		if (audits == null) {
			return null;
		}
		return ItemAudit.of().setItemCode(itemId).setItemType(itemType)
				.setAudits(audits)
				.setVendorLocationAudits(this.findVendorLocationAuditsByItemIdAndItemType(
						itemId, itemType, myFilters))
				.setWarehouseLocationAudits(this.findWarehouseLocationAuditsByItemIdAndItemType(
						itemId, itemType, myFilters));
	}
}
