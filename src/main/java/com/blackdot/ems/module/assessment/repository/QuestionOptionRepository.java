package com.blackdot.ems.module.assessment.repository;

import com.blackdot.ems.shared.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    
    List<QuestionOption> findByQuestionIdOrderById(Long questionId);
    
    void deleteByQuestionId(Long questionId);
}