package com.blackdot.ems.module.task.repository;

import com.blackdot.ems.shared.entity.Task;
import com.blackdot.ems.shared.entity.TaskStatus;
import com.blackdot.ems.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks assigned to a specific user
    List<Task> findByAssignedTo(User user);
    
    // Find tasks assigned by a specific user
    List<Task> findByAssignedBy(User user);
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find tasks assigned to a user with specific status
    List<Task> findByAssignedToAndStatus(User user, TaskStatus status);
    
    // Find tasks assigned by a user with specific status
    List<Task> findByAssignedByAndStatus(User user, TaskStatus status);
    
    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // Find tasks due soon
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :futureDate AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findTasksDueSoon(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);
    
    // Find all tasks for a user (assigned to them)
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId ORDER BY t.dueDate ASC")
    List<Task> findTasksByUserId(@Param("userId") Long userId);
    
    // Find all tasks created by a user
    @Query("SELECT t FROM Task t WHERE t.assignedBy.id = :userId ORDER BY t.createdAt DESC")
    List<Task> findTasksCreatedByUserId(@Param("userId") Long userId);
    
    // Find tasks by due date range
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findTasksByDueDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find my tasks by due date range
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    List<Task> findMyTasksByDueDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
