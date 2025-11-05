package com.blackdot.ems.shared.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_answers")
public class UserAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_result_id")
    private AssessmentResult assessmentResult;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;
    
    @Column(name = "text_answer", columnDefinition = "TEXT")
    private String textAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    @CreationTimestamp
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    // Constructors
    public UserAnswer() {}
    
    public UserAnswer(AssessmentResult assessmentResult, Question question) {
        this.assessmentResult = assessmentResult;
        this.question = question;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public AssessmentResult getAssessmentResult() { return assessmentResult; }
    public void setAssessmentResult(AssessmentResult assessmentResult) { this.assessmentResult = assessmentResult; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public QuestionOption getSelectedOption() { return selectedOption; }
    public void setSelectedOption(QuestionOption selectedOption) { this.selectedOption = selectedOption; }
    
    public String getTextAnswer() { return textAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}