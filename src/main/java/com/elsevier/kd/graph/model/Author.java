package com.elsevier.kd.graph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Author {

    private String id;
    private Integer ordinal;
    private double hindex;
    private List<Work> works = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addWork(Work work) {
        works.add(work);
    }

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

    public double getHindex() {
        return hindex;
    }

    public void setHindex(double hindex) {
        this.hindex = hindex;
    }

    public List<Work> getWorks() {
        return works;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
    }
}
