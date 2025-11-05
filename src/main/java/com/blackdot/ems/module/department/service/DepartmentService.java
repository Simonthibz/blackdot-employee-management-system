package com.blackdot.ems.module.department.service;

import com.blackdot.ems.module.department.dto.CreateDepartmentRequest;
import com.blackdot.ems.module.department.dto.DepartmentResponse;
import com.blackdot.ems.module.department.dto.UpdateDepartmentRequest;
import com.blackdot.ems.module.department.repository.DepartmentRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.shared.entity.Department;
import com.blackdot.ems.shared.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        // Check if department name already exists
        if (departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department with name '" + request.getName() + "' already exists");
        }
        
        // Check if code already exists
        if (request.getCode() != null && departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department with code '" + request.getCode() + "' already exists");
        }
        
        Department department = new Department();
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
        department.setHeadOfDepartmentId(request.getHeadOfDepartmentId());
        department.setBudget(request.getBudget());
        department.setCostCenterCode(request.getCostCenterCode());
        department.setLocation(request.getLocation());
        department.setIsActive(true);
        
        Department savedDepartment = departmentRepository.save(department);
        return convertToResponse(savedDepartment);
    }
    
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Check for duplicate name if name is being changed
        if (request.getName() != null && !request.getName().equals(department.getName())) {
            if (departmentRepository.existsByName(request.getName())) {
                throw new RuntimeException("Department with name '" + request.getName() + "' already exists");
            }
            department.setName(request.getName());
        }
        
        // Check for duplicate code if code is being changed
        if (request.getCode() != null && !request.getCode().equals(department.getCode())) {
            if (departmentRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Department with code '" + request.getCode() + "' already exists");
            }
            department.setCode(request.getCode());
        }
        
        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }
        if (request.getHeadOfDepartmentId() != null) {
            department.setHeadOfDepartmentId(request.getHeadOfDepartmentId());
        }
        if (request.getBudget() != null) {
            department.setBudget(request.getBudget());
        }
        if (request.getCostCenterCode() != null) {
            department.setCostCenterCode(request.getCostCenterCode());
        }
        if (request.getLocation() != null) {
            department.setLocation(request.getLocation());
        }
        if (request.getIsActive() != null) {
            department.setIsActive(request.getIsActive());
        }
        
        Department updatedDepartment = departmentRepository.save(department);
        return convertToResponse(updatedDepartment);
    }
    
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Check if department has employees
        long employeeCount = userRepository.countByDepartmentEntity(department);
        if (employeeCount > 0) {
            throw new RuntimeException("Cannot delete department with " + employeeCount + " employees. Please reassign employees first.");
        }
        
        departmentRepository.delete(department);
    }
    
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return convertToResponse(department);
    }
    
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentResponse> getActiveDepartments() {
        return departmentRepository.findByIsActive(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentResponse> searchDepartments(String query) {
        return departmentRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private DepartmentResponse convertToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setCode(department.getCode());
        response.setDescription(department.getDescription());
        response.setHeadOfDepartmentId(department.getHeadOfDepartmentId());
        response.setBudget(department.getBudget());
        response.setCostCenterCode(department.getCostCenterCode());
        response.setLocation(department.getLocation());
        response.setIsActive(department.getIsActive());
        response.setCreatedAt(department.getCreatedAt());
        response.setUpdatedAt(department.getUpdatedAt());
        
        // Get head of department name
        if (department.getHeadOfDepartmentId() != null) {
            userRepository.findById(department.getHeadOfDepartmentId())
                    .ifPresent(user -> response.setHeadOfDepartmentName(user.getFullName()));
        }
        
        // Get employee count
        Long employeeCount = userRepository.countByDepartmentEntity(department);
        response.setEmployeeCount(employeeCount != null ? employeeCount.intValue() : 0);
        
        return response;
    }
}
