package com.blackdot.ems.module.role.dto;

import jakarta.validation.constraints.Size;

public class UpdateRoleRequest {
    
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private String permissions; // Comma-separated permissions
    
    private Integer priority;
    
    private Boolean isActive;
    
    // Getters and Setters
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
