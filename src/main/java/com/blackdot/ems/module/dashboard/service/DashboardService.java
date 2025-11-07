package com.blackdot.ems.module.dashboard.service;

import com.blackdot.ems.module.assessment.repository.AssessmentRepository;
import com.blackdot.ems.module.assessment.repository.AssessmentResultRepository;
import com.blackdot.ems.module.department.repository.DepartmentRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.task.repository.TaskRepository;
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
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Employee statistics
        long totalEmployees = userRepository.countByIsActiveTrue();
        long totalDataCapturers = userRepository.findActiveUsersByRole(ERole.ROLE_DATA_CAPTURER).size();
        stats.put("totalEmployees", totalEmployees);
        stats.put("activeEmployees", totalEmployees);
        stats.put("dataCapturers", totalDataCapturers);
        
        // Department statistics
        long totalDepartments = departmentRepository.count();
        long activeDepartments = departmentRepository.findByIsActive(true).size();
        stats.put("totalDepartments", totalDepartments);
        stats.put("activeDepartments", activeDepartments);
        
        // Assessment statistics
        long activeAssessments = assessmentRepository.findByIsActiveTrueOrderByCreatedAtDesc().size();
        stats.put("activeAssessments", activeAssessments);
        
        // Current quarter info
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        // Get ALL completed assessment results (not just current quarter)
        List<AssessmentResult> allResults = assessmentResultRepository.findAll();
        List<AssessmentResult> completedResults = allResults.stream()
                .filter(r -> r.getCompletedAt() != null)
                .toList();
        
        long allPassedCount = completedResults.stream()
                .filter(r -> r.getPassed() != null && r.getPassed())
                .count();
        long allFailedCount = completedResults.size() - allPassedCount;
        
        stats.put("quarterlyPassed", allPassedCount);
        stats.put("quarterlyFailed", allFailedCount);
        stats.put("quarterlyAttempts", completedResults.size());
        
        // Calculate overall pass rate (only for completed assessments)
        double passRate = completedResults.isEmpty() ? 0.0 : (double) allPassedCount / completedResults.size() * 100;
        stats.put("passRate", Math.round(passRate));
        
        // Pending/Incomplete assessments (ALL pending, not just current quarter)
        List<AssessmentResult> incompleteAssessments = allResults.stream()
                .filter(r -> r.getCompletedAt() == null)
                .toList();
        stats.put("incompleteAssessments", incompleteAssessments.size());
        
        // Task statistics
        List<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);
        List<Task> completedTasks = taskRepository.findByStatus(TaskStatus.COMPLETED);
        List<Task> cancelledTasks = taskRepository.findByStatus(TaskStatus.CANCELLED);
        List<Task> onHoldTasks = taskRepository.findByStatus(TaskStatus.ON_HOLD);
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        
        stats.put("pendingTasks", pendingTasks.size());
        stats.put("inProgressTasks", inProgressTasks.size());
        stats.put("completedTasks", completedTasks.size());
        stats.put("cancelledTasks", cancelledTasks.size());
        stats.put("onHoldTasks", onHoldTasks.size());
        stats.put("overdueTasks", overdueTasks.size());
        
        // Calculate task completion rate
        long totalTasks = taskRepository.count();
        double taskCompletionRate = totalTasks == 0 ? 0.0 : (double) completedTasks.size() / totalTasks * 100;
        stats.put("taskCompletionRate", Math.round(taskCompletionRate));
        
        // Add current quarter and year for display
        stats.put("currentQuarter", currentQuarter.name());
        stats.put("currentYear", currentYear);
        
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