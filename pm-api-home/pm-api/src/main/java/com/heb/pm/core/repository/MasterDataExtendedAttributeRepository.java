package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.MasterDataExtendedAttribute;
import com.heb.pm.dao.core.entity.MasterDataExtendedAttributeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for MasterDataExtendedAttributes.
 *
 * @author d116773
 * @since 1.4.0
 */
public interface MasterDataExtendedAttributeRepository extends JpaRepository<MasterDataExtendedAttribute, MasterDataExtendedAttributeKey> {

	/**
	 * Returns a list of MasterDataExtendedAttributes constraining on everything in the key except for sequence number.
	 *
	 * @param attributeId The ID of the attribute to pull.
	 * @param keyId The ID for the product/upc/item to pull data for.
	 * @param keyType The type of keyId.
	 * @param sourceSystemId The source system to look for data for.
	 * @return A list of MasterDataExtendedAttributes constrained on everything in the key except for sequence number.
	 */
	List<MasterDataExtendedAttribute> findByKeyAttributeIdAndKeyKeyIdAndKeyKeyTypeAndKeySourceSystemId(Long attributeId, Long keyId, String keyType, Long sourceSystemId);
}
