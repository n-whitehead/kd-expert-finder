package com.elsevier.kd.graph.model;

import java.util.List;

public class Author {

    private String id;
    private Integer ordinal;
    private List<CitationCount> citations;
    private double hindex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public List<CitationCount> getCitations() {
        return citations;
    }

    public void setCitations(List<CitationCount> citations) {
        this.citations = citations;
    }

    public double getHindex() {
        return hindex;
    }

    public void setHindex(double hindex) {
        this.hindex = hindex;
    }
}
