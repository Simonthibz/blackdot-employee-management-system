package com.blackdot.ems.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    
    @Column(nullable = false)
    @NotBlank
    @Size(min = 6, max = 120)
    private String password;
    
    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    
    @Column(name = "first_name", nullable = false)
    @NotBlank
    @Size(max = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    @NotBlank
    @Size(max = 50)
    private String lastName;
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    // Department relationship - replaces the String department field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department departmentEntity;
    
    @Column(name = "department")
    private String department; // Keep for backward compatibility during migration
    
    @Column(name = "position")
    private String position;
    
    // Enhanced Employee Status Management
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_status")
    private EmployeeStatus employeeStatus = EmployeeStatus.PENDING_START;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType = EmploymentType.PERMANENT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "clearance_level")
    private ClearanceLevel clearanceLevel = ClearanceLevel.PUBLIC;
    
    // Professional Details
    @Column(name = "branch_office")
    private String branchOffice;
    
    @Column(name = "cost_center")
    private String costCenter;
    
    @Column(name = "reporting_manager_id")
    private Long reportingManagerId;
    
    @Column(name = "job_grade")
    private String jobGrade;
    
    @Column(name = "salary_band")
    private String salaryBand;
    
    // Contract and Employment Details
    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;
    
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;
    
    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;
    
    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;
    
    // Contact and Personal Information
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "address")
    private String address;
    
    // Professional Qualifications
    @Column(name = "highest_qualification")
    private String highestQualification;
    
    @Column(name = "professional_certifications")
    private String professionalCertifications;
    
    @Column(name = "languages_spoken")
    private String languagesSpoken;
    
    // Employment History and Status
    @Column(name = "previous_employer")
    private String previousEmployer;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "background_check_status")
    private String backgroundCheckStatus;
    
    @Column(name = "background_check_date")
    private LocalDate backgroundCheckDate;
    
    @Column(name = "last_performance_review")
    private LocalDate lastPerformanceReview;
    
    @Column(name = "next_performance_review")
    private LocalDate nextPerformanceReview;
    
    // Status and Access Management
    @Column(name = "status_change_reason")
    private String statusChangeReason;
    
    @Column(name = "status_changed_by")
    private Long statusChangedBy;
    
    @Column(name = "status_change_date")
    private LocalDateTime statusChangeDate;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
    
    // Compliance and Audit
    @Column(name = "data_privacy_consent")
    private Boolean dataPrivacyConsent = false;
    
    @Column(name = "terms_accepted_date")
    private LocalDateTime termsAcceptedDate;
    
    @Column(name = "confidentiality_agreement_signed")
    private Boolean confidentialityAgreementSigned = false;
    
    @Column(name = "security_training_completed")
    private Boolean securityTrainingCompleted = false;
    
    @Column(name = "security_training_date")
    private LocalDate securityTrainingDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssessmentResult> assessmentResults = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Department getDepartmentEntity() { return departmentEntity; }
    public void setDepartmentEntity(Department departmentEntity) { this.departmentEntity = departmentEntity; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    public Set<AssessmentResult> getAssessmentResults() { return assessmentResults; }
    public void setAssessmentResults(Set<AssessmentResult> assessmentResults) { this.assessmentResults = assessmentResults; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enhanced Professional Fields Getters and Setters
    public EmployeeStatus getEmployeeStatus() { return employeeStatus; }
    public void setEmployeeStatus(EmployeeStatus employeeStatus) { this.employeeStatus = employeeStatus; }
    
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
    
    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }
    
    public String getSalaryBand() { return salaryBand; }
    public void setSalaryBand(String salaryBand) { this.salaryBand = salaryBand; }
    
    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }
    
    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }
    
    public LocalDate getProbationEndDate() { return probationEndDate; }
    public void setProbationEndDate(LocalDate probationEndDate) { this.probationEndDate = probationEndDate; }
    
    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
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
    
    public String getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(String backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    
    public LocalDate getBackgroundCheckDate() { return backgroundCheckDate; }
    public void setBackgroundCheckDate(LocalDate backgroundCheckDate) { this.backgroundCheckDate = backgroundCheckDate; }
    
    public LocalDate getLastPerformanceReview() { return lastPerformanceReview; }
    public void setLastPerformanceReview(LocalDate lastPerformanceReview) { this.lastPerformanceReview = lastPerformanceReview; }
    
    public LocalDate getNextPerformanceReview() { return nextPerformanceReview; }
    public void setNextPerformanceReview(LocalDate nextPerformanceReview) { this.nextPerformanceReview = nextPerformanceReview; }
    
    public String getStatusChangeReason() { return statusChangeReason; }
    public void setStatusChangeReason(String statusChangeReason) { this.statusChangeReason = statusChangeReason; }
    
    public Long getStatusChangedBy() { return statusChangedBy; }
    public void setStatusChangedBy(Long statusChangedBy) { this.statusChangedBy = statusChangedBy; }
    
    public LocalDateTime getStatusChangeDate() { return statusChangeDate; }
    public void setStatusChangeDate(LocalDateTime statusChangeDate) { this.statusChangeDate = statusChangeDate; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }
    
    public Boolean getDataPrivacyConsent() { return dataPrivacyConsent; }
    public void setDataPrivacyConsent(Boolean dataPrivacyConsent) { this.dataPrivacyConsent = dataPrivacyConsent; }
    
    public LocalDateTime getTermsAcceptedDate() { return termsAcceptedDate; }
    public void setTermsAcceptedDate(LocalDateTime termsAcceptedDate) { this.termsAcceptedDate = termsAcceptedDate; }
    
    public Boolean getConfidentialityAgreementSigned() { return confidentialityAgreementSigned; }
    public void setConfidentialityAgreementSigned(Boolean confidentialityAgreementSigned) { this.confidentialityAgreementSigned = confidentialityAgreementSigned; }
    
    public Boolean getSecurityTrainingCompleted() { return securityTrainingCompleted; }
    public void setSecurityTrainingCompleted(Boolean securityTrainingCompleted) { this.securityTrainingCompleted = securityTrainingCompleted; }
    
    public LocalDate getSecurityTrainingDate() { return securityTrainingDate; }
    public void setSecurityTrainingDate(LocalDate securityTrainingDate) { this.securityTrainingDate = securityTrainingDate; }
    
    // Utility Methods for Business Logic
    
    /**
     * Determines if employee has system access based on status and account state
     */
    public boolean hasSystemAccess() {
        if (accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now())) {
            return false;
        }
        return employeeStatus != null && employeeStatus.hasSystemAccess();
    }
    
    /**
     * Determines if employee is eligible for payroll processing
     */
    public boolean isPayrollEligible() {
        return employeeStatus != null && employeeStatus.isPayrollEligible();
    }
    
    /**
     * Gets the full name of the employee
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Determines if employee is in probation period
     */
    public boolean isInProbation() {
        return probationEndDate != null && probationEndDate.isAfter(LocalDate.now());
    }
    
    /**
     * Determines if employee requires background check renewal
     */
    public boolean requiresBackgroundCheckRenewal() {
        if (clearanceLevel == null || backgroundCheckDate == null) {
            return true;
        }
        LocalDate renewalDate = backgroundCheckDate.plusMonths(clearanceLevel.getReviewPeriodMonths());
        return renewalDate.isBefore(LocalDate.now());
    }
    
    /**
     * Determines if contract is expiring soon (within 30 days)
     */
    public boolean isContractExpiringSoon() {
        if (contractEndDate == null) {
            return false;
        }
        return contractEndDate.isBefore(LocalDate.now().plusDays(30));
    }
}