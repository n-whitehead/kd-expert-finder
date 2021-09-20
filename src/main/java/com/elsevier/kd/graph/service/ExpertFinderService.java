package com.elsevier.kd.graph.service;

import javax.json.JsonObject;
import java.util.Set;

public interface ExpertFinderService {

    /**
     * Return top-k recommended authors given a set of concept iris.
     * A decay of 0 means no decay is calculated (default).
     *
     * @param iris  - iris of the concepts
     * @param k     - number of authors results to return
     * @param decay - decay rate for citation count
     * @return Json of citation counts and other metadata
     */
    JsonObject findExperts(Set<String> iris, int k, double decay);
}
