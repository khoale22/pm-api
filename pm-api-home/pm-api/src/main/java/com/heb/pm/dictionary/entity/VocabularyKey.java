package com.heb.pm.dictionary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Represents a key on the Vocabulary table.
 *
 * @author m314029
 * @since 1.2.0
 */
@Embeddable
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class VocabularyKey implements Serializable {

	private static final long serialVersionUID = -2309261869117829254L;

	@Column(name = "word_txt")
	private String wordText;

	@Column(name = "cs_cd")
	private String caseCodeAttribute;
}
