package com.heb.pm.util.audit;

import com.heb.pm.core.model.audit.AuditRecord;
import com.heb.pm.dao.audit.Audit;

import java.util.List;
import java.util.Map;

/**
 * Interface for a class to compare two audit objects.
 *
 * @author m314029
 * @since 1.0.0
 */
public interface AuditComparisonDelegate {

	/**
	 * Compares two audit objects, and returns the created audits from the comparison.
	 *
	 * @param currentValue The audit currently being looked at.
	 * @param previousValue The audit containing the previous values.
	 * @param userDisplayName The user friendly format of the user name and user id.
	 */
	List<AuditRecord> processClass(Audit currentValue, Audit previousValue, String userDisplayName, String... filters);

	/**
	 * Takes in a list of audits, and returns the created audits from comparing current audit and previous audit.
	 *
	 * @param audits The audit currently being looked at.
	 */
	List<AuditRecord> processClassFromList(List<? extends Audit> audits, String... filter);

	/**
	 * Maps the display name to a uid by connecting to ldap and retrieving a list of changed by users from audits.
	 * If the user is found, map the user id with the display name formatted text (fullName[user id]).
	 * Else map the user id with the user id.
	 *
	 * @param audits List of audits.
	 * @return Map of user id to display name.
	 */
	Map<String, String> mapUserIdToUserDisplayName(List<? extends Audit> audits);
	/**
	 * Maps the display name to a uid by connecting to ldap and retrieving a list of changed by users from audits.
	 * If the user is found, map the user id with the display name formatted text (fullName[user id]).
	 * Else map the user id with the user id.
	 *
	 * @param audits List of audits.
	 * @return Map of user id to display name.
	 */
	Map<String, String> mapUserIdToUserName(List<? extends Audit> audits);
}
