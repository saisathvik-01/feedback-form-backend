package com.feedback.dto;

public class DashboardSummaryDto {
    private int totalFeedback;
    private double averageRating;
    private int totalCourses;
    private int totalFaculty;
    private String topFaculty;
    private double topFacultyRating;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(int totalFeedback, double averageRating, int totalCourses, int totalFaculty, String topFaculty, double topFacultyRating) {
        this.totalFeedback = totalFeedback;
        this.averageRating = averageRating;
        this.totalCourses = totalCourses;
        this.totalFaculty = totalFaculty;
        this.topFaculty = topFaculty;
        this.topFacultyRating = topFacultyRating;
    }

    public int getTotalFeedback() {
        return totalFeedback;
    }

    public void setTotalFeedback(int totalFeedback) {
        this.totalFeedback = totalFeedback;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public int getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(int totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public String getTopFaculty() {
        return topFaculty;
    }

    public void setTopFaculty(String topFaculty) {
        this.topFaculty = topFaculty;
    }

    public double getTopFacultyRating() {
        return topFacultyRating;
    }

    public void setTopFacultyRating(double topFacultyRating) {
        this.topFacultyRating = topFacultyRating;
    }
}
