package com.feedback.service;

import com.feedback.dto.CourseDTO;
import com.feedback.model.Course;
import com.feedback.model.Form;
import com.feedback.repository.CourseRepository;
import com.feedback.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FormRepository formRepository;

    public List<CourseDTO> getAllActiveCourses() {
        return courseRepository.findByIsActiveTrue().stream()
                .map(CourseDTO::fromCourse)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return CourseDTO.fromCourse(course);
    }

    public CourseDTO getCourseByCourseNameWithForm(String courseName) {
        Course course = courseRepository.findByCourseName(courseName)
                .orElseThrow(() -> new RuntimeException("Course not found with name: " + courseName));
        return CourseDTO.fromCourse(course);
    }

    @Transactional
    public CourseDTO assignFormToCourse(Long courseId, Long formId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + formId));

        course.setForm(form);
        Course updatedCourse = courseRepository.save(course);
        return CourseDTO.fromCourse(updatedCourse);
    }

    @Transactional
    public void createOrUpdateCourse(String courseName, Long formId) {
        Course course = courseRepository.findByCourseName(courseName)
                .orElse(new Course(courseName, null));

        if (formId != null) {
            Form form = formRepository.findById(formId)
                    .orElseThrow(() -> new RuntimeException("Form not found with id: " + formId));
            course.setForm(form);
        }

        courseRepository.save(course);
    }

    public List<CourseDTO> getCoursesByForm(Long formId) {
        return courseRepository.findByFormId(formId).stream()
                .map(CourseDTO::fromCourse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void initializeDefaultCourses() {
        String[] courseNames = {
            "FullStack Application Development",
            "Mathematical Optimization",
            "Design and Analysis of Algorithms",
            "Operating Systems",
            "UX Design",
            "Cloud Infrastructure"
        };

        for (String courseName : courseNames) {
            if (courseRepository.findByCourseName(courseName).isEmpty()) {
                Course course = new Course(courseName, null);
                courseRepository.save(course);
            }
        }
    }
}