package com.elsevier.kd.graph.service;

import com.elsevier.kd.graph.model.CitationCount;

import java.util.List;

/**
 * Service to calculate citation-based metrics index scores, like h-index, g-index, h5-index, etc...
 * Extend this and inject into service for metrics calculations.
 */
public interface IndexMetricService {

    /**
     * Calculate a custom metric based on citation counts.
     *
     * @param citationCounts - citation counts of a set of referencing works.
     * @return - index score.
     */
    int calculate(List<CitationCount> citationCounts);
}
