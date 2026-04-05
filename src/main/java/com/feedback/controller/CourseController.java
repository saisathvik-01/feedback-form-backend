package com.feedback.controller;

import com.feedback.dto.CourseDTO;
import com.feedback.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/initialize")
    public ResponseEntity<?> initializeDefaultCourses() {
        try {
            courseService.initializeDefaultCourses();
            return ResponseEntity.ok(Map.of("message", "Default courses initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error initializing courses: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        try {
            List<CourseDTO> courses = courseService.getAllActiveCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error retrieving course: " + e.getMessage()));
        }
    }

    @GetMapping("/by-name/{courseName}")
    public ResponseEntity<?> getCourseByCourseNameWithForm(@PathVariable String courseName) {
        try {
            CourseDTO course = courseService.getCourseByCourseNameWithForm(courseName);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error retrieving course: " + e.getMessage()));
        }
    }

    @PutMapping("/{courseId}/assign-form/{formId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignFormToCourse(@PathVariable Long courseId, @PathVariable Long formId) {
        try {
            CourseDTO updatedCourse = courseService.assignFormToCourse(courseId, formId);
            return ResponseEntity.ok(Map.of(
                "message", "Form assigned to course successfully",
                "course", updatedCourse
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error assigning form: " + e.getMessage()));
        }
    }

    @GetMapping("/by-form/{formId}")
    public ResponseEntity<List<CourseDTO>> getCoursesByForm(@PathVariable Long formId) {
        try {
            List<CourseDTO> courses = courseService.getCoursesByForm(formId);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}