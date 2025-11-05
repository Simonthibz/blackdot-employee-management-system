package com.blackdot.ems.module.task.controller;

import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.task.dto.CreateTaskRequest;
import com.blackdot.ems.module.task.dto.TaskResponse;
import com.blackdot.ems.module.task.dto.UpdateTaskRequest;
import com.blackdot.ems.module.task.service.TaskService;
import com.blackdot.ems.shared.entity.TaskStatus;
import com.blackdot.ems.shared.entity.User;
import com.blackdot.ems.shared.util.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create task - Only ADMIN and SUPERVISOR can create/assign tasks
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        User assignedBy = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        TaskResponse task = taskService.createTask(request, assignedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
    
    // Update task - Only ADMIN and SUPERVISOR can update tasks
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(task);
    }
    
    // Delete task - Only ADMIN and SUPERVISOR can delete tasks
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    // Get task by ID - All authenticated users can view tasks
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    // Get all tasks - ADMIN and SUPERVISOR can view all tasks
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('HR')")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    // Get my tasks - Users can view tasks assigned to them
    @GetMapping("/my-tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<TaskResponse> tasks = taskService.getTasksByAssignedTo(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks created by me - ADMIN and SUPERVISOR can view tasks they created
    @GetMapping("/created-by-me")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<TaskResponse>> getTasksCreatedByMe(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<TaskResponse> tasks = taskService.getTasksByAssignedBy(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('HR')")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            List<TaskResponse> tasks = taskService.getTasksByStatus(taskStatus);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get overdue tasks
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('HR')")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }
    
    // Get tasks due soon (within next 7 days)
    @GetMapping("/due-soon")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or hasRole('HR')")
    public ResponseEntity<List<TaskResponse>> getTasksDueSoon(
            @RequestParam(defaultValue = "7") int days) {
        List<TaskResponse> tasks = taskService.getTasksDueSoon(days);
        return ResponseEntity.ok(tasks);
    }
    
    // Update task status - Assigned user can update their task status
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        
        TaskResponse task = taskService.getTaskById(id);
        
        // Check if user is assigned to this task or is ADMIN/SUPERVISOR
        boolean isAssignedUser = task.getAssignedToId() != null && 
                                 task.getAssignedToId().equals(currentUser.getId());
        boolean isAdminOrSupervisor = currentUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || 
                                      authority.getAuthority().equals("ROLE_SUPERVISOR"));
        
        if (!isAssignedUser && !isAdminOrSupervisor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setStatus(status);
        TaskResponse updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(updatedTask);
    }
}
