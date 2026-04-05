package com.feedback.repository;

import com.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByStudentId(Long studentId);
    List<Feedback> findByFacultyName(String facultyName);
    List<Feedback> findByCourseNameContainingIgnoreCase(String courseName);
    List<Feedback> findByFacultyNameContainingIgnoreCase(String facultyName);
    List<Feedback> findBySectionContainingIgnoreCase(String section);

    boolean existsByStudentIdAndCourseId(Long studentId, String courseId);
    List<Feedback> findByStudentIdAndCourseIdAndFacultyName(Long studentId, String courseId, String facultyName);

    @Query("SELECT f FROM Feedback f WHERE " +
           "(:course IS NULL OR LOWER(f.courseId) LIKE LOWER(CONCAT('%', :course, '%'))) AND " +
           "(:faculty IS NULL OR LOWER(f.facultyName) LIKE LOWER(CONCAT('%', :faculty, '%'))) AND " +
           "(:section IS NULL OR LOWER(f.section) LIKE LOWER(CONCAT('%', :section, '%'))) AND " +
           "(:semester IS NULL OR LOWER(f.semester) LIKE LOWER(CONCAT('%', :semester, '%'))) AND " +
           "(:academicYear IS NULL OR LOWER(f.academicYear) LIKE LOWER(CONCAT('%', :academicYear, '%')))")
    List<Feedback> findWithFilters(@Param("course") String course,
                                @Param("faculty") String faculty,
                                @Param("section") String section,
                                @Param("semester") String semester,
                                @Param("academicYear") String academicYear);
}