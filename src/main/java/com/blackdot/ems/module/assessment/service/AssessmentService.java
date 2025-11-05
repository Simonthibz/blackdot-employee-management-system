package com.blackdot.ems.module.assessment.service;

import com.blackdot.ems.shared.entity.*;
import com.blackdot.ems.module.assessment.dto.*;
import com.blackdot.ems.module.assessment.repository.*;
import com.blackdot.ems.module.employee.repository.UserRepository;
import com.blackdot.ems.shared.exception.ResourceNotFoundException;
import com.blackdot.ems.shared.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssessmentService {
    
    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    
    @Autowired
    private AssessmentResultRepository assessmentResultRepository;
    
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<AssessmentResponse> getAllAssessments() {
        List<Assessment> assessments = assessmentRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        return assessments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public AssessmentResponse getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", id));
        return convertToResponse(assessment);
    }
    
    public AssessmentResponse createAssessment(CreateAssessmentRequest request) {
        Assessment assessment = new Assessment();
        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setPassingScore(request.getPassingScore());
        assessment.setTimeLimitMinutes(request.getTimeLimitMinutes());
        assessment.setIsActive(request.getIsActive());
        assessment.setQuarter(request.getQuarter());
        assessment.setYear(request.getYear());
        assessment.setMaxAttempts(request.getMaxAttempts());
        
        // Parse deadline from ISO string
        if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
            assessment.setDeadline(LocalDateTime.parse(request.getDeadline() + "T23:59:59"));
        }
        
        Assessment savedAssessment = assessmentRepository.save(assessment);
        return convertToResponse(savedAssessment);
    }
    
    public AssessmentResponse updateAssessment(Long id, CreateAssessmentRequest request) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", id));
        
        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setPassingScore(request.getPassingScore());
        assessment.setTimeLimitMinutes(request.getTimeLimitMinutes());
        assessment.setIsActive(request.getIsActive());
        assessment.setQuarter(request.getQuarter());
        assessment.setYear(request.getYear());
        assessment.setMaxAttempts(request.getMaxAttempts());
        
        // Parse deadline from ISO string
        if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
            assessment.setDeadline(LocalDateTime.parse(request.getDeadline() + "T23:59:59"));
        }
        
        Assessment updatedAssessment = assessmentRepository.save(assessment);
        return convertToResponse(updatedAssessment);
    }
    
    public void deleteAssessment(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", id));
        
        // Soft delete by setting isActive to false
        assessment.setIsActive(false);
        assessmentRepository.save(assessment);
    }
    
    public Question addQuestionToAssessment(Long assessmentId, CreateQuestionRequest request) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", assessmentId));
        
        // Validate question type
        QuestionType questionType;
        try {
            questionType = QuestionType.valueOf(request.getQuestionType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid question type: " + request.getQuestionType());
        }
        
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(questionType);
        question.setPoints(request.getPoints());
        question.setAssessment(assessment);
        
        Question savedQuestion = questionRepository.save(question);
        
        // Add options for multiple choice and true/false questions
        if ((questionType == QuestionType.MULTIPLE_CHOICE || questionType == QuestionType.TRUE_FALSE) 
            && request.getOptions() != null) {
            Set<QuestionOption> options = new HashSet<>();
            for (CreateQuestionRequest.QuestionOptionRequest optionReq : request.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setOptionText(optionReq.getOptionText());
                option.setIsCorrect(optionReq.getIsCorrect());
                option.setQuestion(savedQuestion);
                options.add(questionOptionRepository.save(option));
            }
            savedQuestion.setOptions(options);
        }
        
        return savedQuestion;
    }
    
    public List<Question> getAssessmentQuestions(Long assessmentId) {
        if (!assessmentRepository.existsById(assessmentId)) {
            throw new ResourceNotFoundException("Assessment", "id", assessmentId);
        }
        return questionRepository.findByAssessmentIdOrderByCreatedAtAsc(assessmentId);
    }
    
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        questionRepository.delete(question);
    }
    
    public AssessmentResultResponse startAssessment(Long assessmentId, Long userId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", assessmentId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!assessment.getIsActive()) {
            throw new BadRequestException("Assessment is not active");
        }
        
        // Check if user already has a result for current quarter
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        if (assessmentResultRepository.findByUserIdAndAssessmentIdAndQuarterAndYear(
                userId, assessmentId, currentQuarter, currentYear).isPresent()) {
            throw new BadRequestException("Assessment already completed for this quarter");
        }
        
        // Create new assessment result
        AssessmentResult result = new AssessmentResult();
        result.setUser(user);
        result.setAssessment(assessment);
        result.setQuarter(currentQuarter);
        result.setYear(currentYear);
        result.setStartedAt(LocalDateTime.now());
        result.setTotalQuestions(questionRepository.countByAssessmentId(assessmentId).intValue());
        
        AssessmentResult savedResult = assessmentResultRepository.save(result);
        return convertToResultResponse(savedResult);
    }
    
    public AssessmentResultResponse submitAssessment(Long assessmentId, Long userId, TakeAssessmentRequest request) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", assessmentId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Quarter currentQuarter = getCurrentQuarter();
        Integer currentYear = LocalDateTime.now().getYear();
        
        // Find the started assessment result
        AssessmentResult result = assessmentResultRepository
                .findByUserIdAndAssessmentIdAndQuarterAndYear(userId, assessmentId, currentQuarter, currentYear)
                .orElseThrow(() -> new BadRequestException("Assessment not started or already completed"));
        
        if (result.getCompletedAt() != null) {
            throw new BadRequestException("Assessment already completed");
        }
        
        // Calculate time taken
        long timeTaken = ChronoUnit.MINUTES.between(result.getStartedAt(), LocalDateTime.now());
        if (timeTaken > assessment.getTimeLimitMinutes()) {
            throw new BadRequestException("Assessment time limit exceeded");
        }
        
        // Process answers and calculate score
        List<Question> questions = questionRepository.findByAssessmentIdOrderByCreatedAtAsc(assessmentId);
        int correctAnswers = 0;
        int totalPoints = 0;
        int earnedPoints = 0;
        
        Set<UserAnswer> userAnswers = new HashSet<>();
        
        for (Question question : questions) {
            totalPoints += question.getPoints();
            
            TakeAssessmentRequest.AnswerRequest answerReq = request.getAnswers().get(question.getId());
            if (answerReq != null) {
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setAssessmentResult(result);
                userAnswer.setQuestion(question);
                
                boolean isCorrect = false;
                
                if ((question.getQuestionType() == QuestionType.MULTIPLE_CHOICE || 
                     question.getQuestionType() == QuestionType.TRUE_FALSE) && 
                     answerReq.getSelectedOptionId() != null) {
                    // Handle both MULTIPLE_CHOICE and TRUE_FALSE as option-based questions
                    QuestionOption selectedOption = questionOptionRepository.findById(answerReq.getSelectedOptionId())
                            .orElse(null);
                    if (selectedOption != null && selectedOption.getQuestion().getId().equals(question.getId())) {
                        userAnswer.setSelectedOption(selectedOption);
                        isCorrect = selectedOption.getIsCorrect();
                    }
                }
                
                userAnswer.setIsCorrect(isCorrect);
                userAnswers.add(userAnswerRepository.save(userAnswer));
                
                if (isCorrect) {
                    correctAnswers++;
                    earnedPoints += question.getPoints();
                }
            }
        }
        
        // Calculate final score as percentage
        int score = totalPoints > 0 ? (earnedPoints * 100) / totalPoints : 0;
        boolean passed = score >= assessment.getPassingScore();
        
        // Update assessment result
        result.setCompletedAt(LocalDateTime.now());
        result.setTimeTakenMinutes((int) timeTaken);
        result.setCorrectAnswers(correctAnswers);
        result.setScore(score);
        result.setPassed(passed);
        result.setUserAnswers(userAnswers);
        
        AssessmentResult savedResult = assessmentResultRepository.save(result);
        return convertToResultResponse(savedResult);
    }
    
    public List<AssessmentResultResponse> getUserAssessmentResults(Long userId) {
        List<AssessmentResult> results = assessmentResultRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return results.stream().map(this::convertToResultResponse).collect(Collectors.toList());
    }
    
    public List<AssessmentResultResponse> getAssessmentResults(Long assessmentId) {
        List<AssessmentResult> results = assessmentResultRepository.findByAssessmentIdOrderByCreatedAtDesc(assessmentId);
        return results.stream().map(this::convertToResultResponse).collect(Collectors.toList());
    }
    
    public List<AssessmentResultResponse> getQuarterlyResults(Quarter quarter, Integer year) {
        List<AssessmentResult> results = assessmentResultRepository.findByQuarterAndYear(quarter, year);
        return results.stream().map(this::convertToResultResponse).collect(Collectors.toList());
    }
    
    private Quarter getCurrentQuarter() {
        int month = LocalDateTime.now().getMonthValue();
        if (month <= 3) return Quarter.Q1;
        else if (month <= 6) return Quarter.Q2;
        else if (month <= 9) return Quarter.Q3;
        else return Quarter.Q4;
    }
    
    private AssessmentResponse convertToResponse(Assessment assessment) {
        Integer questionCount = questionRepository.countByAssessmentId(assessment.getId()).intValue();
        
        // Calculate total attempts and pass rate
        List<AssessmentResult> results = assessmentResultRepository.findByAssessmentIdOrderByCreatedAtDesc(assessment.getId());
        Integer totalAttempts = results.size();
        long passedCount = results.stream().filter(r -> r.getPassed() != null && r.getPassed()).count();
        Double passRate = totalAttempts > 0 ? (passedCount * 100.0) / totalAttempts : 0.0;
        
        // Count unique employees who took this assessment
        Long employeesTaken = assessmentResultRepository.countDistinctUsersByAssessmentId(assessment.getId());
        
        // Count pending and completed attempts
        Long pendingAttempts = assessmentResultRepository.countPendingByAssessmentId(assessment.getId());
        Long completedAttempts = assessmentResultRepository.countCompletedByAssessmentId(assessment.getId());
        
        AssessmentResponse response = new AssessmentResponse();
        response.setId(assessment.getId());
        response.setTitle(assessment.getTitle());
        response.setDescription(assessment.getDescription());
        response.setPassingScore(assessment.getPassingScore());
        response.setTimeLimitMinutes(assessment.getTimeLimitMinutes());
        response.setIsActive(assessment.getIsActive());
        response.setQuestionCount(questionCount);
        response.setQuarter(assessment.getQuarter());
        response.setYear(assessment.getYear());
        response.setMaxAttempts(assessment.getMaxAttempts());
        response.setDeadline(assessment.getDeadline());
        response.setTotalAttempts(totalAttempts);
        response.setPassRate(passRate);
        response.setEmployeesTaken(employeesTaken);
        response.setPendingAttempts(pendingAttempts);
        response.setCompletedAttempts(completedAttempts);
        response.setCreatedAt(assessment.getCreatedAt());
        response.setUpdatedAt(assessment.getUpdatedAt());
        
        return response;
    }
    
    private AssessmentResultResponse convertToResultResponse(AssessmentResult result) {
        AssessmentResultResponse response = new AssessmentResultResponse();
        response.setId(result.getId());
        response.setUserId(result.getUser().getId());
        response.setUserName(result.getUser().getFirstName() + " " + result.getUser().getLastName());
        response.setEmployeeId(result.getUser().getEmployeeId());
        response.setAssessmentId(result.getAssessment().getId());
        response.setAssessmentTitle(result.getAssessment().getTitle());
        response.setScore(result.getScore());
        response.setTotalQuestions(result.getTotalQuestions());
        response.setCorrectAnswers(result.getCorrectAnswers());
        response.setTimeTakenMinutes(result.getTimeTakenMinutes());
        response.setPassed(result.getPassed());
        response.setStartedAt(result.getStartedAt());
        response.setCompletedAt(result.getCompletedAt());
        response.setQuarter(result.getQuarter() != null ? result.getQuarter().name() : null);
        response.setYear(result.getYear());
        response.setCreatedAt(result.getCreatedAt());
        return response;
    }
    
    public Long getTotalEmployeeCount() {
        return userRepository.countByIsActiveTrue();
    }
}