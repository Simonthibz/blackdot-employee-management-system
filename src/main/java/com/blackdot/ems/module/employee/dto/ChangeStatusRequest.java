package com.blackdot.ems.module.employee.dto;

import com.blackdot.ems.shared.entity.EmployeeStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeStatusRequest {
    
    @NotNull(message = "New status is required")
    private EmployeeStatus newStatus;
    
    @NotNull(message = "Reason is required")
    private String reason;
    
    @NotNull(message = "Changed by user ID is required")
    private Long changedBy;
    
    // Constructors
    public ChangeStatusRequest() {}
    
    public ChangeStatusRequest(EmployeeStatus newStatus, String reason, Long changedBy) {
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedBy = changedBy;
    }
    
    // Getters and Setters
    public EmployeeStatus getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(EmployeeStatus newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Long getChangedBy() {
        return changedBy;
    }
    
    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }
}
