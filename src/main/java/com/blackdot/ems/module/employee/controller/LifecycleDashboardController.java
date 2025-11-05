package com.blackdot.ems.module.employee.controller;

import com.blackdot.ems.module.employee.service.EmployeeLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Lifecycle Dashboard Controller
 * Provides dedicated pages for employee lifecycle management
 */
@Controller
@RequestMapping("/dashboard/lifecycle-data")
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
public class LifecycleDashboardController {

    @Autowired
    private EmployeeLifecycleService lifecycleService;

    /**
     * Main lifecycle dashboard page
     */
    @GetMapping
    public String lifecycleDashboard(Model model) {
        try {
            // Create basic statistics - we'll enhance these later
            Map<String, Object> statistics = Map.of(
                "totalEvents", "Loading...",
                "upcomingEvents", 0,
                "overdueEvents", 0,
                "employeesOnProbation", 0
            );
            
            model.addAttribute("statistics", statistics);
            model.addAttribute("upcomingEvents", java.util.Collections.emptyList());
            model.addAttribute("overdueEvents", java.util.Collections.emptyList());
            model.addAttribute("lifecycleEvents", Map.of("content", java.util.Collections.emptyList()));
            
            return "dashboard/lifecycle";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load lifecycle dashboard: " + e.getMessage());
            return "dashboard/lifecycle";
        }
    }

    /**
     * Onboarding management page
     */
    @GetMapping("/onboarding")
    public String onboardingManagement(Model model) {
        try {
            model.addAttribute("onboardingEvents", java.util.Collections.emptyList());
            model.addAttribute("pendingOnboarding", java.util.Collections.emptyList());
            model.addAttribute("onboardingStats", Map.of("total", 0));
            
            return "dashboard/lifecycle-onboarding";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load onboarding data: " + e.getMessage());
            return "dashboard/lifecycle-onboarding";
        }
    }

    /**
     * Performance reviews management page
     */
    @GetMapping("/reviews")
    public String performanceReviews(Model model) {
        try {
            model.addAttribute("upcomingReviews", java.util.Collections.emptyList());
            model.addAttribute("overdueReviews", java.util.Collections.emptyList());
            model.addAttribute("reviewStats", Map.of("total", 0));
            
            return "dashboard/lifecycle-reviews";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load review data: " + e.getMessage());
            return "dashboard/lifecycle-reviews";
        }
    }

    /**
     * Contract renewals page
     */
    @GetMapping("/contracts")
    public String contractManagement(Model model) {
        try {
            model.addAttribute("expiringContracts", java.util.Collections.emptyList());
            model.addAttribute("contractRenewals", java.util.Collections.emptyList());
            model.addAttribute("contractStats", Map.of("total", 0));
            
            return "dashboard/lifecycle-contracts";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load contract data: " + e.getMessage());
            return "dashboard/lifecycle-contracts";
        }
    }

    /**
     * Employee lifecycle timeline for specific employee
     */
    @GetMapping("/timeline/{employeeId}")
    public String employeeTimeline(@PathVariable Long employeeId, Model model) {
        try {
            // Use the existing method
            var timelineEvents = lifecycleService.getEmployeeLifecycleEvents(employeeId);
            
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("timelineEvents", timelineEvents);
            model.addAttribute("milestones", java.util.Collections.emptyList());
            model.addAttribute("upcomingEvents", java.util.Collections.emptyList());
            
            return "dashboard/lifecycle-timeline";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load employee timeline: " + e.getMessage());
            return "dashboard/lifecycle-timeline";
        }
    }

    /**
     * Probation management page
     */
    @GetMapping("/probation")
    public String probationManagement(Model model) {
        try {
            model.addAttribute("employeesOnProbation", java.util.Collections.emptyList());
            model.addAttribute("probationReviews", java.util.Collections.emptyList());
            model.addAttribute("probationStats", Map.of("total", 0));
            
            return "dashboard/lifecycle-probation";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load probation data: " + e.getMessage());
            return "dashboard/lifecycle-probation";
        }
    }

    /**
     * Training and development page
     */
    @GetMapping("/training")
    public String trainingManagement(Model model) {
        try {
            model.addAttribute("trainingEvents", java.util.Collections.emptyList());
            model.addAttribute("pendingTraining", java.util.Collections.emptyList());
            model.addAttribute("trainingStats", Map.of("total", 0));
            
            return "dashboard/lifecycle-training";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load training data: " + e.getMessage());
            return "dashboard/lifecycle-training";
        }
    }
}