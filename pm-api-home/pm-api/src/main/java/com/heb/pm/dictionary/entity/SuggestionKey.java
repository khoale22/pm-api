package com.heb.pm.dictionary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Represents a key on the Suggestion table.
 *
 * @author m314029
 * @since 1.2.0
 */
@Embeddable
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class SuggestionKey implements Serializable {

	private static final long serialVersionUID = 1711722945440365673L;

	@Column(name = "word_txt")
	private String wordText;

	@Column(name = "corr_txt")
	private String correctedText;
}
