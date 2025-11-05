package com.blackdot.ems.module.department.repository;

import com.blackdot.ems.shared.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByName(String name);
    
    Optional<Department> findByCode(String code);
    
    List<Department> findByIsActive(Boolean isActive);
    
    List<Department> findByNameContainingIgnoreCase(String name);
    
    boolean existsByName(String name);
    
    boolean existsByCode(String code);
}
