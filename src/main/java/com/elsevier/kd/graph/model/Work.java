package com.elsevier.kd.graph.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class Work {

    private String id;
    private YearMonth publishedDate;
    private List<Work> references;
    private List<Author> authors;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");

    public Work() {
    }

    public Work(String id, String publishedDate) {
        this.id = id;
        this.publishedDate = YearMonth.parse(publishedDate, dtf);
    }

    public double normalizeCitationCount(double decayRate) {
        return 0.0;
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

    public List<Work> getReferences() {
        return references;
    }

    public void setReferences(List<Work> references) {
        this.references = references;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
