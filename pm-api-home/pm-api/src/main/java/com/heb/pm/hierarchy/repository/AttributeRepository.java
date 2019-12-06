package com.heb.pm.hierarchy.repository;

import com.heb.pm.hierarchy.entity.Attribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

/**
 * Neo4j repository for Attributes.
 *
 * @author m314029
 * @since 1.0.0
 */
public interface AttributeRepository extends Neo4jRepository<Attribute, String> {

	/**
	 * Returns a list of attributes by category ID.
	 *
	 * @param categoryId The ID of the category to look for.
	 * @param pageable The page request.
	 * @return The list of attributes for that category.
	 */
	@Query(AttributeRepositoryWithCount.FIND_BY_PROPERTY_OF_CATEGORY_ID_QUERY)
	Slice<Attribute> findByPropertyOfCategoryCategoryId(@Param("categoryId") String categoryId, Pageable pageable);
}
