package com.elsevier.kd.graph.model;

import java.util.Objects;

public class ConceptDetail {

    private Concept concept;
    private double hindexContribution;

    public ConceptDetail(Concept concept, double hindex) {
        this.concept = concept;
        this.hindexContribution = hindex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConceptDetail that = (ConceptDetail) o;
        return Objects.equals(concept, that.concept);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concept);
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public double getHindexContribution() {
        return hindexContribution;
    }

    public void setHindexContribution(double hindexContribution) {
        this.hindexContribution = hindexContribution;
    }
}
