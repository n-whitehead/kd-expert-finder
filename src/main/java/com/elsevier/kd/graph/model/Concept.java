package com.elsevier.kd.graph.model;

import java.util.ArrayList;
import java.util.List;

public class Concept {

    private String iri;
    private List<String> prefLabels = new ArrayList<>();
    private List<Author> authors = new ArrayList<>();

    public Concept() {
    }

    public Concept(String iri) {
        this.iri = iri;
    }

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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
