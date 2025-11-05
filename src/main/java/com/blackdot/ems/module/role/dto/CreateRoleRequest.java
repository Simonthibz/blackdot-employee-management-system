package com.blackdot.ems.module.role.dto;

import com.blackdot.ems.shared.entity.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateRoleRequest {
    
    private ERole name; // Use existing enum for system roles
    
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private String permissions; // Comma-separated permissions
    
    private Integer priority;
    
    // Getters and Setters
    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}
