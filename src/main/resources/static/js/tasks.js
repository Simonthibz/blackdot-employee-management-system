// Global variables
let allTasks = [];
let currentView = 'all';

// Load tasks on page load
document.addEventListener('DOMContentLoaded', function() {
    if (canAssignTasks) {
        loadAllTasks();
        loadEmployeesForAssignment();
    } else {
        loadMyTasks();
    }
});

// Load all tasks
function loadAllTasks() {
    currentView = 'all';
    fetch('/api/tasks', {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(tasks => {
        allTasks = tasks;
        displayTasks(tasks);
    })
    .catch(error => {
        console.error('Error loading tasks:', error);
        showNotification('Failed to load tasks', 'error');
    });
}

// Load my tasks
function loadMyTasks() {
    currentView = 'my';
    fetch('/api/tasks/my-tasks', {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(tasks => {
        allTasks = tasks;
        displayTasks(tasks);
    })
    .catch(error => {
        console.error('Error loading my tasks:', error);
        showNotification('Failed to load tasks', 'error');
    });
}

// Load tasks created by me
function loadMyCreatedTasks() {
    currentView = 'created';
    fetch('/api/tasks/created-by-me', {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(tasks => {
        allTasks = tasks;
        displayTasks(tasks);
    })
    .catch(error => {
        console.error('Error loading created tasks:', error);
        showNotification('Failed to load tasks', 'error');
    });
}

// Load overdue tasks
function loadOverdueTasks() {
    currentView = 'overdue';
    fetch('/api/tasks/overdue', {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(tasks => {
        allTasks = tasks;
        displayTasks(tasks);
    })
    .catch(error => {
        console.error('Error loading overdue tasks:', error);
        showNotification('Failed to load tasks', 'error');
    });
}

// Display tasks in table
function displayTasks(tasks) {
    const tbody = document.getElementById('tasksTableBody');
    
    if (tasks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No tasks found</td></tr>';
        return;
    }
    
    tbody.innerHTML = tasks.map(task => `
        <tr class="${task.overdue ? 'table-danger' : ''}">
            <td>
                <strong>${escapeHtml(task.title)}</strong>
                ${task.overdue ? '<span class="badge badge-danger ml-2">OVERDUE</span>' : ''}
            </td>
            <td>
                <span class="badge badge-${getPriorityBadgeClass(task.priority)}">
                    ${task.priority}
                </span>
            </td>
            <td>
                <span class="badge badge-${getStatusBadgeClass(task.status)}">
                    ${task.status.replace('_', ' ')}
                </span>
            </td>
            <td>${task.assignedToName || 'Unassigned'}</td>
            <td>${task.dueDate ? formatDate(task.dueDate) : 'No due date'}</td>
            <td>
                <button onclick="viewTask(${task.id})" class="btn btn-sm" title="View Details">
                    <i class="fas fa-eye"></i>
                </button>
                ${canAssignTasks ? `
                    <button onclick="editTask(${task.id})" class="btn btn-sm" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="deleteTask(${task.id})" class="btn btn-sm btn-danger" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                ` : `
                    <button onclick="updateTaskStatus(${task.id}, '${task.status}')" class="btn btn-sm" title="Update Status">
                        <i class="fas fa-sync"></i>
                    </button>
                `}
            </td>
        </tr>
    `).join('');
}

// Filter tasks
function filterTasks() {
    const statusFilter = document.getElementById('statusFilter').value;
    const priorityFilter = document.getElementById('priorityFilter').value;
    const searchTerm = document.getElementById('searchTasks').value.toLowerCase();
    
    let filtered = allTasks;
    
    if (statusFilter) {
        filtered = filtered.filter(task => task.status === statusFilter);
    }
    
    if (priorityFilter) {
        filtered = filtered.filter(task => task.priority === priorityFilter);
    }
    
    if (searchTerm) {
        filtered = filtered.filter(task => 
            task.title.toLowerCase().includes(searchTerm) ||
            (task.description && task.description.toLowerCase().includes(searchTerm))
        );
    }
    
    displayTasks(filtered);
}

// Show create task modal
function showCreateTaskModal() {
    document.getElementById('taskModalTitle').innerHTML = '<i class="fas fa-plus"></i> Create Task';
    document.getElementById('taskForm').reset();
    document.getElementById('taskId').value = '';
    document.getElementById('taskStatus').disabled = true;
    document.getElementById('taskModal').style.display = 'block';
}

// Edit task
function editTask(taskId) {
    fetch(`/api/tasks/${taskId}`, {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(task => {
        document.getElementById('taskModalTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Task';
        document.getElementById('taskId').value = task.id;
        document.getElementById('taskTitle').value = task.title;
        document.getElementById('taskDescription').value = task.description || '';
        document.getElementById('taskPriority').value = task.priority;
        document.getElementById('taskStatus').value = task.status;
        document.getElementById('taskStatus').disabled = false;
        document.getElementById('taskAssignedTo').value = task.assignedToId || '';
        document.getElementById('taskDueDate').value = task.dueDate ? task.dueDate.substring(0, 16) : '';
        document.getElementById('taskNotes').value = task.notes || '';
        document.getElementById('taskModal').style.display = 'block';
    })
    .catch(error => {
        console.error('Error loading task:', error);
        showNotification('Failed to load task details', 'error');
    });
}

// View task details
function viewTask(taskId) {
    fetch(`/api/tasks/${taskId}`, {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(task => {
        const content = `
            <div class="p-3">
                <h4>${escapeHtml(task.title)} 
                    <span class="badge badge-${getPriorityBadgeClass(task.priority)}">${task.priority}</span>
                    <span class="badge badge-${getStatusBadgeClass(task.status)}">${task.status.replace('_', ' ')}</span>
                </h4>
                
                <div class="mt-3">
                    <strong>Description:</strong>
                    <p>${task.description || 'No description'}</p>
                </div>
                
                <div class="mt-2">
                    <strong>Assigned To:</strong> ${task.assignedToName || 'Unassigned'}
                    ${task.assignedToEmail ? `(${task.assignedToEmail})` : ''}
                </div>
                
                <div class="mt-2">
                    <strong>Assigned By:</strong> ${task.assignedByName}
                    ${task.assignedByEmail ? `(${task.assignedByEmail})` : ''}
                </div>
                
                <div class="mt-2">
                    <strong>Due Date:</strong> ${task.dueDate ? formatDate(task.dueDate) : 'No due date'}
                    ${task.overdue ? '<span class="badge badge-danger ml-2">OVERDUE</span>' : ''}
                </div>
                
                <div class="mt-2">
                    <strong>Created:</strong> ${formatDate(task.createdAt)}
                </div>
                
                ${task.completedAt ? `
                    <div class="mt-2">
                        <strong>Completed:</strong> ${formatDate(task.completedAt)}
                    </div>
                ` : ''}
                
                ${task.notes ? `
                    <div class="mt-3">
                        <strong>Notes:</strong>
                        <p>${escapeHtml(task.notes)}</p>
                    </div>
                ` : ''}
                
                <div class="mt-4">
                    ${!canAssignTasks && task.assignedToId ? `
                        <button onclick="showStatusUpdateOptions(${task.id}, '${task.status}')" class="btn btn-primary">
                            <i class="fas fa-sync"></i> Update Status
                        </button>
                    ` : ''}
                    <button onclick="closeViewTaskModal()" class="btn">Close</button>
                </div>
            </div>
        `;
        document.getElementById('taskDetailsContent').innerHTML = content;
        document.getElementById('viewTaskModal').style.display = 'block';
    })
    .catch(error => {
        console.error('Error loading task:', error);
        showNotification('Failed to load task details', 'error');
    });
}

// Task form submission
document.getElementById('taskForm')?.addEventListener('submit', function(e) {
    e.preventDefault();
    saveTask();
});

// Save task
function saveTask() {
    const taskId = document.getElementById('taskId').value;
    const isEdit = taskId !== '';
    
    const taskData = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        priority: document.getElementById('taskPriority').value,
        assignedToId: document.getElementById('taskAssignedTo').value || null,
        dueDate: document.getElementById('taskDueDate').value || null,
        notes: document.getElementById('taskNotes').value
    };
    
    if (isEdit) {
        taskData.status = document.getElementById('taskStatus').value;
    }
    
    const url = isEdit ? `/api/tasks/${taskId}` : '/api/tasks';
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: getHeaders(),
        body: JSON.stringify(taskData)
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to save task');
        return response.json();
    })
    .then(() => {
        showNotification(isEdit ? 'Task updated successfully' : 'Task created successfully', 'success');
        closeTaskModal();
        if (currentView === 'all') loadAllTasks();
        else if (currentView === 'my') loadMyTasks();
        else if (currentView === 'created') loadMyCreatedTasks();
        else if (currentView === 'overdue') loadOverdueTasks();
    })
    .catch(error => {
        console.error('Error saving task:', error);
        showNotification('Failed to save task', 'error');
    });
}

// Delete task
function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    fetch(`/api/tasks/${taskId}`, {
        method: 'DELETE',
        headers: getHeaders()
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to delete task');
        showNotification('Task deleted successfully', 'success');
        if (currentView === 'all') loadAllTasks();
        else if (currentView === 'created') loadMyCreatedTasks();
    })
    .catch(error => {
        console.error('Error deleting task:', error);
        showNotification('Failed to delete task', 'error');
    });
}

// Show status update options
function showStatusUpdateOptions(taskId, currentStatus) {
    const statuses = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD'];
    const options = statuses.map(status => 
        `<button onclick="updateMyTaskStatus(${taskId}, '${status}')" 
                 class="btn btn-sm ${status === currentStatus ? 'btn-primary' : ''}" 
                 style="margin: 5px;">
            ${status.replace('_', ' ')}
         </button>`
    ).join('');
    
    document.getElementById('taskDetailsContent').innerHTML += `
        <div class="mt-3 p-3" style="background: #f5f5f5; border-radius: 5px;">
            <strong>Update Status:</strong><br>
            ${options}
        </div>
    `;
}

// Update my task status
function updateMyTaskStatus(taskId, status) {
    fetch(`/api/tasks/${taskId}/status?status=${status}`, {
        method: 'PATCH',
        headers: getHeaders()
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to update status');
        return response.json();
    })
    .then(() => {
        showNotification('Task status updated successfully', 'success');
        closeViewTaskModal();
        loadMyTasks();
    })
    .catch(error => {
        console.error('Error updating status:', error);
        showNotification('Failed to update task status', 'error');
    });
}

// Load employees for assignment dropdown
function loadEmployeesForAssignment() {
    fetch('/api/employees', {
        headers: getHeaders()
    })
    .then(response => response.json())
    .then(data => {
        const select = document.getElementById('taskAssignedTo');
        const employees = data.content || [];
        
        select.innerHTML = '<option value="">Select User...</option>' +
            employees.map(emp => 
                `<option value="${emp.id}">${emp.firstName} ${emp.lastName} - ${emp.department || 'N/A'}</option>`
            ).join('');
    })
    .catch(error => console.error('Error loading employees:', error));
}

// Close modals
function closeTaskModal() {
    document.getElementById('taskModal').style.display = 'none';
}

function closeViewTaskModal() {
    document.getElementById('viewTaskModal').style.display = 'none';
}

// Utility functions
function getHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function getPriorityBadgeClass(priority) {
    const classes = {
        'LOW': 'secondary',
        'MEDIUM': 'info',
        'HIGH': 'warning',
        'URGENT': 'danger'
    };
    return classes[priority] || 'secondary';
}

function getStatusBadgeClass(status) {
    const classes = {
        'PENDING': 'secondary',
        'IN_PROGRESS': 'info',
        'COMPLETED': 'success',
        'CANCELLED': 'danger',
        'ON_HOLD': 'warning'
    };
    return classes[status] || 'secondary';
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#27ae60' : '#e74c3c'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        z-index: 10000;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => notification.remove(), 3000);
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login';
}

// Close modal on outside click
window.onclick = function(event) {
    const taskModal = document.getElementById('taskModal');
    const viewTaskModal = document.getElementById('viewTaskModal');
    if (event.target === taskModal) {
        closeTaskModal();
    }
    if (event.target === viewTaskModal) {
        closeViewTaskModal();
    }
}
