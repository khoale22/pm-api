package com.heb.pm.util.audit;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * Represents an object containing a pair of objects. For example, using a map where the key has an int and a string
 * would be represented as Pair<Integer, String>.
 *
 * @param <T> The type of the first value in the pair.
 * @param <K> The type of the second value in the pair.
 *
 * @author m314029
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public final class Pair<T, K> {

	private static final int PRIME_NUMBER = 31;

	private T firstValue;
	private K secondValue;

	/**
	 * Constructs a new Pair.
	 *
	 * @param firstValue The left-side of the pair.
	 * @param secondValue The right-side of the pari.
	 */
	private Pair(T firstValue, K secondValue) {
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}

	/**
	 * Creates a new Tuple with a integer and a string.
	 *
	 * @param firstValue The integer value.
	 * @param secondValue The string value.
	 * @return Returns a tuple containing the integer and string values. The tuple will be non-empty.
	 */
	public static <T, K> Pair<T, K> of(T firstValue, K secondValue) {
		return new Pair<>(firstValue, secondValue);
	}

	/**
	 * Compares another object to this one. The key is the only thing used to determine equality.
	 *
	 * @param o The object to compare to.
	 * @return True if they are equal and false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair that = (Pair) o;

		if (!Objects.equals(firstValue, that.firstValue)) {
			return false;
		}
		return Objects.equals(secondValue, that.secondValue);
	}

	/**
	 * Returns a hash for this object. If two objects are equal, they will have the same hash. If they are not,
	 * they will (probably) have different hashes.
	 *
	 * @return The hash code for this object.
	 */
	@Override
	public int hashCode() {
		int result = firstValue != null ? firstValue.hashCode() : 0;
		result = PRIME_NUMBER * result + (secondValue != null ? secondValue.hashCode() : 0);
		return result;
	}
}
