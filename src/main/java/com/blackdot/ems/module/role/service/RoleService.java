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
        role.setName(request.getName());
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
