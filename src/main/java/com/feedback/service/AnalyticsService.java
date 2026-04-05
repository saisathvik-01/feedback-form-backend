package com.feedback.service;

import com.feedback.dto.FacultyRatingDto;
import com.feedback.dto.TrendDto;
import com.feedback.model.Feedback;
import com.feedback.model.User;
import com.feedback.repository.FeedbackRepository;
import com.feedback.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<FacultyRatingDto> getAverageRatingPerFaculty(UserPrincipal principal,
                                                             String courseId,
                                                             String facultyName,
                                                             String section,
                                                             String semester,
                                                             String academicYear) {
        List<Feedback> feedbacks = loadFeedbackForAnalytics(principal, courseId, facultyName, section, semester, academicYear);

        Map<String, Double> facultyAverages = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getFacultyName,
                        Collectors.averagingDouble(this::extractFeedbackRating)));

        return facultyAverages.entrySet().stream()
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
        List<Feedback> feedbacks = loadFeedbackForAnalytics(principal, courseId, facultyName, section, semester, academicYear);

        long poor = feedbacks.stream().filter(f -> extractFeedbackRating(f) <= 2.0).count();
        long average = feedbacks.stream().filter(f -> extractFeedbackRating(f) == 3.0).count();
        long good = feedbacks.stream().filter(f -> extractFeedbackRating(f) >= 4.0).count();

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
        List<Feedback> feedbacks = loadFeedbackForAnalytics(principal, courseId, facultyName, section, semester, academicYear);

        return feedbacks.stream()
                .filter(f -> f.getCreatedAt() != null)
                .collect(Collectors.groupingBy(f -> f.getCreatedAt().toLocalDate(), Collectors.averagingDouble(this::extractFeedbackRating)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendDto(entry.getKey().toString(), round(entry.getValue())))
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> getRatingDistribution(UserPrincipal principal,
                                                    String courseId,
                                                    String facultyName,
                                                    String section,
                                                    String semester,
                                                    String academicYear) {
        List<Feedback> feedbacks = loadFeedbackForAnalytics(principal, courseId, facultyName, section, semester, academicYear);

        return feedbacks.stream()
                .filter(f -> f.getRating() != null)
                .collect(Collectors.groupingBy(f -> f.getRating().intValue(), Collectors.counting()));
    }

    private List<Feedback> loadFeedbackForAnalytics(UserPrincipal principal,
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

    private double extractFeedbackRating(Feedback feedback) {
        if (feedback.getRating() != null) {
            return feedback.getRating();
        }
        if (feedback.getRatings() != null && !feedback.getRatings().isEmpty()) {
            return feedback.getRatings().values().stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
