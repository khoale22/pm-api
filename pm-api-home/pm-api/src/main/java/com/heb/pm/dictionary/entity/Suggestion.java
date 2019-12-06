package com.heb.pm.dictionary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a suggestion for a Vocabulary record. It contains the original text, and the suggestion value text.
 * This would be used if the Vocabulary record has a 'fix' case code, but also when the user wants to see all available
 * suggestions for a word.
 *
 * @author m314029
 * @since 1.2.0
 */
@Entity
@Table(name = "suggestion")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Suggestion implements Serializable {

	private static final long serialVersionUID = 3042002280251334512L;

	@EmbeddedId
	@EqualsAndHashCode.Include
	private SuggestionKey key;

	@Column(name = "actv_sw")
	private Boolean active;

	@Column(name = "LST_UPDT_TS")
	private LocalDate lastUpdatedTimestamp;

	@Column(name = "LST_UPDT_UID")
	private String lastUpdatedUserId;
}
