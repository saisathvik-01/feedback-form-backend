package com.feedback.controller;

import com.feedback.dto.FacultyRatingDto;
import com.feedback.dto.TrendDto;
import com.feedback.security.UserPrincipal;
import com.feedback.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/faculty")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<FacultyRatingDto>> getFacultyAverage(
            Authentication authentication,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<FacultyRatingDto> result = analyticsService.getAverageRatingPerFaculty(userPrincipal, courseId, facultyName, section, semester, academicYear);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/satisfaction")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<Map<String, Long>> getSatisfactionDistribution(
            Authentication authentication,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<String, Long> result = analyticsService.getSatisfactionDistribution(userPrincipal, courseId, facultyName, section, semester, academicYear);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<TrendDto>> getTrendOverTime(
            Authentication authentication,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<TrendDto> result = analyticsService.getTrendOverTime(userPrincipal, courseId, facultyName, section, semester, academicYear);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/rating-distribution")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(
            Authentication authentication,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String facultyName,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<Integer, Long> result = analyticsService.getRatingDistribution(userPrincipal, courseId, facultyName, section, semester, academicYear);
        return ResponseEntity.ok(result);
    }
}
