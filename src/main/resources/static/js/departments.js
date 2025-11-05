// Department Management JavaScript

let allDepartments = [];
let allEmployees = [];

// Load departments on page load
document.addEventListener('DOMContentLoaded', function() {
    loadActiveDepartments();
    loadEmployeesForDepartmentHead();
});

// Get JWT token from localStorage
function getToken() {
    return localStorage.getItem('jwtToken');
}

// Logout function
function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
}

// Load active departments
function loadActiveDepartments() {
    fetch('/api/departments/active', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        console.log('Loaded departments:', data); // Debug log
        allDepartments = data;
        displayDepartments(data);
    })
    .catch(error => {
        console.error('Error loading departments:', error);
        document.getElementById('departmentsTableBody').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Error loading departments</td></tr>';
    });
}

// Load all departments
function loadAllDepartments() {
    fetch('/api/departments', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allDepartments = data;
        displayDepartments(data);
    })
    .catch(error => console.error('Error loading departments:', error));
}

// Load employees for department head dropdown
function loadEmployeesForDepartmentHead() {
    fetch('/api/employees/list', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to load employees');
        }
        return response.json();
    })
    .then(data => {
        console.log('Loaded employees:', data); // Debug log
        allEmployees = data;
        populateDepartmentHeadDropdown(data);
    })
    .catch(error => {
        console.error('Error loading employees:', error);
        alert('Failed to load employees. Please check console for details.');
    });
}

// Populate department head dropdown
function populateDepartmentHeadDropdown(employees) {
    const select = document.getElementById('headOfDepartmentId');
    if (!select) {
        console.error('Head of Department dropdown not found');
        return;
    }
    
    select.innerHTML = '<option value="">Select Head of Department</option>';
    
    if (!employees || employees.length === 0) {
        console.warn('No employees available');
        return;
    }
    
    employees.forEach(employee => {
        const option = document.createElement('option');
        option.value = employee.id;
        option.textContent = `${employee.firstName} ${employee.lastName} (${employee.employeeId || 'N/A'})`;
        select.appendChild(option);
    });
    
    console.log('Populated dropdown with', employees.length, 'employees');
}

// Display departments in table
function displayDepartments(departments) {
    const tbody = document.getElementById('departmentsTableBody');
    
    if (departments.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No departments found</td></tr>';
        return;
    }
    
    tbody.innerHTML = departments.map(dept => `
        <tr>
            <td><strong>${dept.name}</strong></td>
            <td>${dept.code || '-'}</td>
            <td>${dept.headOfDepartmentName || '-'}</td>
            <td>${dept.employeeCount || 0}</td>
            <td>${dept.location || '-'}</td>
            <td>
                <span class="badge ${dept.isActive ? 'badge-success' : 'badge-danger'}">
                    ${dept.isActive ? 'Active' : 'Inactive'}
                </span>
            </td>
            <td class="text-center">
                <div class="action-menu">
                    <button class="action-btn" onclick="toggleActionMenu(event, ${dept.id})" title="Actions">
                        <i class="fas fa-ellipsis-v"></i>
                    </button>
                    <div class="action-dropdown" id="actionMenu-${dept.id}">
                        <button class="action-item" onclick="viewDepartment(${dept.id})">
                            <i class="fas fa-eye"></i>
                            <span>View Details</span>
                        </button>
                        <button class="action-item" onclick="editDepartment(${dept.id})">
                            <i class="fas fa-edit"></i>
                            <span>Edit</span>
                        </button>
                        <button class="action-item danger" onclick="deleteDepartment(${dept.id})">
                            <i class="fas fa-trash"></i>
                            <span>Delete</span>
                        </button>
                    </div>
                </div>
            </td>
        </tr>
    `).join('');
}

// Toggle action menu
function toggleActionMenu(event, deptId) {
    event.stopPropagation();
    const menu = document.getElementById(`actionMenu-${deptId}`);
    const allMenus = document.querySelectorAll('.action-dropdown');
    
    // Close all other menus
    allMenus.forEach(m => {
        if (m !== menu) {
            m.classList.remove('show');
        }
    });
    
    // Toggle current menu
    menu.classList.toggle('show');
}

// Filter departments
function filterDepartments() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const statusFilter = document.getElementById('statusFilter').value;
    
    let filtered = allDepartments;
    
    // Apply search filter
    if (searchText) {
        filtered = filtered.filter(dept => 
            dept.name.toLowerCase().includes(searchText) ||
            (dept.code && dept.code.toLowerCase().includes(searchText)) ||
            (dept.location && dept.location.toLowerCase().includes(searchText))
        );
    }
    
    // Apply status filter
    if (statusFilter !== '') {
        const isActive = statusFilter === 'true';
        filtered = filtered.filter(dept => dept.isActive === isActive);
    }
    
    displayDepartments(filtered);
}

// Show create department modal
function showCreateDepartmentModal() {
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-sitemap"></i> Create Department';
    document.getElementById('departmentForm').reset();
    document.getElementById('departmentId').value = '';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('statusHeader').style.display = 'none';
    
    // Reload employees to ensure dropdown is populated
    if (allEmployees.length > 0) {
        populateDepartmentHeadDropdown(allEmployees);
    } else {
        loadEmployeesForDepartmentHead();
    }
    
    document.getElementById('departmentModal').style.display = 'flex';
}

// Close department modal
function closeDepartmentModal() {
    document.getElementById('departmentModal').style.display = 'none';
}

// View department
function viewDepartment(id) {
    fetch(`/api/departments/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(dept => {
        document.getElementById('viewName').textContent = dept.name;
        document.getElementById('viewCode').textContent = dept.code || '-';
        document.getElementById('viewDescription').textContent = dept.description || '-';
        document.getElementById('viewHeadOfDepartment').textContent = dept.headOfDepartmentName || '-';
        document.getElementById('viewLocation').textContent = dept.location || '-';
        document.getElementById('viewCostCenterCode').textContent = dept.costCenterCode || '-';
        document.getElementById('viewBudget').textContent = dept.budget ? `$${dept.budget.toFixed(2)}` : '-';
        document.getElementById('viewEmployeeCount').textContent = dept.employeeCount || 0;
        
        const statusElement = document.getElementById('viewStatus');
        statusElement.innerHTML = `<span class="badge ${dept.isActive ? 'badge-success' : 'badge-danger'}">${dept.isActive ? 'Active' : 'Inactive'}</span>`;
        
        document.getElementById('viewCreatedAt').textContent = new Date(dept.createdAt).toLocaleString();
        
        document.getElementById('viewDepartmentModal').style.display = 'flex';
    })
    .catch(error => {
        console.error('Error loading department:', error);
        alert('Failed to load department details');
    });
}

// Close view department modal
function closeViewDepartmentModal() {
    document.getElementById('viewDepartmentModal').style.display = 'none';
}

// Close action menus when clicking outside
document.addEventListener('click', function(event) {
    if (!event.target.closest('.action-menu')) {
        const allMenus = document.querySelectorAll('.action-dropdown');
        allMenus.forEach(menu => menu.classList.remove('show'));
    }
});

// Edit department
function editDepartment(id) {
    fetch(`/api/departments/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(dept => {
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Department';
        document.getElementById('departmentId').value = dept.id;
        document.getElementById('name').value = dept.name;
        document.getElementById('code').value = dept.code || '';
        document.getElementById('description').value = dept.description || '';
        document.getElementById('location').value = dept.location || '';
        document.getElementById('costCenterCode').value = dept.costCenterCode || '';
        document.getElementById('budget').value = dept.budget || '';
        document.getElementById('isActive').value = dept.isActive.toString();
        document.getElementById('statusGroup').style.display = 'block';
        document.getElementById('statusHeader').style.display = 'flex';
        
        // Reload employees and set the selected value
        if (allEmployees.length > 0) {
            populateDepartmentHeadDropdown(allEmployees);
            document.getElementById('headOfDepartmentId').value = dept.headOfDepartmentId || '';
        } else {
            loadEmployeesForDepartmentHead();
            setTimeout(() => {
                document.getElementById('headOfDepartmentId').value = dept.headOfDepartmentId || '';
            }, 500);
        }
        
        document.getElementById('departmentModal').style.display = 'flex';
    })
    .catch(error => {
        console.error('Error loading department:', error);
        alert('Failed to load department for editing');
    });
}

// Save department (create or update)
function saveDepartment(event) {
    event.preventDefault();
    
    const id = document.getElementById('departmentId').value;
    const departmentData = {
        name: document.getElementById('name').value,
        code: document.getElementById('code').value || null,
        description: document.getElementById('description').value || null,
        headOfDepartmentId: document.getElementById('headOfDepartmentId').value || null,
        location: document.getElementById('location').value || null,
        costCenterCode: document.getElementById('costCenterCode').value || null,
        budget: document.getElementById('budget').value ? parseFloat(document.getElementById('budget').value) : null
    };
    
    if (id) {
        departmentData.isActive = document.getElementById('isActive').value === 'true';
    }
    
    const url = id ? `/api/departments/${id}` : '/api/departments';
    const method = id ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(departmentData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to save department');
        }
        return response.json();
    })
    .then(data => {
        alert(id ? 'Department updated successfully' : 'Department created successfully');
        closeDepartmentModal();
        loadActiveDepartments();
    })
    .catch(error => {
        console.error('Error saving department:', error);
        alert('Failed to save department. Please check if the name or code already exists.');
    });
}

// Delete department
function deleteDepartment(id) {
    if (!confirm('Are you sure you want to delete this department? This action cannot be undone.')) {
        return;
    }
    
    fetch(`/api/departments/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete department');
        }
        alert('Department deleted successfully');
        loadActiveDepartments();
    })
    .catch(error => {
        console.error('Error deleting department:', error);
        alert('Failed to delete department. The department may have employees assigned to it.');
    });
}

// Close modals when clicking outside
window.onclick = function(event) {
    const departmentModal = document.getElementById('departmentModal');
    const viewDepartmentModal = document.getElementById('viewDepartmentModal');
    
    if (event.target === departmentModal) {
        closeDepartmentModal();
    }
    if (event.target === viewDepartmentModal) {
        closeViewDepartmentModal();
    }
}

// Load all departments when status filter changes to show all
document.getElementById('statusFilter').addEventListener('change', function() {
    const statusValue = this.value;
    if (statusValue === '') {
        loadAllDepartments();
    } else {
        loadActiveDepartments();
    }
});
