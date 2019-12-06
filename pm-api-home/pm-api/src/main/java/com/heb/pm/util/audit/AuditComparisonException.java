package com.heb.pm.util.audit;

/**
 * Thrown when an error happens during comparison of two audits.
 *
 * @author m314029
 * @since 1.0.0
 */
public class AuditComparisonException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a AuditComparisonException.
	 * @param t The raw exception.
	 */
	public AuditComparisonException(Throwable t) {
		super(t);
	}

	/**
	 * Constructs a new AuditComparisonException.
	 * @param s A message for the exception.
	 */
	public AuditComparisonException(String s) {
		super(s);
	}
}
