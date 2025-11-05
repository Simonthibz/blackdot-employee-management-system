package com.blackdot.ems.module.assessment.repository;

import com.blackdot.ems.shared.entity.AssessmentResult;
import com.blackdot.ems.shared.entity.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    
    List<AssessmentResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<AssessmentResult> findByAssessmentIdOrderByCreatedAtDesc(Long assessmentId);
    
    Optional<AssessmentResult> findByUserIdAndAssessmentIdAndQuarterAndYear(
            Long userId, Long assessmentId, Quarter quarter, Integer year);
    
    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.quarter = :quarter AND ar.year = :year")
    List<AssessmentResult> findByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
    
    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.quarter = :quarter AND ar.year = :year AND ar.completedAt IS NULL")
    List<AssessmentResult> findByQuarterAndYearAndCompletedAtIsNull(@Param("quarter") Quarter quarter, @Param("year") Integer year);
    
    @Query("SELECT ar FROM AssessmentResult ar JOIN ar.user u JOIN u.roles r WHERE r.name = 'ROLE_DATA_CAPTURER' AND ar.quarter = :quarter AND ar.year = :year")
    List<AssessmentResult> findDataCapturerResultsByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
    
    @Query("SELECT COUNT(ar) FROM AssessmentResult ar WHERE ar.passed = true AND ar.quarter = :quarter AND ar.year = :year")
    Long countPassedByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
    
    @Query("SELECT COUNT(ar) FROM AssessmentResult ar WHERE ar.quarter = :quarter AND ar.year = :year")
    Long countTotalByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
}