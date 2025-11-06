package com.blackdot.ems.module.task.service;

import com.blackdot.ems.module.department.repository.DepartmentRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.module.task.dto.CreateTaskRequest;
import com.blackdot.ems.module.task.dto.TaskResponse;
import com.blackdot.ems.module.task.dto.UpdateTaskRequest;
import com.blackdot.ems.module.task.repository.TaskRepository;
import com.blackdot.ems.shared.entity.Department;
import com.blackdot.ems.shared.entity.Task;
import com.blackdot.ems.shared.entity.TaskStatus;
import com.blackdot.ems.shared.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User assignedBy) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setNotes(request.getNotes());
        task.setAssignedBy(assignedBy);
        
        // Assign to user if provided
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        }
        
        // Assign department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            task.setDepartment(department);
        }
        
        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }
    
    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getNotes() != null) {
            task.setNotes(request.getNotes());
        }
        
        // Update assigned user if provided
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
    
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        return convertToResponse(task);
    }
    
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByAssignedTo(Long userId) {
        return taskRepository.findTasksByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByAssignedBy(Long userId) {
        return taskRepository.findTasksCreatedByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksDueSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return taskRepository.findTasksDueSoon(now, futureDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCompletedAt(task.getCompletedAt());
        response.setNotes(task.getNotes());
        
        // Set assigned to user info
        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToName(task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName());
            response.setAssignedToEmail(task.getAssignedTo().getEmail());
            
            // Set assigned user's department
            if (task.getAssignedTo().getDepartmentEntity() != null) {
                response.setAssignedToDepartment(task.getAssignedTo().getDepartmentEntity().getName());
            } else if (task.getAssignedTo().getDepartment() != null) {
                response.setAssignedToDepartment(task.getAssignedTo().getDepartment());
            }
        }
        
        // Set assigned by user info
        if (task.getAssignedBy() != null) {
            response.setAssignedById(task.getAssignedBy().getId());
            response.setAssignedByName(task.getAssignedBy().getFirstName() + " " + task.getAssignedBy().getLastName());
            response.setAssignedByEmail(task.getAssignedBy().getEmail());
        }
        
        // Set department info
        if (task.getDepartment() != null) {
            response.setDepartmentId(task.getDepartment().getId());
            response.setDepartmentName(task.getDepartment().getName());
        }
        
        // Calculate overdue and days until due
        if (task.getDueDate() != null && task.getStatus() != TaskStatus.COMPLETED && task.getStatus() != TaskStatus.CANCELLED) {
            LocalDateTime now = LocalDateTime.now();
            response.setOverdue(task.getDueDate().isBefore(now));
            response.setDaysUntilDue(ChronoUnit.DAYS.between(now, task.getDueDate()));
        }
        
        return response;
    }
}
