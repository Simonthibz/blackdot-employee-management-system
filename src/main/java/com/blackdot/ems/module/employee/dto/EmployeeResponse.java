package com.blackdot.ems.module.employee.dto;

import com.blackdot.ems.shared.entity.EmployeeStatus;
import com.blackdot.ems.shared.entity.EmploymentType;
import com.blackdot.ems.shared.entity.ClearanceLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class EmployeeResponse {
    
    // Basic Information
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String department;
    private String position;
    private LocalDate hireDate;
    
    // Government-Grade Status Management
    private EmployeeStatus employeeStatus;
    private EmploymentType employmentType;
    private ClearanceLevel clearanceLevel;
    private String branchOffice;
    private String costCenter;
    private Long reportingManagerId;
    private String reportingManagerName;
    private String jobGrade;
    private String salaryBand;
    
    // Contract Information
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate probationEndDate;
    private Integer noticePeriodDays;
    private boolean isInProbation;
    private boolean isContractExpiringSoon;
    
    // Contact Information
    private String phoneNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String address;
    
    // Professional Information
    private String highestQualification;
    private String professionalCertifications;
    private String languagesSpoken;
    private String previousEmployer;
    private Integer yearsOfExperience;
    
    // Security & Compliance
    private String backgroundCheckStatus;
    private LocalDate backgroundCheckDate;
    private boolean requiresBackgroundCheckRenewal;
    private Boolean securityTrainingCompleted;
    private LocalDate securityTrainingDate;
    
    // Performance Management
    private LocalDate lastPerformanceReview;
    private LocalDate nextPerformanceReview;
    
    // Status Change Audit
    private String statusChangeReason;
    private Long statusChangedBy;
    private String statusChangedByName;
    private LocalDateTime statusChangeDate;
    
    // System Access
    private LocalDateTime lastLogin;
    private Integer failedLoginAttempts;
    private LocalDateTime accountLockedUntil;
    private boolean hasSystemAccess;
    private boolean isPayrollEligible;
    
    // Compliance
    private Boolean dataPrivacyConsent;
    private LocalDateTime termsAcceptedDate;
    private Boolean confidentialityAgreementSigned;
    
    // Legacy fields for backward compatibility
    private Boolean isActive;
    private Set<String> roleNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public EmployeeResponse() {}
    
    // Comprehensive Constructor for Government-Grade System
    public EmployeeResponse(Long id, String username, String email, String firstName, 
                           String lastName, String employeeId, String department, String position,
                           LocalDate hireDate, EmployeeStatus employeeStatus, EmploymentType employmentType,
                           ClearanceLevel clearanceLevel, String branchOffice, String costCenter,
                           Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.employeeStatus = employeeStatus;
        this.employmentType = employmentType;
        this.clearanceLevel = clearanceLevel;
        this.branchOffice = branchOffice;
        this.costCenter = costCenter;
        this.roleNames = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
        // Set computed fields
        this.isActive = employeeStatus == EmployeeStatus.ACTIVE;
        this.hasSystemAccess = employeeStatus != null && employeeStatus.hasSystemAccess();
        this.isPayrollEligible = employeeStatus != null && employeeStatus.isPayrollEligible();
    }
    
    // Legacy Constructor for backward compatibility
    public EmployeeResponse(Long id, String username, String email, String firstName, 
                           String lastName, String employeeId, String department, String position,
                           LocalDate hireDate, Boolean isActive, Set<String> roles, 
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
        this.isActive = isActive;
        this.roleNames = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
        // Set default status based on legacy isActive
        this.employeeStatus = Boolean.TRUE.equals(isActive) ? EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE;
        this.hasSystemAccess = Boolean.TRUE.equals(isActive);
        this.isPayrollEligible = Boolean.TRUE.equals(isActive);
    }
    
    // Getters and Setters - Basic Information
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    // Government-Grade Status Management
    public EmployeeStatus getEmployeeStatus() { return employeeStatus; }
    public void setEmployeeStatus(EmployeeStatus employeeStatus) { 
        this.employeeStatus = employeeStatus; 
        this.isActive = employeeStatus == EmployeeStatus.ACTIVE;
        this.hasSystemAccess = employeeStatus != null && employeeStatus.hasSystemAccess();
        this.isPayrollEligible = employeeStatus != null && employeeStatus.isPayrollEligible();
    }
    
    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }
    
    public ClearanceLevel getClearanceLevel() { return clearanceLevel; }
    public void setClearanceLevel(ClearanceLevel clearanceLevel) { this.clearanceLevel = clearanceLevel; }
    
    public String getBranchOffice() { return branchOffice; }
    public void setBranchOffice(String branchOffice) { this.branchOffice = branchOffice; }
    
    public String getCostCenter() { return costCenter; }
    public void setCostCenter(String costCenter) { this.costCenter = costCenter; }
    
    public Long getReportingManagerId() { return reportingManagerId; }
    public void setReportingManagerId(Long reportingManagerId) { this.reportingManagerId = reportingManagerId; }
    
    public String getReportingManagerName() { return reportingManagerName; }
    public void setReportingManagerName(String reportingManagerName) { this.reportingManagerName = reportingManagerName; }
    
    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }
    
    public String getSalaryBand() { return salaryBand; }
    public void setSalaryBand(String salaryBand) { this.salaryBand = salaryBand; }
    
    // Contract Information
    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }
    
    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }
    
    public LocalDate getProbationEndDate() { return probationEndDate; }
    public void setProbationEndDate(LocalDate probationEndDate) { this.probationEndDate = probationEndDate; }
    
    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }
    
    public boolean isInProbation() { return isInProbation; }
    public void setInProbation(boolean inProbation) { isInProbation = inProbation; }
    
    public boolean isContractExpiringSoon() { return isContractExpiringSoon; }
    public void setContractExpiringSoon(boolean contractExpiringSoon) { isContractExpiringSoon = contractExpiringSoon; }
    
    // Contact Information
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    // Professional Information
    public String getHighestQualification() { return highestQualification; }
    public void setHighestQualification(String highestQualification) { this.highestQualification = highestQualification; }
    
    public String getProfessionalCertifications() { return professionalCertifications; }
    public void setProfessionalCertifications(String professionalCertifications) { this.professionalCertifications = professionalCertifications; }
    
    public String getLanguagesSpoken() { return languagesSpoken; }
    public void setLanguagesSpoken(String languagesSpoken) { this.languagesSpoken = languagesSpoken; }
    
    public String getPreviousEmployer() { return previousEmployer; }
    public void setPreviousEmployer(String previousEmployer) { this.previousEmployer = previousEmployer; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    // Security & Compliance
    public String getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(String backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    
    public LocalDate getBackgroundCheckDate() { return backgroundCheckDate; }
    public void setBackgroundCheckDate(LocalDate backgroundCheckDate) { this.backgroundCheckDate = backgroundCheckDate; }
    
    public boolean isRequiresBackgroundCheckRenewal() { return requiresBackgroundCheckRenewal; }
    public void setRequiresBackgroundCheckRenewal(boolean requiresBackgroundCheckRenewal) { this.requiresBackgroundCheckRenewal = requiresBackgroundCheckRenewal; }
    
    public Boolean getSecurityTrainingCompleted() { return securityTrainingCompleted; }
    public void setSecurityTrainingCompleted(Boolean securityTrainingCompleted) { this.securityTrainingCompleted = securityTrainingCompleted; }
    
    public LocalDate getSecurityTrainingDate() { return securityTrainingDate; }
    public void setSecurityTrainingDate(LocalDate securityTrainingDate) { this.securityTrainingDate = securityTrainingDate; }
    
    // Performance Management
    public LocalDate getLastPerformanceReview() { return lastPerformanceReview; }
    public void setLastPerformanceReview(LocalDate lastPerformanceReview) { this.lastPerformanceReview = lastPerformanceReview; }
    
    public LocalDate getNextPerformanceReview() { return nextPerformanceReview; }
    public void setNextPerformanceReview(LocalDate nextPerformanceReview) { this.nextPerformanceReview = nextPerformanceReview; }
    
    // Status Change Audit
    public String getStatusChangeReason() { return statusChangeReason; }
    public void setStatusChangeReason(String statusChangeReason) { this.statusChangeReason = statusChangeReason; }
    
    public Long getStatusChangedBy() { return statusChangedBy; }
    public void setStatusChangedBy(Long statusChangedBy) { this.statusChangedBy = statusChangedBy; }
    
    public String getStatusChangedByName() { return statusChangedByName; }
    public void setStatusChangedByName(String statusChangedByName) { this.statusChangedByName = statusChangedByName; }
    
    public LocalDateTime getStatusChangeDate() { return statusChangeDate; }
    public void setStatusChangeDate(LocalDateTime statusChangeDate) { this.statusChangeDate = statusChangeDate; }
    
    // System Access
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }
    
    public boolean isHasSystemAccess() { return hasSystemAccess; }
    public void setHasSystemAccess(boolean hasSystemAccess) { this.hasSystemAccess = hasSystemAccess; }
    
    public boolean isPayrollEligible() { return isPayrollEligible; }
    public void setPayrollEligible(boolean payrollEligible) { isPayrollEligible = payrollEligible; }
    
    // Compliance
    public Boolean getDataPrivacyConsent() { return dataPrivacyConsent; }
    public void setDataPrivacyConsent(Boolean dataPrivacyConsent) { this.dataPrivacyConsent = dataPrivacyConsent; }
    
    public LocalDateTime getTermsAcceptedDate() { return termsAcceptedDate; }
    public void setTermsAcceptedDate(LocalDateTime termsAcceptedDate) { this.termsAcceptedDate = termsAcceptedDate; }
    
    public Boolean getConfidentialityAgreementSigned() { return confidentialityAgreementSigned; }
    public void setConfidentialityAgreementSigned(Boolean confidentialityAgreementSigned) { this.confidentialityAgreementSigned = confidentialityAgreementSigned; }
    
    // Legacy fields for backward compatibility
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive; 
        if (this.employeeStatus == null) {
            this.employeeStatus = Boolean.TRUE.equals(isActive) ? EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE;
        }
    }
    
    public Set<String> getRoleNames() { return roleNames; }
    public void setRoleNames(Set<String> roleNames) { this.roleNames = roleNames; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getStatusDisplayName() {
        return employeeStatus != null ? employeeStatus.getDisplayName() : "Unknown";
    }
    
    public String getEmploymentTypeDisplayName() {
        return employmentType != null ? employmentType.getDisplayName() : "Unknown";
    }
    
    public String getClearanceLevelDisplayName() {
        return clearanceLevel != null ? clearanceLevel.getDisplayName() : "None";
    }
}