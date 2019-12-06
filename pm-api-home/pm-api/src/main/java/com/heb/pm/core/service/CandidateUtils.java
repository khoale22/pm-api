package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.PhSubCommodity;
import com.heb.pm.pam.model.Lane;
import com.heb.pm.pam.model.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Properties;

/**
 * Common functions across all the Candidate processing classes.
 */
public final class CandidateUtils {

	private static final Logger logger = LoggerFactory.getLogger(CandidateUtils.class);

	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String UNIT_OF_MEASURE_EACH = "EACH";
	private static final String UNIT_OF_MEASURE_DECIMAL_FORMAT = "#.##";

	private static final Properties UOM_CONVERSION_LIST = new Properties();

	public static final String NONE_ITEM_WEIGHT_TYPE = "None";
	public static final String CATCH_WEIGHT_ITEM_WEIGHT_TYPE = "Catch Weight";
	public static final String VARIABLE_WEIGHT_ITEM_WEIGHT_TYPE = "Variable Weight";

	public static final BigDecimal CUBE_FACTOR = new BigDecimal("1728.000");

	static {
		InputStream is = null;
		try {
			ClassPathResource classPathResource = new ClassPathResource("/uoms.properties");
			is = classPathResource.getInputStream();
			UOM_CONVERSION_LIST.load(is);
		} catch (IOException e) {
			logger.error(String.format("Unable to load item UOM conversions: \"%s\",", e.getLocalizedMessage()));
		} finally {

			if (Objects.nonNull(is)) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(String.format("Unable to close item UOM conversions file: \"%s\",", e.getLocalizedMessage()));
				}
			}
		}
	}

	/**
	 * Converts a Retail UOM to an item UOM.
	 *
	 * @param retailUom The Retail UOM.
	 * @return The Item UOM.
	 */
	public static String convertToItemUom(String retailUom) {
		return UOM_CONVERSION_LIST.getProperty(retailUom.trim());
	}

	/**
	 * Converts a boolean to Y or N for the EMD database. Null will return "N".
	 *
	 * @param value The value to convert.
	 * @return "Y" if true and "N" if false.
	 */
	public static String booleanToSwitch(Boolean value) {
		if (Objects.isNull(value)) {
			return NO;
		}
		return value ? YES : NO;
	}

	/**
	 * Takes the size and unit of measure code and converts them to the combined field (ITEM_SIZE_TXT).
	 *
	 * @param size The size of the product.
	 * @param unitOfMeasureCode The UOM for the product.
	 * @return A field combining the two.
	 */
	public static String sizeAndUnitOfMeasureToCombined(Double size, String unitOfMeasureCode) {

		if (UNIT_OF_MEASURE_EACH.equals(unitOfMeasureCode)) {
			return unitOfMeasureCode;
		}

		DecimalFormat decimalFormat = new DecimalFormat(UNIT_OF_MEASURE_DECIMAL_FORMAT);
		return String.format("%s%s", decimalFormat.format(size), unitOfMeasureCode);
	}

	/**
	 * Takes a Land and Warehouse and returns the bicep number for the two.
	 *
	 * @param lane The Lane to use.
	 * @param warehouse The Warehouse to use.
	 * @return The bicep for the supplied Lane and Warehouse.
	 */
	public static Long bicepFromLaneAndWarehouse(Lane lane, Warehouse warehouse) {

		String bicepAsString = String.format("%s%s", warehouse.getOmiId(), lane.getId());
		return Long.valueOf(bicepAsString);
	}

	/**
	 * Takes whether or not a product is taxable and a product's sub-commodity and returns its Vertex tax categroy.
	 *
	 * @param taxable Whether or not a product is taxable.
	 * @param subCommodity The sub-commodity to tie the product to.
	 * @return The Vertex tax category.
	 */
	public static String vertexTaxCategoryFromTaxableAndSubCommodity(boolean taxable, PhSubCommodity subCommodity) {
		 return taxable ? subCommodity.getTaxCategoryCode() : subCommodity.getNonTaxCategoryCode();
	}


    /**
     * Returns catch weight switch from weight type code.
     *
     * @param itemWeightType the weight type code.
     * @return catch weight switch from weight type code.
     */
    public static String getCatchWeightSwitch(String itemWeightType) {
    	if (itemWeightType == null || itemWeightType.isBlank()) {
    		return CandidateUtils.NO;
		} else {
			return itemWeightType.trim().equalsIgnoreCase(CATCH_WEIGHT_ITEM_WEIGHT_TYPE) ?
					CandidateUtils.YES : CandidateUtils.NO;
		}
    }
    /**
     * Returns varaiable weight switch from weight type code.
     *
     * @param itemWeightType the weight type code.
     * @return varaiable weight switch from weight type code.
     */
    public static String getVariableWeightSwitch(String itemWeightType) {
		if (itemWeightType == null || itemWeightType.isBlank()) {
			return CandidateUtils.NO;
		} else {
			return itemWeightType.trim().equalsIgnoreCase(VARIABLE_WEIGHT_ITEM_WEIGHT_TYPE) ?
					CandidateUtils.YES : CandidateUtils.NO;
		}
    }

	/**
	 * Calculates cube for a given length, width, and height. It will set the scale to 3, which is what the tables
	 * are set at.
	 *
	 * @param length The length.
	 * @param width The width.
	 * @param height The height.
	 * @return Cube.
	 */
    public static BigDecimal calculateCube(BigDecimal length, BigDecimal width, BigDecimal height) {
		return calculateCube(length, width, height, 3);
	}

	/**
	 * Calculates cube for a given length, width, and height. It will set the scale to 3, which is what the tables
	 * are set at. It will return a number scaled to a provided precision.
	 *
	 * @param length The length.
	 * @param width The width.
	 * @param height The height.
	 * @param scale The scale to use in the result.
	 * @return Cube.
	 */
	public static BigDecimal calculateCube(BigDecimal length, BigDecimal width, BigDecimal height, int scale) {
		BigDecimal interim = length.multiply(width)
				.multiply(height);
		return interim.divide(CUBE_FACTOR, new MathContext(interim.precision(), RoundingMode.HALF_UP))
				.setScale(scale, RoundingMode.HALF_UP);
	}

	/**
	 * Hide the constructor.
	 */
	private CandidateUtils() {
	}

}
