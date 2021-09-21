package com.elsevier.kd.graph.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Work {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
    private String id;
    private YearMonth publishedDate;
    private List<Work> references;

    public Work(String id, String publishedDate) {
        this.id = id;
        this.publishedDate = YearMonth.parse(publishedDate, dtf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Work work = (Work) o;
        return Objects.equals(id, work.id);
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
}
