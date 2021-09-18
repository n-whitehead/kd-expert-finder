package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.CitationCount;
import com.elsevier.kd.graph.service.IndexMetricService;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class HIndexServiceImpl implements IndexMetricService {

    @Override
    public int calculate(List<CitationCount> citationCounts) {
        int hindex = 0;
        List<CitationCount> reversedCitations = new ArrayList<>(citationCounts);
        reversedCitations.sort(Comparator.comparing(CitationCount::getContribution));
        for (int h = 0; h < reversedCitations.size(); h++) {
            if (h >= reversedCitations.get(h).getContribution()) {
                hindex = h;
            }
        }
        if (hindex == 0) {
            hindex = citationCounts.size();
        }
        return hindex;
    }
}
