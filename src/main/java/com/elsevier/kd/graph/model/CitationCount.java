package com.elsevier.kd.graph.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CitationCount {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM");
    private String referencingId;
    private YearMonth publishedDate;
    private Double contribution;
    private int coathorsCount;

    public CitationCount(String referencingId, String publishedDate, double decay, int coauthorsCount) {
        this.referencingId = referencingId;
        this.publishedDate = YearMonth.parse(publishedDate, dtf);
        this.contribution = calculateTimeDecay(decay);
        this.coathorsCount = coauthorsCount;
    }

    private double calculateTimeDecay(double decay) {
        double timeDifference = publishedDate.until(YearMonth.now(), ChronoUnit.MONTHS);
        return Math.exp(-decay * (timeDifference / 12.0));
    }

    public YearMonth getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(YearMonth publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getReferencingId() {
        return referencingId;
    }

    public void setReferencingId(String referencingId) {
        this.referencingId = referencingId;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public int getCoathorsCount() {
        return coathorsCount;
    }

    public void setCoathorsCount(int coathorsCount) {
        this.coathorsCount = coathorsCount;
    }
}
