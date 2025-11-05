// Global variables
let assessmentId = null;
let assessment = null;
let questions = [];
let currentQuestionIndex = 0;
let userAnswers = {};
let startTime = null;
let timerInterval = null;
let assessmentResultId = null;

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Get assessment ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    assessmentId = urlParams.get('id');
    
    if (!assessmentId) {
        alert('No assessment specified');
        window.location.href = '/dashboard/assessments';
        return;
    }
    
    loadAssessment();
});

// Load assessment and start
async function loadAssessment() {
    showLoading(true);
    
    try {
        // Get assessment details
        const assessmentResponse = await fetch(`/api/assessments/${assessmentId}`, {
            credentials: 'include'
        });
        
        if (!assessmentResponse.ok) {
            throw new Error('Failed to load assessment');
        }
        
        assessment = await assessmentResponse.json();
        
        // Get questions
        const questionsResponse = await fetch(`/api/assessments/${assessmentId}/questions`, {
            credentials: 'include'
        });
        
        if (!questionsResponse.ok) {
            throw new Error('Failed to load questions');
        }
        
        questions = await questionsResponse.json();
        
        if (questions.length === 0) {
            alert('This assessment has no questions yet.');
            window.location.href = '/dashboard/assessments';
            return;
        }
        
        // Start the assessment
        const startResponse = await fetch(`/api/assessments/${assessmentId}/start`, {
            method: 'POST',
            credentials: 'include'
        });
        
        if (!startResponse.ok) {
            const error = await startResponse.json();
            alert(error.message || 'Failed to start assessment');
            window.location.href = '/dashboard/assessments';
            return;
        }
        
        const result = await startResponse.json();
        assessmentResultId = result.id;
        
        // Initialize the UI
        initializeAssessment();
        showLoading(false);
        
    } catch (error) {
        console.error('Error loading assessment:', error);
        alert('Error loading assessment. Please try again.');
        window.location.href = '/dashboard/assessments';
    }
}

// Initialize assessment UI
function initializeAssessment() {
    // Set header info
    document.getElementById('assessmentTitle').textContent = assessment.title;
    document.getElementById('questionCount').textContent = questions.length;
    document.getElementById('passingScore').textContent = assessment.passingScore;
    document.getElementById('totalQuestions').textContent = questions.length;
    document.getElementById('totalQuestionsModal').textContent = questions.length;
    
    // Start timer
    startTime = new Date();
    startTimer();
    
    // Initialize question indicators
    renderQuestionIndicators();
    
    // Show first question
    showQuestion(0);
}

// Start countdown timer
function startTimer() {
    const timeLimit = assessment.timeLimitMinutes * 60; // Convert to seconds
    let elapsedSeconds = 0;
    
    timerInterval = setInterval(() => {
        elapsedSeconds++;
        const remainingSeconds = timeLimit - elapsedSeconds;
        
        if (remainingSeconds <= 0) {
            clearInterval(timerInterval);
            alert('Time is up! Your assessment will be submitted automatically.');
            submitAssessment();
            return;
        }
        
        const minutes = Math.floor(remainingSeconds / 60);
        const seconds = remainingSeconds % 60;
        document.getElementById('timer').textContent = 
            `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
        
        // Warning when 5 minutes left
        if (remainingSeconds === 300) {
            alert('5 minutes remaining!');
        }
    }, 1000);
}

// Render question indicators
function renderQuestionIndicators() {
    const container = document.getElementById('questionIndicators');
    container.innerHTML = '';
    
    questions.forEach((question, index) => {
        const indicator = document.createElement('div');
        indicator.className = 'question-indicator';
        indicator.textContent = index + 1;
        indicator.onclick = () => showQuestion(index);
        
        if (userAnswers[question.id]) {
            indicator.classList.add('answered');
        }
        if (index === currentQuestionIndex) {
            indicator.classList.add('current');
        }
        
        container.appendChild(indicator);
    });
}

// Show specific question
function showQuestion(index) {
    currentQuestionIndex = index;
    const question = questions[index];
    
    // Update question display
    document.getElementById('questionNumber').textContent = index + 1;
    document.getElementById('currentQuestion').textContent = index + 1;
    document.getElementById('questionText').textContent = question.questionText;
    document.getElementById('questionPoints').textContent = question.points;
    
    // Update progress bar
    const progress = ((index + 1) / questions.length) * 100;
    document.getElementById('progressBar').style.width = `${progress}%`;
    
    // Render options based on question type
    renderQuestionOptions(question);
    
    // Update navigation buttons
    document.getElementById('prevBtn').disabled = index === 0;
    
    if (index === questions.length - 1) {
        document.getElementById('nextBtn').style.display = 'none';
        document.getElementById('submitBtn').style.display = 'inline-flex';
    } else {
        document.getElementById('nextBtn').style.display = 'inline-flex';
        document.getElementById('submitBtn').style.display = 'none';
    }
    
    // Update indicators
    renderQuestionIndicators();
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Render question options based on type
function renderQuestionOptions(question) {
    const container = document.getElementById('optionsContainer');
    container.innerHTML = '';
    
    console.log('Rendering question:', question.questionType, 'Options:', question.options);
    
    if (question.questionType === 'MULTIPLE_CHOICE' || question.questionType === 'TRUE_FALSE') {
        // Check if options exist
        if (!question.options || question.options.length === 0) {
            container.innerHTML = '<p style="color: red; padding: 1rem;">Error: This question has no options. Please contact the administrator.</p>';
            return;
        }
        
        // Render options for both multiple choice and true/false
        question.options.forEach(option => {
            const optionDiv = document.createElement('div');
            optionDiv.className = 'option-item';
            
            if (userAnswers[question.id]?.selectedOptionId === option.id) {
                optionDiv.classList.add('selected');
            }
            
            optionDiv.onclick = () => selectOption(question.id, option.id);
            
            optionDiv.innerHTML = `
                <div class="option-radio"></div>
                <div class="option-text">${option.optionText}</div>
            `;
            
            container.appendChild(optionDiv);
        });
    } else {
        // Fallback for any other question types (should not happen)
        const textarea = document.createElement('textarea');
        textarea.className = 'text-answer-input';
        textarea.placeholder = 'Type your answer here...';
        textarea.value = userAnswers[question.id]?.textAnswer || '';
        textarea.oninput = (e) => selectTextAnswer(question.id, e.target.value);
        
        container.appendChild(textarea);
    }
}

// Select multiple choice option
function selectOption(questionId, optionId) {
    userAnswers[questionId] = {
        selectedOptionId: optionId,
        textAnswer: null
    };
    
    // Re-render current question to update UI
    renderQuestionOptions(questions[currentQuestionIndex]);
    renderQuestionIndicators();
}

// Select text answer
function selectTextAnswer(questionId, text) {
    userAnswers[questionId] = {
        selectedOptionId: null,
        textAnswer: text
    };
    
    renderQuestionIndicators();
}

// Navigation functions
function previousQuestion() {
    if (currentQuestionIndex > 0) {
        showQuestion(currentQuestionIndex - 1);
    }
}

function nextQuestion() {
    if (currentQuestionIndex < questions.length - 1) {
        showQuestion(currentQuestionIndex + 1);
    }
}

// Show submit confirmation modal
function showSubmitConfirmation() {
    const answeredCount = Object.keys(userAnswers).length;
    const unansweredCount = questions.length - answeredCount;
    
    document.getElementById('answeredCount').textContent = answeredCount;
    document.getElementById('unansweredCount').textContent = unansweredCount;
    
    if (unansweredCount > 0) {
        document.getElementById('unansweredWarning').style.display = 'flex';
    } else {
        document.getElementById('unansweredWarning').style.display = 'none';
    }
    
    document.getElementById('submitModal').classList.add('active');
}

function closeSubmitModal() {
    document.getElementById('submitModal').classList.remove('active');
}

// Submit assessment
async function submitAssessment() {
    closeSubmitModal();
    showLoading(true);
    
    // Stop timer
    if (timerInterval) {
        clearInterval(timerInterval);
    }
    
    try {
        const response = await fetch(`/api/assessments/${assessmentId}/submit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                answers: userAnswers
            })
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to submit assessment');
        }
        
        const result = await response.json();
        showResults(result);
        
    } catch (error) {
        console.error('Error submitting assessment:', error);
        alert('Error submitting assessment: ' + error.message);
        showLoading(false);
    }
}

// Show results
function showResults(result) {
    showLoading(false);
    
    // Update result display
    document.getElementById('finalScore').textContent = result.score || 0;
    document.getElementById('correctAnswers').textContent = result.correctAnswers || 0;
    document.getElementById('totalQuestionsResult').textContent = result.totalQuestions || 0;
    document.getElementById('timeTaken').textContent = result.timeTakenMinutes || 0;
    document.getElementById('passingScoreResult').textContent = assessment.passingScore;
    
    // Update pass/fail status
    const passStatus = document.getElementById('passStatus');
    const scoreCircle = document.getElementById('scoreCircle');
    
    if (result.passed) {
        passStatus.innerHTML = '<i class="fas fa-check-circle"></i><span>Passed</span>';
        passStatus.className = 'pass-status passed';
        scoreCircle.style.background = 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)';
    } else {
        passStatus.innerHTML = '<i class="fas fa-times-circle"></i><span>Failed</span>';
        passStatus.className = 'pass-status failed';
        scoreCircle.style.background = 'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)';
    }
    
    // Show results modal
    document.getElementById('resultsModal').classList.add('active');
}

// Return to assessments page
function returnToAssessments() {
    window.location.href = '/dashboard/assessments';
}

// Show/hide loading overlay
function showLoading(show) {
    const overlay = document.getElementById('loadingOverlay');
    if (show) {
        overlay.classList.add('active');
    } else {
        overlay.classList.remove('active');
    }
}

// Prevent accidental page closure
window.addEventListener('beforeunload', function(e) {
    if (assessmentResultId && !document.getElementById('resultsModal').classList.contains('active')) {
        e.preventDefault();
        e.returnValue = 'You have an assessment in progress. Are you sure you want to leave?';
        return e.returnValue;
    }
});
