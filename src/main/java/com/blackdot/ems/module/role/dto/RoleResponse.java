package com.blackdot.ems.module.role.dto;

import com.blackdot.ems.shared.entity.ERole;
import java.time.LocalDateTime;
import java.util.List;

public class RoleResponse {
    
    private Integer id;
    private ERole name;
    private String displayName;
    private String description;
    private String permissions;
    private List<String> permissionList; // Split permissions for UI
    private Boolean isSystemRole;
    private Boolean isActive;
    private Integer priority;
    private Integer userCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public List<String> getPermissionList() { return permissionList; }
    public void setPermissionList(List<String> permissionList) { this.permissionList = permissionList; }
    
    public Boolean getIsSystemRole() { return isSystemRole; }
    public void setIsSystemRole(Boolean isSystemRole) { this.isSystemRole = isSystemRole; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
