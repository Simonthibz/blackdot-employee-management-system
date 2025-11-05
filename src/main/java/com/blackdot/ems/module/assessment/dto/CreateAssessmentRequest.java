package com.blackdot.ems.module.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class CreateAssessmentRequest {
    
    @NotBlank
    @Size(max = 100)
    private String title;
    
    @Size(max = 1000)
    private String description;
    
    @Min(0)
    @Max(100)
    private Integer passingScore = 70;
    
    @Min(1)
    @Max(480) // Max 8 hours
    private Integer timeLimitMinutes = 60;
    
    private Boolean isActive = true;
    
    @Size(max = 10)
    private String quarter; // Q1, Q2, Q3, Q4
    
    private Integer year;
    
    @Min(1)
    @Max(10)
    private Integer maxAttempts = 3;
    
    private String deadline; // ISO format date string
    
    // Constructors
    public CreateAssessmentRequest() {}
    
    // Getters and Setters
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
    
    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}