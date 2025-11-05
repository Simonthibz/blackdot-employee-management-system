package com.blackdot.ems.shared.entity;

/**
 * Lifecycle Event Categories for grouping and filtering
 */
public enum LifecycleCategory {
    ONBOARDING("Onboarding"),
    PROBATION_TRAINING("Probation & Training"),
    PERFORMANCE("Performance Management"),
    CAREER_DEVELOPMENT("Career Development"),
    CONTRACT_LEGAL("Contract & Legal"),
    LEAVE_ABSENCE("Leave & Absence"),
    SECURITY_COMPLIANCE("Security & Compliance"),
    DISCIPLINARY("Disciplinary"),
    EXIT_PROCESS("Exit Process"),
    OTHER("Other");
    
    private final String displayName;
    
    LifecycleCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}