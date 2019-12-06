package com.heb.pm.util.audit;

import com.heb.pm.core.model.audit.AuditRecord;
import com.heb.pm.core.repository.UserSearchRepository;
import com.heb.pm.dao.audit.Audit;
import com.heb.pm.dao.audit.AuditableField;
import com.heb.pm.dao.audit.AuditableFieldFormatter;
import com.heb.pm.dao.audit.DefaultAuditableFieldFormatter;
import com.heb.pm.util.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Handles the logic to do an audit comparison.
 *
 * @author m314029
 * @since 1.0.0
 */
@Service
@Qualifier("auditComparisonImpl")
// This is a very complex class pulled in from PM. It might be worth refactoring, but probably not.
@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.PossibleGodClass", "PMD.NcssCount", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public class AuditComparisonImpl implements AuditComparisonDelegate {

	private static final Logger logger = LoggerFactory.getLogger(AuditComparisonImpl.class);

	private static final String EQUAL = "equal.";
	private static final String NOT_EQUAL = "not equal.";

	private static final String USER_DISPLAY_NAME_FORMAT = "%s[%s]";

	// These are the constants for auditable values.
	private static final String NOT_APPLICABLE_VALUE = "N/A";
	private static final String UNKNOWN_VALUE = "Unknown";
	private static final String NULL_VALUE = "null";
	/** STRING_EMPTY.*/
	public static final String EMPTY = "";
	// log messages
	private static final String LOG_COMPARING_VALUES = "Comparing {%s} and {%s}.";
	private static final String LOG_IS_EQUALS = "Values were found to be: %s";
	private static final String LOG_AUDIT_CREATED = "Created audit record: %s.";

	// Errors
	private static final String INCOMPATIBLE_COMPARISON_ERROR = "ERROR -- Current value: %s and previous value: %s " +
			"are of different class types and should not be compared.";

	@Autowired
	private transient UserSearchRepository ldapSearchRepository;

	private final transient Map<Class<? extends AuditableFieldFormatter>, AuditableFieldFormatter> auditableFieldFormatterCache = new ConcurrentHashMap<>();

	/**
	 * Takes in a list of audits ordered in changed by ascending order (most recent audit is last in list), and returns
	 * the created audits from comparing current audit and previous audit in descending order (most recent audit is
	 * first in list).
	 *
	 * @param audits The list of audits ordered in changed by ascending order.
	 * @return List of all audit records created from comparing current and previous values of each audit in
	 * descending order.
	 */
	@Override
	public List<AuditRecord> processClassFromList(List<? extends Audit> audits, String...filters) {

		if (CollectionUtils.isEmpty(audits)) {
			return new ArrayList<>();
		}

		// make sure audits are in the order they were changed (most historical records first)
		audits.sort(Comparator.comparing(Audit::getChangedOn));

		// map of user id -> user display names
		Map<String, String> mapUserNames = this.mapUserIdToUserDisplayName(audits);

		List<AuditRecord> toReturn = new ArrayList<>();

		// get the audits by comparing current and previous audits, then add to beginning of return list
		// if first audit in list, pass null as previous value to compare
		IntStream.range(0, audits.size()).forEach(index -> toReturn.addAll(0, this.processClass(
				audits.get(index),
				index == 0 ? null : audits.get(index - 1),
				mapUserNames.get(audits.get(index).getChangedBy()),
				filters)));

		// make sure audits are in the reverse order they were changed on return (most recent records first)
		toReturn.sort(Comparator.comparing(AuditRecord::getChangedOn).reversed());

		return toReturn;
	}

	/**
	 * Compares two audit records, looking at all fields annotated with 'AuditableField', and creates a new audit for
	 * each of these fields that changed. After looking at all the auditable fields, return the list of new audits.
	 *
	 * @param currentValue The audit currently being looked at.
	 * @param previousValue The audit containing the previous values.
	 * @param fullUserName The full user name of the person who made the change.
	 * @return List of all audit records created from comparing current and previous values.
	 */
	@Override
	public List<AuditRecord> processClass(Audit currentValue, Audit previousValue, String fullUserName, String...filters) {

		if (previousValue == null || currentValue.getClass().isInstance(previousValue)) {

			List<AuditRecord> toReturnAudits = new ArrayList<>();
			AuditRecord newAudit;

			// Loop through all the fields in the destination object.
			for (Field field : currentValue.getClass().getDeclaredFields()) {

				// If it is annotated with AuditableField, process the field.
				if (field.isAnnotationPresent(AuditableField.class)) {
					try {
						newAudit = this.processField(field, currentValue, previousValue, fullUserName, filters);
						if (newAudit != null) {
							toReturnAudits.add(newAudit);
						}
					} catch (IllegalAccessException | NoSuchMethodException |
							// Convert all the exceptions to a runtime exception.
							InvocationTargetException e) {
						throw new AuditComparisonException(e.getCause());
					}
				}
			}
			return toReturnAudits;

		} else {
			throw new IllegalArgumentException(String.format(
					AuditComparisonImpl.INCOMPATIBLE_COMPARISON_ERROR, currentValue, previousValue));
		}
	}

	/**
	 * Handles creating an audit from an individual field.
	 *
	 * @param field The field to compare.
	 * @param currentAudit The current audit.
	 * @param previousAudit The previous audit.
	 * @param userDisplayName The user friendly format of the user name and user id.
	 *
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *
	 * @return New Audit if there is a change, null otherwise.
	 */
	private AuditRecord processField(Field field, Audit currentAudit, Audit previousAudit, String userDisplayName, String...filters)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		//Check to see if the field is needed for this audit.
		boolean contains = false;
		List<String> currentFilters = Arrays.asList(field.getAnnotation(AuditableField.class).filter());
		if (filters.length > 0) {
			for (String filter: filters) {
				contains = currentFilters.contains(filter);
			}
			if (!contains) {
				return null;
			}
		}

		boolean isAccessible = field.isAccessible();
		AuditRecord returnAudit = null;

		try {
			boolean isEquals;

			field.setAccessible(true);

			// compare previous audit records
			logger.trace(String.format(
					AuditComparisonImpl.LOG_COMPARING_VALUES,
					field.get(currentAudit), previousAudit != null ? field.get(previousAudit) : null));

			// if previous audit is null, audits are not equal
			if (previousAudit == null || currentAudit.getAction().trim().equals(ActionCodesMap.PURGE)) {
				isEquals = false;
			} else if (field.get(currentAudit) == null || field.get(previousAudit)  == null) {
				isEquals = field.get(currentAudit) == null && field.get(previousAudit) == null;
			} else if (field.getType().equals(Integer.TYPE)) {
				// For the remainder, compare previous and current audit record values by type
				// if the type is primitive, use primitive comparison (==) on the values.
				isEquals = field.getInt(currentAudit) == field.getInt(previousAudit);
			} else if (field.getType().equals(Long.TYPE)) {
				isEquals = field.getLong(currentAudit) == field.getLong(previousAudit);
			} else if (field.getType().equals(Double.TYPE)) {
				isEquals = field.getDouble(currentAudit) == field.getDouble(previousAudit);
			} else if (field.getType().equals(Float.TYPE)) {
				isEquals = field.getFloat(currentAudit) == field.getFloat(previousAudit);
			} else if (field.getType().equals(Character.TYPE)) {
				isEquals = field.getChar(currentAudit) == field.getChar(previousAudit);
			} else if (field.getType().equals(Boolean.TYPE)) {
				isEquals = field.getBoolean(currentAudit) == field.getBoolean(previousAudit);
			} else if (field.getType().equals(Short.TYPE)) {
				isEquals = field.getShort(currentAudit) == field.getShort(previousAudit);
			} else if (field.getType().equals(Byte.TYPE)) {
				isEquals = field.getByte(currentAudit) == field.getByte(previousAudit);
			} else {
				// else compare using object equals (.equals) on the values
				isEquals = field.get(currentAudit).equals(field.get(previousAudit));
			}

			// log whether values were equal or not
			logger.trace(String.format(AuditComparisonImpl.LOG_IS_EQUALS,
					isEquals ? AuditComparisonImpl.EQUAL : AuditComparisonImpl.NOT_EQUAL));

			// if current field != previous field, create the audit record
			if (!isEquals) {
				// need to extract name dynamically if audit is Dynamic Attribute
				String attrName;
				String methodName = field.getAnnotation(AuditableField.class).displayNameMethod();
				// this means that displayNameMethod is NOT in the AuditableField
				if (methodName.equals(AuditableField.NOT_APPLICABLE)) {
					attrName = field.getAnnotation(AuditableField.class).displayName();
				} else {
					Method method = currentAudit.getClass().getDeclaredMethod(methodName);
					attrName = method.invoke(currentAudit).toString();
					// format DB's result of attribute name from all caps to 1st letter of each word in upper case
					attrName = WordUtils.capitalizeFully(attrName);
				}
				// call constructor with changed on, changed by, action, and attribute display name
				returnAudit = AuditRecord.of()
						.setChangedOn(currentAudit.getChangedOn())
						.setChangedBy(userDisplayName)
						.setAction(ActionCodesMap.get(currentAudit.getAction().trim()))
						.setAttributeName(attrName);

				// if purge action
				if (currentAudit.getAction().trim().equals(ActionCodesMap.PURGE)) {

					// changed to is not applicable
					returnAudit.setChangedTo(AuditComparisonImpl.NOT_APPLICABLE_VALUE);

					// changed from is current audit field value
					returnAudit.setChangedFrom(this.getAuditPropertyValue(field, currentAudit));
				} else {
					// else add or update action
					// changed to is current audit field value
					returnAudit.setChangedTo(this.getAuditPropertyValue(field, currentAudit));
					// if add action: changed from is not applicable
					if (currentAudit.getAction().trim().equals(ActionCodesMap.ADD)) {
						returnAudit.setChangedFrom(AuditComparisonImpl.NOT_APPLICABLE_VALUE);
					} else {
						// else update action: changed from is previous field value
						returnAudit.setChangedFrom(this.getAuditPropertyValue(field, previousAudit));
					}
				}

				logger.trace(String.format(AuditComparisonImpl.LOG_AUDIT_CREATED, returnAudit));
			}
		} finally {
			field.setAccessible(isAccessible);
		}

		return returnAudit;
	}

	/**
	 * Returns the value for an audit object for a particular field. If the audit is null, the value is unknown.
	 * If the field has a given code table display name method that is not the default (NOT_APPLICABLE), this method
	 * will call that method. Else return the field value as is or 'null' if value is empty
	 *
	 * @param auditableField The auditable field in the audit object you want the value for.
	 * @param audit The audit you want the getter from.
	 * @return The value 'UNKNOWN_VALUE' if the audit is null, code table display name method invocation if the method
	 * exists, or string value for a property on an audit.
	 *
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private String getAuditPropertyValue(Field auditableField, Audit audit) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		// if audit is null, property value is unknown
		if (audit == null) {
			return AuditComparisonImpl.UNKNOWN_VALUE;
		}

		String currentFieldStringValue;
		Object currentFieldValue;

		String codeTableDisplayNameMethod =
				auditableField.getAnnotation(AuditableField.class).codeTableDisplayNameMethod();

		// if field does not have a code table display name method, current property value is the value.toString()
		if (codeTableDisplayNameMethod.equals(AuditableField.NOT_APPLICABLE)) {
			currentFieldValue = auditableField.get(audit);
			currentFieldStringValue = this.getAuditableFieldFormatter(auditableField.getAnnotation(AuditableField.class)).format(currentFieldValue);
		} else {
			// else current property value is the code table display name method call
			Method method = audit.getClass().getMethod(codeTableDisplayNameMethod);
			currentFieldValue = method.invoke(audit);
			if (currentFieldValue != null) {
				currentFieldStringValue = currentFieldValue.toString();
			} else {
				currentFieldStringValue = StringUtils.EMPTY;
			}
		}

		// if current property value is empty, return 'null'
		if (currentFieldStringValue.isEmpty()) {
			return AuditComparisonImpl.NULL_VALUE;
		} else {
			// else return the property value
			return currentFieldStringValue;
		}
	}

	/**
	 *
	 * Gets a list of user Information from Ldap using the userId. If there is an error, it logs the error.
	 *
	 * @param userIds A list of user ids that get sent to be searched on LDAP.
	 * @return the ldap searched list of userIds or null if nothing came back.
	 */
	private List<User> getUserInformation(Set<String> userIds) {
		try {
			return ldapSearchRepository.getUserList(new ArrayList<>(userIds));
		}  catch (Exception e) {
			AuditComparisonImpl.logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	/**
	 * Maps the display name to a uid by connecting to ldap and retrieving a list of changed by users from audits.
	 * If the user is found, map the user id with the display name formatted text (fullName[user id]).
	 * Else map the user id with the user id.
	 *
	 * @param audits List of audits.
	 * @return Map of user id to display name.
	 */
	@Override
	public Map<String, String> mapUserIdToUserDisplayName(List<? extends Audit> audits) {

		Set<String> userIds = this.getChangedByUsersFromAudits(audits);

		Map<String, String> mapUserNames = new ConcurrentHashMap<>();

		List<User> userList = getUserInformation(userIds);

		// for each user id
		for (String userId : userIds) {
			User foundUser = null;

			// find the user with same user id
			for (User user : userList) {
				if (user.getUid().trim().equalsIgnoreCase(userId.trim())) {
					foundUser = user;
					break;
				}
			}

			// map the user id to its display name
			mapUserNames.put(userId, this.getUserDisplayName(foundUser, userId));
		}
		return mapUserNames;
	}

	/**
	 * Helper method to put each changed by user from a list of audits into a set. A set is used so there is only one
	 * instance of a user in the return collection.
	 *
	 * @param audits List of audits to extract the changed by user.
	 * @return Set of changed by users.
	 */
	private Set<String> getChangedByUsersFromAudits(List<? extends Audit> audits) {
		Set<String> userIdSet = new HashSet<>();

		// Loops through the audits and pull the userId's
		for (Audit audit : audits) {
			userIdSet.add(audit.getChangedBy());
		}
		return userIdSet;
	}

	/**
	 * Returns the username as it should be displayed on the GUI (i.e. 'fullName[user id]').
	 *
	 * @param user The user found using ldap.
	 * @param userId The user id of the user.
	 * @return A String representation of the modifying user as it is meant to be displayed on the GUI.
	 */
	private String getUserDisplayName(User user, String userId) {

		// if user is found, and full name is not blank, return formatted display name: fullName[userId]
		if (user != null && !StringUtils.isBlank(user.getFullName())) {
			return String.format(AuditComparisonImpl.USER_DISPLAY_NAME_FORMAT, user.getFullName().trim(),
					userId.trim());
		} else {
			// else return user id
			return userId.trim();
		}
	}
	/**
	 * Maps the display name to a uid by connecting to ldap and retrieving a list of changed by users from audits.
	 * If the user is found, map the user id with the display name formatted text (fullName[user id]).
	 * Else map the user id with the user id.
	 *
	 * @param audits List of audits.
	 * @return Map of user id to display name.
	 */
	@Override
	public Map<String, String> mapUserIdToUserName(List<? extends Audit> audits) {

		return this.mapUserIdToUserDisplayName(audits);
	}

	/**
	 * Returns the AuditableFieldFormatter for a given field. This will cache them to keep from instantiating so
	 * many classes while processing big objects. This method is guaranteed to return a formatter. If the
	 * desired one cannot be instantiated, the method will return the default.
	 *
	 * @param auditableField The field to pull the formatter for.
	 * @return The formatter for the passed in field or the default.
	 */
	private AuditableFieldFormatter getAuditableFieldFormatter(AuditableField auditableField) {

		// First, check the cache.
		Class<? extends AuditableFieldFormatter> formatterClass = auditableField.formatter();
		if (this.auditableFieldFormatterCache.containsKey(formatterClass)) {
			return this.auditableFieldFormatterCache.get(formatterClass);
		}

		// If it's not there, try and instantiate and add it to the cache. If you can't, use the default.
		try {
			AuditableFieldFormatter formatter = formatterClass.getDeclaredConstructor().newInstance();
			this.auditableFieldFormatterCache.put(formatterClass, formatter);
			return formatter;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error(String.format("Unable to instantiate formatter %s: %s.", formatterClass.getName(), e.getLocalizedMessage()));
			return new DefaultAuditableFieldFormatter();
		}

	}

}
