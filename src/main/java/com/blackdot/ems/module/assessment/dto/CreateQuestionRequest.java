package com.blackdot.ems.module.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateQuestionRequest {
    
    @NotBlank
    private String questionText;
    
    @NotNull
    private String questionType; // MULTIPLE_CHOICE, TRUE_FALSE, TEXT_INPUT, ESSAY
    
    private Integer points = 1;
    
    private List<QuestionOptionRequest> options;
    
    // Constructors
    public CreateQuestionRequest() {}
    
    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
    public List<QuestionOptionRequest> getOptions() { return options; }
    public void setOptions(List<QuestionOptionRequest> options) { this.options = options; }
    
    public static class QuestionOptionRequest {
        @NotBlank
        private String optionText;
        private Boolean isCorrect = false;
        
        public QuestionOptionRequest() {}
        
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        
        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    }
}