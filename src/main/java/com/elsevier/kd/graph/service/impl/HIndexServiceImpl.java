package com.elsevier.kd.graph.service.impl;

import com.elsevier.kd.graph.model.Score;
import com.elsevier.kd.graph.service.IndexMetricService;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculate H-index for an author.
 */
@ApplicationScoped
public class HIndexServiceImpl implements IndexMetricService {

    @Override
    public int calculate(List<Score> scores) {
        int hindex = 0;
        List<Double> reversedCitations = scores.stream().map(Score::getCitationCount).collect(Collectors.toList());
        reversedCitations.sort(Collections.reverseOrder());
        for (int h = 0; h < reversedCitations.size(); h++) {
            if (h >= reversedCitations.get(h)) {
                hindex = h;
            }
        }
        if (hindex == 0) {
            hindex = reversedCitations.size();
        }
        return hindex;
    }
}
