package com.feedback.controller;

import com.feedback.dto.ResponseDTO;
import com.feedback.security.UserPrincipal;
import com.feedback.service.ResponseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitResponse(@Valid @RequestBody ResponseDTO responseDTO,
                                            Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            responseDTO.setStudentId(userPrincipal.getId());

            ResponseDTO submittedResponse = responseService.submitResponse(responseDTO);
            return ResponseEntity.ok(Map.of(
                "message", "Feedback submitted successfully",
                "response", submittedResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error submitting response: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResponseById(@PathVariable Long id) {
        try {
            ResponseDTO response = responseService.getResponseById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error retrieving response: " + e.getMessage()));
        }
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ResponseDTO>> getStudentResponses(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<ResponseDTO> responses = responseService.getResponsesByStudent(userPrincipal.getId());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<ResponseDTO>> getResponsesByCourse(@PathVariable Long courseId) {
        try {
            List<ResponseDTO> responses = responseService.getResponsesByCourse(courseId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/form/{formId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseDTO>> getResponsesByForm(@PathVariable Long formId) {
        try {
            List<ResponseDTO> responses = responseService.getResponsesByForm(formId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/course/{courseId}/form/{formId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<ResponseDTO>> getResponsesByCourseAndForm(@PathVariable Long courseId,
                                                                         @PathVariable Long formId) {
        try {
            List<ResponseDTO> responses = responseService.getResponsesByCourseAndForm(courseId, formId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> checkStudentSubmission(@PathVariable Long courseId,
                                                    Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            boolean hasSubmitted = responseService.hasStudentSubmittedForCourse(
                    userPrincipal.getId(), courseId);
            return ResponseEntity.ok(Map.of("hasSubmitted", hasSubmitted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseDTO>> getAllResponses() {
        try {
            List<ResponseDTO> responses = responseService.getAllResponses();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}