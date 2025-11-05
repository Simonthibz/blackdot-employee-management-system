package com.blackdot.ems.module.assessment.repository;

import com.blackdot.ems.shared.entity.QuarterlySchedule;
import com.blackdot.ems.shared.entity.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuarterlyScheduleRepository extends JpaRepository<QuarterlySchedule, Long> {
    
    List<QuarterlySchedule> findByUserIdAndCompletedFalse(Long userId);
    
    Optional<QuarterlySchedule> findByUserIdAndAssessmentIdAndQuarterAndYear(
            Long userId, Long assessmentId, Quarter quarter, Integer year);
    
    @Query("SELECT qs FROM QuarterlySchedule qs WHERE qs.completed = false AND qs.dueDate < :date")
    List<QuarterlySchedule> findOverdueSchedules(@Param("date") LocalDateTime date);
    
    @Query("SELECT qs FROM QuarterlySchedule qs WHERE qs.completed = false AND qs.reminderSent = false AND qs.dueDate BETWEEN :startDate AND :endDate")
    List<QuarterlySchedule> findSchedulesForReminder(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT qs FROM QuarterlySchedule qs WHERE qs.quarter = :quarter AND qs.year = :year")
    List<QuarterlySchedule> findByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
    
    @Query("SELECT qs FROM QuarterlySchedule qs JOIN qs.user u JOIN u.roles r WHERE r.name = 'ROLE_DATA_CAPTURER' AND qs.quarter = :quarter AND qs.year = :year")
    List<QuarterlySchedule> findDataCapturerSchedulesByQuarterAndYear(@Param("quarter") Quarter quarter, @Param("year") Integer year);
}