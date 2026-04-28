package com.feedback.service;

import com.feedback.dto.CourseDTO;
import com.feedback.model.Course;
import com.feedback.model.Form;
import com.feedback.model.Question;
import com.feedback.model.Response;
import com.feedback.model.User;
import com.feedback.repository.CourseRepository;
import com.feedback.repository.FormRepository;
import com.feedback.repository.ResponseRepository;
import com.feedback.repository.UserRepository;
import com.feedback.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

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
        User admin = createUserIfMissing("admin", "admin@example.com", "Admin123!", User.Role.ADMIN, null, null);
        createUserIfMissing("faculty", "faculty@example.com", "Faculty123!", User.Role.FACULTY, "Dr. Smith", "A");
        User student = createUserIfMissing("2400032267", "2400032267@kluniversity.in", "Student123!", User.Role.STUDENT, null, "A");

        Map<String, Course> courseMap = seedCourseList();
        Map<String, Form> formMap = seedForms(admin, courseMap);
        seedSampleResponses(student, formMap, courseMap);
    }

    private User createUserIfMissing(String username, String email, String rawPassword, User.Role role, String facultyName, String section) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(rawPassword);
        user.setRole(role);
        user.setFacultyName(facultyName);
        user.setSection(section);
        return authService.save(user);
    }

    private Map<String, Course> seedCourseList() {
        String[][] courses = {
            {"24CC3010", "AWS CERTIFIED CLOUD PRACTITIONER", "CSE-1"},
            {"CRTCODL3V3", "CODING SKILLS TRAINING - ALGORITHMS", "CSE-1"},
            {"24CS2203", "DESIGN AND ANALYSIS OF ALGORITHMS", "CSE-3"},
            {"24CS2221F", "UX DESIGN", "CSE-2"},
            {"24CS2204", "CLOUD INFRASTRUCTURE AND SERVICES", "EL&GE"},
            {"24SDCS02", "FULL STACK APPLICATION DEVELOPMENT", "CSE-1"},
            {"24CS2101", "OPERATING SYSTEMS", "EL&GE"},
            {"24MT2012", "MATHEMATICAL OPTIMIZATION", "AI&DS"}
        };

        Map<String, Course> courseMap = new HashMap<>();
        for (String[] courseData : courses) {
            String code = courseData[0];
            String name = courseData[1];
            String offeredBy = courseData[2];

            Course course = courseRepository.findByCourseCode(code).orElseGet(() -> {
                Course c = new Course(code, name, offeredBy, null);
                c.setIsActive(true);
                return c;
            });

            course.setCourseName(name);
            course.setFacultyName(offeredBy);
            courseMap.put(code, courseRepository.save(course));
        }

        return courseMap;
    }

    private Map<String, Form> seedForms(User admin, Map<String, Course> courseMap) {
        Map<String, Form> formMap = new HashMap<>();

        Map<String, String[]> formDefinitions = Map.of(
            "24CC3010", new String[]{"AWS Certified Cloud Practitioner Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "CRTCODL3V3", new String[]{"Coding Skills Training Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24CS2203", new String[]{"DAA Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24CS2221F", new String[]{"UX Design Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24CS2204", new String[]{"Cloud Infrastructure Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24SDCS02", new String[]{"Full Stack Application Development Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24CS2101", new String[]{"Operating Systems Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"},
            "24MT2012", new String[]{"Mathematical Optimization Feedback", "Rate course content", "Rate faculty teaching", "Rate difficulty level", "Suggestions"}
        );

        for (Map.Entry<String, String[]> entry : formDefinitions.entrySet()) {
            String courseCode = entry.getKey();
            String[] definition = entry.getValue();
            String formTitle = definition[0];

            Form form = formRepository.findByCourseCode(courseCode)
                    .orElseGet(() -> {
                        Form created = new Form(formTitle, "Feedback form for " + courseCode, admin);
                        created.setCourseCode(courseCode);
                        created.setFacultyName(courseMap.get(courseCode) != null ? courseMap.get(courseCode).getFacultyName() : "");
                        created.setIsActive(true);
                        for (int i = 1; i < definition.length; i++) {
                            Question question = new Question(definition[i], Question.QuestionType.RATING, List.of(), i - 1);
                            created.addQuestion(question);
                        }
                        return formRepository.save(created);
                    });

            Course course = courseMap.get(courseCode);
            if (course != null && (course.getForm() == null || !course.getForm().getId().equals(form.getId()))) {
                course.setForm(form);
                courseRepository.save(course);
            }
            formMap.put(courseCode, form);
        }

        return formMap;
    }

    private void seedSampleResponses(User student, Map<String, Form> formMap, Map<String, Course> courseMap) {
        if (responseRepository.count() > 0 || student == null) {
            return;
        }

        addSampleResponse(student, courseMap.get("24CC3010"), formMap.get("24CC3010"), List.of("5", "4", "4", "Loved the practical labs"));
        addSampleResponse(student, courseMap.get("24CS2203"), formMap.get("24CS2203"), List.of("4", "5", "4", "Clear explanations"));
    }

    private void addSampleResponse(User student, Course course, Form form, List<String> answers) {
        if (student == null || course == null || form == null) {
            return;
        }

        Response response = new Response();
        response.setStudentId(student.getId());
        response.setCourseId(course.getId());
        response.setFormId(form.getId());

        Map<Long, String> answerMap = new HashMap<>();
        for (int i = 0; i < form.getQuestions().size(); i++) {
            if (i < answers.size()) {
                Question question = form.getQuestions().get(i);
                answerMap.put(question.getId(), answers.get(i));
            }
        }
        response.setAnswers(answerMap);
        responseRepository.save(response);
    }
}