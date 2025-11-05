package com.blackdot.ems.module.assessment.repository;

import com.blackdot.ems.shared.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByAssessmentIdOrderByCreatedAtAsc(Long assessmentId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.assessment.id = :assessmentId")
    Long countByAssessmentId(@Param("assessmentId") Long assessmentId);
    
    @Query("SELECT SUM(q.points) FROM Question q WHERE q.assessment.id = :assessmentId")
    Integer getTotalPointsByAssessmentId(@Param("assessmentId") Long assessmentId);
}