package com.elsevier.kd.graph.service;

import com.elsevier.kd.graph.model.Score;

/**
 * Normalization strategy for citation counts
 */
public interface NormalizationService {

    /**
     * Normalize citation counts for a given author.
     */
    void normalize(Score score);
}
