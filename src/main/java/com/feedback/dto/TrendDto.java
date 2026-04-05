package com.feedback.dto;

public class TrendDto {
    private String date;
    private Double avgRating;

    public TrendDto() {
    }

    public TrendDto(String date, Double avgRating) {
        this.date = date;
        this.avgRating = avgRating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
