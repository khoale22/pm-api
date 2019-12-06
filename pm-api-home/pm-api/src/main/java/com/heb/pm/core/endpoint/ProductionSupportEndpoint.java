package com.heb.pm.core.endpoint;

import com.heb.pm.core.endpoint.requests.ProductEventRequest;
import com.heb.pm.core.exception.InvalidRequestException;
import com.heb.pm.core.service.ProductionSupportService;
import com.heb.pm.util.security.wsag.ClientInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * REST endpoint for production support related functions.
 *
 * @author d116773
 * @since 1.8.0
 */
@RestController()
@RequestMapping(ProductionSupportEndpoint.PRODUCTION_SUPPORT_BASE_URL)
public class ProductionSupportEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(ProductionSupportEndpoint.class);
	private static final int MAX_EVENT_STAGE_SIZE = 5_000;

	protected static final String PRODUCTION_SUPPORT_BASE_URL = "/support";

	protected static final String GENERATE_PRM2 = "/prm2";

	@Autowired
	private transient ProductionSupportService productionSupportService;

	@Autowired
	private transient ClientInfoService clientInfoService;

	/**
	 * Stages PRM2 events.
	 *
	 * @param eventRequest The request with the user ID and list of products to stage events for.
	 * @param request The HttpServletRequest that triggered this call.
	 * @return OK if successful.
	 */
	@PreAuthorize("hasAuthority('STAGE_EVENT')")
	@RequestMapping(method = RequestMethod.POST, value = GENERATE_PRM2, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String stagePRM2(@RequestBody ProductEventRequest eventRequest, HttpServletRequest request) {

		validateEventRequest(eventRequest);
		logger.info(String.format("User %s from IP %s using application %s requested PRM2 events for %d products.", eventRequest.getUserId(),
				 this.clientInfoService.getClientApplicationName(), request.getRemoteAddr(), eventRequest.getProductIds().size()));

		this.productionSupportService.stagePRM2(eventRequest.getUserId(), eventRequest.getProductIds());
		return "OK";
	}


	/**
	 * Validates a ProductEventRequest. This will throw an exception if it does not pass validation.
	 *
	 * @param productEventRequest The request to validate.
	 */
	private static void validateEventRequest(ProductEventRequest productEventRequest) {
		if (Objects.isNull(productEventRequest)) {
			throw new InvalidRequestException("Event request cannot be empty.");
		} else if (Objects.isNull(productEventRequest.getUserId())) {
			throw new InvalidRequestException("User ID is required when requesting event generation.");
		} else if (Objects.isNull(productEventRequest.getProductIds()) || productEventRequest.getProductIds().isEmpty()) {
			throw new InvalidRequestException("Product IDs are required when requesting event generation.");
		} else if (productEventRequest.getProductIds().size() > MAX_EVENT_STAGE_SIZE) {
			throw new InvalidRequestException(String.format("No more than %,d products can have events generated at a time.", MAX_EVENT_STAGE_SIZE));
		}
	}
}
