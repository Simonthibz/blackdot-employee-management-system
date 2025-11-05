package com.blackdot.ems.module.assessment.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AssessmentResponse {
    
    private Long id;
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Boolean isActive;
    private Integer questionCount;
    private String quarter;
    private Integer year;
    private Integer maxAttempts;
    private LocalDateTime deadline;
    private Integer totalAttempts; // Calculated field
    private Double passRate; // Calculated field
    private Long employeesTaken; // Number of unique employees who took this assessment
    private Long pendingAttempts; // Number of started but not completed attempts
    private Long completedAttempts; // Number of completed attempts
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public AssessmentResponse() {}
    
    public AssessmentResponse(Long id, String title, String description, Integer passingScore,
                             Integer timeLimitMinutes, Boolean isActive, Integer questionCount,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.passingScore = passingScore;
        this.timeLimitMinutes = timeLimitMinutes;
        this.isActive = isActive;
        this.questionCount = questionCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }
    
    public Integer getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    
    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public Integer getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(Integer totalAttempts) { this.totalAttempts = totalAttempts; }
    
    public Double getPassRate() { return passRate; }
    public void setPassRate(Double passRate) { this.passRate = passRate; }
    
    public Long getEmployeesTaken() { return employeesTaken; }
    public void setEmployeesTaken(Long employeesTaken) { this.employeesTaken = employeesTaken; }
    
    public Long getPendingAttempts() { return pendingAttempts; }
    public void setPendingAttempts(Long pendingAttempts) { this.pendingAttempts = pendingAttempts; }
    
    public Long getCompletedAttempts() { return completedAttempts; }
    public void setCompletedAttempts(Long completedAttempts) { this.completedAttempts = completedAttempts; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}