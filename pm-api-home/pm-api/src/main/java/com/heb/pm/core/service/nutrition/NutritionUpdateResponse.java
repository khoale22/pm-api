package com.heb.pm.core.service.nutrition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * The response to send back from a request to update scale nutrition.
 *
 * @author d116773
 * @since 1.9.0
 */
@Data
@Accessors(chain = true)
public class NutritionUpdateResponse {

	@ApiModelProperty("The ID of the saved nutrient statement.")
	private Long nutrientStatementId;

	@ApiModelProperty("The UPC the nutrient statement is meant for. This will only be returned if the UPC exists in the scale system.")
	private Long upc;

	@ApiModelProperty("True if the statement is tied to the UPC and false otherwise. It will only be present if upc is also present.")
	private Boolean nutritionIsTiedToUpc;
}
