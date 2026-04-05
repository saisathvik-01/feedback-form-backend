package com.feedback.service;

import com.feedback.dto.ResponseDTO;
import com.feedback.model.Response;
import com.feedback.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Transactional
    public ResponseDTO submitResponse(ResponseDTO responseDTO) {
        // Check for duplicate submission
        Optional<Response> existingResponse = responseRepository.findByStudentIdAndCourseIdAndFormId(
                responseDTO.getStudentId(),
                responseDTO.getCourseId(),
                responseDTO.getFormId()
        );

        if (existingResponse.isPresent()) {
            throw new RuntimeException("You have already submitted feedback for this course. Duplicate submissions are not allowed.");
        }

        Response response = new Response();
        response.setStudentId(responseDTO.getStudentId());
        response.setCourseId(responseDTO.getCourseId());
        response.setFormId(responseDTO.getFormId());
        response.setAnswers(responseDTO.getAnswers());

        Response savedResponse = responseRepository.save(response);
        return convertToDTO(savedResponse);
    }

    public ResponseDTO getResponseById(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found with id: " + id));
        return convertToDTO(response);
    }

    public List<ResponseDTO> getResponsesByStudent(Long studentId) {
        return responseRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseDTO> getResponsesByCourse(Long courseId) {
        return responseRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseDTO> getResponsesByForm(Long formId) {
        return responseRepository.findByFormId(formId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ResponseDTO> getResponsesByCourseAndForm(Long courseId, Long formId) {
        return responseRepository.findByCourseAndForm(courseId, formId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean hasStudentSubmittedForCourse(Long studentId, Long courseId) {
        return responseRepository.existsByStudentAndCourse(studentId, courseId);
    }

    public List<ResponseDTO> getAllResponses() {
        return responseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ResponseDTO convertToDTO(Response response) {
        return new ResponseDTO(
                response.getId(),
                response.getStudentId(),
                response.getCourseId(),
                response.getFormId(),
                response.getAnswers(),
                response.getSubmittedAt()
        );
    }
}