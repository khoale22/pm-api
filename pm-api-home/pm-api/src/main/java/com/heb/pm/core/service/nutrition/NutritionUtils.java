package com.heb.pm.core.service.nutrition;

import com.heb.pm.util.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A collection of utility functions related to producing nutrition information.
 *
 * @author d116773
 * @since 1.4.0
 */
/* default */ final class NutritionUtils {

	private static final BigDecimal NO_RDA = BigDecimal.ZERO;
	private static final List<Long> RESERVED_NUTRIENT_STATEMENTS = ListUtils.of(0L, 9999L, 99_999L, 9_999_999L);
	private static final int MAX_UNITS_OF_MEASURE_FOR_DESCRIPTION = 1;

	private static final String ONE_TWELFTH_STRING = "1/12";
	private static final String ONE_TENTH_STRING = "1/10";
	private static final String ONE_EIGHTH_STRING = "1/8";
	private static final String ONE_SIXTH_STRING = "1/6";
	private static final String ONE_FIFTH_STRING = "1/5";
	private static final String ONE_FOURTH_STRING = "1/4";
	private static final String ONE_THIRD_STRING = "1/3";
	private static final String ONE_HALF_STRING = "1/2";
	private static final String TWO_THIRDS_STRING = "2/3";
	private static final String THREE_FOURTHS_STRING = "3/4";

	private static final BigDecimal ONE_TWELFTH = new BigDecimal("0.08");
	private static final BigDecimal ONE_TENTH = new BigDecimal("0.10");
	private static final BigDecimal ONE_EIGHTH = new BigDecimal("0.12");
	private static final BigDecimal ONE_SIXTH = new BigDecimal("0.16");
	private static final BigDecimal ONE_FIFTH = new BigDecimal("0.20");
	private static final BigDecimal ONE_FOURTH = new BigDecimal("0.25");
	private static final BigDecimal ONE_THIRD = new BigDecimal("0.33");
	private static final BigDecimal ONE_HALF = new BigDecimal("0.50");
	private static final BigDecimal TWO_THIRDS = new BigDecimal("0.66");
	private static final BigDecimal THREE_FOURTHS = new BigDecimal("0.75");

	private static Map<BigDecimal, String> prettyFractionsMap = new ConcurrentHashMap<>();

	static {

		// Initialize the map with the well-defined fractions.
		prettyFractionsMap.put(ONE_TWELFTH, ONE_TWELFTH_STRING);
		prettyFractionsMap.put(ONE_TENTH, ONE_TENTH_STRING);
		prettyFractionsMap.put(ONE_EIGHTH, ONE_EIGHTH_STRING);
		prettyFractionsMap.put(ONE_SIXTH, ONE_SIXTH_STRING);
		prettyFractionsMap.put(ONE_FIFTH, ONE_FIFTH_STRING);
		prettyFractionsMap.put(ONE_FOURTH, ONE_FOURTH_STRING);
		prettyFractionsMap.put(ONE_THIRD, ONE_THIRD_STRING);
		prettyFractionsMap.put(ONE_HALF, ONE_HALF_STRING);
		prettyFractionsMap.put(TWO_THIRDS, TWO_THIRDS_STRING);
		prettyFractionsMap.put(THREE_FOURTHS, THREE_FOURTHS_STRING);
	}

	private NutritionUtils(){
	}

	/**
	 * Determines if a nutrient statement number is one of the reserved ones. These are the ones that are
	 * used when a nutrition statement is not present on the scale label.
	 *
	 * @param nutrientStatementNumber The nutrient statement number.
	 * @return  True if the nutrient statement number is reserved and false otherwise.
	 */
	public static boolean isReservedNutrientStatementNumber(Long nutrientStatementNumber) {
			return RESERVED_NUTRIENT_STATEMENTS.contains(nutrientStatementNumber);
	}

	/**
	 * Determines if a given value means that there is no RDA for the nutrient.
	 *
	 * @param rda The supplied RDA amount.
	 * @return True if there is no RDA for the amount and false otherwise.
	 */
	public static boolean isNoRda(BigDecimal rda) {

		if (Objects.isNull(rda)) {
			return true;
		}
		return NO_RDA.equals(rda.setScale(0, RoundingMode.UP));
	}

	/**
	 * Takes a serving size and converts it to a string formatted for the nutrition panel.
	 *
	 * @param servingSize The serving size as a number.
	 * @return The serving size converted to something appropriate for the nutrition panel.
	 */
	public static String servingSizeToText(BigDecimal servingSize) {

		// Get the fractional part of the serving size.
		BigDecimal fractionalMeasureQuantityPart = servingSize.remainder(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);

		// See if we have a nice way to print it.
		String prettyFaction = prettyFractionsMap.get(fractionalMeasureQuantityPart);

		if (Objects.isNull(prettyFaction)) {
			// If we get here, there's no pretty version of the fraction, so just use decimal formatter. This also
			// handles when there's no fractional part.
			DecimalFormat df = new DecimalFormat("#.##");
			return df.format(servingSize);
		}

		// If we got here, then there for sure is a a pretty-formatted fraction.
		Long wholeMeasureQuantityPart = servingSize.longValue();
		// If there's a whole part too, combine it with the fraction and return it.
		if (wholeMeasureQuantityPart > 0) {
			return String.format("%d %s", wholeMeasureQuantityPart, prettyFaction);
		}

		// If we got here, there was only pretty-formatted fraction and no whole part.
		return prettyFaction;
	}

	/**
	 * Formats a percent of recommended daily amount to what should be on the nutrition panel.
	 *
	 * @param rda The percent of the RDA to format.
	 * @return The RDA amount formatted for the label.
	 */
	public static Optional<String> rdaToFormattedRda(BigDecimal rda, BigDecimal percentageAmount) {

		if (Objects.isNull(rda) || Objects.isNull(percentageAmount)) {
			return Optional.empty();
		}

		// To get rid of different DB's handling of loading a BigDecimal, get the nutrient's RDA without a fractional part
		// rounding up. This'll be compared to zero when deciding if the nutrient statement record gets a daily value percent.
		if (isNoRda(rda)) {
			return Optional.empty();
		}

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return Optional.of(decimalFormat.format(percentageAmount));
	}

	/**
	 * Get nutrient statement id from the upc.
	 *
	 * @param upc The upc.
	 * @return The nutrient statement id.
	 */
	public static Long generateNutrientStatementIdFromUpc(Long upc) {

		String upcString = upc.toString();
		return Long.valueOf(upcString.substring(1, 6));
	}

	/**
	 * Executes a function to pull a unit of measure for a given description. If multiple or zero come back, it
	 * will throw an exception.
	 *
	 * @param uomDescription The description of the UOM.
	 * @param producer The function to call to get the UOM.
	 * @param <T> The type of object the call will return.
	 * @return The UOM for the passed in description.
	 */
	public static <T> T getUomCodeByUomDes(String uomDescription, Function<String, List<? extends T>> producer) {

		String uomDes = StringUtils.trimToEmpty(uomDescription);
		List<? extends T> scaleUnitOfMeasures = producer.apply(uomDes);

		if (scaleUnitOfMeasures.isEmpty()) {
			throw new IllegalArgumentException(String.format("'%s' is not a valid unit of measure.", uomDes));
		}
		if (scaleUnitOfMeasures.size() > MAX_UNITS_OF_MEASURE_FOR_DESCRIPTION) {
			throw new IllegalArgumentException(String.format("'%s' returns more than one unit of measure.", uomDes));
		}

		return scaleUnitOfMeasures.get(0);
	}
}
