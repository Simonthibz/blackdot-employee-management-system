package com.blackdot.ems.module.role.controller;

import com.blackdot.ems.module.role.dto.CreateRoleRequest;
import com.blackdot.ems.module.role.dto.RoleResponse;
import com.blackdot.ems.module.role.dto.UpdateRoleRequest;
import com.blackdot.ems.module.role.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    // Create role - Only ADMIN can create
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        try {
            RoleResponse response = roleService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    // Update role - Only ADMIN can update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRoleRequest request) {
        try {
            RoleResponse response = roleService.updateRole(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    // Delete role - Only ADMIN can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Get role by ID - All authenticated users can view
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Integer id) {
        try {
            RoleResponse response = roleService.getRoleById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    // Get all roles - All authenticated users can view
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    
    // Get active roles - All authenticated users can view
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleResponse>> getActiveRoles() {
        List<RoleResponse> roles = roleService.getActiveRoles();
        return ResponseEntity.ok(roles);
    }
    
    // Get system roles - All authenticated users can view
    @GetMapping("/system")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleResponse>> getSystemRoles() {
        List<RoleResponse> roles = roleService.getSystemRoles();
        return ResponseEntity.ok(roles);
    }
    
    // Get custom roles - All authenticated users can view
    @GetMapping("/custom")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoleResponse>> getCustomRoles() {
        List<RoleResponse> roles = roleService.getCustomRoles();
        return ResponseEntity.ok(roles);
    }
}
