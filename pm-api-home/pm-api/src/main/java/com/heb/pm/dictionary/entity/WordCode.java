package com.heb.pm.dictionary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents a spelling word code for a Vocabulary record.
 * Word codes can be banned, fix, review, suggest, or valid.
 *
 * @author m314029
 * @since 1.2.0
 */
@Entity
@Table(name = "word_cd")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class WordCode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "word_cd")
	@EqualsAndHashCode.Include
	private String id;

	@Column(name = "word_abb")
	private String abbreviation;

	@Column(name = "word_des")
	private String description;

	/**
	 * Word code values.
	 */
	public enum Code {
		BANNED("baned"),
		FIX("fix"),
		REVIEW("reviw"),
		SUGGEST("sugst"),
		VALID("valid");

		private String name;

		Code(String name) {
			this.name = name;
		}

		/**
		 * Gets name.
		 *
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}
	}
}
