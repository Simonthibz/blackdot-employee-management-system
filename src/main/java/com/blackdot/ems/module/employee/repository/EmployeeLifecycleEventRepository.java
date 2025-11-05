package com.blackdot.ems.module.employee.repository;

import com.blackdot.ems.shared.entity.EmployeeLifecycleEvent;
import com.blackdot.ems.shared.entity.EventStatus;
import com.blackdot.ems.shared.entity.LifecycleEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Employee Lifecycle Events
 */
@Repository
public interface EmployeeLifecycleEventRepository extends JpaRepository<EmployeeLifecycleEvent, Long> {
    
    /**
     * Find all events for a specific employee
     */
    List<EmployeeLifecycleEvent> findByEmployeeIdOrderByEventDateDesc(Long employeeId);
    
    /**
     * Find events by employee and status
     */
    List<EmployeeLifecycleEvent> findByEmployeeIdAndStatusOrderByEventDateDesc(Long employeeId, EventStatus status);
    
    /**
     * Find events by employee and event type
     */
    List<EmployeeLifecycleEvent> findByEmployeeIdAndEventTypeOrderByEventDateDesc(Long employeeId, LifecycleEventType eventType);
    
    /**
     * Find all pending events for an employee
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE e.employee.id = :employeeId AND e.status IN :statuses ORDER BY e.dueDate ASC, e.priority ASC")
    List<EmployeeLifecycleEvent> findPendingEventsByEmployee(@Param("employeeId") Long employeeId, @Param("statuses") List<EventStatus> statuses);
    
    /**
     * Find all overdue events
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE e.dueDate < :currentDate AND e.status IN :activeStatuses ORDER BY e.dueDate ASC")
    List<EmployeeLifecycleEvent> findOverdueEvents(@Param("currentDate") LocalDate currentDate, @Param("activeStatuses") List<EventStatus> activeStatuses);
    
    /**
     * Find events due soon (within specified days)
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE e.dueDate BETWEEN :startDate AND :endDate AND e.status IN :activeStatuses ORDER BY e.dueDate ASC")
    List<EmployeeLifecycleEvent> findEventsDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("activeStatuses") List<EventStatus> activeStatuses);
    
    /**
     * Find events assigned to a specific person
     */
    List<EmployeeLifecycleEvent> findByAssignedToAndStatusInOrderByDueDateAsc(Long assignedTo, List<EventStatus> statuses);
    
    /**
     * Find events by event type and status
     */
    List<EmployeeLifecycleEvent> findByEventTypeAndStatusOrderByEventDateDesc(LifecycleEventType eventType, EventStatus status);
    
    /**
     * Find recent events (within last N days)
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE e.eventDate >= :fromDate ORDER BY e.eventDate DESC")
    List<EmployeeLifecycleEvent> findRecentEvents(@Param("fromDate") LocalDate fromDate);
    
    /**
     * Count events by status
     */
    long countByStatus(EventStatus status);
    
    /**
     * Count overdue events
     */
    @Query("SELECT COUNT(e) FROM EmployeeLifecycleEvent e WHERE e.dueDate < :currentDate AND e.status IN :activeStatuses")
    long countOverdueEvents(@Param("currentDate") LocalDate currentDate, @Param("activeStatuses") List<EventStatus> activeStatuses);
    
    /**
     * Find all events with pagination
     */
    Page<EmployeeLifecycleEvent> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find events by status with pagination
     */
    Page<EmployeeLifecycleEvent> findByStatusOrderByDueDateAsc(EventStatus status, Pageable pageable);
    
    /**
     * Search events by description or notes
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.employee.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.employee.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY e.eventDate DESC")
    List<EmployeeLifecycleEvent> searchEvents(@Param("searchTerm") String searchTerm);
    
    /**
     * Find events for employees in probation
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE " +
           "e.employee.probationEndDate IS NOT NULL AND " +
           "e.employee.probationEndDate > CURRENT_DATE AND " +
           "e.eventType IN :probationEventTypes " +
           "ORDER BY e.dueDate ASC")
    List<EmployeeLifecycleEvent> findProbationEvents(@Param("probationEventTypes") List<LifecycleEventType> probationEventTypes);
    
    /**
     * Find contract renewal events due soon
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE " +
           "e.eventType = :contractRenewalType AND " +
           "e.dueDate BETWEEN :startDate AND :endDate AND " +
           "e.status IN :activeStatuses " +
           "ORDER BY e.dueDate ASC")
    List<EmployeeLifecycleEvent> findContractRenewalsDue(
            @Param("contractRenewalType") LifecycleEventType contractRenewalType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activeStatuses") List<EventStatus> activeStatuses);
    
    /**
     * Find performance review events due
     */
    @Query("SELECT e FROM EmployeeLifecycleEvent e WHERE " +
           "e.eventType IN :performanceEventTypes AND " +
           "e.status IN :activeStatuses " +
           "ORDER BY e.dueDate ASC")
    List<EmployeeLifecycleEvent> findPerformanceReviewsDue(
            @Param("performanceEventTypes") List<LifecycleEventType> performanceEventTypes,
            @Param("activeStatuses") List<EventStatus> activeStatuses);
}