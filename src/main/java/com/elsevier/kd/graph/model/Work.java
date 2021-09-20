package com.elsevier.kd.graph.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Work {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
    private String id;
    private YearMonth publishedDate;
    private List<Author> authors;
    private List<Work> references;
    private double citationCount;
    private double decay;

    public Work(String id, String publishedDate) {
        this.id = id;
        this.publishedDate = YearMonth.parse(publishedDate, dtf);
    }

    public void calculateTimeDecay() {
        for (Work reference : references) {
            double timeDifference = reference.getPublishedDate().until(YearMonth.now(), ChronoUnit.MONTHS);
            citationCount += Math.exp(-decay * (timeDifference / 12.0));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public YearMonth getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(YearMonth publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Work> getReferences() {
        return references;
    }

    public void setReferences(List<Work> references) {
        this.references = references;
    }

    public double getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(double citationCount) {
        this.citationCount = citationCount;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }
}
