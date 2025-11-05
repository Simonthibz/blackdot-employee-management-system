package com.blackdot.ems.module.employee.repository;

import com.blackdot.ems.shared.entity.Department;
import com.blackdot.ems.shared.entity.User;
import com.blackdot.ems.shared.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmployeeId(String employeeId);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByEmployeeId(String employeeId);
    
    List<User> findByIsActiveTrue();
    
    Long countByIsActiveTrue();
    
    Long countByDepartmentEntity(Department department);
    
    List<User> findByDepartmentEntity(Department department);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("roleName") ERole roleName);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchActiveUsers(@Param("search") String search);
}