package com.blackdot.ems.shared.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee Lifecycle Event Entity
 * Tracks all significant events in an employee's lifecycle
 */
@Entity
@Table(name = "employee_lifecycle_events")
public class EmployeeLifecycleEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private LifecycleEventType eventType;
    
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "completed_date")
    private LocalDate completedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "assigned_to")
    private Long assignedTo; // User ID of person responsible
    
    @Column(name = "priority")
    private Integer priority; // 1 = High, 2 = Medium, 3 = Low
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    // Constructors
    public EmployeeLifecycleEvent() {
        this.createdAt = LocalDateTime.now();
        this.status = EventStatus.PENDING;
        this.priority = 2; // Default to medium priority
    }
    
    public EmployeeLifecycleEvent(User employee, LifecycleEventType eventType, LocalDate eventDate) {
        this();
        this.employee = employee;
        this.eventType = eventType;
        this.eventDate = eventDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
    public LifecycleEventType getEventType() { return eventType; }
    public void setEventType(LifecycleEventType eventType) { this.eventType = eventType; }
    
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
    
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    // Utility methods
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != EventStatus.COMPLETED;
    }
    
    public boolean isDueSoon() {
        return dueDate != null && dueDate.isBefore(LocalDate.now().plusDays(7)) && status != EventStatus.COMPLETED;
    }
    
    public String getPriorityText() {
        switch (priority) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Low";
            default: return "Unknown";
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}