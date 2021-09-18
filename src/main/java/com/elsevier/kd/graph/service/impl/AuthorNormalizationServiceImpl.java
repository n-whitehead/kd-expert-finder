package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.CitationCount;
import com.elsevier.kd.graph.service.AuthorNormalizationService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorNormalizationServiceImpl implements AuthorNormalizationService {

    @Override
    public void normalize(Author author) {
        double citationCount = 0.0;
        for (CitationCount count : author.getCitations()) {
            int coauthorsCount = count.getCoathorsCount();
            double currentContribution = count.getContribution();
            count.setContribution(currentContribution / coauthorsCount);
        }
    }
}

