package com.heb.pm.dictionary.repository;

import com.heb.pm.dictionary.entity.Suggestion;
import com.heb.pm.dictionary.entity.SuggestionKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Suggestion entity.
 *
 * @author m314029
 * @since 1.2.0
 */
public interface SuggestionRepository extends JpaRepository<Suggestion, SuggestionKey> {

    /**
     * Searches for suggestion records matching given word text ignoring case.
     *
     * @param wordText Word text to search for records on.
     * @return List of all suggestions with given word text.
     */
    List<Suggestion> findByKeyWordTextIgnoreCase(String wordText);
}
