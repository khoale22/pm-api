package com.heb.pm.core.endpoint;

import com.heb.pm.core.model.Department;
import com.heb.pm.core.service.HierarchyService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Rest endpoint for merchant hierarchy data.
 */
@RestController()
@RequestMapping(HierarchyEndpoint.HIERARCHY_BASE_URL)
@Api(value = "HierarchyAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class HierarchyEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(HierarchyEndpoint.class);

	protected static final String HIERARCHY_BASE_URL = "/hierarchy";

	@Autowired
	private transient HierarchyService hierarchyService;

	/**
	 * Returns a graph of merchant hierarchy data.
	 *
	 * @param toLevel The lowest level of data to return. By default, the method will return all levels.
	 * @param request The HTTP Servlet request that initiated this call.
	 * @return A graph of merchant hierarchy data.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation("Returns a graph of the merchant hierarchy.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "An array holding the graph of the merchant hierarchy starting at department.", response = Department.class)
			})
	public List<Department> get(
			@RequestParam(value = "level", defaultValue = HierarchyService.LEVEL_ALL)
			@ApiParam("An optional level at which to stop. Can be empty, ALL, DEPARTMENT, SUB_DEPARTMENT, CLASS, or COMMODITY") String toLevel,
			HttpServletRequest request) {

		HierarchyEndpoint.logger.info("IP %s requested the merchant hierarchy.", request.getRemoteAddr());
		return this.hierarchyService.getFullHierarchy(toLevel);
	}
}
