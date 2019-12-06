package com.heb.pm.hierarchy.endpoint;

import com.heb.pm.hierarchy.service.CategoryService;
import com.heb.pm.hierarchy.entity.Category;
import com.heb.pm.hierarchy.entity.PartOf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

/**
 * Rest endpoint for hierarchy categories.
 */
@RestController
@RequestMapping("/")
public class CategoryController {

	private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	private final transient CategoryService categoryService;

	/**
	 * Constructs a new CategoryController.
	 *
	 * @param categoryService The CategoryService used to fetch information.
	 */
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Returns a Category searching by name. Will set the response status to 404 if not found.
	 *
	 * @param categoryName The name of the category to search for.
	 * @param request The HttpServletRequest that initiated this call.
	 * @param response The HttpServletResponse that the response will be written to.
	 * @return The found category.
	 */
    @GetMapping("/category")
    public Category findByCategoryName(@RequestParam String categoryName, HttpServletRequest request, HttpServletResponse response) {

    	logger.debug(String.format("Search request for category %s came from IP %s", categoryName, request.getRemoteAddr()));

        Category c = categoryService.findByCategoryName(categoryName);
        if (Objects.isNull(c)) {
        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
        return c;
    }

	/**
	 * Retuns a list of categories with a name like the passed parameter.
	 *
	 * @param categoryName The string to search for categories like that name.
	 * @return A list of categories.
	 */
	@GetMapping("/categories")
    public Collection<Category> findByCategoryNameLike(@RequestParam String categoryName) {
        return categoryService.findByCategoryNameLike(categoryName);
    }

	/**
	 * Returns the full category graph.
	 *
	 * @param limit An optional limit to the size of the graph. If none is provided, will default to 100.
	 * @return The category graph.
	 */
	@CrossOrigin(origins = "http://127.0.0.1:9000")
    @GetMapping("/categoryGraph")
	public Collection<PartOf> graph(@RequestParam(value = "limit", required = false) Integer limit) {
		return categoryService.graph(limit == null ? 100 : limit);
	}
}
