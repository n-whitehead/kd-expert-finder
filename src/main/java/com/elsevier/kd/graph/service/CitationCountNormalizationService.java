package com.elsevier.kd.graph.service;

import com.elsevier.kd.graph.model.Work;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

/**
 * Normalization strategy for citation counts
 */
public interface CitationCountNormalizationService {

    /**
     * Normalize citation counts for a given work.
     * A decay of 1 means no decay is calculated (default).
     *
     * @param work - work with citations to be normalized.
     */
    void normalize(Work work, double decay);

    /**
     * Calculate the time decay based on the published date of the referencing works.
     * A decay of 1 means no decay is calculated (default).
     *
     * @param reference - referencing work we are calculating decay for.
     * @param decay     - rate of decay.
     * @return - time decay citation count.
     */
    default double calculateTimeDecay(Work reference, double decay) {
        double timeDifference = reference.getPublishedDate().until(YearMonth.now(), ChronoUnit.MONTHS);
        return Math.exp(-decay * (timeDifference / 12.0));
    }
}
