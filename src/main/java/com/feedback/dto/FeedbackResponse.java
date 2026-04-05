package com.feedback.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class FeedbackResponse {
    private Long id;
    private String studentName;
    private String studentEmail;
    private String courseId;
    private String courseName;
    private String facultyName;
    private String section;
    private String semester;
    private String academicYear;
    private Integer rating;
    private Map<String, Integer> ratings;
    private String comment;
    private LocalDateTime createdAt;

    // Constructors
    public FeedbackResponse() {}

    public FeedbackResponse(Long id, String studentName, String studentEmail, String courseId,
                           String courseName, String facultyName, String section, String semester,
                           String academicYear, Integer rating, Map<String, Integer> ratings, String comment,
                           LocalDateTime createdAt) {
        this.id = id;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseName = courseName;
        this.facultyName = facultyName;
        this.section = section;
        this.semester = semester;
        this.academicYear = academicYear;
        this.rating = rating;
        this.ratings = ratings;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Integer> ratings) {
        this.ratings = ratings;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}