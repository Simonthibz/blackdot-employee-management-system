// Role Management JavaScript

let allRoles = [];
let availablePermissions = [];

// Load roles on page load
document.addEventListener('DOMContentLoaded', function() {
    loadActiveRoles();
    loadAvailablePermissions();
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

// Load available permissions
function loadAvailablePermissions() {
    fetch('/api/roles/permissions', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        availablePermissions = data;
    })
    .catch(error => console.error('Error loading permissions:', error));
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
            <td><strong>${role.displayName || role.name || '-'}</strong></td>
            <td>${role.name ? role.name.replace('ROLE_', '') : 'Custom'}</td>
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
            <td class="text-center">
                <div class="action-menu">
                    <button class="action-btn" onclick="toggleActionMenu(event, ${role.id})">
                        <i class="fas fa-ellipsis-v"></i>
                    </button>
                    <div class="action-dropdown" id="actionMenu${role.id}">
                        <button class="action-item" onclick="viewRole(${role.id})">
                            <i class="fas fa-eye"></i> View Details
                        </button>
                        ${!role.isSystemRole ? `
                            <button class="action-item" onclick="editRole(${role.id})">
                                <i class="fas fa-edit"></i> Edit Role
                            </button>
                            <button class="action-item danger" onclick="deleteRole(${role.id})">
                                <i class="fas fa-trash"></i> Delete Role
                            </button>
                        ` : `
                            <button class="action-item" onclick="editRole(${role.id})">
                                <i class="fas fa-eye"></i> View Role
                            </button>
                        `}
                    </div>
                </div>
            </td>
        </tr>
    `).join('');
}

// Toggle action menu
function toggleActionMenu(event, roleId) {
    event.stopPropagation();
    const menu = document.getElementById(`actionMenu${roleId}`);
    const allMenus = document.querySelectorAll('.action-dropdown');
    
    allMenus.forEach(m => {
        if (m !== menu) m.classList.remove('show');
    });
    
    menu.classList.toggle('show');
}

// Close action menus when clicking outside
document.addEventListener('click', function() {
    document.querySelectorAll('.action-dropdown').forEach(menu => {
        menu.classList.remove('show');
    });
});

// Show create role modal
function showCreateRoleModal() {
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-user-shield"></i> Create Custom Role';
    document.getElementById('roleForm').reset();
    document.getElementById('roleId').value = '';
    document.getElementById('isSystemRole').value = 'false';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('roleTypeSection').style.display = 'block';
    document.getElementById('displayName').disabled = false;
    
    // Load permissions checkboxes
    renderPermissionsCheckboxes([]);
    
    document.getElementById('roleModal').style.display = 'flex';
}

// Render permissions as checkboxes
function renderPermissionsCheckboxes(selectedPermissions) {
    const container = document.getElementById('permissionsContainer');
    
    if (!availablePermissions || availablePermissions.length === 0) {
        container.innerHTML = '<p class="text-center" style="color: #6c757d;">No permissions available</p>';
        return;
    }
    
    // Group permissions by category
    const grouped = groupPermissionsByCategory(availablePermissions);
    
    let html = '';
    for (const [category, permissions] of Object.entries(grouped)) {
        html += `
            <div class="permission-category">
                <div class="permission-category-title">
                    <i class="fas fa-${getCategoryIcon(category)}"></i>
                    ${category}
                </div>
                <div class="permission-grid">
        `;
        
        permissions.forEach(permission => {
            const isChecked = selectedPermissions.includes(permission);
            const permissionLabel = permission.replace(/_/g, ' ').toLowerCase()
                .replace(/\b\w/g, l => l.toUpperCase());
            
            html += `
                <div class="permission-item">
                    <input type="checkbox" 
                           id="perm_${permission}" 
                           name="permissions" 
                           value="${permission}"
                           ${isChecked ? 'checked' : ''}>
                    <label for="perm_${permission}">${permissionLabel}</label>
                </div>
            `;
        });
        
        html += `
                </div>
            </div>
        `;
    }
    
    container.innerHTML = html;
}

// Group permissions by category
function groupPermissionsByCategory(permissions) {
    const groups = {};
    
    permissions.forEach(permission => {
        const parts = permission.split('_');
        const category = parts.length > 1 ? parts.slice(1).join(' ') : 'General';
        const mainCategory = parts.length > 1 ? parts[1].toLowerCase() : 'general';
        
        const categoryName = mainCategory.charAt(0).toUpperCase() + mainCategory.slice(1);
        
        if (!groups[categoryName]) {
            groups[categoryName] = [];
        }
        groups[categoryName].push(permission);
    });
    
    return groups;
}

// Get category icon
function getCategoryIcon(category) {
    const icons = {
        'Employee': 'users',
        'Department': 'building',
        'Task': 'tasks',
        'Role': 'user-shield',
        'Leave': 'calendar-days',
        'Attendance': 'clock',
        'Payroll': 'dollar-sign',
        'Performance': 'chart-line',
        'Report': 'file-alt',
        'System': 'cog',
        'Manage': 'cogs',
        'View': 'eye'
    };
    
    return icons[category] || 'shield-alt';
}

// Toggle all permissions
function toggleAllPermissions() {
    const checkboxes = document.querySelectorAll('#permissionsContainer input[type="checkbox"]');
    const allChecked = Array.from(checkboxes).every(cb => cb.checked);
    
    checkboxes.forEach(cb => {
        cb.checked = !allChecked;
    });
}

// Handle role type change
function handleRoleTypeChange() {
    const roleType = document.querySelector('input[name="roleType"]:checked').value;
    // Currently only custom roles are allowed
    document.getElementById('isSystemRole').value = 'false';
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
        
        document.getElementById('viewRoleModal').style.display = 'flex';
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
        document.getElementById('modalTitle').innerHTML = role.isSystemRole 
            ? '<i class="fas fa-user-shield"></i> View System Role' 
            : '<i class="fas fa-user-shield"></i> Edit Custom Role';
        document.getElementById('roleId').value = role.id;
        document.getElementById('isSystemRole').value = role.isSystemRole.toString();
        document.getElementById('displayName').value = role.displayName || '';
        document.getElementById('displayName').disabled = role.isSystemRole; // Can't change system role name
        document.getElementById('description').value = role.description || '';
        document.getElementById('priority').value = role.priority || '';
        document.getElementById('isActive').value = role.isActive.toString();
        document.getElementById('statusGroup').style.display = 'block';
        document.getElementById('roleTypeSection').style.display = 'none';
        
        // Load permissions checkboxes with selected permissions
        const selectedPermissions = role.permissionList || [];
        renderPermissionsCheckboxes(selectedPermissions);
        
        // Disable all inputs for system roles
        if (role.isSystemRole) {
            document.getElementById('description').disabled = true;
            document.getElementById('priority').disabled = true;
            document.getElementById('isActive').disabled = true;
            
            // Disable all permission checkboxes
            document.querySelectorAll('#permissionsContainer input[type="checkbox"]').forEach(cb => {
                cb.disabled = true;
            });
        }
        
        document.getElementById('roleModal').style.display = 'flex';
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
    
    // Prevent saving system roles
    if (isSystemRole) {
        alert('System roles cannot be modified');
        return;
    }
    
    // Get selected permissions from checkboxes
    const selectedPermissions = Array.from(document.querySelectorAll('#permissionsContainer input[type="checkbox"]:checked'))
        .map(cb => cb.value);
    
    if (selectedPermissions.length === 0) {
        alert('Please select at least one permission');
        return;
    }
    
    const roleData = {
        displayName: document.getElementById('displayName').value,
        description: document.getElementById('description').value || null,
        permissions: selectedPermissions.join(','),
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
