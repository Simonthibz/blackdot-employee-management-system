package com.blackdot.ems.module.assessment.repository;

import com.blackdot.ems.shared.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByIsActiveTrue();
    
    List<Assessment> findByIsActiveTrueOrderByCreatedAtDesc();
}