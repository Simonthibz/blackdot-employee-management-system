package com.blackdot.ems.module.employee.controller;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.employee.service.EmployeeLifecycleService;
import com.blackdot.ems.shared.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Employee Lifecycle Management Controller
 * Handles REST API endpoints for employee lifecycle events and workflows
 */
@RestController
@RequestMapping("/api/lifecycle")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeLifecycleController {
    
    @Autowired
    private EmployeeLifecycleService lifecycleService;
    
    // Employee Lifecycle Event Management
    
    /**
     * Create a new lifecycle event
     */
    @PostMapping("/events")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeLifecycleEvent> createLifecycleEvent(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            LifecycleEventType eventType = LifecycleEventType.valueOf(request.get("eventType").toString());
            LocalDate eventDate = LocalDate.parse(request.get("eventDate").toString());
            String description = request.get("description").toString();
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            EmployeeLifecycleEvent event = lifecycleService.createLifecycleEvent(
                    employeeId, eventType, eventDate, description, createdBy);
            
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create lifecycle event: " + e.getMessage());
        }
    }
    
    /**
     * Update lifecycle event status
     */
    @PutMapping("/events/{eventId}/status")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeLifecycleEvent> updateEventStatus(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> request) {
        try {
            EventStatus newStatus = EventStatus.valueOf(request.get("status").toString());
            String notes = request.getOrDefault("notes", "").toString();
            Long updatedBy = Long.valueOf(request.get("updatedBy").toString());
            
            EmployeeLifecycleEvent event = lifecycleService.updateEventStatus(eventId, newStatus, notes, updatedBy);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            throw new BadRequestException("Failed to update event status: " + e.getMessage());
        }
    }
    
    /**
     * Complete lifecycle event
     */
    @PutMapping("/events/{eventId}/complete")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeLifecycleEvent> completeEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> request) {
        try {
            String notes = request.getOrDefault("notes", "").toString();
            Long completedBy = Long.valueOf(request.get("completedBy").toString());
            
            EmployeeLifecycleEvent event = lifecycleService.completeEvent(eventId, notes, completedBy);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            throw new BadRequestException("Failed to complete event: " + e.getMessage());
        }
    }
    
    /**
     * Get all lifecycle events for an employee
     */
    @GetMapping("/employee/{employeeId}/events")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getEmployeeLifecycleEvents(@PathVariable Long employeeId) {
        List<EmployeeLifecycleEvent> events = lifecycleService.getEmployeeLifecycleEvents(employeeId);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get pending events for an employee
     */
    @GetMapping("/employee/{employeeId}/pending")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getPendingEventsForEmployee(@PathVariable Long employeeId) {
        List<EmployeeLifecycleEvent> events = lifecycleService.getPendingEventsForEmployee(employeeId);
        return ResponseEntity.ok(events);
    }
    
    // Onboarding Workflows
    
    /**
     * Initialize onboarding workflow for new employee
     */
    @PostMapping("/onboarding/initialize")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> initializeOnboardingWorkflow(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            List<EmployeeLifecycleEvent> events = lifecycleService.initializeOnboardingWorkflow(employeeId, startDate, createdBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            throw new BadRequestException("Failed to initialize onboarding workflow: " + e.getMessage());
        }
    }
    
    // Probation Management
    
    /**
     * Initialize probation workflow
     */
    @PostMapping("/probation/initialize")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> initializeProbationWorkflow(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            List<EmployeeLifecycleEvent> events = lifecycleService.initializeProbationWorkflow(employeeId, startDate, createdBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            throw new BadRequestException("Failed to initialize probation workflow: " + e.getMessage());
        }
    }
    
    /**
     * Complete probation successfully
     */
    @PostMapping("/probation/{employeeId}/complete")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<EmployeeLifecycleEvent> completeProbation(
            @PathVariable Long employeeId,
            @RequestBody Map<String, Object> request) {
        try {
            String notes = request.getOrDefault("notes", "").toString();
            Long completedBy = Long.valueOf(request.get("completedBy").toString());
            
            EmployeeLifecycleEvent event = lifecycleService.completeProbation(employeeId, notes, completedBy);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            throw new BadRequestException("Failed to complete probation: " + e.getMessage());
        }
    }
    
    /**
     * Get probation events requiring attention
     */
    @GetMapping("/probation/attention-required")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getProbationEventsRequiringAttention() {
        List<EmployeeLifecycleEvent> events = lifecycleService.getProbationEventsRequiringAttention();
        return ResponseEntity.ok(events);
    }
    
    // Performance Management
    
    /**
     * Schedule performance review
     */
    @PostMapping("/performance/schedule-review")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<EmployeeLifecycleEvent> schedulePerformanceReview(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            LocalDate reviewDate = LocalDate.parse(request.get("reviewDate").toString());
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            EmployeeLifecycleEvent event = lifecycleService.schedulePerformanceReview(employeeId, reviewDate, createdBy);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            throw new BadRequestException("Failed to schedule performance review: " + e.getMessage());
        }
    }
    
    /**
     * Create annual performance review cycle
     */
    @PostMapping("/performance/annual-cycle")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> createAnnualPerformanceReviewCycle(@RequestBody Map<String, Object> request) {
        try {
            LocalDate reviewPeriodStart = LocalDate.parse(request.get("reviewPeriodStart").toString());
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            List<EmployeeLifecycleEvent> events = lifecycleService.createAnnualPerformanceReviewCycle(reviewPeriodStart, createdBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create annual review cycle: " + e.getMessage());
        }
    }
    
    /**
     * Get performance reviews due
     */
    @GetMapping("/performance/reviews-due")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getPerformanceReviewsDue() {
        List<EmployeeLifecycleEvent> events = lifecycleService.getPerformanceReviewsDue();
        return ResponseEntity.ok(events);
    }
    
    // Contract Management
    
    /**
     * Create contract renewal reminders
     */
    @PostMapping("/contracts/renewal-reminders")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> createContractRenewalReminders(@RequestBody Map<String, Object> request) {
        try {
            Long createdBy = Long.valueOf(request.get("createdBy").toString());
            
            List<EmployeeLifecycleEvent> events = lifecycleService.createContractRenewalReminders(createdBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create contract renewal reminders: " + e.getMessage());
        }
    }
    
    /**
     * Get contract renewals due
     */
    @GetMapping("/contracts/renewals-due")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getContractRenewalsDue() {
        List<EmployeeLifecycleEvent> events = lifecycleService.getContractRenewalsDue();
        return ResponseEntity.ok(events);
    }
    
    // Reporting and Analytics
    
    /**
     * Get overdue events
     */
    @GetMapping("/events/overdue")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getOverdueEvents() {
        List<EmployeeLifecycleEvent> events = lifecycleService.getOverdueEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get events due soon
     */
    @GetMapping("/events/due-soon")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getEventsDueSoon(@RequestParam(defaultValue = "7") int days) {
        List<EmployeeLifecycleEvent> events = lifecycleService.getEventsDueSoon(days);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get events assigned to user
     */
    @GetMapping("/events/assigned/{userId}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> getAssignedEvents(@PathVariable Long userId) {
        List<EmployeeLifecycleEvent> events = lifecycleService.getAssignedEvents(userId);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get lifecycle statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLifecycleStatistics() {
        Map<String, Object> stats = lifecycleService.getLifecycleStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Search lifecycle events
     */
    @GetMapping("/events/search")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<EmployeeLifecycleEvent>> searchEvents(@RequestParam String searchTerm) {
        List<EmployeeLifecycleEvent> events = lifecycleService.searchEvents(searchTerm);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get all events with pagination
     */
    @GetMapping("/events")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Page<EmployeeLifecycleEvent>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeLifecycleEvent> events = lifecycleService.getAllEvents(page, size);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get events by status with pagination
     */
    @GetMapping("/events/status/{status}")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Page<EmployeeLifecycleEvent>> getEventsByStatus(
            @PathVariable EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeLifecycleEvent> events = lifecycleService.getEventsByStatus(status, page, size);
        return ResponseEntity.ok(events);
    }
    
    // Enum endpoints for frontend dropdowns
    
    /**
     * Get all lifecycle event types
     */
    @GetMapping("/enums/event-types")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getLifecycleEventTypes() {
        Map<String, Object> eventTypes = new java.util.HashMap<>();
        for (LifecycleEventType type : LifecycleEventType.values()) {
            Map<String, Object> typeInfo = new java.util.HashMap<>();
            typeInfo.put("name", type.name());
            typeInfo.put("displayName", type.getDisplayName());
            typeInfo.put("description", type.getDescription());
            typeInfo.put("category", type.getCategory().name());
            typeInfo.put("isHighPriority", type.isHighPriority());
            typeInfo.put("defaultDueDays", type.getDefaultDueDays());
            eventTypes.put(type.name(), typeInfo);
        }
        return ResponseEntity.ok(eventTypes);
    }
    
    /**
     * Get all event statuses
     */
    @GetMapping("/enums/event-statuses")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getEventStatuses() {
        Map<String, Object> statuses = new java.util.HashMap<>();
        for (EventStatus status : EventStatus.values()) {
            Map<String, Object> statusInfo = new java.util.HashMap<>();
            statusInfo.put("name", status.name());
            statusInfo.put("displayName", status.getDisplayName());
            statusInfo.put("description", status.getDescription());
            statusInfo.put("isActive", status.isActive());
            statusInfo.put("isCompleted", status.isCompleted());
            statusInfo.put("requiresAttention", status.requiresAttention());
            statusInfo.put("badgeClass", status.getBadgeClass());
            statusInfo.put("icon", status.getIcon());
            statuses.put(status.name(), statusInfo);
        }
        return ResponseEntity.ok(statuses);
    }
}