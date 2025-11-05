package com.blackdot.ems.module.assessment.scheduler;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.assessment.repository.AssessmentRepository;
import com.blackdot.ems.module.assessment.repository.AssessmentResultRepository;
import com.blackdot.ems.module.assessment.repository.QuestionRepository;
import com.blackdot.ems.module.employee.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class QuarterlyAssessmentScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(QuarterlyAssessmentScheduler.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private AssessmentResultRepository assessmentResultRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    // Run every day at 9:00 AM to check for quarterly assessments
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleQuarterlyAssessments() {
        logger.info("Starting quarterly assessment schedule check...");
        
        try {
            Quarter currentQuarter = getCurrentQuarter();
            Integer currentYear = LocalDateTime.now().getYear();
            
            // Get all active assessments
            List<Assessment> activeAssessments = assessmentRepository.findByIsActiveTrueOrderByCreatedAtDesc();
            
            // Get all DATA_CAPTURER users
            List<User> dataCapturers = userRepository.findActiveUsersByRole(ERole.ROLE_DATA_CAPTURER);
            
            logger.info("Found {} active assessments and {} data capturers", 
                       activeAssessments.size(), dataCapturers.size());
            
            int assignedCount = 0;
            
            for (Assessment assessment : activeAssessments) {
                for (User dataCapturer : dataCapturers) {
                    // Check if user already has this assessment for current quarter
                    Optional<AssessmentResult> existingResult = assessmentResultRepository
                            .findByUserIdAndAssessmentIdAndQuarterAndYear(
                                    dataCapturer.getId(), 
                                    assessment.getId(), 
                                    currentQuarter, 
                                    currentYear);
                    
                    if (existingResult.isEmpty()) {
                        // Create new assessment assignment
                        AssessmentResult newAssignment = new AssessmentResult();
                        newAssignment.setUser(dataCapturer);
                        newAssignment.setAssessment(assessment);
                        newAssignment.setQuarter(currentQuarter);
                        newAssignment.setYear(currentYear);
                        newAssignment.setTotalQuestions(getQuestionCount(assessment.getId()));
                        
                        assessmentResultRepository.save(newAssignment);
                        assignedCount++;
                        
                        logger.debug("Assigned assessment '{}' to user '{}' for {} {}", 
                                   assessment.getTitle(), 
                                   dataCapturer.getUsername(),
                                   currentQuarter,
                                   currentYear);
                    }
                }
            }
            
            logger.info("Quarterly assessment schedule completed. Assigned {} new assessments", assignedCount);
            
        } catch (Exception e) {
            logger.error("Error occurred during quarterly assessment scheduling", e);
        }
    }
    
    // Run on the first day of each quarter at 8:00 AM
    @Scheduled(cron = "0 0 8 1 1,4,7,10 ?")
    public void notifyQuarterlyAssessmentStart() {
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        logger.info("Starting new quarter: {} {}", currentQuarter, currentYear);
        
        // You can add email notification logic here
        // For now, just log the notification
        List<User> dataCapturers = userRepository.findActiveUsersByRole(ERole.ROLE_DATA_CAPTURER);
        logger.info("New quarterly assessments are now available for {} data capturers", dataCapturers.size());
    }
    
    // Run weekly on Monday at 10:00 AM to send reminders
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendAssessmentReminders() {
        logger.info("Sending weekly assessment reminders...");
        
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        // Find incomplete assessments for current quarter
        List<AssessmentResult> incompleteAssessments = assessmentResultRepository
                .findByQuarterAndYearAndCompletedAtIsNull(currentQuarter, currentYear);
        
        logger.info("Found {} incomplete assessments for {} {}", 
                   incompleteAssessments.size(), currentQuarter, currentYear);
        
        // Group by user and send reminders
        incompleteAssessments.stream()
                .collect(java.util.stream.Collectors.groupingBy(AssessmentResult::getUser))
                .forEach((user, results) -> {
                    logger.info("User '{}' has {} incomplete assessments", 
                               user.getUsername(), results.size());
                    // Here you would send email/notification to the user
                });
    }
    
    // Run on last day of quarter at 11:00 PM to finalize
    @Scheduled(cron = "0 0 23 L 3,6,9,12 ?")
    public void finalizeQuarterlyAssessments() {
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        logger.info("Finalizing quarterly assessments for {} {}", currentQuarter, currentYear);
        
        // Find all incomplete assessments for current quarter
        List<AssessmentResult> incompleteAssessments = assessmentResultRepository
                .findByQuarterAndYearAndCompletedAtIsNull(currentQuarter, currentYear);
        
        logger.warn("Found {} incomplete assessments at quarter end", incompleteAssessments.size());
        
        // Mark incomplete assessments as failed
        for (AssessmentResult result : incompleteAssessments) {
            result.setCompletedAt(LocalDateTime.now());
            result.setScore(0);
            result.setPassed(false);
            result.setTimeTakenMinutes(0);
            result.setCorrectAnswers(0);
            assessmentResultRepository.save(result);
            
            logger.warn("Marked assessment as failed for user '{}' - assessment '{}'", 
                       result.getUser().getUsername(), 
                       result.getAssessment().getTitle());
        }
        
        logger.info("Quarter finalization completed");
    }
    
    private Quarter getCurrentQuarter() {
        int month = LocalDateTime.now().getMonthValue();
        if (month <= 3) return Quarter.Q1;
        else if (month <= 6) return Quarter.Q2;
        else if (month <= 9) return Quarter.Q3;
        else return Quarter.Q4;
    }
    
    private Integer getQuestionCount(Long assessmentId) {
        return questionRepository.countByAssessmentId(assessmentId).intValue();
    }
}