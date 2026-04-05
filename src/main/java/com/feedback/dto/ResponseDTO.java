package com.feedback.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

public class ResponseDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Form ID is required")
    private Long formId;

    @NotNull(message = "Answers are required")
    private Map<Long, String> answers;

    private LocalDateTime submittedAt;

    // Constructors
    public ResponseDTO() {}

    public ResponseDTO(Long studentId, Long courseId, Long formId, Map<Long, String> answers) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.formId = formId;
        this.answers = answers;
    }

    public ResponseDTO(Long id, Long studentId, Long courseId, Long formId,
                      Map<Long, String> answers, LocalDateTime submittedAt) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.formId = formId;
        this.answers = answers;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}