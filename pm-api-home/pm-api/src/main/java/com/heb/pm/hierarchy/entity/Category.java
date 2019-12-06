package com.heb.pm.hierarchy.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * Represents a grouping for products by type.
 */
@NodeEntity
@Data
public class Category {

    @Id
    @GeneratedValue
	private Long id;

	private String categoryName;
	private String categoryId;
	private String description;
	private String longName;

	/**
	 * Returns a string representation of this class.
	 *
	 * @return A string representation of this class.
	 */
	@Override
	public String toString() {
		return "Category{" +
				"id=" + id +
				", categoryName='" + categoryName + '\'' +
				", categoryId='" + categoryId + '\'' +
				", description='" + description + '\'' +
				", longName='" + longName + '\'' +
				'}';
	}
}
