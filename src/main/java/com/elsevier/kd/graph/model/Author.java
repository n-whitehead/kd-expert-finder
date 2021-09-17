package com.elsevier.kd.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Author {

    private String id;
    private Integer ordinal;
    private List<Double> citationCounts = new ArrayList<>();
    private int hindex;

    public void calculate() {
        hindex = 0;
        citationCounts.sort(Collections.reverseOrder());
        for (int h = 0; h < citationCounts.size(); h++) {
            if (h >= citationCounts.get(h)) {
                hindex = h;
            }
        }
        if (hindex == 0) {
            hindex = citationCounts.size();
        }
    }

    public void addCitationCount(Double count) {
        if (count != null) {
            this.citationCounts.add(count);
        }
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

    public List<Double> getCitationCounts() {
        return citationCounts;
    }

    public int getHindex() {
        return hindex;
    }
}
