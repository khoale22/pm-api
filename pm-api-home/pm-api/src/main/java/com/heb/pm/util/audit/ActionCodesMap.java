package com.heb.pm.util.audit;

import com.heb.pm.core.model.audit.ActionCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps between the various ways that action codes are stored in the DB to the ActionCodes in the model project.
 *
 * @author d116773
 * @since 1.5.0
 */
/* default */ final class ActionCodesMap {

	public static final String PURGE = "PURGE";
	public static final String ADD = "ADD";
	public static final String UPDAT = "UPDAT";
	public static final String UPDT = "UPDT";
	public static final String DEL = "DEL";

	private static final Map<String, ActionCode> ACTION_CODE_MAP = new ConcurrentHashMap<>();

	static {
		ACTION_CODE_MAP.put(PURGE, ActionCode.DELETE);
		ACTION_CODE_MAP.put(ADD, ActionCode.ADD);
		ACTION_CODE_MAP.put(UPDAT, ActionCode.UPDATE);
		ACTION_CODE_MAP.put(UPDT, ActionCode.UPDATE);
		ACTION_CODE_MAP.put(DEL, ActionCode.DELETE);
	}

	// Private constructor.
	private ActionCodesMap() {
	}

	/**
	 * Returns the action code for given key.
	 *
	 * @param key The key to look for.
	 * @return The ActionCode for that key or null.
	 */
	public static ActionCode get(String key) {
		return ACTION_CODE_MAP.get(key);
	}
}
