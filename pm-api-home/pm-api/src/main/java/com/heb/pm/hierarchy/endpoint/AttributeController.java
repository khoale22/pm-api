package com.heb.pm.hierarchy.endpoint;

import com.heb.pm.hierarchy.service.AttributeService;
import com.heb.pm.util.endpoint.PageableResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Rest endpoint for looking up attribute information.
 */
@RestController
@RequestMapping("/attribute")
public class AttributeController {

	@Autowired
	private final transient AttributeService attributeService;

	/**
	 * Constructs a new AttributeController.
	 *
	 * @param attributeService The AttributeService for this class to use.
	 */
	public AttributeController(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * Finds a page of attributes given a category id (of selected hierarchy level), page, page size, and include
	 * count.
	 *
	 * @param page Page to search for.
	 * @param pageSize Page size to use.
	 * @param includeCount Whether count needs to be returned or not.
	 * @param categoryId ID of the selected hierarchy level.
	 * @return Requested page of attributes.
	 */
	@CrossOrigin(origins = "http://127.0.0.1:9000")
	@GetMapping("/findPageByCategoryId")
	public PageableResult findPageByCategoryId(
			@RequestParam("page") Integer page,
			@RequestParam("pageSize") Integer pageSize,
			@RequestParam("includeCount") Boolean includeCount,
			@RequestParam("categoryId") String categoryId) {
		return this.attributeService.findPageByCategoryId(page, pageSize, includeCount, categoryId);
	}
}
