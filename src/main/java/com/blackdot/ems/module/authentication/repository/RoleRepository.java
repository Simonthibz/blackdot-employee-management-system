package com.blackdot.ems.module.authentication.repository;

import com.blackdot.ems.shared.entity.Role;
import com.blackdot.ems.shared.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
    
    List<Role> findByIsActive(Boolean isActive);
    
    List<Role> findByIsSystemRole(Boolean isSystemRole);
    
    List<Role> findByIsActiveOrderByPriorityDesc(Boolean isActive);
    
    Optional<Role> findByDisplayNameIgnoreCase(String displayName);
}