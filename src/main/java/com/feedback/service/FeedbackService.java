package com.feedback.service;

import com.feedback.dto.DashboardSummaryDto;
import com.feedback.dto.FeedbackRequest;
import com.feedback.dto.FacultyRatingDto;
import com.feedback.dto.TrendDto;
import com.feedback.model.Feedback;
import com.feedback.model.User;
import com.feedback.repository.FeedbackRepository;
import com.feedback.repository.UserRepository;
import com.feedback.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    public Feedback submitFeedback(FeedbackRequest request, Long studentId) {
        java.util.Objects.requireNonNull(studentId, "studentId must not be null");
        Optional<User> userOpt = userRepository.findById(studentId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Check for duplicate feedback: same student, course, and faculty
        List<Feedback> existingFeedback = feedbackRepository.findByStudentIdAndCourseIdAndFacultyName(
            studentId, request.getCourseId(), request.getFacultyName());
        
        if (!existingFeedback.isEmpty()) {
            throw new RuntimeException("Feedback already submitted for this subject vs faculty. You cannot submit twice for the same course and faculty.");
        }

        Feedback feedback = new Feedback();
        feedback.setStudentId(studentId);
        feedback.setStudentName(user.getUsername());
        feedback.setStudentEmail(user.getEmail());
        feedback.setCourseId(request.getCourseId());
        feedback.setCourseName(request.getCourseName());
        feedback.setFacultyName(request.getFacultyName());
        feedback.setSection(request.getSection() != null ? request.getSection() : user.getSection());
        feedback.setRating(request.getRating() != null ? request.getRating() : calculateRatingFromMap(request.getRatings()));
        feedback.setComment(request.getComment());
        feedback.setRatings(request.getRatings());
        feedback.setCreatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> getFeedbackWithFilters(String courseId, String facultyName,
                                                 String section, String semester, String academicYear) {
        return feedbackRepository.findWithFilters(courseId, facultyName, section, semester, academicYear);
    }

    public List<Feedback> getFeedbackByStudentId(Long studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    public List<Feedback> getFeedbackByFacultyName(String facultyName) {
        return feedbackRepository.findByFacultyName(facultyName);
    }

    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(id);
    }

    public DashboardSummaryDto getDashboardSummary() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        int totalFeedback = feedbacks.size();
        double averageRating = feedbacks.stream()
                .mapToDouble(this::extractScore)
                .average()
                .orElse(0.0);

        int totalCourses = (int) feedbacks.stream()
                .map(Feedback::getCourseName)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        int totalFaculty = (int) feedbacks.stream()
                .map(Feedback::getFacultyName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .count();

        Map.Entry<String, Double> topFacultyEntry = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getFacultyName,
                        Collectors.averagingDouble(this::extractScore)))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank())
                .max(Map.Entry.comparingByValue())
                .orElse(Map.entry("N/A", 0.0));

        return new DashboardSummaryDto(
                totalFeedback,
                round(averageRating),
                totalCourses,
                totalFaculty,
                topFacultyEntry.getKey(),
                round(topFacultyEntry.getValue())
        );
    }

    public List<FacultyRatingDto> getAverageRatingPerFaculty(UserPrincipal principal,
                                                             String courseId,
                                                             String facultyName,
                                                             String section,
                                                             String semester,
                                                             String academicYear) {
        List<Feedback> feedbacks = getFeedbackForPrincipal(principal, courseId, facultyName, section, semester, academicYear);
        return feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getFacultyName,
                        Collectors.averagingDouble(this::extractScore)))
                .entrySet().stream()
                .map(entry -> new FacultyRatingDto(entry.getKey(), round(entry.getValue())))
                .sorted(Comparator.comparing(FacultyRatingDto::getAvgRating).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, Long> getSatisfactionDistribution(UserPrincipal principal,
                                                          String courseId,
                                                          String facultyName,
                                                          String section,
                                                          String semester,
                                                          String academicYear) {
        List<Feedback> feedbacks = getFeedbackForPrincipal(principal, courseId, facultyName, section, semester, academicYear);
        long poor = feedbacks.stream().filter(f -> extractScore(f) <= 2.0).count();
        long average = feedbacks.stream().filter(f -> extractScore(f) == 3.0).count();
        long good = feedbacks.stream().filter(f -> extractScore(f) >= 4.0).count();

        return Map.of(
                "poor", poor,
                "average", average,
                "good", good
        );
    }

    public List<TrendDto> getTrendOverTime(UserPrincipal principal,
                                           String courseId,
                                           String facultyName,
                                           String section,
                                           String semester,
                                           String academicYear) {
        List<Feedback> feedbacks = getFeedbackForPrincipal(principal, courseId, facultyName, section, semester, academicYear);

        return feedbacks.stream()
                .filter(f -> f.getCreatedAt() != null)
                .collect(Collectors.groupingBy(f -> f.getCreatedAt().toLocalDate(), Collectors.averagingDouble(this::extractScore)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendDto(entry.getKey().toString(), round(entry.getValue())))
                .collect(Collectors.toList());
    }

    private List<Feedback> getFeedbackForPrincipal(UserPrincipal principal,
                                                   String courseId,
                                                   String facultyName,
                                                   String section,
                                                   String semester,
                                                   String academicYear) {
        String effectiveFaculty = facultyName;
        if (principal.getRole() == User.Role.FACULTY) {
            effectiveFaculty = principal.getFacultyName();
        }
        return feedbackRepository.findWithFilters(courseId, effectiveFaculty, section, semester, academicYear);
    }

    private int calculateRatingFromMap(Map<String, Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0;
        }
        return (int) Math.round(ratings.values().stream().mapToDouble(Integer::doubleValue).average().orElse(0.0));
    }

    private double extractScore(Feedback feedback) {
        if (feedback.getRating() != null) {
            return feedback.getRating();
        }
        if (feedback.getRatings() != null && !feedback.getRatings().isEmpty()) {
            return feedback.getRatings().values().stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
        }
        return 0.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}