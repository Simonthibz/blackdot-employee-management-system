package com.blackdot.ems.module.dashboard.service;

import com.blackdot.ems.module.assessment.repository.AssessmentRepository;
import com.blackdot.ems.module.assessment.repository.AssessmentResultRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.shared.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentResultRepository assessmentResultRepository;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        stats.put("totalEmployees", userRepository.count());
        stats.put("activeEmployees", userRepository.countByIsActiveTrue());
        stats.put("dataCapturers", userRepository.findActiveUsersByRole(ERole.ROLE_DATA_CAPTURER).size());
        
        // Assessment statistics
        stats.put("totalAssessments", assessmentRepository.count());
        stats.put("activeAssessments", assessmentRepository.findByIsActiveTrueOrderByCreatedAtDesc().size());
        
        // Current quarter statistics
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        List<AssessmentResult> quarterlyResults = assessmentResultRepository.findByQuarterAndYear(currentQuarter, currentYear);
        stats.put("quarterlyAttempts", quarterlyResults.size());
        
        long passedCount = quarterlyResults.stream().filter(AssessmentResult::getPassed).count();
        stats.put("quarterlyPassed", passedCount);
        stats.put("quarterlyFailed", quarterlyResults.size() - passedCount);
        
        // Calculate pass rate
        double passRate = quarterlyResults.isEmpty() ? 0.0 : (double) passedCount / quarterlyResults.size() * 100;
        stats.put("passRate", Math.round(passRate * 100.0) / 100.0);
        
        // Incomplete assessments
        List<AssessmentResult> incompleteAssessments = assessmentResultRepository
                .findByQuarterAndYearAndCompletedAtIsNull(currentQuarter, currentYear);
        stats.put("incompleteAssessments", incompleteAssessments.size());
        
        return stats;
    }

    public Map<String, Object> getDataCapturerAssessments(Long userId) {
        Map<String, Object> data = new HashMap<>();
        
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        // Get user's assessments for current quarter
        List<AssessmentResult> currentQuarterResults = assessmentResultRepository
                .findByQuarterAndYear(currentQuarter, currentYear)
                .stream()
                .filter(result -> result.getUser().getId().equals(userId))
                .toList();
        
        data.put("currentQuarter", currentQuarter.name());
        data.put("currentYear", currentYear);
        data.put("currentQuarterAssessments", currentQuarterResults);
        
        // Get all user's historical results
        List<AssessmentResult> allResults = assessmentResultRepository.findByUserIdOrderByCreatedAtDesc(userId);
        data.put("allResults", allResults);
        
        // Calculate user statistics
        long totalAttempts = allResults.size();
        long passedAttempts = allResults.stream().filter(AssessmentResult::getPassed).count();
        double userPassRate = totalAttempts == 0 ? 0.0 : (double) passedAttempts / totalAttempts * 100;
        
        data.put("totalAttempts", totalAttempts);
        data.put("passedAttempts", passedAttempts);
        data.put("userPassRate", Math.round(userPassRate * 100.0) / 100.0);
        
        return data;
    }

    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> profile = new HashMap<>();
        
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            profile.put("user", user);
            profile.put("roles", user.getRoles());
        }
        
        return profile;
    }

    private Quarter getCurrentQuarter() {
        int month = LocalDateTime.now().getMonthValue();
        if (month <= 3) return Quarter.Q1;
        else if (month <= 6) return Quarter.Q2;
        else if (month <= 9) return Quarter.Q3;
        else return Quarter.Q4;
    }
}