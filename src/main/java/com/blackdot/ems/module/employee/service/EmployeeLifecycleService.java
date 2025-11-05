package com.blackdot.ems.module.employee.service;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.employee.repository.EmployeeLifecycleEventRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Employee Lifecycle Management Service
 * Handles all aspects of employee lifecycle from onboarding to exit
 */
@Service
@Transactional
public class EmployeeLifecycleService {
    
    @Autowired
    private EmployeeLifecycleEventRepository lifecycleEventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final List<EventStatus> ACTIVE_STATUSES = Arrays.asList(
            EventStatus.PENDING, EventStatus.IN_PROGRESS, EventStatus.OVERDUE, EventStatus.POSTPONED
    );
    
    private static final List<LifecycleEventType> PROBATION_EVENT_TYPES = Arrays.asList(
            LifecycleEventType.PROBATION_REVIEW_30,
            LifecycleEventType.PROBATION_REVIEW_60,
            LifecycleEventType.PROBATION_REVIEW_90
    );
    
    private static final List<LifecycleEventType> PERFORMANCE_EVENT_TYPES = Arrays.asList(
            LifecycleEventType.PERFORMANCE_REVIEW_DUE,
            LifecycleEventType.PERFORMANCE_REVIEW_SCHEDULED,
            LifecycleEventType.GOAL_SETTING
    );
    
    // Employee Lifecycle Event Management
    
    /**
     * Create a new lifecycle event
     */
    public EmployeeLifecycleEvent createLifecycleEvent(Long employeeId, LifecycleEventType eventType, 
                                                      LocalDate eventDate, String description, Long createdBy) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        EmployeeLifecycleEvent event = new EmployeeLifecycleEvent(employee, eventType, eventDate);
        event.setDescription(description);
        event.setCreatedBy(createdBy);
        
        // Set default due date if applicable
        int defaultDueDays = eventType.getDefaultDueDays();
        if (defaultDueDays > 0) {
            event.setDueDate(eventDate.plusDays(defaultDueDays));
        }
        
        // Set priority based on event type
        event.setPriority(eventType.isHighPriority() ? 1 : 2);
        
        return lifecycleEventRepository.save(event);
    }
    
    /**
     * Update lifecycle event status
     */
    public EmployeeLifecycleEvent updateEventStatus(Long eventId, EventStatus newStatus, String notes, Long updatedBy) {
        EmployeeLifecycleEvent event = lifecycleEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Lifecycle Event", "id", eventId));
        
        event.setStatus(newStatus);
        event.setNotes(notes);
        event.setUpdatedAt(LocalDateTime.now());
        
        if (newStatus == EventStatus.COMPLETED) {
            event.setCompletedDate(LocalDate.now());
        }
        
        return lifecycleEventRepository.save(event);
    }
    
    /**
     * Complete lifecycle event
     */
    public EmployeeLifecycleEvent completeEvent(Long eventId, String notes, Long completedBy) {
        return updateEventStatus(eventId, EventStatus.COMPLETED, notes, completedBy);
    }
    
    // Onboarding Workflow
    
    /**
     * Initialize onboarding workflow for new employee
     */
    public List<EmployeeLifecycleEvent> initializeOnboardingWorkflow(Long employeeId, LocalDate startDate, Long createdBy) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        List<EmployeeLifecycleEvent> onboardingEvents = Arrays.asList(
            createOnboardingEvent(employee, LifecycleEventType.FIRST_DAY, startDate, createdBy),
            createOnboardingEvent(employee, LifecycleEventType.ORIENTATION_SCHEDULED, startDate, createdBy),
            createOnboardingEvent(employee, LifecycleEventType.IT_SETUP_COMPLETED, startDate.minusDays(1), createdBy),
            createOnboardingEvent(employee, LifecycleEventType.WORKSPACE_ASSIGNED, startDate.minusDays(1), createdBy),
            createOnboardingEvent(employee, LifecycleEventType.BUDDY_ASSIGNED, startDate, createdBy)
        );
        
        // Initialize probation workflow if applicable
        if (employee.getProbationEndDate() != null) {
            onboardingEvents.addAll(initializeProbationWorkflow(employeeId, startDate, createdBy));
        }
        
        return lifecycleEventRepository.saveAll(onboardingEvents);
    }
    
    private EmployeeLifecycleEvent createOnboardingEvent(User employee, LifecycleEventType eventType, LocalDate eventDate, Long createdBy) {
        EmployeeLifecycleEvent event = new EmployeeLifecycleEvent(employee, eventType, eventDate);
        event.setDescription(eventType.getDescription());
        event.setCreatedBy(createdBy);
        
        int defaultDueDays = eventType.getDefaultDueDays();
        if (defaultDueDays > 0) {
            event.setDueDate(eventDate.plusDays(defaultDueDays));
        }
        
        event.setPriority(eventType.isHighPriority() ? 1 : 2);
        return event;
    }
    
    // Probation Management
    
    /**
     * Initialize probation workflow
     */
    public List<EmployeeLifecycleEvent> initializeProbationWorkflow(Long employeeId, LocalDate startDate, Long createdBy) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        List<EmployeeLifecycleEvent> probationEvents = Arrays.asList(
            createProbationEvent(employee, LifecycleEventType.PROBATION_STARTED, startDate, createdBy),
            createProbationEvent(employee, LifecycleEventType.PROBATION_REVIEW_30, startDate.plusDays(30), createdBy),
            createProbationEvent(employee, LifecycleEventType.PROBATION_REVIEW_60, startDate.plusDays(60), createdBy),
            createProbationEvent(employee, LifecycleEventType.PROBATION_REVIEW_90, startDate.plusDays(90), createdBy)
        );
        
        return lifecycleEventRepository.saveAll(probationEvents);
    }
    
    private EmployeeLifecycleEvent createProbationEvent(User employee, LifecycleEventType eventType, LocalDate eventDate, Long createdBy) {
        EmployeeLifecycleEvent event = new EmployeeLifecycleEvent(employee, eventType, eventDate);
        event.setDescription("Probation review for " + employee.getFullName());
        event.setDueDate(eventDate);
        event.setCreatedBy(createdBy);
        event.setPriority(1); // High priority for probation reviews
        return event;
    }
    
    /**
     * Complete probation successfully
     */
    public EmployeeLifecycleEvent completeProbation(Long employeeId, String notes, Long completedBy) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        // Mark employee as no longer in probation
        employee.setProbationEndDate(LocalDate.now());
        userRepository.save(employee);
        
        // Create probation completion event
        EmployeeLifecycleEvent completionEvent = new EmployeeLifecycleEvent(
                employee, LifecycleEventType.PROBATION_COMPLETED, LocalDate.now());
        completionEvent.setDescription("Probation completed successfully");
        completionEvent.setNotes(notes);
        completionEvent.setStatus(EventStatus.COMPLETED);
        completionEvent.setCompletedDate(LocalDate.now());
        completionEvent.setCreatedBy(completedBy);
        
        return lifecycleEventRepository.save(completionEvent);
    }
    
    // Performance Management
    
    /**
     * Schedule performance review
     */
    public EmployeeLifecycleEvent schedulePerformanceReview(Long employeeId, LocalDate reviewDate, Long createdBy) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        EmployeeLifecycleEvent reviewEvent = new EmployeeLifecycleEvent(
                employee, LifecycleEventType.PERFORMANCE_REVIEW_SCHEDULED, reviewDate);
        reviewEvent.setDescription("Annual performance review for " + employee.getFullName());
        reviewEvent.setDueDate(reviewDate);
        reviewEvent.setCreatedBy(createdBy);
        reviewEvent.setPriority(1);
        
        return lifecycleEventRepository.save(reviewEvent);
    }
    
    /**
     * Create annual performance review cycle for all employees
     */
    public List<EmployeeLifecycleEvent> createAnnualPerformanceReviewCycle(LocalDate reviewPeriodStart, Long createdBy) {
        List<User> activeEmployees = userRepository.findAll().stream()
                .filter(user -> user.getEmployeeStatus() == EmployeeStatus.ACTIVE)
                .collect(Collectors.toList());
        
        List<EmployeeLifecycleEvent> reviewEvents = activeEmployees.stream()
                .map(employee -> {
                    // Schedule review based on hire date anniversary
                    LocalDate reviewDate = reviewPeriodStart.plusDays(
                            (employee.getHireDate().getDayOfYear() % 365));
                    
                    EmployeeLifecycleEvent event = new EmployeeLifecycleEvent(
                            employee, LifecycleEventType.PERFORMANCE_REVIEW_DUE, reviewDate);
                    event.setDescription("Annual performance review");
                    event.setDueDate(reviewDate);
                    event.setCreatedBy(createdBy);
                    event.setPriority(2);
                    return event;
                })
                .collect(Collectors.toList());
        
        return lifecycleEventRepository.saveAll(reviewEvents);
    }
    
    // Contract Management
    
    /**
     * Create contract renewal reminders
     */
    public List<EmployeeLifecycleEvent> createContractRenewalReminders(Long createdBy) {
        List<User> contractEmployees = userRepository.findAll().stream()
                .filter(user -> user.getContractEndDate() != null && 
                               user.getContractEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
        
        List<EmployeeLifecycleEvent> renewalEvents = contractEmployees.stream()
                .filter(employee -> employee.isContractExpiringSoon())
                .map(employee -> {
                    EmployeeLifecycleEvent event = new EmployeeLifecycleEvent(
                            employee, LifecycleEventType.CONTRACT_RENEWAL_DUE, 
                            employee.getContractEndDate().minusDays(30));
                    event.setDescription("Contract renewal required for " + employee.getFullName());
                    event.setDueDate(employee.getContractEndDate().minusDays(30));
                    event.setCreatedBy(createdBy);
                    event.setPriority(1);
                    return event;
                })
                .collect(Collectors.toList());
        
        return lifecycleEventRepository.saveAll(renewalEvents);
    }
    
    // Reporting and Analytics
    
    /**
     * Get employee lifecycle events
     */
    public List<EmployeeLifecycleEvent> getEmployeeLifecycleEvents(Long employeeId) {
        return lifecycleEventRepository.findByEmployeeIdOrderByEventDateDesc(employeeId);
    }
    
    /**
     * Get pending events for employee
     */
    public List<EmployeeLifecycleEvent> getPendingEventsForEmployee(Long employeeId) {
        return lifecycleEventRepository.findPendingEventsByEmployee(employeeId, ACTIVE_STATUSES);
    }
    
    /**
     * Get overdue events
     */
    public List<EmployeeLifecycleEvent> getOverdueEvents() {
        return lifecycleEventRepository.findOverdueEvents(LocalDate.now(), ACTIVE_STATUSES);
    }
    
    /**
     * Get events due soon (next 7 days)
     */
    public List<EmployeeLifecycleEvent> getEventsDueSoon(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return lifecycleEventRepository.findEventsDueSoon(startDate, endDate, ACTIVE_STATUSES);
    }
    
    /**
     * Get events assigned to user
     */
    public List<EmployeeLifecycleEvent> getAssignedEvents(Long userId) {
        return lifecycleEventRepository.findByAssignedToAndStatusInOrderByDueDateAsc(userId, ACTIVE_STATUSES);
    }
    
    /**
     * Get probation events requiring attention
     */
    public List<EmployeeLifecycleEvent> getProbationEventsRequiringAttention() {
        return lifecycleEventRepository.findProbationEvents(PROBATION_EVENT_TYPES);
    }
    
    /**
     * Get performance reviews due
     */
    public List<EmployeeLifecycleEvent> getPerformanceReviewsDue() {
        return lifecycleEventRepository.findPerformanceReviewsDue(PERFORMANCE_EVENT_TYPES, ACTIVE_STATUSES);
    }
    
    /**
     * Get contract renewals due soon
     */
    public List<EmployeeLifecycleEvent> getContractRenewalsDue() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(60); // Next 60 days
        return lifecycleEventRepository.findContractRenewalsDue(
                LifecycleEventType.CONTRACT_RENEWAL_DUE, startDate, endDate, ACTIVE_STATUSES);
    }
    
    /**
     * Get lifecycle statistics
     */
    public Map<String, Object> getLifecycleStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Event status counts
        Map<EventStatus, Long> statusCounts = Arrays.stream(EventStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> lifecycleEventRepository.countByStatus(status)
                ));
        
        stats.put("statusCounts", statusCounts);
        stats.put("overdueCount", lifecycleEventRepository.countOverdueEvents(LocalDate.now(), ACTIVE_STATUSES));
        stats.put("dueSoonCount", getEventsDueSoon(7).size());
        stats.put("probationCount", getProbationEventsRequiringAttention().size());
        stats.put("performanceReviewCount", getPerformanceReviewsDue().size());
        stats.put("contractRenewalCount", getContractRenewalsDue().size());
        
        return stats;
    }
    
    /**
     * Search lifecycle events
     */
    public List<EmployeeLifecycleEvent> searchEvents(String searchTerm) {
        return lifecycleEventRepository.searchEvents(searchTerm);
    }
    
    /**
     * Get all events with pagination
     */
    public Page<EmployeeLifecycleEvent> getAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lifecycleEventRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Get events by status with pagination
     */
    public Page<EmployeeLifecycleEvent> getEventsByStatus(EventStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lifecycleEventRepository.findByStatusOrderByDueDateAsc(status, pageable);
    }
}