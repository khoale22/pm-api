package com.heb.pm;

import com.heb.pm.dao.core.entity.DcmCostLink;
import com.heb.pm.dao.core.entity.ItemMasterKey;
import com.heb.pm.core.repository.CostLinkRepository;
import com.heb.pm.core.repository.ItemMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Used instead of the web-service client to fetch cost link.
 */
@Service
public class TestCostLinkRepository implements CostLinkRepository {

	@Autowired
	private ItemMasterRepository itemMasterRepository;

	@Override
	public Optional<DcmCostLink> findById(Long costLinkNumber) {

		if (costLinkNumber.equals(454354L)) {
			DcmCostLink dcmCostLink = new DcmCostLink();
			dcmCostLink.setCostLinkNumber(454354L).setDescription("TEST COST LINK").setActive(Boolean.TRUE)
					.setApNumber(23221L).setApType("AP").setItems(new LinkedList<>());

			ItemMasterKey itemMasterKey = new ItemMasterKey().setItemKeyTypeCode(ItemMasterKey.WAREHOUSE).setItemId(209486L);

			this.itemMasterRepository.findById(itemMasterKey).ifPresent(dcmCostLink.getItems()::add);

			return Optional.of(dcmCostLink);
		}

		if (costLinkNumber.equals(2524L)) {
			DcmCostLink dcmCostLink = new DcmCostLink();
			dcmCostLink.setCostLinkNumber(2524L).setDescription("").setActive(Boolean.FALSE)
					.setApNumber(23221L).setApType("AP").setItems(new LinkedList<>());

			ItemMasterKey itemMasterKey = new ItemMasterKey().setItemKeyTypeCode(ItemMasterKey.WAREHOUSE).setItemId(209486L);

			this.itemMasterRepository.findById(itemMasterKey).ifPresent(dcmCostLink.getItems()::add);

			return Optional.of(dcmCostLink);
		}

		return Optional.empty();


	}
}
