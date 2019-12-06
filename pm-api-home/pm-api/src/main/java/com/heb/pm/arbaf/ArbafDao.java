package com.heb.pm.arbaf;

import java.util.Collection;

/**
 * Interface to the DAO that returns ARBAF data.
 *
 * @author d116773
 * @since 1.1.0
 */
public interface ArbafDao {

	/**
	 * Returns a list of resource permissions for a user.
	 *
	 * @param applicationName The name of the application.
	 * @param userId The ID of the user.
	 * @return The list of resources and permissions a user has.
	 */
	Collection<Permission> getUserPermissions(String applicationName, String userId);

	/**
	 * Returns a list of resource permissions for a user.
	 *
	 * @param applicationName The name of the application.
	 * @param userId The ID of the user.
	 * @param jobCode The user's job code.
	 * @return The list of resources and permissions a user has.
	 */
	Collection<Permission> getUserPermissions(String applicationName, String userId, String jobCode);
}
