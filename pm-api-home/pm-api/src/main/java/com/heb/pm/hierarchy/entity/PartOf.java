package com.heb.pm.hierarchy.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

/**
 * Not sure what this is.
 */
@RelationshipEntity(type = "PART_OF")
@Data
public class PartOf {

	@Id
    @GeneratedValue
	private Long id;

	@StartNode
	private Category category1;

	@EndNode
	private Category category2;

	/**
	 * Constructs a new PartOf.
	 */
	public PartOf() {
		// Intentionally empty.
	}

	/**
	 * Constructs a new PartOf.
	 *
	 * @param category1 Not sure.
	 * @param category2 Not Sure.
	 */
	public PartOf(Category category1, Category category2) {
		this.category1 = category1;
		this.category2 = category2;
	}

	/**
	 * Returns a string representation of this object.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "PartOf{" +
				"id=" + id +
				", category1=" + category1 +
				", category2=" + category2 +
				'}';
	}
}
