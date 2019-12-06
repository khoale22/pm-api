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
 * Represents a spelling case code for a Vocabulary record. This means what the user has defined needs to be done on
 * this word before it can be considered 'valid'. Case values are:
 * articl: Part of speech is an Article
 * cap1: Capitalize the first letter
 * conjs: Coordinating conjunctions are lower cased
 * lower: Lower case all letters
 * preps: Preps word length>=4 r capitalized, shorter aren't
 * upper: Capitalize all letters
 * mixed: mixed
 *
 * @author m314029
 * @since 1.2.0
 */
@Entity
@Table(name = "case_cd")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class CaseCode implements Serializable {

	private static final long serialVersionUID = 4306624350627985429L;

	@Id
	@Column(name = "cs_cd")
	@EqualsAndHashCode.Include
	private String id;

	@Column(name = "cs_abb")
	private String abbreviation;

	@Column(name = "cs_des")
	private String description;

	/**
	 * Case code values.
	 */
	public enum Code {
		MIXED("mixed"),
		UPPER_CASE("upper"),
		CAPITALIZE_FIRST_LETTER("cap1"),
		LOWER_CASE_CODE("lower"),
		PREPOSITION("preps"),
		ARTICLE("artcl"),
		CONJUNCTION("conjs");

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
