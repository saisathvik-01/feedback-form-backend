package com.feedback.controller;

import com.feedback.dto.DashboardSummaryDto;
import com.feedback.dto.FeedbackRequest;
import com.feedback.dto.FeedbackResponse;
import com.feedback.model.Feedback;
import com.feedback.service.FeedbackService;
import com.feedback.security.UserPrincipal;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest,
                                            Authentication authentication) {
        logger.info("Submit feedback called for user: {}", authentication.getName());
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            feedbackService.submitFeedback(feedbackRequest, userPrincipal.getId());
            return ResponseEntity.ok(Map.of("message", "Feedback submitted successfully"));
        } catch (Exception e) {
            logger.error("Error submitting feedback: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error submitting feedback: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback(
            Authentication authentication,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Feedback> feedbacks;
        
        // Faculty: only see their own feedback and cannot see student identity
        if ("FACULTY".equals(userPrincipal.getRole().toString())) {
            if (courseId != null || facultyName != null || section != null || semester != null || academicYear != null) {
                feedbacks = feedbackService.getFeedbackWithFilters(courseId, userPrincipal.getFacultyName(), section, semester, academicYear);
            } else {
                feedbacks = feedbackService.getFeedbackByFacultyName(userPrincipal.getFacultyName());
            }
        } else {
            // Admin: see all feedback
            if (courseId != null || facultyName != null || section != null || semester != null || academicYear != null) {
                feedbacks = feedbackService.getFeedbackWithFilters(courseId, facultyName, section, semester, academicYear);
            } else {
                feedbacks = feedbackService.getAllFeedback();
            }
        }

        List<FeedbackResponse> responses = feedbacks.stream()
                .map(feedback -> convertToResponse(feedback, userPrincipal))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-feedback")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Feedback> feedbacks = feedbackService.getFeedbackByStudentId(userPrincipal.getId());
        List<FeedbackResponse> responses = feedbacks.stream()
                .map(feedback -> convertToResponse(feedback))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/faculty-summary")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getFacultyFeedbackSummary(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Feedback> feedbacks = feedbackService.getFeedbackByFacultyName(userPrincipal.getFacultyName());

        // Calculate aggregated data for faculty
        int totalResponses = feedbacks.size();
        double averageRating = feedbacks.stream()
                .mapToDouble(f -> f.getRating() != null ? f.getRating() : 0.0)
                .average()
                .orElse(0.0);

        Map<String, Object> summary = Map.of(
                "facultyName", userPrincipal.getFacultyName(),
                "totalResponses", totalResponses,
                "averageRating", Math.round(averageRating * 100.0) / 100.0
        );

        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok(Map.of("message", "Feedback deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error deleting feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(feedbackService.getDashboardSummary());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY') or hasRole('STUDENT')")
    public ResponseEntity<String> exportFeedbackCsv(Authentication authentication,
                                                    @RequestParam(required = false) String courseId,
                                                    @RequestParam(required = false) String facultyName,
                                                    @RequestParam(required = false) String section,
                                                    @RequestParam(required = false) String semester,
                                                    @RequestParam(required = false) String academicYear) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Feedback> feedbacks;
        if (userPrincipal.getRole() == com.feedback.model.User.Role.STUDENT) {
            feedbacks = feedbackService.getFeedbackByStudentId(userPrincipal.getId());
        } else {
            feedbacks = feedbackService.getFeedbackWithFilters(courseId, facultyName, section, semester, academicYear);
        }

        String filename = "feedback_export.csv";
        String csv = buildCsv(feedbacks);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }

    private String buildCsv(List<Feedback> feedbacks) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String header = "ID,Student Name,Student Email,Course ID,Course Name,Faculty Name,Section,Semester,Academic Year,Rating,Comment,Submitted At";

        String rows = feedbacks.stream()
                .map(feedback -> String.join(",",
                        safeCsv(String.valueOf(feedback.getId())),
                        safeCsv(feedback.getStudentName()),
                        safeCsv(feedback.getStudentEmail()),
                        safeCsv(feedback.getCourseId()),
                        safeCsv(feedback.getCourseName()),
                        safeCsv(feedback.getFacultyName()),
                        safeCsv(feedback.getSection()),
                        safeCsv(feedback.getSemester()),
                        safeCsv(feedback.getAcademicYear()),
                        safeCsv(String.valueOf(feedback.getRating())),
                        safeCsv(feedback.getComment()),
                        safeCsv(feedback.getCreatedAt() != null ? feedback.getCreatedAt().format(formatter) : "")
                ))
                .collect(Collectors.joining("\n"));

        return header + "\n" + rows;
    }

    private String safeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private FeedbackResponse convertToResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getStudentName(),
                feedback.getStudentEmail(),
                feedback.getCourseId(),
                feedback.getCourseName(),
                feedback.getFacultyName(),
                feedback.getSection(),
                feedback.getSemester(),
                feedback.getAcademicYear(),
                feedback.getRating(),
                feedback.getRatings(),
                feedback.getComment(),
                feedback.getCreatedAt()
        );
    }

    // Overload for role-based visibility - Faculty should not see student info
    private FeedbackResponse convertToResponse(Feedback feedback, UserPrincipal userPrincipal) {
        // For Faculty: hide student identity, keep only aggregated data
        if ("FACULTY".equals(userPrincipal.getRole().toString())) {
            return new FeedbackResponse(
                    feedback.getId(),
                    "Anonymous",  // Hide student name
                    "hidden@kluniversity.in",  // Hide student email
                    feedback.getCourseId(),
                    feedback.getCourseName(),
                    feedback.getFacultyName(),
                    feedback.getSection(),
                    feedback.getSemester(),
                    feedback.getAcademicYear(),
                    feedback.getRating(),
                    feedback.getRatings(),
                    feedback.getComment(),
                    feedback.getCreatedAt()
            );
        }
        // For Admin: show all data
        return convertToResponse(feedback);
    }
}