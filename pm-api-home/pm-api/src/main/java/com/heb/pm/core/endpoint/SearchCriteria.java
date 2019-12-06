package com.heb.pm.core.endpoint;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Class that holds search criteria to pass to the "/search" endpoints.
 *
 * @author d116773
 * @since 1.4.0
 */
@Data
@Accessors(chain = true)
public class SearchCriteria {

	@ApiModelProperty("A list of product IDs to include in the search results.")
	private List<Long> productIds;

	@ApiModelProperty("Whether or not to include products with nutrition. Null means don't consider nutrition. " +
			"True means only include products with nutrition. False means only include products without nutrition. This criterion" +
			"can only be applied through a streaming operation; it will be ignored otherwise.")
	private Boolean hasNutrition;
}
