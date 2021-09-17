package com.elsevier.kd.graph.model;

import java.util.List;

public class Concept {

    private String iri;
    private List<String> prefLabels;
    private List<Work> subjectOf;

    public Concept() {
        
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public List<Work> getSubjectOf() {
        return subjectOf;
    }

    public void setSubjectOf(List<Work> subjectOf) {
        this.subjectOf = subjectOf;
    }

    public List<String> getPrefLabels() {
        return prefLabels;
    }

    public void setPrefLabels(List<String> prefLabels) {
        this.prefLabels = prefLabels;
    }
}
