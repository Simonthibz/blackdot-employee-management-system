package com.blackdot.ems.module.assessment.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class TakeAssessmentRequest {
    
    private Map<Long, AnswerRequest> answers; // questionId -> answer
    
    // Constructors
    public TakeAssessmentRequest() {}
    
    // Getters and Setters
    public Map<Long, AnswerRequest> getAnswers() { return answers; }
    public void setAnswers(Map<Long, AnswerRequest> answers) { this.answers = answers; }
    
    public static class AnswerRequest {
        private Long selectedOptionId; // For multiple choice
        private String textAnswer; // For text/essay questions
        
        public AnswerRequest() {}
        
        public Long getSelectedOptionId() { return selectedOptionId; }
        public void setSelectedOptionId(Long selectedOptionId) { this.selectedOptionId = selectedOptionId; }
        
        public String getTextAnswer() { return textAnswer; }
        public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    }
}