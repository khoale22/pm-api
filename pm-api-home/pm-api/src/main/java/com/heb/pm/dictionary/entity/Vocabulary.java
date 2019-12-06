/*
 * Vocabulary
 *
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.dictionary.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a vocabulary source that is a created (or imported) text that a user can, or cannot enter as a word, and
 * how the text should be cased (i.e. lowercase or uppercase).
 *
 * @author m314029
 * @since 1.2.0
 */
@Entity
@Table(name = "vocabulary")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Vocabulary implements Serializable {

	private static final long serialVersionUID = -2651360227769794237L;

	@EmbeddedId
	@EqualsAndHashCode.Include
	private VocabularyKey key;

	@Column(name = "word_cd")
	private String wordCodeAttribute;

	@Column(name = "actv_sw")
	private Boolean active;

	@Column(name = "LST_UPDT_TS")
	private LocalDate lastUpdatedTimestamp;

	@Column(name = "LST_UPDT_UID")
	private String lastUpdatedUserId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "word_cd", referencedColumnName = "word_cd", insertable = false, updatable = false)
	private WordCode wordCode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cs_cd", referencedColumnName = "cs_cd", insertable = false, updatable = false)
	private CaseCode caseCode;
}
