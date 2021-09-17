package com.elsevier.kd.graph.service;

import javax.json.JsonArray;
import java.util.Set;

public interface ExpertFinderService {

    /**
     * Return top-k recommended authors given a set of concept iris.
     *
     * @param iris  - iris of the concepts
     * @param k     - number of authors results to return
     * @param decay - decay rate for citation count
     * @return Json array of citation counts
     */
    JsonArray findExperts(Set<String> iris, int k, double decay);
}
