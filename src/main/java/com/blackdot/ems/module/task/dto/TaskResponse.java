package com.blackdot.ems.module.task.dto;

import com.blackdot.ems.shared.entity.TaskPriority;
import com.blackdot.ems.shared.entity.TaskStatus;

import java.time.LocalDateTime;

public class TaskResponse {
    
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String notes;
    
    // Assigned to user info
    private Long assignedToId;
    private String assignedToName;
    private String assignedToEmail;
    private String assignedToDepartment;
    
    // Assigned by user info
    private Long assignedById;
    private String assignedByName;
    private String assignedByEmail;
    
    // Department info
    private Long departmentId;
    private String departmentName;
    
    // Computed fields
    private boolean overdue;
    private long daysUntilDue;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    
    public String getAssignedToEmail() { return assignedToEmail; }
    public void setAssignedToEmail(String assignedToEmail) { this.assignedToEmail = assignedToEmail; }
    
    public String getAssignedToDepartment() { return assignedToDepartment; }
    public void setAssignedToDepartment(String assignedToDepartment) { this.assignedToDepartment = assignedToDepartment; }
    
    public Long getAssignedById() { return assignedById; }
    public void setAssignedById(Long assignedById) { this.assignedById = assignedById; }
    
    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    
    public String getAssignedByEmail() { return assignedByEmail; }
    public void setAssignedByEmail(String assignedByEmail) { this.assignedByEmail = assignedByEmail; }
    
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    
    public long getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(long daysUntilDue) { this.daysUntilDue = daysUntilDue; }
}
