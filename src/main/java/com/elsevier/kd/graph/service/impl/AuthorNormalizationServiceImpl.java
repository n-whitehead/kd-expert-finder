package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Author;
import com.elsevier.kd.graph.model.Work;
import com.elsevier.kd.graph.service.CitationCountNormalizationService;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorNormalizationServiceImpl implements CitationCountNormalizationService {

    public void normalize(Work work, double decay) {
        double citationCount = 0.0;
        for (Work reference : work.getReferences()) {
            citationCount += this.calculateTimeDecay(reference, decay);
        }
        double authorCount = work.getAuthors().size();
        for (Author author : work.getAuthors()) {
            author.addCitationCount(citationCount / authorCount);
        }
    }
}
