package com.elsevier.kd.graph.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AuthorScore {

    private String id;
    private double hindex;
    private Set<ConceptDetail> concepts = new HashSet<>();

    public AuthorScore(String id, double hindex) {
        this.id = id;
        this.hindex = hindex;
    }

    public AuthorScore(String id, double hindex, Set<ConceptDetail> concepts) {
        this.id = id;
        this.hindex = hindex;
        this.concepts = concepts;
    }

    public void addConcepts(Set<ConceptDetail> concepts) {
        this.concepts.addAll(concepts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthorScore that = (AuthorScore) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHindex() {
        return hindex;
    }

    public void setHindex(double hindex) {
        this.hindex = hindex;
    }

    public Set<ConceptDetail> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<ConceptDetail> concepts) {
        this.concepts = concepts;
    }
}
