package com.heb.pm.hierarchy.repository;

import com.heb.pm.hierarchy.entity.Category;
import com.heb.pm.hierarchy.entity.PartOf;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

/**
 * Repository for Categories.
 */
public interface CategoryRepository extends Neo4jRepository<Category, Long> {

	/**
	 * Returns a category with a given name.
	 *
	 * @param categoryName The name of the category to look for.
	 * @return The category that matches that name.
	 */
	Category findByCategoryName(@Param("categoryName") String categoryName);

	/**
	 * Returns a category by ID.
	 *
	 * @param id The ID of the category to look for.
	 * @return The category with that ID.
	 */
	Category findById(@Param("id") String id);

	/**
	 * Searches for a list of categories by name by regular expression.
	 *
	 * @param categoryName The regular expression for the name to look for.
	 * @return
	 */
	Collection<Category> findByCategoryNameLike(@Param("categoryName") String categoryName);

	/**
	 * Returns the category graph.
	 *
	 * @param limit A limit to the size of the graph.
	 * @return The category graph.
	 */
    @Query("MATCH (c1:Category)<-[r:PART_OF]-(c2:Category) RETURN r, c1, c2 LIMIT {limit}")
	Collection<PartOf> graph(@Param("limit") int limit);
}
