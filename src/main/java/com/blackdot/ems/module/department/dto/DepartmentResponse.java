package com.blackdot.ems.module.department.dto;

import java.time.LocalDateTime;

public class DepartmentResponse {
    
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long headOfDepartmentId;
    private String headOfDepartmentName;
    private Double budget;
    private String costCenterCode;
    private String location;
    private Boolean isActive;
    private Integer employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getHeadOfDepartmentId() { return headOfDepartmentId; }
    public void setHeadOfDepartmentId(Long headOfDepartmentId) { this.headOfDepartmentId = headOfDepartmentId; }
    
    public String getHeadOfDepartmentName() { return headOfDepartmentName; }
    public void setHeadOfDepartmentName(String headOfDepartmentName) { this.headOfDepartmentName = headOfDepartmentName; }
    
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    
    public String getCostCenterCode() { return costCenterCode; }
    public void setCostCenterCode(String costCenterCode) { this.costCenterCode = costCenterCode; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
