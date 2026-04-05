package com.feedback.dto;

import com.feedback.model.Course;

public class CourseDTO {

    private Long id;
    private String courseName;
    private Long formId;
    private Boolean isActive;

    // Constructors
    public CourseDTO() {}

    public CourseDTO(Long id, String courseName, Long formId, Boolean isActive) {
        this.id = id;
        this.courseName = courseName;
        this.formId = formId;
        this.isActive = isActive;
    }

    public static CourseDTO fromCourse(Course course) {
        return new CourseDTO(
            course.getId(),
            course.getCourseName(),
            course.getForm() != null ? course.getForm().getId() : null,
            course.getIsActive()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}