package com.blackdot.ems.module.assessment.controller;

import com.blackdot.ems.module.assessment.dto.*;
import com.blackdot.ems.module.assessment.service.AssessmentService;
import com.blackdot.ems.shared.entity.Question;
import com.blackdot.ems.shared.entity.Quarter;
import com.blackdot.ems.shared.util.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssessmentController {
    
    @Autowired
    private AssessmentService assessmentService;
    
    // Get all assessments (HR and ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<List<AssessmentResponse>> getAllAssessments() {
        List<AssessmentResponse> assessments = assessmentService.getAllAssessments();
        return ResponseEntity.ok(assessments);
    }
    
    // Get assessment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('DATA_CAPTURER')")
    public ResponseEntity<AssessmentResponse> getAssessmentById(@PathVariable Long id) {
        AssessmentResponse assessment = assessmentService.getAssessmentById(id);
        return ResponseEntity.ok(assessment);
    }
    
    // Create new assessment (HR and ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<AssessmentResponse> createAssessment(@Valid @RequestBody CreateAssessmentRequest request) {
        AssessmentResponse assessment = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assessment);
    }
    
    // Update assessment (HR and ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<AssessmentResponse> updateAssessment(@PathVariable Long id, 
                                                               @Valid @RequestBody CreateAssessmentRequest request) {
        AssessmentResponse assessment = assessmentService.updateAssessment(id, request);
        return ResponseEntity.ok(assessment);
    }
    
    // Delete assessment (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }
    
    // Add question to assessment (HR and ADMIN only)
    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Question> addQuestionToAssessment(@PathVariable Long id, 
                                                           @Valid @RequestBody CreateQuestionRequest request) {
        Question question = assessmentService.addQuestionToAssessment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }
    
    // Get assessment questions (for taking assessment)
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<Question>> getAssessmentQuestions(@PathVariable Long id) {
        List<Question> questions = assessmentService.getAssessmentQuestions(id);
        return ResponseEntity.ok(questions);
    }
    
    // Delete question (HR and ADMIN only)
    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        assessmentService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
    
    // Start assessment (All authenticated users)
    @PostMapping("/{id}/start")
    public ResponseEntity<AssessmentResultResponse> startAssessment(@PathVariable Long id, 
                                                                   @AuthenticationPrincipal UserDetailsImpl userPrincipal) {
        AssessmentResultResponse result = assessmentService.startAssessment(id, userPrincipal.getId());
        return ResponseEntity.ok(result);
    }
    
    // Submit assessment (All authenticated users)
    @PostMapping("/{id}/submit")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(@PathVariable Long id, 
                                                                    @AuthenticationPrincipal UserDetailsImpl userPrincipal,
                                                                    @Valid @RequestBody TakeAssessmentRequest request) {
        AssessmentResultResponse result = assessmentService.submitAssessment(id, userPrincipal.getId(), request);
        return ResponseEntity.ok(result);
    }
    
    // Get user's assessment results
    @GetMapping("/my-results")
    public ResponseEntity<List<AssessmentResultResponse>> getMyAssessmentResults(@AuthenticationPrincipal UserDetailsImpl userPrincipal) {
        List<AssessmentResultResponse> results = assessmentService.getUserAssessmentResults(userPrincipal.getId());
        return ResponseEntity.ok(results);
    }
    
    // Get all results for an assessment (HR and ADMIN only)
    @GetMapping("/{id}/results")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<List<AssessmentResultResponse>> getAssessmentResults(@PathVariable Long id) {
        List<AssessmentResultResponse> results = assessmentService.getAssessmentResults(id);
        return ResponseEntity.ok(results);
    }
    
    // Get quarterly results (HR and ADMIN only)
    @GetMapping("/results/quarterly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<List<AssessmentResultResponse>> getQuarterlyResults(
            @RequestParam Quarter quarter,
            @RequestParam Integer year) {
        List<AssessmentResultResponse> results = assessmentService.getQuarterlyResults(quarter, year);
        return ResponseEntity.ok(results);
    }
    
    // Get specific user's assessment results (HR and ADMIN only)
    @GetMapping("/users/{userId}/results")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<List<AssessmentResultResponse>> getUserAssessmentResults(@PathVariable Long userId) {
        List<AssessmentResultResponse> results = assessmentService.getUserAssessmentResults(userId);
        return ResponseEntity.ok(results);
    }
    
    // Get total employee count (HR and ADMIN only)
    @GetMapping("/stats/employee-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<Long> getTotalEmployeeCount() {
        Long count = assessmentService.getTotalEmployeeCount();
        return ResponseEntity.ok(count);
    }
}