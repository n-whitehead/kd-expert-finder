package com.elsevier.kd.graph.service;

import com.elsevier.kd.graph.model.Author;

/**
 * Normalization strategy for citation counts
 */
public interface AuthorNormalizationService {

    /**
     * Normalize citation counts for a given author.
     *
     * @param author - <code>Author</code>> with <code>CitationCount</code> to be normalized.
     */
    void normalize(Author author);
}
