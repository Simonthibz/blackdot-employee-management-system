/**
 * Employee Lifecycle Management JavaScript
 * Handles all frontend interactions for lifecycle management
 */

// Global variables
let lifecycleData = {
    events: [],
    employees: [],
    eventTypes: {},
    eventStatuses: {},
    currentPage: 0,
    pageSize: 10,
    totalPages: 0
};

// Initialize when document is ready
document.addEventListener('DOMContentLoaded', function() {
    initializeLifecycleManagement();
});

/**
 * Initialize the lifecycle management system
 */
async function initializeLifecycleManagement() {
    try {
        showLoadingSpinner();
        
        // Load initial data
        await Promise.all([
            loadStatistics(),
            loadEventTypes(),
            loadEventStatuses(),
            loadEmployees(),
            loadDashboardData()
        ]);
        
        // Initialize event listeners
        setupEventListeners();
        
        hideLoadingSpinner();
        
        console.log('Employee Lifecycle Management initialized successfully');
    } catch (error) {
        console.error('Error initializing lifecycle management:', error);
        showErrorAlert('Failed to initialize lifecycle management system');
    }
}

/**
 * Load lifecycle statistics
 */
async function loadStatistics() {
    try {
        const response = await fetch('/api/lifecycle/statistics', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const stats = await response.json();
            updateStatisticsDisplay(stats);
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

/**
 * Update statistics display
 */
function updateStatisticsDisplay(stats) {
    document.getElementById('totalEventsCount').textContent = stats.totalEvents || 0;
    document.getElementById('pendingEventsCount').textContent = stats.pendingEvents || 0;
    document.getElementById('overdueEventsCount').textContent = stats.overdueEvents || 0;
    document.getElementById('completedEventsCount').textContent = stats.completedEvents || 0;
}

/**
 * Load event types for dropdowns
 */
async function loadEventTypes() {
    try {
        const response = await fetch('/api/lifecycle/enums/event-types', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            lifecycleData.eventTypes = await response.json();
            populateEventTypeDropdown();
        }
    } catch (error) {
        console.error('Error loading event types:', error);
    }
}

/**
 * Load event statuses for dropdowns
 */
async function loadEventStatuses() {
    try {
        const response = await fetch('/api/lifecycle/enums/event-statuses', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            lifecycleData.eventStatuses = await response.json();
            populateStatusDropdown();
        }
    } catch (error) {
        console.error('Error loading event statuses:', error);
    }
}

/**
 * Load employees for dropdowns
 */
async function loadEmployees() {
    try {
        const response = await fetch('/api/employees', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            lifecycleData.employees = await response.json();
        }
    } catch (error) {
        console.error('Error loading employees:', error);
    }
}

/**
 * Load dashboard data
 */
async function loadDashboardData() {
    await Promise.all([
        loadOverdueEvents(),
        loadDueSoonEvents(),
        loadRecentActivity()
    ]);
}

/**
 * Load overdue events
 */
async function loadOverdueEvents() {
    try {
        const response = await fetch('/api/lifecycle/events/overdue', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const events = await response.json();
            displayEventsList(events, 'overdueEventsList', 'overdue');
        }
    } catch (error) {
        console.error('Error loading overdue events:', error);
        displayEmptyState('overdueEventsList', 'No overdue events found');
    }
}

/**
 * Load events due soon
 */
async function loadDueSoonEvents() {
    try {
        const response = await fetch('/api/lifecycle/events/due-soon?days=7', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const events = await response.json();
            displayEventsList(events, 'dueSoonEventsList', 'due-soon');
        }
    } catch (error) {
        console.error('Error loading due soon events:', error);
        displayEmptyState('dueSoonEventsList', 'No events due soon');
    }
}

/**
 * Load recent activity
 */
async function loadRecentActivity() {
    try {
        const response = await fetch('/api/lifecycle/events?page=0&size=10', {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const data = await response.json();
            displayEventsList(data.content || [], 'recentActivityList', 'recent');
        }
    } catch (error) {
        console.error('Error loading recent activity:', error);
        displayEmptyState('recentActivityList', 'No recent activity found');
    }
}

/**
 * Display events list
 */
function displayEventsList(events, containerId, type) {
    const container = document.getElementById(containerId);
    
    if (!events || events.length === 0) {
        displayEmptyState(containerId, getEmptyStateMessage(type));
        return;
    }
    
    container.innerHTML = events.map(event => createEventItemHTML(event, type)).join('');
}

/**
 * Create event item HTML
 */
function createEventItemHTML(event, type) {
    const statusInfo = lifecycleData.eventStatuses[event.status] || {};
    const typeInfo = lifecycleData.eventTypes[event.eventType] || {};
    
    const eventClass = getEventClass(event, type);
    const priorityClass = getPriorityClass(event.priority);
    
    return `
        <div class="event-item ${eventClass} ${priorityClass}" data-event-id="${event.id}">
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <div class="d-flex align-items-center mb-2">
                        <div class="me-3">
                            <h6 class="mb-0">${event.employee?.firstName || 'Unknown'} ${event.employee?.lastName || 'Employee'}</h6>
                            <small class="text-muted">${typeInfo.displayName || event.eventType}</small>
                        </div>
                        <span class="badge ${statusInfo.badgeClass || 'bg-secondary'} ms-auto">
                            <i class="${statusInfo.icon || 'bi-circle'}"></i> ${statusInfo.displayName || event.status}
                        </span>
                    </div>
                    <p class="mb-2 text-muted small">${event.description || 'No description'}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">
                            <i class="bi bi-calendar"></i> Due: ${formatDate(event.dueDate)}
                        </small>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary btn-sm" onclick="viewEventDetails(${event.id})" title="View Details">
                                <i class="bi bi-eye"></i>
                            </button>
                            <button class="btn btn-outline-success btn-sm" onclick="updateEventStatus(${event.id}, 'IN_PROGRESS')" title="Start">
                                <i class="bi bi-play"></i>
                            </button>
                            <button class="btn btn-outline-warning btn-sm" onclick="updateEventStatus(${event.id}, 'COMPLETED')" title="Complete">
                                <i class="bi bi-check"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Get event CSS class based on type
 */
function getEventClass(event, type) {
    if (type === 'overdue') return 'event-overdue';
    if (type === 'due-soon') return 'event-due-soon';
    if (event.status === 'COMPLETED') return 'event-completed';
    return '';
}

/**
 * Get priority CSS class
 */
function getPriorityClass(priority) {
    switch (priority) {
        case 'HIGH': return 'priority-high';
        case 'MEDIUM': return 'priority-medium';
        case 'LOW': return 'priority-low';
        default: return '';
    }
}

/**
 * Display empty state
 */
function displayEmptyState(containerId, message) {
    document.getElementById(containerId).innerHTML = `
        <div class="empty-state">
            <i class="bi bi-inbox"></i>
            <h5>${message}</h5>
            <p class="text-muted">Check back later or create new events</p>
        </div>
    `;
}

/**
 * Get empty state message
 */
function getEmptyStateMessage(type) {
    switch (type) {
        case 'overdue': return 'No overdue events';
        case 'due-soon': return 'No events due soon';
        case 'recent': return 'No recent activity';
        default: return 'No events found';
    }
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Tab change listeners
    document.querySelectorAll('[data-bs-toggle="pill"]').forEach(tab => {
        tab.addEventListener('shown.bs.tab', handleTabChange);
    });
    
    // Search functionality
    const searchInput = document.getElementById('eventSearchInput');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(handleSearch, 300));
    }
    
    // Filter functionality
    const statusFilter = document.getElementById('statusFilter');
    const typeFilter = document.getElementById('typeFilter');
    
    if (statusFilter) statusFilter.addEventListener('change', handleFilterChange);
    if (typeFilter) typeFilter.addEventListener('change', handleFilterChange);
}

/**
 * Handle tab change
 */
function handleTabChange(event) {
    const targetTab = event.target.getAttribute('data-bs-target');
    
    switch (targetTab) {
        case '#events':
            loadAllEvents();
            break;
        case '#reports':
            loadReports();
            break;
        default:
            break;
    }
}

/**
 * Load all events with pagination
 */
async function loadAllEvents(page = 0) {
    try {
        showTableLoading();
        
        const response = await fetch(`/api/lifecycle/events?page=${page}&size=${lifecycleData.pageSize}`, {
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            const data = await response.json();
            lifecycleData.events = data.content || [];
            lifecycleData.currentPage = data.number || 0;
            lifecycleData.totalPages = data.totalPages || 0;
            
            displayEventsTable();
            updatePagination();
        }
    } catch (error) {
        console.error('Error loading events:', error);
        showTableError();
    }
}

/**
 * Display events table
 */
function displayEventsTable() {
    const tbody = document.getElementById('eventsTableBody');
    
    if (!lifecycleData.events || lifecycleData.events.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center py-4">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <h5>No events found</h5>
                        <p class="text-muted">Create new events to get started</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = lifecycleData.events.map(event => createTableRowHTML(event)).join('');
}

/**
 * Create table row HTML
 */
function createTableRowHTML(event) {
    const statusInfo = lifecycleData.eventStatuses[event.status] || {};
    const typeInfo = lifecycleData.eventTypes[event.eventType] || {};
    
    return `
        <tr data-event-id="${event.id}">
            <td>
                <div>
                    <strong>${event.employee?.firstName || 'Unknown'} ${event.employee?.lastName || 'Employee'}</strong>
                    <br>
                    <small class="text-muted">${event.employee?.email || 'No email'}</small>
                </div>
            </td>
            <td>
                <span class="badge bg-info">${typeInfo.displayName || event.eventType}</span>
                <br>
                <small class="text-muted">${typeInfo.category || ''}</small>
            </td>
            <td>
                <div>
                    ${formatDate(event.dueDate)}
                    ${event.isOverdue ? '<br><span class="badge bg-danger">Overdue</span>' : ''}
                    ${event.isDueSoon ? '<br><span class="badge bg-warning">Due Soon</span>' : ''}
                </div>
            </td>
            <td>
                <span class="badge ${statusInfo.badgeClass || 'bg-secondary'}">
                    <i class="${statusInfo.icon || 'bi-circle'}"></i> ${statusInfo.displayName || event.status}
                </span>
            </td>
            <td>
                <span class="badge ${getPriorityBadgeClass(event.priority)}">${event.priority || 'MEDIUM'}</span>
            </td>
            <td>
                ${event.assignedTo ? `${event.assignedTo.firstName} ${event.assignedTo.lastName}` : 'Unassigned'}
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="viewEventDetails(${event.id})" title="View">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-outline-secondary" onclick="editEvent(${event.id})" title="Edit">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-success dropdown-toggle" data-bs-toggle="dropdown" title="Update Status">
                            <i class="bi bi-gear"></i>
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" onclick="updateEventStatus(${event.id}, 'IN_PROGRESS')">
                                <i class="bi bi-play text-primary"></i> Start
                            </a></li>
                            <li><a class="dropdown-item" onclick="updateEventStatus(${event.id}, 'ON_HOLD')">
                                <i class="bi bi-pause text-warning"></i> Hold
                            </a></li>
                            <li><a class="dropdown-item" onclick="updateEventStatus(${event.id}, 'COMPLETED')">
                                <i class="bi bi-check text-success"></i> Complete
                            </a></li>
                        </ul>
                    </div>
                </div>
            </td>
        </tr>
    `;
}

/**
 * Get priority badge class
 */
function getPriorityBadgeClass(priority) {
    switch (priority) {
        case 'HIGH': return 'bg-danger';
        case 'MEDIUM': return 'bg-warning';
        case 'LOW': return 'bg-info';
        default: return 'bg-secondary';
    }
}

/**
 * Update pagination
 */
function updatePagination() {
    const pagination = document.getElementById('eventsPagination');
    
    if (lifecycleData.totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let paginationHTML = '';
    
    // Previous button
    paginationHTML += `
        <li class="page-item ${lifecycleData.currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${lifecycleData.currentPage - 1})">&laquo;</a>
        </li>
    `;
    
    // Page numbers
    const startPage = Math.max(0, lifecycleData.currentPage - 2);
    const endPage = Math.min(lifecycleData.totalPages - 1, lifecycleData.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === lifecycleData.currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
            </li>
        `;
    }
    
    // Next button
    paginationHTML += `
        <li class="page-item ${lifecycleData.currentPage === lifecycleData.totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${lifecycleData.currentPage + 1})">&raquo;</a>
        </li>
    `;
    
    pagination.innerHTML = paginationHTML;
}

/**
 * Change page
 */
function changePage(page) {
    if (page >= 0 && page < lifecycleData.totalPages && page !== lifecycleData.currentPage) {
        loadAllEvents(page);
    }
}

/**
 * Populate dropdowns
 */
function populateEventTypeDropdown() {
    const typeFilter = document.getElementById('typeFilter');
    if (!typeFilter) return;
    
    const options = Object.values(lifecycleData.eventTypes)
        .map(type => `<option value="${type.name}">${type.displayName}</option>`)
        .join('');
    
    typeFilter.innerHTML = '<option value="">All Types</option>' + options;
}

function populateStatusDropdown() {
    const statusFilter = document.getElementById('statusFilter');
    if (!statusFilter) return;
    
    const options = Object.values(lifecycleData.eventStatuses)
        .map(status => `<option value="${status.name}">${status.displayName}</option>`)
        .join('');
    
    statusFilter.innerHTML = '<option value="">All Statuses</option>' + options;
}

/**
 * Handle search
 */
async function handleSearch() {
    const searchTerm = document.getElementById('eventSearchInput').value.trim();
    
    if (searchTerm) {
        try {
            const response = await fetch(`/api/lifecycle/events/search?searchTerm=${encodeURIComponent(searchTerm)}`, {
                headers: getAuthHeaders()
            });
            
            if (response.ok) {
                lifecycleData.events = await response.json();
                displayEventsTable();
                document.getElementById('eventsPagination').innerHTML = '';
            }
        } catch (error) {
            console.error('Error searching events:', error);
        }
    } else {
        loadAllEvents();
    }
}

/**
 * Handle filter change
 */
async function handleFilterChange() {
    // For simplicity, reload events with basic filtering
    // In a real implementation, you'd send filter parameters to the backend
    loadAllEvents();
}

/**
 * Workflow functions
 */
function showOnboardingModal() {
    showWorkflowModal('onboarding', 'Initialize Onboarding Workflow', initializeOnboarding);
}

function showProbationModal() {
    showWorkflowModal('probation', 'Initialize Probation Workflow', initializeProbation);
}

function showPerformanceReviewModal() {
    showWorkflowModal('performance', 'Schedule Performance Review', schedulePerformanceReview);
}

/**
 * Show workflow modal
 */
function showWorkflowModal(type, title, submitFunction) {
    const modal = createWorkflowModal(type, title, submitFunction);
    document.getElementById('modalContainer').innerHTML = modal;
    
    const modalElement = new bootstrap.Modal(document.getElementById('workflowModal'));
    modalElement.show();
}

/**
 * Create workflow modal HTML
 */
function createWorkflowModal(type, title, submitFunction) {
    const employeeOptions = lifecycleData.employees
        .map(emp => `<option value="${emp.id}">${emp.firstName} ${emp.lastName}</option>`)
        .join('');
    
    return `
        <div class="modal fade" id="workflowModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${title}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="workflowForm">
                            <div class="mb-3">
                                <label for="employeeSelect" class="form-label">Employee *</label>
                                <select class="form-select" id="employeeSelect" required>
                                    <option value="">Select Employee</option>
                                    ${employeeOptions}
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="startDate" class="form-label">Start Date *</label>
                                <input type="date" class="form-control" id="startDate" required>
                            </div>
                            <div class="mb-3">
                                <label for="notes" class="form-label">Notes</label>
                                <textarea class="form-control" id="notes" rows="3" placeholder="Additional notes..."></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" onclick="${submitFunction.name}()">Initialize</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Initialize onboarding workflow
 */
async function initializeOnboarding() {
    const employeeId = document.getElementById('employeeSelect').value;
    const startDate = document.getElementById('startDate').value;
    const notes = document.getElementById('notes').value;
    
    if (!employeeId || !startDate) {
        showErrorAlert('Please fill in all required fields');
        return;
    }
    
    try {
        const response = await fetch('/api/lifecycle/onboarding/initialize', {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                employeeId: parseInt(employeeId),
                startDate: startDate,
                createdBy: getCurrentUserId(),
                notes: notes
            })
        });
        
        if (response.ok) {
            showSuccessAlert('Onboarding workflow initialized successfully');
            bootstrap.Modal.getInstance(document.getElementById('workflowModal')).hide();
            refreshDashboard();
        } else {
            throw new Error('Failed to initialize onboarding workflow');
        }
    } catch (error) {
        console.error('Error initializing onboarding:', error);
        showErrorAlert('Failed to initialize onboarding workflow');
    }
}

/**
 * Initialize probation workflow
 */
async function initializeProbation() {
    const employeeId = document.getElementById('employeeSelect').value;
    const startDate = document.getElementById('startDate').value;
    const notes = document.getElementById('notes').value;
    
    if (!employeeId || !startDate) {
        showErrorAlert('Please fill in all required fields');
        return;
    }
    
    try {
        const response = await fetch('/api/lifecycle/probation/initialize', {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                employeeId: parseInt(employeeId),
                startDate: startDate,
                createdBy: getCurrentUserId(),
                notes: notes
            })
        });
        
        if (response.ok) {
            showSuccessAlert('Probation workflow initialized successfully');
            bootstrap.Modal.getInstance(document.getElementById('workflowModal')).hide();
            refreshDashboard();
        } else {
            throw new Error('Failed to initialize probation workflow');
        }
    } catch (error) {
        console.error('Error initializing probation:', error);
        showErrorAlert('Failed to initialize probation workflow');
    }
}

/**
 * Schedule performance review
 */
async function schedulePerformanceReview() {
    const employeeId = document.getElementById('employeeSelect').value;
    const reviewDate = document.getElementById('startDate').value; // Reusing start date field
    const notes = document.getElementById('notes').value;
    
    if (!employeeId || !reviewDate) {
        showErrorAlert('Please fill in all required fields');
        return;
    }
    
    try {
        const response = await fetch('/api/lifecycle/performance/schedule-review', {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                employeeId: parseInt(employeeId),
                reviewDate: reviewDate,
                createdBy: getCurrentUserId(),
                notes: notes
            })
        });
        
        if (response.ok) {
            showSuccessAlert('Performance review scheduled successfully');
            bootstrap.Modal.getInstance(document.getElementById('workflowModal')).hide();
            refreshDashboard();
        } else {
            throw new Error('Failed to schedule performance review');
        }
    } catch (error) {
        console.error('Error scheduling performance review:', error);
        showErrorAlert('Failed to schedule performance review');
    }
}

/**
 * Create annual review cycle
 */
async function createAnnualReviewCycle() {
    const currentDate = new Date();
    const reviewPeriodStart = new Date(currentDate.getFullYear(), 0, 1).toISOString().split('T')[0];
    
    if (!confirm('This will create performance review events for all active employees. Continue?')) {
        return;
    }
    
    try {
        const response = await fetch('/api/lifecycle/performance/annual-cycle', {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                reviewPeriodStart: reviewPeriodStart,
                createdBy: getCurrentUserId()
            })
        });
        
        if (response.ok) {
            const events = await response.json();
            showSuccessAlert(`Annual review cycle created with ${events.length} events`);
            refreshDashboard();
        } else {
            throw new Error('Failed to create annual review cycle');
        }
    } catch (error) {
        console.error('Error creating annual review cycle:', error);
        showErrorAlert('Failed to create annual review cycle');
    }
}

/**
 * Create contract renewal reminders
 */
async function createContractRenewalReminders() {
    if (!confirm('This will create reminder events for all contract employees with upcoming renewals. Continue?')) {
        return;
    }
    
    try {
        const response = await fetch('/api/lifecycle/contracts/renewal-reminders', {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                createdBy: getCurrentUserId()
            })
        });
        
        if (response.ok) {
            const events = await response.json();
            showSuccessAlert(`Contract renewal reminders created for ${events.length} employees`);
            refreshDashboard();
        } else {
            throw new Error('Failed to create contract renewal reminders');
        }
    } catch (error) {
        console.error('Error creating contract renewal reminders:', error);
        showErrorAlert('Failed to create contract renewal reminders');
    }
}

/**
 * Update event status
 */
async function updateEventStatus(eventId, newStatus, notes = '') {
    try {
        const response = await fetch(`/api/lifecycle/events/${eventId}/status`, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                status: newStatus,
                notes: notes,
                updatedBy: getCurrentUserId()
            })
        });
        
        if (response.ok) {
            showSuccessAlert('Event status updated successfully');
            refreshDashboard();
        } else {
            throw new Error('Failed to update event status');
        }
    } catch (error) {
        console.error('Error updating event status:', error);
        showErrorAlert('Failed to update event status');
    }
}

/**
 * View event details
 */
function viewEventDetails(eventId) {
    // Implementation for viewing event details modal
    console.log('View event details:', eventId);
    // This would open a modal with detailed event information
}

/**
 * Edit event
 */
function editEvent(eventId) {
    // Implementation for editing event modal
    console.log('Edit event:', eventId);
    // This would open a modal for editing event details
}

/**
 * Refresh dashboard
 */
function refreshDashboard() {
    loadStatistics();
    loadDashboardData();
    
    // Refresh current tab content
    const activeTab = document.querySelector('.nav-link.active');
    if (activeTab) {
        const targetTab = activeTab.getAttribute('data-bs-target');
        if (targetTab === '#events') {
            loadAllEvents();
        }
    }
}

/**
 * Load reports
 */
function loadReports() {
    // Implementation for loading reports and charts
    console.log('Loading reports...');
}

/**
 * Utility functions
 */
function showLoadingSpinner() {
    document.querySelectorAll('.loading-spinner').forEach(spinner => {
        spinner.style.display = 'block';
    });
}

function hideLoadingSpinner() {
    document.querySelectorAll('.loading-spinner').forEach(spinner => {
        spinner.style.display = 'none';
    });
}

function showTableLoading() {
    document.getElementById('eventsTableBody').innerHTML = `
        <tr>
            <td colspan="7" class="text-center py-4">
                <div class="loading-spinner">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading events...</span>
                    </div>
                </div>
            </td>
        </tr>
    `;
}

function showTableError() {
    document.getElementById('eventsTableBody').innerHTML = `
        <tr>
            <td colspan="7" class="text-center py-4">
                <div class="text-danger">
                    <i class="bi bi-exclamation-triangle"></i>
                    <h5>Error loading events</h5>
                    <p>Please try again later</p>
                </div>
            </td>
        </tr>
    `;
}

function formatDate(dateString) {
    if (!dateString) return 'No date';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getAuthHeaders() {
    // Return headers with authentication token
    return {
        'Authorization': 'Bearer ' + (localStorage.getItem('authToken') || ''),
        'Content-Type': 'application/json'
    };
}

function getCurrentUserId() {
    // Return current user ID (would come from authentication context)
    return parseInt(localStorage.getItem('currentUserId') || '1');
}

function showSuccessAlert(message) {
    showAlert(message, 'success');
}

function showErrorAlert(message) {
    showAlert(message, 'danger');
}

function showAlert(message, type) {
    const alertHTML = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 9999;" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', alertHTML);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        const alert = document.querySelector('.alert');
        if (alert) {
            bootstrap.Alert.getOrCreateInstance(alert).close();
        }
    }, 5000);
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}