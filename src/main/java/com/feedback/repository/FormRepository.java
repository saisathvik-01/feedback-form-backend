package com.feedback.repository;

import com.feedback.model.Form;
import com.feedback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    List<Form> findByIsActiveTrue();

    List<Form> findByCreatedBy(User createdBy);

    List<Form> findByCreatedByAndIsActiveTrue(User createdBy);

    @Query("SELECT f FROM Form f WHERE f.isActive = true ORDER BY f.createdAt DESC")
    List<Form> findActiveFormsOrderByCreatedAtDesc();

    @Query("SELECT f FROM Form f WHERE f.createdBy = :user AND f.isActive = true ORDER BY f.createdAt DESC")
    List<Form> findActiveFormsByUserOrderByCreatedAtDesc(@Param("user") User user);
}