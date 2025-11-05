package com.blackdot.ems.shared.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "question_options")
public class QuestionOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank
    private String optionText;
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
    
    // Constructors
    public QuestionOption() {}
    
    public QuestionOption(String optionText, Boolean isCorrect, Question question) {
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.question = question;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}