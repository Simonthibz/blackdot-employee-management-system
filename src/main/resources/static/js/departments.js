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
    fetch('/api/employees', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allEmployees = data;
        const select = document.getElementById('headOfDepartmentId');
        select.innerHTML = '<option value="">Select Head of Department</option>';
        
        data.forEach(employee => {
            const option = document.createElement('option');
            option.value = employee.id;
            option.textContent = `${employee.firstName} ${employee.lastName} (${employee.employeeId || 'N/A'})`;
            select.appendChild(option);
        });
    })
    .catch(error => console.error('Error loading employees:', error));
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
            <td>
                <button onclick="viewDepartment(${dept.id})" class="btn btn-sm btn-info" title="View">
                    <i class="fas fa-eye"></i>
                </button>
                <button onclick="editDepartment(${dept.id})" class="btn btn-sm btn-warning" title="Edit">
                    <i class="fas fa-edit"></i>
                </button>
                <button onclick="deleteDepartment(${dept.id})" class="btn btn-sm btn-danger" title="Delete">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
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
    document.getElementById('modalTitle').textContent = 'Create Department';
    document.getElementById('departmentForm').reset();
    document.getElementById('departmentId').value = '';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('departmentModal').style.display = 'block';
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
        document.getElementById('viewStatus').textContent = dept.isActive ? 'Active' : 'Inactive';
        document.getElementById('viewCreatedAt').textContent = new Date(dept.createdAt).toLocaleString();
        
        document.getElementById('viewDepartmentModal').style.display = 'block';
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
        document.getElementById('modalTitle').textContent = 'Edit Department';
        document.getElementById('departmentId').value = dept.id;
        document.getElementById('name').value = dept.name;
        document.getElementById('code').value = dept.code || '';
        document.getElementById('description').value = dept.description || '';
        document.getElementById('headOfDepartmentId').value = dept.headOfDepartmentId || '';
        document.getElementById('location').value = dept.location || '';
        document.getElementById('costCenterCode').value = dept.costCenterCode || '';
        document.getElementById('budget').value = dept.budget || '';
        document.getElementById('isActive').value = dept.isActive.toString();
        document.getElementById('statusGroup').style.display = 'block';
        
        document.getElementById('departmentModal').style.display = 'block';
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
