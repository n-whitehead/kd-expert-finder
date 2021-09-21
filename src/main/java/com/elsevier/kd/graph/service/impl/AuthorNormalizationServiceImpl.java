package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Score;
import com.elsevier.kd.graph.service.NormalizationService;

import javax.enterprise.context.ApplicationScoped;

/**
 * Normalize the citation count
 */
@ApplicationScoped
public class AuthorNormalizationServiceImpl implements NormalizationService {

    @Override
    public void normalize(Score score) {
        int totalAuthorCount = score.getAuthorsCount();
        double currentContribution = score.getCitationCount();
        score.setCitationCount(currentContribution / totalAuthorCount);
    }
}
