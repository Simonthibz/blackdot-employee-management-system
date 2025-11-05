// Role Management JavaScript

let allRoles = [];

// Load roles on page load
document.addEventListener('DOMContentLoaded', function() {
    loadActiveRoles();
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

// Load active roles
function loadActiveRoles() {
    fetch('/api/roles/active', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allRoles = data;
        displayRoles(data);
    })
    .catch(error => {
        console.error('Error loading roles:', error);
        document.getElementById('rolesTableBody').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Error loading roles</td></tr>';
    });
}

// Load all roles
function loadAllRoles() {
    fetch('/api/roles', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allRoles = data;
        displayRoles(data);
    })
    .catch(error => console.error('Error loading roles:', error));
}

// Load system roles
function loadSystemRoles() {
    fetch('/api/roles/system', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allRoles = data;
        displayRoles(data);
    })
    .catch(error => console.error('Error loading system roles:', error));
}

// Load custom roles
function loadCustomRoles() {
    fetch('/api/roles/custom', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        allRoles = data;
        displayRoles(data);
    })
    .catch(error => console.error('Error loading custom roles:', error));
}

// Display roles in table
function displayRoles(roles) {
    const tbody = document.getElementById('rolesTableBody');
    
    if (roles.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No roles found</td></tr>';
        return;
    }
    
    tbody.innerHTML = roles.map(role => `
        <tr>
            <td><strong>${role.displayName || role.name}</strong></td>
            <td>${role.name || '-'}</td>
            <td>
                <span class="badge ${role.isSystemRole ? 'badge-primary' : 'badge-info'}">
                    ${role.isSystemRole ? 'System' : 'Custom'}
                </span>
            </td>
            <td>${role.userCount || 0}</td>
            <td>${role.priority || '-'}</td>
            <td>
                <span class="badge ${role.isActive ? 'badge-success' : 'badge-danger'}">
                    ${role.isActive ? 'Active' : 'Inactive'}
                </span>
            </td>
            <td>
                <button onclick="viewRole(${role.id})" class="btn btn-sm btn-info" title="View">
                    <i class="fas fa-eye"></i>
                </button>
                <button onclick="editRole(${role.id})" class="btn btn-sm btn-warning" title="Edit">
                    <i class="fas fa-edit"></i>
                </button>
                ${!role.isSystemRole ? `
                    <button onclick="deleteRole(${role.id})" class="btn btn-sm btn-danger" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                ` : ''}
            </td>
        </tr>
    `).join('');
}

// Show create role modal
function showCreateRoleModal() {
    document.getElementById('modalTitle').textContent = 'Create Custom Role';
    document.getElementById('roleForm').reset();
    document.getElementById('roleId').value = '';
    document.getElementById('isSystemRole').value = 'false';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('roleModal').style.display = 'block';
}

// Close role modal
function closeRoleModal() {
    document.getElementById('roleModal').style.display = 'none';
}

// View role
function viewRole(id) {
    fetch(`/api/roles/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(role => {
        document.getElementById('viewDisplayName').textContent = role.displayName || role.name;
        document.getElementById('viewName').textContent = role.name || '-';
        document.getElementById('viewType').textContent = role.isSystemRole ? 'System Role' : 'Custom Role';
        document.getElementById('viewDescription').textContent = role.description || '-';
        document.getElementById('viewPermissions').innerHTML = role.permissionList && role.permissionList.length > 0 
            ? '<ul>' + role.permissionList.map(p => `<li>${p}</li>`).join('') + '</ul>'
            : '-';
        document.getElementById('viewPriority').textContent = role.priority || '-';
        document.getElementById('viewUserCount').textContent = role.userCount || 0;
        document.getElementById('viewStatus').textContent = role.isActive ? 'Active' : 'Inactive';
        
        document.getElementById('viewRoleModal').style.display = 'block';
    })
    .catch(error => {
        console.error('Error loading role:', error);
        alert('Failed to load role details');
    });
}

// Close view role modal
function closeViewRoleModal() {
    document.getElementById('viewRoleModal').style.display = 'none';
}

// Edit role
function editRole(id) {
    fetch(`/api/roles/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(role => {
        document.getElementById('modalTitle').textContent = role.isSystemRole ? 'Edit System Role' : 'Edit Custom Role';
        document.getElementById('roleId').value = role.id;
        document.getElementById('isSystemRole').value = role.isSystemRole.toString();
        document.getElementById('displayName').value = role.displayName || '';
        document.getElementById('displayName').disabled = role.isSystemRole; // Can't change system role name
        document.getElementById('description').value = role.description || '';
        document.getElementById('permissions').value = role.permissions || '';
        document.getElementById('priority').value = role.priority || '';
        document.getElementById('isActive').value = role.isActive.toString();
        document.getElementById('statusGroup').style.display = 'block';
        
        document.getElementById('roleModal').style.display = 'block';
    })
    .catch(error => {
        console.error('Error loading role:', error);
        alert('Failed to load role for editing');
    });
}

// Save role (create or update)
function saveRole(event) {
    event.preventDefault();
    
    const id = document.getElementById('roleId').value;
    const isSystemRole = document.getElementById('isSystemRole').value === 'true';
    
    const roleData = {
        displayName: document.getElementById('displayName').value,
        description: document.getElementById('description').value || null,
        permissions: document.getElementById('permissions').value || null,
        priority: document.getElementById('priority').value ? parseInt(document.getElementById('priority').value) : null
    };
    
    if (id) {
        roleData.isActive = document.getElementById('isActive').value === 'true';
    }
    
    const url = id ? `/api/roles/${id}` : '/api/roles';
    const method = id ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(roleData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to save role');
        }
        return response.json();
    })
    .then(data => {
        alert(id ? 'Role updated successfully' : 'Role created successfully');
        closeRoleModal();
        loadActiveRoles();
    })
    .catch(error => {
        console.error('Error saving role:', error);
        alert('Failed to save role. Please check if the role name already exists.');
    });
}

// Delete role
function deleteRole(id) {
    if (!confirm('Are you sure you want to delete this role? This action cannot be undone.')) {
        return;
    }
    
    fetch(`/api/roles/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete role');
        }
        alert('Role deleted successfully');
        loadActiveRoles();
    })
    .catch(error => {
        console.error('Error deleting role:', error);
        alert('Failed to delete role. The role may have users assigned to it or it may be a system role.');
    });
}

// Close modals when clicking outside
window.onclick = function(event) {
    const roleModal = document.getElementById('roleModal');
    const viewRoleModal = document.getElementById('viewRoleModal');
    
    if (event.target === roleModal) {
        closeRoleModal();
    }
    if (event.target === viewRoleModal) {
        closeViewRoleModal();
    }
}
