package com.heb.pm.core.service;

import com.heb.pm.pam.model.Candidate;
import com.heb.pm.util.ValidatorUtils;
import org.apache.commons.lang3.Range;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Validate DRU fields in Candidate.
 */
public final class CandidateDRUValidator {

	public static final int MAX_DRU_ROWS = 9999;

	public static final String ORIENTATION_BY_DEPTH = "stock_facing_by_depth";
	public static final String ORIENTATION_BY_DEPTH_OR_FACING = "stock_facing_by_depth_or_facing";

	public static final String DRU_TYPE_DRP = "display_ready_pallets";
	public static final String DRU_TYPE_RRP = "retail_ready_packaging";


	private CandidateDRUValidator() {}

	/**
	 * Returns error string if DRU fields are invalid.
	 *
	 * @param candidate the candidate to validate.
	 * @return An error string if DRU fields are invalid.
	 */
	public static List<String> validDRU(Candidate candidate) {

		List<String> errorMessages = new LinkedList<>();

		if (Objects.nonNull(candidate.getDisplayReadyUnit()) && candidate.getDisplayReadyUnit()) {

			validDRURows(candidate, errorMessages);

			validDRUOrientation(candidate, errorMessages);

			validDRUType(candidate, errorMessages);

			validDRUCalc(candidate, errorMessages);
		}

		return errorMessages;
	}

	/**
	 * validDRURows.
	 *
	 * @param candidate
	 * @param errorMessages
	 */
	private static void validDRURows(Candidate candidate, List<String> errorMessages) {
		Range<Integer> validRange = Range.between(1, MAX_DRU_ROWS);

		if (Objects.nonNull(candidate.getDisplayReadyUnitRowsDeep()) && !validRange.contains(candidate.getDisplayReadyUnitRowsDeep().intValue())) {
			errorMessages.add("EACT-067: Length of Rows Deep in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
		} else {
			ValidatorUtils.validateFieldExists(candidate::getDisplayReadyUnitRowsDeep, "EACT-068: Rows Deep in Retail Units is not specified.").ifPresent(errorMessages::add);
		}

		if (Objects.nonNull(candidate.getDisplayReadyUnitRowsFacing()) && !validRange.contains(candidate.getDisplayReadyUnitRowsFacing().intValue())) {
			errorMessages.add("EACT-069: Length of Rows Facing in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
		} else {
			ValidatorUtils.validateFieldExists(candidate::getDisplayReadyUnitRowsFacing, "EACT-070: Rows Facing in Retail Units is not specified.").ifPresent(errorMessages::add);
		}

		if (Objects.nonNull(candidate.getDisplayReadyUnitRowsHigh()) && !validRange.contains(candidate.getDisplayReadyUnitRowsHigh().intValue())) {
			errorMessages.add("EACT-071: Length of Rows High in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
		} else {
			ValidatorUtils.validateFieldExists(candidate::getDisplayReadyUnitRowsHigh, "EACT-072: Rows High in Retail Units is not specified.").ifPresent(errorMessages::add);
		}
	}

	/**
	 * validDRUCalc.
	 *
	 * @param candidate
	 * @param errorMessages
	 */
	private static void validDRUCalc(Candidate candidate, List<String> errorMessages) {
		if (Objects.nonNull(candidate.getDisplayReadyUnitRowsDeep()) &&
				Objects.nonNull(candidate.getDisplayReadyUnitRowsFacing()) &&
				Objects.nonNull(candidate.getDisplayReadyUnitRowsHigh()) &&
				Objects.nonNull(candidate.getInnerPack()) &&
				CandidateDRUValidator.calculateDRUDimensions(candidate) != candidate.getInnerPack()) {
			errorMessages.add("EACT-077: The Rows Facing x Rows Deep x Rows High must be equal to the Inner Pack. " +
					"Please update the values.");
		}
	}

	/**
	 * validDRUType.
	 *
	 * @param candidate
	 * @param errorMessages
	 */
	private static void validDRUType(Candidate candidate, List<String> errorMessages) {
		if (Objects.nonNull(candidate.getDisplayReadyUnitType())) {
			ValidatorUtils.validateFieldInList(candidate::getDisplayReadyUnitType, "EACT-075: DRU type is not specified correctly.",
					DRU_TYPE_DRP, DRU_TYPE_RRP);
		} else {
			ValidatorUtils.validateFieldExists(candidate::getDisplayReadyUnitType, "EACT-076: DRU type is not specified.").ifPresent(errorMessages::add);
		}
	}

	/**
	 * validDRUOrientation.
	 *
	 * @param candidate
	 * @param errorMessages
	 */
	private static void validDRUOrientation(Candidate candidate, List<String> errorMessages) {
		if (Objects.nonNull(candidate.getDisplayReadyUnitOrientation())) {
			ValidatorUtils.validateFieldInList(candidate::getDisplayReadyUnitOrientation, "EACT-073: DRU Orientation is not specified correctly.",
					ORIENTATION_BY_DEPTH, ORIENTATION_BY_DEPTH_OR_FACING);
		} else {
			ValidatorUtils.validateFieldExists(candidate::getDisplayReadyUnitOrientation, "EACT-074: DRU Orientation is not specified.").ifPresent(errorMessages::add);
		}
	}

	/**
	 * Calculate the DRU Dimensions.
	 *
	 * @param candidate to check.
	 * @return dimension calculation.
	 */
	private static long calculateDRUDimensions(Candidate candidate) {
		return candidate.getDisplayReadyUnitRowsDeep() *
				candidate.getDisplayReadyUnitRowsFacing() *
				candidate.getDisplayReadyUnitRowsHigh();
	}
}
