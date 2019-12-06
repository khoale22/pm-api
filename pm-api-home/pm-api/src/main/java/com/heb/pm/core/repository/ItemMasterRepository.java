/*
 * ItemMasterRepository
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ItemMaster;
import com.heb.pm.dao.core.entity.ItemMasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to retrieve information about an item.
 *
 * @author m314029
 * @since 1.0.0
 */
@Repository
public interface ItemMasterRepository extends JpaRepository<ItemMaster, ItemMasterKey> {
	/**
	 * Returns ItemMasters by ItemType and OrderingUpc.
	 *
	 * @param itemType the itemType.
	 * @param orderingUpc the orderingUpc.
	 * @return ItemMasters by ItemType and OrderingUpc.
	 */
	List<ItemMaster> findByKeyItemKeyTypeCodeAndOrderingUpc(String itemType, Long orderingUpc);
}
