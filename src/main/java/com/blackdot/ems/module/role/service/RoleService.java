package com.blackdot.ems.module.role.service;

import com.blackdot.ems.module.authentication.repository.RoleRepository;
import com.blackdot.ems.module.role.dto.CreateRoleRequest;
import com.blackdot.ems.module.role.dto.RoleResponse;
import com.blackdot.ems.module.role.dto.UpdateRoleRequest;
import com.blackdot.ems.shared.entity.Role;
import com.blackdot.ems.shared.entity.User;
import com.blackdot.ems.module.employee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public RoleResponse createRole(CreateRoleRequest request) {
        // Check if role with this display name already exists
        if (roleRepository.findByDisplayNameIgnoreCase(request.getDisplayName()).isPresent()) {
            throw new RuntimeException("Role with display name '" + request.getDisplayName() + "' already exists");
        }
        
        Role role = new Role();
        role.setName(request.getName()); // Can be null for custom roles
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());
        role.setPermissions(request.getPermissions());
        role.setPriority(request.getPriority());
        role.setIsSystemRole(false); // New roles are custom roles
        role.setIsActive(true);
        
        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }
    
    public RoleResponse updateRole(Integer id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        // Prevent modification of system roles' core properties
        if (role.getIsSystemRole()) {
            // Only allow updating description and permissions for system roles
            if (request.getDescription() != null) {
                role.setDescription(request.getDescription());
            }
            if (request.getPermissions() != null) {
                role.setPermissions(request.getPermissions());
            }
            if (request.getPriority() != null) {
                role.setPriority(request.getPriority());
            }
        } else {
            // Custom roles can be fully updated
            if (request.getDisplayName() != null) {
                role.setDisplayName(request.getDisplayName());
            }
            if (request.getDescription() != null) {
                role.setDescription(request.getDescription());
            }
            if (request.getPermissions() != null) {
                role.setPermissions(request.getPermissions());
            }
            if (request.getPriority() != null) {
                role.setPriority(request.getPriority());
            }
            if (request.getIsActive() != null) {
                role.setIsActive(request.getIsActive());
            }
        }
        
        Role updatedRole = roleRepository.save(role);
        return convertToResponse(updatedRole);
    }
    
    public void deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        // Prevent deletion of system roles
        if (role.getIsSystemRole()) {
            throw new RuntimeException("Cannot delete system role");
        }
        
        // Check if role has users
        long userCount = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .count();
        
        if (userCount > 0) {
            throw new RuntimeException("Cannot delete role with " + userCount + " users. Please reassign users first.");
        }
        
        roleRepository.delete(role);
    }
    
    public RoleResponse getRoleById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return convertToResponse(role);
    }
    
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RoleResponse> getActiveRoles() {
        return roleRepository.findByIsActiveOrderByPriorityDesc(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RoleResponse> getSystemRoles() {
        return roleRepository.findByIsSystemRole(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RoleResponse> getCustomRoles() {
        return roleRepository.findByIsSystemRole(false).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<String> getAvailablePermissions() {
        return Arrays.asList(
            // Employee Permissions
            "READ_EMPLOYEE",
            "CREATE_EMPLOYEE",
            "UPDATE_EMPLOYEE",
            "DELETE_EMPLOYEE",
            "MANAGE_EMPLOYEE_ROLES",
            "MANAGE_EMPLOYEE_STATUS",
            
            // Department Permissions
            "READ_DEPARTMENT",
            "CREATE_DEPARTMENT",
            "UPDATE_DEPARTMENT",
            "DELETE_DEPARTMENT",
            "MANAGE_DEPARTMENT_HEAD",
            
            // Task Permissions
            "READ_TASK",
            "CREATE_TASK",
            "UPDATE_TASK",
            "DELETE_TASK",
            "ASSIGN_TASK",
            "UPDATE_TASK_STATUS",
            
            // Role Permissions
            "READ_ROLE",
            "CREATE_ROLE",
            "UPDATE_ROLE",
            "DELETE_ROLE",
            "ASSIGN_ROLE",
            
            // Leave Permissions
            "READ_LEAVE",
            "CREATE_LEAVE",
            "UPDATE_LEAVE",
            "DELETE_LEAVE",
            "APPROVE_LEAVE",
            "REJECT_LEAVE",
            
            // Attendance Permissions
            "READ_ATTENDANCE",
            "CREATE_ATTENDANCE",
            "UPDATE_ATTENDANCE",
            "DELETE_ATTENDANCE",
            "MANAGE_ATTENDANCE",
            
            // Payroll Permissions
            "READ_PAYROLL",
            "CREATE_PAYROLL",
            "UPDATE_PAYROLL",
            "DELETE_PAYROLL",
            "PROCESS_PAYROLL",
            
            // Performance Permissions
            "READ_PERFORMANCE",
            "CREATE_PERFORMANCE",
            "UPDATE_PERFORMANCE",
            "DELETE_PERFORMANCE",
            "REVIEW_PERFORMANCE",
            
            // Report Permissions
            "READ_REPORT",
            "CREATE_REPORT",
            "EXPORT_REPORT",
            "VIEW_ANALYTICS",
            
            // System Permissions
            "SYSTEM_ADMIN",
            "MANAGE_SETTINGS",
            "VIEW_AUDIT_LOG",
            "MANAGE_USERS"
        );
    }
    
    private RoleResponse convertToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDisplayName(role.getDisplayName());
        response.setDescription(role.getDescription());
        response.setPermissions(role.getPermissions());
        response.setIsSystemRole(role.getIsSystemRole());
        response.setIsActive(role.getIsActive());
        response.setPriority(role.getPriority());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        
        // Split permissions into list for UI
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            response.setPermissionList(Arrays.asList(role.getPermissions().split(",")));
        }
        
        // Count users with this role
        int userCount = (int) userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .count();
        response.setUserCount(userCount);
        
        return response;
    }
}
