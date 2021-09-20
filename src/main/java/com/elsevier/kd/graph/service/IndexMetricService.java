package com.elsevier.kd.graph.service;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.Work;

import java.util.List;

/**
 * Service to calculate citation-based metrics index scores, like h-index, g-index, h5-index, etc...
 * Extend this and inject into service for metrics calculations.
 */
public interface IndexMetricService {

    /**
     * Calculate an index metric for an author.
     * References must be added to the object, or hindex will return 0.
     *
     * @param author
     * @return
     */
    int calculate(Author author);
}
