package com.feedback.dto;

import com.feedback.model.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuestionDTO {

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Question type is required")
    private Question.QuestionType type;

    private List<String> options; // For MCQ questions

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    // Constructors
    public QuestionDTO() {}

    public QuestionDTO(String questionText, Question.QuestionType type, List<String> options, Integer orderIndex) {
        this.questionText = questionText;
        this.type = type;
        this.options = options;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
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
}