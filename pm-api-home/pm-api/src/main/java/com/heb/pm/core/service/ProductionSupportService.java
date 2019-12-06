package com.heb.pm.core.service;

import com.heb.pm.core.event.LegacyEventProcessor;
import com.heb.pm.core.repository.ProductRepository;
import com.heb.pm.dao.core.LegacyEventGenerator;
import com.heb.pm.dao.core.entity.codes.LegacyEventFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for production support related functions.
 *
 * @author d116773
 * @since 1.8.0
 */
@Service
public class ProductionSupportService {

	@Value("${app.operation.productionSupport}")
	private transient String operationName;

	@Autowired
	private transient LegacyEventProcessor legacyEventProcessor;

	@Autowired
	private transient ProductRepository productRepository;

	/**
	 * Stages PRM2 events.
	 *
	 * @param userId The ID of the user who kicked this off.
	 * @param productIds The list of products to stage the events for.
	 */
	public void stagePRM2(String userId, List<Long> productIds) {

		// See if the product exists, and, if it does, stage the PRM2.
		productIds.forEach(
				(p) -> this.productRepository.findById(p)
						.map((product) -> LegacyEventGenerator.generatePRM2(product.getProdId(), this.operationName, userId, LegacyEventFunction.UPDATE))
						.ifPresent(this.legacyEventProcessor::add)
		);
		this.legacyEventProcessor.flush();
	}
}
