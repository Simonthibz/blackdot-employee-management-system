// Employee Management JavaScript

let currentEmployees = [];
let currentPage = 0;
const pageSize = 10;
let searchTerm = '';
let roleFilter = '';
let statusFilter = '';

// Load employees from API
async function loadEmployees(page = 0) {
    currentPage = page;
    
    try {
        let url = `/api/employees?page=${currentPage}&size=${pageSize}`;
        
        if (searchTerm) url += `&search=${encodeURIComponent(searchTerm)}`;
        if (roleFilter) url += `&role=${roleFilter}`;
        if (statusFilter) url += `&status=${statusFilter}`;
        
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            currentEmployees = data.content || data;
            displayEmployees(currentEmployees);
            updatePagination(data);
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            console.error('Failed to load employees. Status:', response.status);
            const errorText = await response.text();
            console.error('Error details:', errorText);
            showMessage(`Failed to load employees: ${response.status}`, 'error');
        }
    } catch (error) {
        console.error('Error loading employees:', error);
        showMessage('Network error loading employees', 'error');
    }
}

// Display employees in table
function displayEmployees(employees) {
    const tbody = document.getElementById('employeeTableBody');
    tbody.innerHTML = '';
    
    if (employees.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="11" class="text-center p-3">
                    <i class="fas fa-info-circle"></i> No employees found
                </td>
            </tr>
        `;
        return;
    }
    
    employees.forEach(employee => {
        const row = document.createElement('tr');
        
        // Employee Status Badge
        const statusBadge = getStatusBadge(employee.employeeStatus || (employee.isActive ? 'ACTIVE' : 'INACTIVE'));
        
        // Employment Type Badge
        const typeBadge = getEmploymentTypeBadge(employee.employmentType || 'PERMANENT');
        
        // Clearance Level Badge
        const clearanceBadge = getClearanceBadge(employee.clearanceLevel || 'PUBLIC');
        
        // Contract Information
        const contractInfo = getContractInfo(employee);
        
        row.innerHTML = `
            <td>
                <strong>${employee.employeeId || 'N/A'}</strong>
                ${employee.isInProbation ? '<br><span class="badge badge-warning">Probation</span>' : ''}
            </td>
            <td>
                <strong>${employee.firstName} ${employee.lastName}</strong><br>
                <small style="color: #7f8c8d;">@${employee.username}</small>
                ${employee.branchOffice ? `<br><small><i class="fas fa-building"></i> ${employee.branchOffice}</small>` : ''}
            </td>
            <td>
                ${employee.email}
                ${employee.phoneNumber ? `<br><small><i class="fas fa-phone"></i> ${employee.phoneNumber}</small>` : ''}
            </td>
            <td>
                <strong>${employee.department || 'N/A'}</strong>
                ${employee.costCenter ? `<br><small>CC: ${employee.costCenter}</small>` : ''}
            </td>
            <td>
                <strong>${employee.position || 'N/A'}</strong>
                ${employee.jobGrade ? `<br><small>Grade: ${employee.jobGrade}</small>` : ''}
            </td>
            <td>${statusBadge}</td>
            <td>${typeBadge}</td>
            <td>${clearanceBadge}</td>
            <td>
                ${employee.roleNames ? employee.roleNames.map(role => 
                    `<span class="badge badge-secondary" style="margin-right: 0.25rem;">
                        ${role.replace('ROLE_', '')}
                    </span>`
                ).join('') : 'No roles'}
            </td>
            <td class="contract-info">
                ${contractInfo}
            </td>
            <td>
                <button onclick="viewEmployeeDetails(${employee.id})" class="btn btn-sm" style="margin-right: 0.5rem;" title="View Details">
                    <i class="fas fa-eye"></i>
                </button>
                <button onclick="editEmployee(${employee.id})" class="btn btn-sm" style="margin-right: 0.5rem;" title="Edit">
                    <i class="fas fa-edit"></i>
                </button>
                <button onclick="changeEmployeeStatus(${employee.id})" class="btn btn-sm btn-warning" style="margin-right: 0.5rem;" title="Change Status">
                    <i class="fas fa-exchange-alt"></i>
                </button>
                <button onclick="deleteEmployee(${employee.id})" class="btn btn-sm btn-danger" title="Delete">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Helper functions for badges and formatting
function getStatusBadge(status) {
    const statusMap = {
        'ACTIVE': { class: 'badge-success', text: 'Active', icon: 'check-circle' },
        'INACTIVE': { class: 'badge-secondary', text: 'Inactive', icon: 'circle' },
        'SUSPENDED': { class: 'badge-warning', text: 'Suspended', icon: 'pause-circle' },
        'BLOCKED': { class: 'badge-danger', text: 'Blocked', icon: 'ban' },
        'TERMINATED': { class: 'badge-danger', text: 'Terminated', icon: 'times-circle' },
        'ON_LEAVE': { class: 'badge-info', text: 'On Leave', icon: 'calendar' },
        'PROBATION': { class: 'badge-warning', text: 'Probation', icon: 'hourglass-half' },
        'PENDING_START': { class: 'badge-info', text: 'Pending Start', icon: 'clock' }
    };
    
    const statusInfo = statusMap[status] || statusMap['INACTIVE'];
    return `<span class="badge ${statusInfo.class}">
        <i class="fas fa-${statusInfo.icon}"></i> ${statusInfo.text}
    </span>`;
}

function getEmploymentTypeBadge(type) {
    const typeMap = {
        'PERMANENT': { class: 'badge-primary', text: 'Permanent' },
        'CONTRACT': { class: 'badge-warning', text: 'Contract' },
        'TEMPORARY': { class: 'badge-info', text: 'Temporary' },
        'CONSULTANT': { class: 'badge-secondary', text: 'Consultant' },
        'INTERN': { class: 'badge-success', text: 'Intern' },
        'PART_TIME': { class: 'badge-info', text: 'Part-Time' },
        'SEASONAL': { class: 'badge-warning', text: 'Seasonal' }
    };
    
    const typeInfo = typeMap[type] || typeMap['PERMANENT'];
    return `<span class="badge ${typeInfo.class}">${typeInfo.text}</span>`;
}

function getClearanceBadge(level) {
    const levelMap = {
        'PUBLIC': { class: 'clearance-public', text: 'Public' },
        'INTERNAL': { class: 'clearance-internal', text: 'Internal' },
        'CONFIDENTIAL': { class: 'clearance-confidential', text: 'Confidential' },
        'RESTRICTED': { class: 'clearance-restricted', text: 'Restricted' },
        'SECRET': { class: 'clearance-secret', text: 'Secret' },
        'EXECUTIVE': { class: 'clearance-executive', text: 'Executive' }
    };
    
    const levelInfo = levelMap[level] || levelMap['PUBLIC'];
    return `<span class="clearance-badge ${levelInfo.class}">
        <i class="fas fa-lock"></i> ${levelInfo.text}
    </span>`;
}

function getContractInfo(employee) {
    let info = [];
    
    if (employee.contractStartDate) {
        info.push(`Start: ${formatDate(employee.contractStartDate)}`);
    }
    
    if (employee.contractEndDate) {
        const isExpiring = employee.isContractExpiringSoon;
        info.push(`End: <span class="${isExpiring ? 'contract-expiring' : ''}">${formatDate(employee.contractEndDate)}</span>`);
        if (isExpiring) {
            info.push('<i class="fas fa-exclamation-triangle"></i> Expiring Soon');
        }
    }
    
    if (employee.isInProbation && employee.probationEndDate) {
        info.push(`Probation: ${formatDate(employee.probationEndDate)}`);
    }
    
    return info.length > 0 ? info.join('<br>') : 'No contract data';
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Update pagination info
function updatePagination(data) {
    const info = document.getElementById('paginationInfo');
    const controls = document.getElementById('paginationControls');
    
    if (data.content) {
        // Paginated response
        const start = data.number * data.size + 1;
        const end = Math.min((data.number + 1) * data.size, data.totalElements);
        info.textContent = `Showing ${start}-${end} of ${data.totalElements} employees`;
        
        // Pagination controls
        controls.innerHTML = '';
        if (data.totalPages > 1) {
            for (let i = 0; i < data.totalPages; i++) {
                const button = document.createElement('button');
                button.textContent = i + 1;
                button.className = `btn btn-sm ${i === data.number ? 'btn-success' : 'btn-secondary'}`;
                button.style.marginRight = '0.5rem';
                button.onclick = () => {
                    currentPage = i;
                    filterEmployees();
                };
                controls.appendChild(button);
            }
        }
    } else {
        // Simple array response
        info.textContent = `Showing ${data.length} employees`;
        controls.innerHTML = '';
    }
}

// Filter employees
function filterEmployees() {
    const search = document.getElementById('searchInput').value;
    const role = document.getElementById('roleFilter').value;
    const status = document.getElementById('statusFilter') ? document.getElementById('statusFilter').value : '';
    
    let url = `/api/employees?page=${currentPage}&size=${pageSize}`;
    
    if (search) url += `&search=${encodeURIComponent(search)}`;
    if (role) url += `&role=${role}`;
    if (status) url += `&status=${status}`;
    
    loadEmployeesWithUrl(url);
}

// Helper function to load employees with custom URL
async function loadEmployeesWithUrl(url) {
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            currentEmployees = data.content || data;
            displayEmployees(currentEmployees);
            updatePagination(data);
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            console.error('Failed to load employees. Status:', response.status);
            showMessage(`Failed to load employees: ${response.status}`, 'error');
        }
    } catch (error) {
        console.error('Error loading employees:', error);
        showMessage('Network error loading employees', 'error');
    }
}

// Show add employee modal
function showAddEmployeeModal() {
    document.getElementById('modalTitle').textContent = 'Add Employee';
    document.getElementById('employeeForm').reset();
    document.getElementById('employeeId').value = '';
    document.getElementById('passwordGroup').style.display = 'block';
    document.getElementById('password').required = true;
    document.getElementById('username').disabled = false; // Enable username for new employees
    document.getElementById('employeeModal').style.display = 'flex';
}

// Edit employee
async function editEmployee(id) {
    try {
        const response = await fetch(`/api/employees/${id}`, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const employee = await response.json();
            populateEmployeeForm(employee);
            document.getElementById('modalTitle').textContent = 'Edit Employee';
            document.getElementById('passwordGroup').style.display = 'block';
            document.getElementById('password').required = false;
            document.getElementById('username').disabled = true; // Username cannot be changed
            document.getElementById('employeeModal').style.display = 'flex';
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            showMessage('Failed to load employee details', 'error');
        }
    } catch (error) {
        console.error('Error loading employee:', error);
        showMessage('Network error loading employee', 'error');
    }
}

// Populate form with employee data
function populateEmployeeForm(employee) {
    // Basic Information
    document.getElementById('employeeId').value = employee.id;
    document.getElementById('employeeIdInput').value = employee.employeeId || '';
    document.getElementById('firstName').value = employee.firstName;
    document.getElementById('lastName').value = employee.lastName;
    document.getElementById('username').value = employee.username;
    document.getElementById('email').value = employee.email;
    
    // Government-Grade Status
    document.getElementById('employeeStatus').value = employee.employeeStatus || 'PENDING_START';
    document.getElementById('employmentType').value = employee.employmentType || 'PERMANENT';
    document.getElementById('clearanceLevel').value = employee.clearanceLevel || 'PUBLIC';
    
    // Organizational Information
    document.getElementById('department').value = employee.department || '';
    document.getElementById('position').value = employee.position || '';
    document.getElementById('branchOffice').value = employee.branchOffice || '';
    document.getElementById('costCenter').value = employee.costCenter || '';
    document.getElementById('jobGrade').value = employee.jobGrade || '';
    
    // Contract Information
    document.getElementById('hireDate').value = employee.hireDate || '';
    document.getElementById('contractStartDate').value = employee.contractStartDate || '';
    document.getElementById('contractEndDate').value = employee.contractEndDate || '';
    document.getElementById('probationEndDate').value = employee.probationEndDate || '';
    document.getElementById('noticePeriodDays').value = employee.noticePeriodDays || 30;
    
    // Contact Information
    document.getElementById('phoneNumber').value = employee.phoneNumber || '';
    document.getElementById('emergencyContactName').value = employee.emergencyContactName || '';
    document.getElementById('address').value = employee.address || '';
    
    // Professional Information
    document.getElementById('highestQualification').value = employee.highestQualification || '';
    document.getElementById('yearsOfExperience').value = employee.yearsOfExperience || '';
    document.getElementById('professionalCertifications').value = employee.professionalCertifications || '';
    
    // Security & Compliance
    document.getElementById('backgroundCheckStatus').value = employee.backgroundCheckStatus || 'Pending';
    document.getElementById('backgroundCheckDate').value = employee.backgroundCheckDate || '';
    document.getElementById('securityTrainingCompleted').checked = employee.securityTrainingCompleted || false;
    document.getElementById('confidentialityAgreementSigned').checked = employee.confidentialityAgreementSigned || false;
    document.getElementById('dataPrivacyConsent').checked = employee.dataPrivacyConsent || false;
    
    // Legacy fields
    document.getElementById('isActive').checked = employee.isActive;
    
    // Set roles
    const roleCheckboxes = document.querySelectorAll('input[name="roles"]');
    roleCheckboxes.forEach(checkbox => {
        const roleName = checkbox.value.replace('ROLE_', '');
        checkbox.checked = employee.roleNames && employee.roleNames.includes(roleName);
    });
}

// Save employee
async function saveEmployee() {
    const form = document.getElementById('employeeForm');
    const formData = new FormData(form);
    
    const id = document.getElementById('employeeId').value;
    const isEdit = id !== '';
    
    const employeeData = {
        // Basic Information
        employeeId: formData.get('employeeId'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        email: formData.get('email'),
        
        // Government-Grade Status
        employeeStatus: formData.get('employeeStatus'),
        employmentType: formData.get('employmentType'),
        clearanceLevel: formData.get('clearanceLevel'),
        
        // Organizational Information
        department: formData.get('department'),
        position: formData.get('position'),
        branchOffice: formData.get('branchOffice'),
        costCenter: formData.get('costCenter'),
        jobGrade: formData.get('jobGrade'),
        
        // Contract Information
        hireDate: formData.get('hireDate') || null,
        contractStartDate: formData.get('contractStartDate') || null,
        contractEndDate: formData.get('contractEndDate') || null,
        probationEndDate: formData.get('probationEndDate') || null,
        noticePeriodDays: parseInt(formData.get('noticePeriodDays')) || 30,
        
        // Contact Information
        phoneNumber: formData.get('phoneNumber'),
        emergencyContactName: formData.get('emergencyContactName'),
        address: formData.get('address'),
        
        // Professional Information
        highestQualification: formData.get('highestQualification'),
        yearsOfExperience: parseInt(formData.get('yearsOfExperience')) || null,
        professionalCertifications: formData.get('professionalCertifications'),
        
        // Security & Compliance
        backgroundCheckStatus: formData.get('backgroundCheckStatus'),
        backgroundCheckDate: formData.get('backgroundCheckDate') || null,
        securityTrainingCompleted: document.getElementById('securityTrainingCompleted').checked,
        confidentialityAgreementSigned: document.getElementById('confidentialityAgreementSigned').checked,
        dataPrivacyConsent: document.getElementById('dataPrivacyConsent').checked,
        
        // Legacy and System Access
        isActive: document.getElementById('isActive').checked,
        roles: Array.from(document.querySelectorAll('input[name="roles"]:checked')).map(cb => cb.value.replace('ROLE_', ''))
    };
    
    // Only include username and password for new employees
    if (!isEdit) {
        employeeData.username = formData.get('username');
        const password = formData.get('password');
        if (password) {
            employeeData.password = password;
        }
    } else {
        // For updates, only include password if it was changed
        const password = formData.get('password');
        if (password && password.trim() !== '') {
            employeeData.password = password;
        }
    }
    
    try {
        console.log('Saving employee:', isEdit ? 'UPDATE' : 'CREATE', employeeData);
        
        const response = await fetch(`/api/employees${isEdit ? `/${id}` : ''}`, {
            method: isEdit ? 'PUT' : 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(employeeData)
        });
        
        if (response.ok) {
            const savedEmployee = await response.json();
            console.log('Employee saved successfully:', savedEmployee);
            showMessage(`Employee ${isEdit ? 'updated' : 'created'} successfully`, 'success');
            closeEmployeeModal();
            loadEmployees(currentPage);
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            const error = await response.json();
            console.error('Server error:', error);
            showMessage(error.message || `Failed to ${isEdit ? 'update' : 'create'} employee`, 'error');
        }
    } catch (error) {
        console.error('Error saving employee:', error);
        showMessage('Network error saving employee', 'error');
    }
}

// Delete employee
async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to delete this employee?')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/employees/${id}`, {
            method: 'DELETE',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            showMessage('Employee deleted successfully', 'success');
            loadEmployees(currentPage);
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            showMessage('Failed to delete employee', 'error');
        }
    } catch (error) {
        console.error('Error deleting employee:', error);
        showMessage('Network error deleting employee', 'error');
    }
}

// Close modal
function closeEmployeeModal() {
    document.getElementById('employeeModal').style.display = 'none';
}

// Show message
function showMessage(message, type) {
    // Create a simple toast notification
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: 4px;
        color: white;
        font-weight: 500;
        z-index: 3000;
        background-color: ${type === 'success' ? '#27ae60' : '#e74c3c'};
    `;
    toast.textContent = message;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        document.body.removeChild(toast);
    }, 3000);
}

// Government-Grade Features

// View employee details in a modal
async function viewEmployeeDetails(id) {
    try {
        const response = await fetch(`/api/employees/${id}`, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const employee = await response.json();
            showEmployeeDetailsModal(employee);
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            showMessage('Failed to load employee details', 'error');
        }
    } catch (error) {
        console.error('Error loading employee details:', error);
        showMessage('Network error loading employee details', 'error');
    }
}

// Show employee details modal
function showEmployeeDetailsModal(employee) {
    const detailsHtml = `
        <div id="employeeDetailsModal" class="modal" style="display: flex;">
            <div class="modal-content" style="max-width: 800px;">
                <div class="modal-header">
                    <h3><i class="fas fa-user"></i> ${employee.firstName} ${employee.lastName} - Employee Details</h3>
                    <span class="modal-close" onclick="closeEmployeeDetailsModal()">&times;</span>
                </div>
                <div class="modal-body" style="max-height: 70vh; overflow-y: auto;">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
                        <div>
                            <h4><i class="fas fa-user"></i> Basic Information</h4>
                            <table class="table">
                                <tr><td><strong>Employee ID:</strong></td><td>${employee.employeeId || 'N/A'}</td></tr>
                                <tr><td><strong>Full Name:</strong></td><td>${employee.firstName} ${employee.lastName}</td></tr>
                                <tr><td><strong>Username:</strong></td><td>${employee.username}</td></tr>
                                <tr><td><strong>Email:</strong></td><td>${employee.email}</td></tr>
                                <tr><td><strong>Phone:</strong></td><td>${employee.phoneNumber || 'N/A'}</td></tr>
                            </table>
                            
                            <h4><i class="fas fa-user-check"></i> Status & Classification</h4>
                            <table class="table">
                                <tr><td><strong>Status:</strong></td><td>${getStatusBadge(employee.employeeStatus || 'INACTIVE')}</td></tr>
                                <tr><td><strong>Employment Type:</strong></td><td>${getEmploymentTypeBadge(employee.employmentType || 'PERMANENT')}</td></tr>
                                <tr><td><strong>Security Level:</strong></td><td>${getClearanceBadge(employee.clearanceLevel || 'PUBLIC')}</td></tr>
                                <tr><td><strong>System Access:</strong></td><td>${employee.hasSystemAccess ? '<span class="badge badge-success">Allowed</span>' : '<span class="badge badge-danger">Denied</span>'}</td></tr>
                            </table>
                            
                            <h4><i class="fas fa-building"></i> Organization</h4>
                            <table class="table">
                                <tr><td><strong>Department:</strong></td><td>${employee.department || 'N/A'}</td></tr>
                                <tr><td><strong>Position:</strong></td><td>${employee.position || 'N/A'}</td></tr>
                                <tr><td><strong>Job Grade:</strong></td><td>${employee.jobGrade || 'N/A'}</td></tr>
                                <tr><td><strong>Branch Office:</strong></td><td>${employee.branchOffice || 'N/A'}</td></tr>
                                <tr><td><strong>Cost Center:</strong></td><td>${employee.costCenter || 'N/A'}</td></tr>
                            </table>
                        </div>
                        
                        <div>
                            <h4><i class="fas fa-file-contract"></i> Contract Information</h4>
                            <table class="table">
                                <tr><td><strong>Hire Date:</strong></td><td>${formatDate(employee.hireDate)}</td></tr>
                                <tr><td><strong>Contract Start:</strong></td><td>${formatDate(employee.contractStartDate)}</td></tr>
                                <tr><td><strong>Contract End:</strong></td><td>${formatDate(employee.contractEndDate)}</td></tr>
                                <tr><td><strong>Probation End:</strong></td><td>${formatDate(employee.probationEndDate)}</td></tr>
                                <tr><td><strong>Notice Period:</strong></td><td>${employee.noticePeriodDays || 'N/A'} days</td></tr>
                                <tr><td><strong>In Probation:</strong></td><td>${employee.isInProbation ? '<span class="badge badge-warning">Yes</span>' : '<span class="badge badge-success">No</span>'}</td></tr>
                            </table>
                            
                            <h4><i class="fas fa-lock"></i> Security & Compliance</h4>
                            <table class="table">
                                <tr><td><strong>Background Check:</strong></td><td>${employee.backgroundCheckStatus || 'N/A'}</td></tr>
                                <tr><td><strong>Check Date:</strong></td><td>${formatDate(employee.backgroundCheckDate)}</td></tr>
                                <tr><td><strong>Security Training:</strong></td><td>${employee.securityTrainingCompleted ? '<span class="badge badge-success">Completed</span>' : '<span class="badge badge-warning">Pending</span>'}</td></tr>
                                <tr><td><strong>NDA Signed:</strong></td><td>${employee.confidentialityAgreementSigned ? '<span class="badge badge-success">Yes</span>' : '<span class="badge badge-danger">No</span>'}</td></tr>
                                <tr><td><strong>Data Privacy:</strong></td><td>${employee.dataPrivacyConsent ? '<span class="badge badge-success">Consented</span>' : '<span class="badge badge-danger">Not Consented</span>'}</td></tr>
                            </table>
                            
                            <h4><i class="fas fa-graduation-cap"></i> Professional Info</h4>
                            <table class="table">
                                <tr><td><strong>Qualifications:</strong></td><td>${employee.highestQualification || 'N/A'}</td></tr>
                                <tr><td><strong>Experience:</strong></td><td>${employee.yearsOfExperience || 'N/A'} years</td></tr>
                                <tr><td><strong>Certifications:</strong></td><td>${employee.professionalCertifications || 'N/A'}</td></tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" onclick="editEmployee(${employee.id})" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Edit Employee
                    </button>
                    <button type="button" onclick="changeEmployeeStatus(${employee.id})" class="btn btn-warning">
                        <i class="fas fa-exchange-alt"></i> Change Status
                    </button>
                    <button type="button" onclick="closeEmployeeDetailsModal()" class="btn btn-secondary">Close</button>
                </div>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', detailsHtml);
}

function closeEmployeeDetailsModal() {
    const modal = document.getElementById('employeeDetailsModal');
    if (modal) {
        document.body.removeChild(modal);
    }
}

// Change employee status
async function changeEmployeeStatus(id) {
    const newStatus = prompt(`Enter new status for employee:
    
Available statuses:
- ACTIVE: Active employee with full access
- INACTIVE: Inactive employee (no system access)
- SUSPENDED: Temporarily suspended
- BLOCKED: Blocked from system access
- ON_LEAVE: Employee on leave
- PROBATION: Employee in probation period
- TERMINATED: Employment terminated
- PENDING_START: New employee pending start date

Enter status:`);
    
    if (!newStatus) return;
    
    const reason = prompt('Enter reason for status change:');
    if (!reason) return;
    
    try {
        const response = await fetch(`/api/employees/${id}/status`, {
            method: 'PUT',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                newStatus: newStatus.toUpperCase(),
                reason: reason,
                changedBy: 1 // This should be the current user's ID
            })
        });
        
        if (response.ok) {
            showMessage('Employee status updated successfully', 'success');
            loadEmployees(currentPage);
            closeEmployeeDetailsModal();
        } else if (response.status === 401) {
            window.location.href = '/login';
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to update employee status', 'error');
        }
    } catch (error) {
        console.error('Error updating employee status:', error);
        showMessage('Network error updating employee status', 'error');
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadEmployees();
    
    // Search functionality
    document.getElementById('searchInput').addEventListener('input', function(e) {
        searchTerm = e.target.value;
        currentPage = 0;
        loadEmployees();
    });
    
    // Role filter
    document.getElementById('roleFilter').addEventListener('change', function(e) {
        roleFilter = e.target.value;
        currentPage = 0;
        loadEmployees();
    });
    
    // Status filter
    document.getElementById('statusFilter').addEventListener('change', function(e) {
        statusFilter = e.target.value;
        currentPage = 0;
        loadEmployees();
    });
});