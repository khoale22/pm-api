package com.heb.pm.core.endpoint.requests;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * ProductEventRequest.
 */
@Data
@Accessors(chain = true)
public class ProductEventRequest {

	private String userId;
	private List<Long> productIds;
}
