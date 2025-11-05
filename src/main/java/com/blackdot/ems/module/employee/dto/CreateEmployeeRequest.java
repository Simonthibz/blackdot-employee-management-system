package com.blackdot.ems.module.employee.dto;

import com.blackdot.ems.shared.entity.EmployeeStatus;
import com.blackdot.ems.shared.entity.EmploymentType;
import com.blackdot.ems.shared.entity.ClearanceLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public class CreateEmployeeRequest {
    
    // Basic Information
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    @NotBlank
    @Size(max = 50)
    private String firstName;
    
    @NotBlank
    @Size(max = 50)
    private String lastName;
    
    @NotBlank
    private String employeeId;
    
    // Government-Grade Status
    private EmployeeStatus employeeStatus;
    private EmploymentType employmentType;
    private ClearanceLevel clearanceLevel;
    
    // Organizational Information
    private String department;
    private String position;
    private String branchOffice;
    private String costCenter;
    private String jobGrade;
    
    // Contract Information
    private LocalDate hireDate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate probationEndDate;
    private Integer noticePeriodDays;
    
    // Contact Information
    private String phoneNumber;
    private String emergencyContactName;
    private String address;
    
    // Professional Information
    private String highestQualification;
    private Integer yearsOfExperience;
    private String professionalCertifications;
    
    // Security & Compliance
    private String backgroundCheckStatus;
    private LocalDate backgroundCheckDate;
    private Boolean securityTrainingCompleted;
    private Boolean confidentialityAgreementSigned;
    private Boolean dataPrivacyConsent;
    
    // Roles
    private Set<String> roles;
    
    // Constructors
    public CreateEmployeeRequest() {}
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public EmployeeStatus getEmployeeStatus() { return employeeStatus; }
    public void setEmployeeStatus(EmployeeStatus employeeStatus) { this.employeeStatus = employeeStatus; }
    
    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }
    
    public ClearanceLevel getClearanceLevel() { return clearanceLevel; }
    public void setClearanceLevel(ClearanceLevel clearanceLevel) { this.clearanceLevel = clearanceLevel; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public String getBranchOffice() { return branchOffice; }
    public void setBranchOffice(String branchOffice) { this.branchOffice = branchOffice; }
    
    public String getCostCenter() { return costCenter; }
    public void setCostCenter(String costCenter) { this.costCenter = costCenter; }
    
    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
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
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getHighestQualification() { return highestQualification; }
    public void setHighestQualification(String highestQualification) { this.highestQualification = highestQualification; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public String getProfessionalCertifications() { return professionalCertifications; }
    public void setProfessionalCertifications(String professionalCertifications) { this.professionalCertifications = professionalCertifications; }
    
    public String getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(String backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    
    public LocalDate getBackgroundCheckDate() { return backgroundCheckDate; }
    public void setBackgroundCheckDate(LocalDate backgroundCheckDate) { this.backgroundCheckDate = backgroundCheckDate; }
    
    public Boolean getSecurityTrainingCompleted() { return securityTrainingCompleted; }
    public void setSecurityTrainingCompleted(Boolean securityTrainingCompleted) { this.securityTrainingCompleted = securityTrainingCompleted; }
    
    public Boolean getConfidentialityAgreementSigned() { return confidentialityAgreementSigned; }
    public void setConfidentialityAgreementSigned(Boolean confidentialityAgreementSigned) { this.confidentialityAgreementSigned = confidentialityAgreementSigned; }
    
    public Boolean getDataPrivacyConsent() { return dataPrivacyConsent; }
    public void setDataPrivacyConsent(Boolean dataPrivacyConsent) { this.dataPrivacyConsent = dataPrivacyConsent; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}