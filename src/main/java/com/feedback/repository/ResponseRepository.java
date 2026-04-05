package com.feedback.repository;

import com.feedback.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByStudentId(Long studentId);

    List<Response> findByCourseId(Long courseId);

    List<Response> findByFormId(Long formId);

    List<Response> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Response> findByStudentIdAndCourseIdAndFormId(Long studentId, Long courseId, Long formId);

    @Query("SELECT r FROM Response r WHERE r.courseId = :courseId AND r.formId = :formId")
    List<Response> findByCourseAndForm(@Param("courseId") Long courseId, @Param("formId") Long formId);

    @Query("SELECT COUNT(r) > 0 FROM Response r WHERE r.studentId = :studentId AND r.courseId = :courseId")
    boolean existsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}