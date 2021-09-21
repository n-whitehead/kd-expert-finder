package com.elsevier.kd.graph.model;

import java.util.*;

public class Concept {

    private String iri;
    private List<String> prefLabels = new ArrayList<>();
    private Map<String, List<Score>> authorScores = new HashMap<>();

    public Concept(String iri) {
        this.iri = iri;
    }

    public void addScore(String authorId, Score score) {
        if (!authorScores.containsKey(authorId)) {
            authorScores.put(authorId, new ArrayList<>());
        }
        authorScores.get(authorId).add(score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Concept concept = (Concept) o;
        return iri.equals(concept.iri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iri);
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

    public Map<String, List<Score>> getAuthorScores() {
        return authorScores;
    }

    public void setAuthorScores(Map<String, List<Score>> authorScores) {
        this.authorScores = authorScores;
    }
}
