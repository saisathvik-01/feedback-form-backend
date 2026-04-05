package com.feedback.dto;

import com.feedback.model.Question;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class FormDTO {

    @NotBlank(message = "Form title is required")
    private String title;

    private String description;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @NotEmpty(message = "At least one question is required")
    @Valid
    private List<QuestionDTO> questions;

    // Constructors
    public FormDTO() {}

    public FormDTO(String title, String description, Boolean isActive, List<QuestionDTO> questions) {
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}