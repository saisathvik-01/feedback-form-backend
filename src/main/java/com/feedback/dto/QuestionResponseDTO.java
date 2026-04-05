package com.feedback.dto;

import com.feedback.model.Question;
import java.time.LocalDateTime;
import java.util.List;

public class QuestionResponseDTO {

    private Long id;
    private String questionText;
    private Question.QuestionType type;
    private List<String> options;
    private Integer orderIndex;
    private LocalDateTime createdAt;

    // Constructors
    public QuestionResponseDTO() {}

    public QuestionResponseDTO(Long id, String questionText, Question.QuestionType type,
                              List<String> options, Integer orderIndex, LocalDateTime createdAt) {
        this.id = id;
        this.questionText = questionText;
        this.type = type;
        this.options = options;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Question.QuestionType getType() {
        return type;
    }

    public void setType(Question.QuestionType type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}