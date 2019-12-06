/*
 * User
 *
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.util.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type User.
 *
 * @author l730832
 * @since  1.0.0
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String DISPLAY_NAME_FORMAT = "%s [%s]";
	private String uid;
	private String hebLegacyID;
	private String givenName;
	private String lastName;
	private String fullName;
	private String mail;
	private String telephoneNumber;
	private String jobCode;

	/**
	 * Constructs a new User object.
	 */
	public User() {
		// Intentionally empty.
	}

	/**
	 * Constructs a new User object.
	 *
	 * @param uid The user's ID.
	 */
	public User(String uid) {
		this.uid = uid;
	}

	/**
	 * Constructs a new User object.
	 *
	 * @param uid The user's ID.
	 * @param hebLegacyID The user's legacy ID.
	 * @param givenName The user's first name.
	 * @param lastName The user's last name.
	 */
	public User(String uid, String hebLegacyID, String givenName, String lastName) {
		super();
		this.uid = uid;
		this.hebLegacyID = hebLegacyID;
		this.givenName = givenName;
		this.lastName = lastName;
	}


	/**
	 * Returns the user's name in the default display format (Name [userId]).
	 *
	 * @return The user's name in the default display format.
	 */
	public String getDisplayName() {
		return String.format(DISPLAY_NAME_FORMAT,
				this.getFullName(), this.getUid());
	}
	/**
	 * Compares another object to this one. This is a deep compare.
	 *
	 * @param o The object to compare to.
	 * @return True if they are equal and false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User)) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(uid, user.uid);
	}

	/**
	 * Returns a hash for this object. If two objects are equal, they will have the same hash. If they are not,
	 * they will (probably) have different hashes.
	 *
	 * @return The hash code for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(uid);
	}

	/**
	 * Returns a String representation of this object.
	 *
	 * @return A String representation of this object.
	 */
	@Override
	public String toString() {
		return "User{" +
				"uid='" + uid + '\'' +
				", hebLegacyID='" + hebLegacyID + '\'' +
				", givenName='" + givenName + '\'' +
				", lastName='" + lastName + '\'' +
				", fullName='" + fullName + '\'' +
				'}';
	}
}
