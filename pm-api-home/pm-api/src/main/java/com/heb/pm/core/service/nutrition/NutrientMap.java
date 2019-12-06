package com.heb.pm.core.service.nutrition;

import java.util.Objects;

/**
 * Holds all nutrient ids for nutrient and scale nutrient.
 *
 * @author vn73545
 * @since 1.9.0
 */
/* default */ enum NutrientMap {

	VITAMIN_A(null, 1L, "vitaminA"),
	VITAMIN_C(null, 2L, "vitaminC"),
	THIAMINE(null, 3L, "thiamine"),
	RIBOFLAVIN(null, 4L, "riboflavin"),
	NIACIN(null, 5L, "niacin"),
	CALCIUM(12L, 6L, "calcium"),
	IRON(13L, 7L, "iron"),
	VITAMIN_D(11L, 8L, "vitaminD"),
	VITAMIN_E(null, 9L, "vitaminE"),
	VITAMIN_B6(null, 10L, "vitaminB6"),
	FOLIC_ACID(null, 11L, "folicAcid"),
	VITAMIN_B12(null, 12L, "vitaminB12"),
	PHOSPHORUS(null, 13L, "phosphorus"),
	IODINE(null, 14L, "iodine"),
	MAGNESIUM(null, 15L, "magnesium"),
	ZINC(null, 16L, "zinc"),
	COPPER(null, 17L, "copper"),
	BIOTIN(null, 18L, "biotin"),
	PANTOTHENIC_ACID(null, 19L, "pantothenicAcid"),
	ASCORBIC_ACID(null, 20L, "ascorbicAcid"),
	FOLACIN(null, 21L, "folacin"),
	VITAMIN_B2(null, 22L, "vitaminB2"),
	VITAMIN_B1(null, 23L, "vitaminB1"),
	CALORIES(null, 100L, "calories"),
	CALORIES_FROM_FAT(null, 101L, "caloriesFromFat"),
	TOTAL_FAT(1L, 102L, "totalFat"),
	SATURATED_FAT(2L, 103L, "saturatedFat"),
	CHOLESTEROL(4L, 104L, "cholesterol"),
	TOTAL_CARBOHYDRATE(6L, 105L, "totalCarbohydrates"),
	DIETARY_FIBER(7L, 106L, "dietaryFiber"),
	SODIUM(5L, 107L, "sodium"),
	SUGARS(null, 108L, "sugars"),
	PROTEIN(10L, 109L, "protein"),
	POTASSIUM(14L, 110L, "potassium"),
	TRANS_FAT(3L, 113L, "transFat"),
	SUGAR_ALCOHOL(15L, 120L, "sugarAlcohol"),
	TOTAL_SUGARS(8L, null, "totalSugars"),
	INCLUDES_ADDED_SUGARS(9L, null, "includesAddedSugars");

	private Long id;
	private Long scaleNutrientId;
	private String attributeName;

	/**
	 * Constructor for a nutrient id.
	 *
	 * @param id            The value of the id in the table.
	 * @param attributeName The value of the attributeName in the table.
	 */
	NutrientMap(Long id, Long scaleNutrientId, String attributeName) {
		this.id = id;
		this.scaleNutrientId = scaleNutrientId;
		this.attributeName = attributeName;
	}

	/**
	 * Returns the id that represents each value (the value that will go into the database).
	 *
	 * @return The id that represents each value.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Returns the scale nutrient id that represents each value (the value that will go into the database).
	 *
	 * @return The scale nutrient id that represents each value.
	 */
	public Long getScaleNutrientId() {
		return this.scaleNutrientId;
	}

	/**
	 * Returns the attributeName that represents each value (the value that will go into the database).
	 *
	 * @return The attributeName that represents each value.
	 */
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * Returns a NutrientMap for a particular attribute name. Will throw an error if not found.
	 *
	 * @param attributeName The name of the attribute to look for.
	 * @return The NutrientMap with the given name.
	 */
	public static NutrientMap of(String attributeName) {

		String trimmedName = attributeName.trim();
		for (NutrientMap nutrientMap : values()) {
			if (nutrientMap.getAttributeName().equals(trimmedName)) {
				return nutrientMap;
			}
		}

		throw new IllegalArgumentException(String.format("'%s' is not a valid attribute name.", trimmedName));
	}

	/**
	 * Returns a NutrientMap for a particular legacy (old tables) ID. Will throw an error if not found.
	 *
	 * @param legacyId The ID of the attribute to look for.
	 * @return The NutrientMap with the given ID.
	 */
	protected static NutrientMap ofLegacyId(Long legacyId) {

		for (NutrientMap nutrientMap : values()) {
			if (Objects.isNull(nutrientMap.getScaleNutrientId())) {
				continue;
			}
			if (nutrientMap.getScaleNutrientId().equals(legacyId)) {
				return nutrientMap;
			}
		}

		throw new IllegalArgumentException(String.format("%d is not a valid attribute ID.", legacyId));
	}

	/**
	 * Returns a NutrientMap for a particular ID (new tables). Will throw an error if not found.
	 *
	 * @param id The ID of the attribute to look for.
	 * @return The NutrientMap with the given ID.
	 */
	protected static NutrientMap ofId(Long id) {

		for (NutrientMap nutrientMap : values()) {
			if (Objects.isNull(nutrientMap.getId())) {
				continue;
			}
			if (nutrientMap.getId().equals(id)) {
				return nutrientMap;
			}
		}

		throw new IllegalArgumentException(String.format("%d is not a valid attribute ID.", id));
	}
}
