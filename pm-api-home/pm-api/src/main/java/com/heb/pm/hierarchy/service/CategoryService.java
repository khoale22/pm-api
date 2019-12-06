package com.heb.pm.hierarchy.service;

import com.heb.pm.hierarchy.entity.Category;
import com.heb.pm.hierarchy.entity.PartOf;
import com.heb.pm.hierarchy.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds business logic related to categories.
 */
@Service
public class CategoryService {

	@Autowired
	private final transient CategoryRepository categoryRepository;

	/**
	 * Creates a new CateogoryService.
	 *
	 * @param categoryRepository The CategoryRepository used to lookup data.
	 */
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	/**
	 * Finds a category by name.
	 *
	 * @param categoryName The name of the category to search for.
	 * @return A category matching that name.
	 */
	@Transactional(readOnly = true)
	public Category findByCategoryName(String categoryName) {
		return categoryRepository.findByCategoryName(categoryName);
	}

	/**
	 * Finds a list of categories that match a name by pattern.
	 *
	 * @param categoryName The name to seacrh for categories like.
	 * @return
	 */
	@Transactional(readOnly = true)
	public Collection<Category> findByCategoryNameLike(String categoryName) {
		return categoryRepository.findByCategoryNameLike(categoryName);
	}

	/**
	 * Returns a graph of categories.
	 *
	 * @param limit The maximum size of the graph.
	 * @return A graph of categories.
	 */
	@Transactional(readOnly = true)
	public Collection<PartOf> graph(int limit) {
		Collection<PartOf> result = categoryRepository.graph(limit);

		Map<String, PartOf> hm2 = addRootNodes(result);

		result.addAll(hm2.values());

		return result;
	}

	/**
	 * Add root nodes to result.
	 *
	 * @param result
	 * @return
	 */
	private Map<String, PartOf> addRootNodes(Collection<PartOf> result) {
		Map<String, PartOf> hm = new ConcurrentHashMap<>();

		for (PartOf partOf : result) {
			hm.put(partOf.getCategory1().getCategoryId(), partOf);
		}

		long nextId = result.size();

		Map<String, PartOf> hm2 = new ConcurrentHashMap<>();

		for (PartOf partOf : result) {
			if (hm.get(partOf.getCategory2().getCategoryId()) == null &&
					hm2.get(partOf.getCategory2().getCategoryId()) == null) {

				PartOf newPartOf = this.categoryToTopLevelPartOf(partOf.getCategory2(), nextId++);
				hm2.put(newPartOf.getCategory1().getCategoryId(), newPartOf);
			}
		}

		return hm2;
	}

	/**
	 * Constructs a Category tied to the root node.
	 *
	 * @param source The source category.
	 * @param id The ID to use for the category.
	 * @return A PartOf with the category passed tied to a root node.
	 */
	private PartOf categoryToTopLevelPartOf(Category source, long id) {

		Category category1 = new Category();

		category1.setId(source.getId());
		category1.setCategoryId(source.getCategoryId());
		category1.setCategoryName(source.getCategoryName());
		category1.setDescription(source.getDescription());
		category1.setLongName(source.getLongName());

		Category category2 = new Category();
		category2.setId(-1L);
		category2.setCategoryId("Root Level");
		category2.setCategoryName("MAT00000");
		category2.setDescription("Root Node");
		category2.setLongName("Root Node of Hierarchy");

		PartOf newPartOf = new PartOf(category1, category2);
		newPartOf.setId(id);
		return newPartOf;
	}
}
