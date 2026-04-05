package com.feedback.config;

import com.feedback.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseService courseService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default courses on application startup
        courseService.initializeDefaultCourses();
    }
}