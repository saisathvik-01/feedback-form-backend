package com.feedback.repository;

import com.feedback.model.Form;
import com.feedback.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByForm(Form form);

    List<Question> findByFormOrderByOrderIndex(Form form);

    @Query("SELECT q FROM Question q WHERE q.form.id = :formId ORDER BY q.orderIndex")
    List<Question> findByFormIdOrderByOrderIndex(@Param("formId") Long formId);

    void deleteByForm(Form form);
}