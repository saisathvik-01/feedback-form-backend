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
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.findByCourseCode(courseDTO.getCourseCode()).isPresent()) {
            throw new RuntimeException("Course with code " + courseDTO.getCourseCode() + " already exists");
        }
        
        Course course = new Course();
        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseName(courseDTO.getCourseName());
        course.setFacultyName(courseDTO.getFacultyName());
        course.setIsActive(true);
        
        Course savedCourse = courseRepository.save(course);
        return CourseDTO.fromCourse(savedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        course.setIsActive(false);
        courseRepository.save(course);
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
        String[][] courses = {
            {"FSAD", "FullStack Application Development", "Faculty Name"},
            {"MATH", "Mathematical Optimization", "Faculty Name"},
            {"DAA", "Design and Analysis of Algorithms", "Faculty Name"},
            {"OS", "Operating Systems", "Faculty Name"},
            {"UX", "UX Design", "Faculty Name"},
            {"CLOUD", "Cloud Infrastructure", "Faculty Name"}
        };

        for (String[] courseData : courses) {
            if (courseRepository.findByCourseCode(courseData[0]).isEmpty()) {
                Course course = new Course(courseData[0], courseData[1], courseData[2], null);
                courseRepository.save(course);
            }
        }
    }
}