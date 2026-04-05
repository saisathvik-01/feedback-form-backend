package com.feedback.dto;

public class FacultyRatingDto {
    private String faculty;
    private Double avgRating;

    public FacultyRatingDto() {
    }

    public FacultyRatingDto(String faculty, Double avgRating) {
        this.faculty = faculty;
        this.avgRating = avgRating;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
