package com.blackdot.ems.module.department.dto;

import jakarta.validation.constraints.Size;

public class UpdateDepartmentRequest {
    
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 20, message = "Department code must not exceed 20 characters")
    private String code;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Long headOfDepartmentId;
    
    private Double budget;
    
    private String costCenterCode;
    
    private String location;
    
    private Boolean isActive;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getHeadOfDepartmentId() { return headOfDepartmentId; }
    public void setHeadOfDepartmentId(Long headOfDepartmentId) { this.headOfDepartmentId = headOfDepartmentId; }
    
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    
    public String getCostCenterCode() { return costCenterCode; }
    public void setCostCenterCode(String costCenterCode) { this.costCenterCode = costCenterCode; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
