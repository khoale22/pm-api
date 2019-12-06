/*
 * VocabularyRepository
 *
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.dictionary.repository;

import com.heb.pm.dictionary.entity.Vocabulary;
import com.heb.pm.dictionary.entity.VocabularyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Vocabulary entity.
 *
 * @author m314029
 * @since 1.2.0
 */
public interface VocabularyRepository extends JpaRepository<Vocabulary, VocabularyKey> {

	/**
	 * Searches for a vocabulary by word text ignoring case, and matching one of the given case codes.
	 *
	 * @param wordText Text to search by.
	 * @param caseCodes The case codes to search by.
	 * @return A list of vocabularies matching the search.
	 */
	List<Vocabulary> findByKeyWordTextIgnoreCaseAndKeyCaseCodeAttributeIn(String wordText, List<String> caseCodes);

	/**
	 * Searches for a vocabulary by word text ignoring case, and matching one of the given word codes.
	 *
	 * @param wordText Text to search by.
	 * @param wordCodes The word codes to search by.
	 * @return A list of vocabularies matching the search.
	 */
	List<Vocabulary> findByKeyWordTextIgnoreCaseAndWordCodeAttributeIn(String wordText, List<String> wordCodes);
}
