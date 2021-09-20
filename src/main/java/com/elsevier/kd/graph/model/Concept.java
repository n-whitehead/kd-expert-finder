package com.elsevier.kd.graph.model;

import java.util.ArrayList;
import java.util.List;

public class Concept {

    private String iri;
    private List<String> prefLabels = new ArrayList<>();
    private List<Work> works = new ArrayList<>();

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public List<String> getPrefLabels() {
        return prefLabels;
    }

    public void setPrefLabels(List<String> prefLabels) {
        this.prefLabels = prefLabels;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }
}
