package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Score;
import com.elsevier.kd.graph.service.NormalizationService;
import com.elsevier.kd.graph.service.qualifiers.OrdinalNormalization;

import javax.enterprise.context.ApplicationScoped;

@OrdinalNormalization
@ApplicationScoped
public class OrdinalAuthorNormalizationServiceImpl implements NormalizationService {

    @Override
    public void normalize(Score score) {
        int totalAuthorCount = score.getAuthorsCount();
        double currentContribution = score.getCitationCount();
        Integer ordinal = score.getOrdinal();
        if (ordinal == null) {
            score.setCitationCount(currentContribution / totalAuthorCount);
        } else {
            score.setCitationCount((2 * ((double) totalAuthorCount - ordinal.doubleValue() + 1)) / ((double) totalAuthorCount * ((double) totalAuthorCount + 1)));
        }
    }
}
