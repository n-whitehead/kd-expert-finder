package com.elsevier.kd.graph.model;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class Score {

    private double citationCount;
    private double decay;
    private Integer ordinal;
    private int authorsCount;
    private Work work;

    public void calculateCitationCount() {
        for (Work reference : work.getReferences()) {
            double timeDifference = reference.getPublishedDate().until(YearMonth.now(), ChronoUnit.MONTHS);
            citationCount += Math.exp(-decay * (timeDifference / 12.0));
        }
    }

    public double getCitationCount() {
        return citationCount;
    }

    public void setCitationCount(double citationCount) {
        this.citationCount = citationCount;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public int getAuthorsCount() {
        return authorsCount;
    }

    public void setAuthorsCount(int authorsCount) {
        this.authorsCount = authorsCount;
    }
}
