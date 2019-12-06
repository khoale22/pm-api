package com.heb.pm.hierarchy.repository;

import com.heb.pm.hierarchy.entity.Attribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

/**
 * Neo4j repository with count for Attributes.
 *
 * @author m314029
 * @since 1.0.0
 */
public interface AttributeRepositoryWithCount extends Neo4jRepository<Attribute, String> {

	String FIND_BY_PROPERTY_OF_CATEGORY_ID_QUERY = "MATCH (c3:Category{categoryID:{categoryId}})-" +
			"[:PART_OF*0..]->(c2:Category)<-[:PROPERTY_OF]-(a:Attribute) return a";

	String FIND_BY_PROPERTY_OF_CATEGORY_ID_COUNT_QUERY = "MATCH (c3:Category{categoryID:{categoryId}})-" +
			"[:PART_OF*0..]->(c2:Category)<-[:PROPERTY_OF]-(a:Attribute) return count(a)";

	/**
	 * Returns a list of attributes by category ID.
	 *
	 * @param categoryId The ID of the category to look for.
	 * @param pageable The page request.
	 * @return The list of attributes for that category.
	 */
	@Query(
			value = FIND_BY_PROPERTY_OF_CATEGORY_ID_QUERY,
			countQuery = FIND_BY_PROPERTY_OF_CATEGORY_ID_COUNT_QUERY)
	Page<Attribute> findByPropertyOfCategoryCategoryId(@Param("categoryId") String categoryId, Pageable pageable);
}
