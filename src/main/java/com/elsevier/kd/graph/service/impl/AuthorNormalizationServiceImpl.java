package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Work;
import com.elsevier.kd.graph.service.NormalizationService;

import javax.enterprise.context.ApplicationScoped;

/**
 * Normalize the citation count
 */
@ApplicationScoped
public class AuthorNormalizationServiceImpl implements NormalizationService {

    @Override
    public void normalize(Work work) {
        int totalAuthorCount = work.getAuthors().size();
        double currentContribution = work.getCitationCount();
        work.setCitationCount(currentContribution / totalAuthorCount);
    }
}
