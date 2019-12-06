package com.heb.pm.hierarchy.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * Represents a category specific attribute.
 */
@NodeEntity
public class Attribute implements Serializable {

	private static final long serialVersionUID = -5508078102791630657L;
	private static final String ATTRIBUTE_NAME_SORT_FIELD = "a.name";

	@Id
	@GeneratedValue
	private Long id;

	private String attributeID;
	private String name;

	/**
	 * Returns Id.
	 *
	 * @return The Id.
	 **/
	public Long getId() {
		return id;
	}

	/**
	 * Sets the Id.
	 *
	 * @param id The Id.
	 **/
	public Attribute setId(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Returns AttributeID.
	 *
	 * @return The AttributeID.
	 **/
	public String getAttributeID() {
		return attributeID;
	}

	/**
	 * Sets the AttributeID.
	 *
	 * @param attributeID The AttributeID.
	 **/
	public Attribute setAttributeID(String attributeID) {
		this.attributeID = attributeID;
		return this;
	}

	/**
	 * Returns Name.
	 *
	 * @return The Name.
	 **/
	public String getName() {
		return name;
	}

	/**
	 * Sets the Name.
	 *
	 * @param name The Name.
	 **/
	public Attribute setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Returns the default sort for the class (by attribute name ascending).
	 *
	 * @return The default sort for this class.
	 */
	public static Sort getDefaultSort() {
		return Sort.by(
				new Sort.Order(Sort.Direction.ASC, Attribute.ATTRIBUTE_NAME_SORT_FIELD)
		);
	}

	/**
	 * Compares another object to this one. The key is the only thing used to determine equality.
	 *
	 * @param o The object to compare to.
	 * @return True if they are equal and false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Attribute attribute = (Attribute) o;

		return id != null ? id.equals(attribute.id) : attribute.id == null;
	}

	/**
	 * Returns a hash for this object. If two objects are equal, they will have the same hash. If they are not,
	 * they will (probably) have different hashes.
	 *
	 * @return The hash code for this object.
	 */
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	/**
	 * Returns a String representation of the object.
	 *
	 * @return A String representation of the object.
	 */
	@Override
	public String toString() {
		return "Attribute{" +
				"id=" + id +
				", attributeID='" + attributeID + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
