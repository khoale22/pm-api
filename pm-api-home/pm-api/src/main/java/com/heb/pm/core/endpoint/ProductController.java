package com.heb.pm.core.endpoint;

import com.heb.pm.core.exception.NotFoundException;
import com.heb.pm.core.model.Product;
import com.heb.pm.core.service.ProductService;
import com.heb.pm.util.endpoint.PageableResult;
import com.heb.pm.util.security.wsag.ClientInfoService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * Rest endpoint for finding product information.
 */
@RestController()
@RequestMapping(ProductController.PRODUCT_BASE_URL)
@Api(value = "ProductAPI", produces = MediaType.APPLICATION_JSON_VALUE, description = "Endpoint for product level data.")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	protected static final String PRODUCT_BASE_URL = "/product";

	private static final String BY_PRODUCT_ID_PARAMETER = "{productId}";

	private static final String BY_SEARCH_CRITERIA = "/search";

	private static final String BY_SEARCH_CRITERIA_STREAMING = "/search/stream";

	private static final String PRODUCT_SEARCH_MESSAGE = "Application %s from IP %s has requested information for product: %d";
	@Autowired
	private transient ProductService productService;

	@Autowired
	private transient ClientInfoService clientInfoService;

	/**
	 * Searches for a product by ID. Will set the response status to 404 if it does not exist.
	 *
	 * @param productId The ID of the product to search for.
	 * @param filters An optional list of filters to apply to the result.
	 * @param request The HTTP request that initiated the call.
	 * @return A Product with the requested ID
	 */
	@RequestMapping(method = RequestMethod.GET, value = BY_PRODUCT_ID_PARAMETER, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation("Returns product information linked to the given product id.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "\"productId\" : {productId}", response = Product.class),
			@ApiResponse(code = 404, message = "error : Product id not found.")})
	public Product findProductById(@ApiParam(value = "The product id being requested.", example = "127127", required = true)
								   @PathVariable("productId") Long productId,
								   @ApiParam("Filter names of the fields that the user wants removed from results. If no filters are included, then all fields are " +
										   "returned. If any are included, only the requested fields are returned." +
										   " E.G. 'SUPPLY-CHAIN', 'ECOMMERCE', 'NUTRITION', 'SHELF-EDGE'")
								   @RequestParam(value = "filters", required = false) List<String> filters,
								   HttpServletRequest request) {

		ProductController.logger.info(String.format(ProductController.PRODUCT_SEARCH_MESSAGE, this.clientInfoService.getClientApplicationName(),
				request.getRemoteAddr(), productId));

		return this.productService.getProductById(productId, filters)
				.orElseThrow(NotFoundException.NOT_FOUND_EXCEPTION_SUPPLIER);
	}

	/**
	 * Searches for a list of products. Any product not found in the list will not be returned. If no products are
	 * found, then the response is an empty list.
	 *
	 * @param searchCriteria The search criteria to use when looking for products.
	 * @param filters An optional list of filters to apply to the result.
	 * @param request The HTTP request that initiated the call.
	 * @return A list of Products with the requested IDs.
	 */
	@RequestMapping(method = RequestMethod.POST, value = BY_SEARCH_CRITERIA, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searches for a collection of product IDs based on search criteria.", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "A list of products that match the search criteria.", response = Product.class),
			@ApiResponse(code = 400, message = "Search criteria invalid.")
	})
	public PageableResult findProductBySearchCriteria(@ApiParam(value = "A search criteria object to use to find products with.",
			example = "{ \"productIds\": [1977448,1626795,1422289,124999,2171990]}")
									@RequestBody SearchCriteria searchCriteria,
									@ApiParam("Optional filter of the fields that the user wants removed from results. " +
										"If no filters are included, then all fields are " +
										"returned. If any are included, only the requested fields are returned." +
										" E.G. 'SUPPLY-CHAIN', 'ECOMMERCE', 'NUTRITION', 'SHELF-EDGE'")
									@RequestParam(value = "filters", required = false) List<String> filters,
															   HttpServletRequest request) {

		SearchCriteriaValidator.validate(searchCriteria);

		ProductController.logger.info(String.format("Application %s from IP %s has requested information for the following search criteria: %s",
				this.clientInfoService.getClientApplicationName(), request.getRemoteAddr(), searchCriteria.toString()));

		List<Product> products = this.productService.getProductsBySearchCriteria(searchCriteria, filters);
		long totalRecordCount = products.size();
		return PageableResult.of(1, 1, totalRecordCount, products);
	}

	/**
	 * Searches for a list of products. Any product not found in the list will not be returned. This endpoint will stream
	 * data as it becomes available rather than generate the full list as one message.
	 *
	 * @param searchCriteria The search criteria to use when looking for products.
	 * @param filters An optional list of filters to apply to the result.
	 * @param request The HTTP request that initiated the call.
	 * @param response The HTTP response to write the products to.
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = BY_SEARCH_CRITERIA_STREAMING, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Searches for a collection of product IDs based on search criteria. This operation will stream" +
			"the data as it becomes available.	", httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "A list of products that match the search criteria.", response = Product.class),
			@ApiResponse(code = 400, message = "Search criteria invalid.")
	})
	public void streamProductsBySearchCriteria(@ApiParam(value = "A search criteria object to use to find products with.",
												example = "{ \"productIds\": [1977448,1626795,1422289,124999,2171990]}")
												@RequestBody SearchCriteria searchCriteria,
												@ApiParam("Optional filter of the fields that the user wants removed from results. " +
													   "If no filters are included, then all fields are " +
													   "returned. If any are included, only the requested fields are returned." +
													   " E.G. 'SUPPLY-CHAIN', 'ECOMMERCE', 'NUTRITION', 'SHELF-EDGE'")
											   	@RequestParam(value = "filters", required = false)List<String> filters,
											   	HttpServletRequest request, HttpServletResponse response) throws IOException {

		SearchCriteriaValidator.validate(searchCriteria);

		ProductController.logger.info(String.format("Application %s from IP %s has requested to stream information for the following search criteria: %s",
				this.clientInfoService.getClientApplicationName(), request.getRemoteAddr(), searchCriteria.toString()));

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		this.productService.streamProductsBySearchCriteria(searchCriteria, filters, response.getOutputStream());

	}
}
