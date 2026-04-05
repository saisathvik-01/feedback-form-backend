package com.feedback.service;

import com.feedback.dto.FormDTO;
import com.feedback.dto.FormResponseDTO;
import com.feedback.dto.QuestionDTO;
import com.feedback.dto.QuestionResponseDTO;
import com.feedback.model.Form;
import com.feedback.model.Question;
import com.feedback.model.User;
import com.feedback.repository.FormRepository;
import com.feedback.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public FormResponseDTO createForm(FormDTO formDTO, User createdBy) {
        Form form = new Form();
        form.setTitle(formDTO.getTitle());
        form.setDescription(formDTO.getDescription());
        form.setIsActive(formDTO.getIsActive());
        form.setCreatedBy(createdBy);

        // Save form first to get ID
        Form savedForm = formRepository.save(form);

        // Create and save questions
        List<Question> questions = formDTO.getQuestions().stream()
                .map(questionDTO -> {
                    Question question = new Question();
                    question.setQuestionText(questionDTO.getQuestionText());
                    question.setType(questionDTO.getType());
                    question.setOptions(questionDTO.getOptions());
                    question.setOrderIndex(questionDTO.getOrderIndex());
                    question.setForm(savedForm);
                    return question;
                })
                .collect(Collectors.toList());

        questionRepository.saveAll(questions);
        savedForm.setQuestions(questions);

        return convertToResponseDTO(savedForm);
    }

    public List<FormResponseDTO> getAllActiveForms() {
        return formRepository.findActiveFormsOrderByCreatedAtDesc().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public FormResponseDTO getFormById(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));

        if (!form.getIsActive()) {
            throw new RuntimeException("Form is not active");
        }

        return convertToResponseDTO(form);
    }

    public List<FormResponseDTO> getFormsByUser(User user) {
        return formRepository.findActiveFormsByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FormResponseDTO updateForm(Long id, FormDTO formDTO, User user) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));

        // Check if user is the creator or admin
        if (!form.getCreatedBy().getId().equals(user.getId()) &&
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to update this form");
        }

        form.setTitle(formDTO.getTitle());
        form.setDescription(formDTO.getDescription());
        form.setIsActive(formDTO.getIsActive());

        // Delete existing questions
        questionRepository.deleteByForm(form);

        // Create new questions
        List<Question> questions = formDTO.getQuestions().stream()
                .map(questionDTO -> {
                    Question question = new Question();
                    question.setQuestionText(questionDTO.getQuestionText());
                    question.setType(questionDTO.getType());
                    question.setOptions(questionDTO.getOptions());
                    question.setOrderIndex(questionDTO.getOrderIndex());
                    question.setForm(form);
                    return question;
                })
                .collect(Collectors.toList());

        questionRepository.saveAll(questions);
        form.setQuestions(questions);

        Form updatedForm = formRepository.save(form);
        return convertToResponseDTO(updatedForm);
    }

    @Transactional
    public void deleteForm(Long id, User user) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));

        // Check if user is the creator or admin
        if (!form.getCreatedBy().getId().equals(user.getId()) &&
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this form");
        }

        formRepository.delete(form);
    }

    private FormResponseDTO convertToResponseDTO(Form form) {
        List<QuestionResponseDTO> questionDTOs = form.getQuestions().stream()
                .map(question -> new QuestionResponseDTO(
                        question.getId(),
                        question.getQuestionText(),
                        question.getType(),
                        question.getOptions(),
                        question.getOrderIndex(),
                        question.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new FormResponseDTO(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getCreatedBy().getUsername(),
                form.getIsActive(),
                form.getCreatedAt(),
                form.getUpdatedAt(),
                questionDTOs
        );
    }
}