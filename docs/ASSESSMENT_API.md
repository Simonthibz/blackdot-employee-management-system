# Assessment System API Documentation

## Overview

The Assessment System provides quarterly testing functionality for data capturers with automatic scheduling, grading, and reporting features.

## Endpoints

### 1. Assessment Management (HR/ADMIN only)

#### Get All Assessments

```
GET /api/assessments
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR
```

#### Get Assessment by ID

```
GET /api/assessments/{id}
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR, DATA_CAPTURER
```

#### Create Assessment

```
POST /api/assessments
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR

Request Body:
{
    "title": "Q1 2024 Data Entry Assessment",
    "description": "Quarterly assessment for data entry skills",
    "passingScore": 70,
    "timeLimitMinutes": 60,
    "isActive": true
}
```

#### Update Assessment

```
PUT /api/assessments/{id}
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR

Request Body: Same as Create Assessment
```

#### Delete Assessment

```
DELETE /api/assessments/{id}
Authorization: Bearer <jwt_token>
Roles: ADMIN
```

### 2. Question Management (HR/ADMIN only)

#### Add Question to Assessment

```
POST /api/assessments/{id}/questions
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR

Request Body:
{
    "questionText": "What is the correct format for entering dates?",
    "questionType": "MULTIPLE_CHOICE",
    "points": 10,
    "options": [
        {
            "optionText": "DD/MM/YYYY",
            "isCorrect": true
        },
        {
            "optionText": "MM/DD/YYYY",
            "isCorrect": false
        },
        {
            "optionText": "YYYY-MM-DD",
            "isCorrect": false
        }
    ]
}
```

#### Get Assessment Questions

```
GET /api/assessments/{id}/questions
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR, DATA_CAPTURER
```

### 3. Taking Assessments (DATA_CAPTURER only)

#### Start Assessment

```
POST /api/assessments/{id}/start
Authorization: Bearer <jwt_token>
Roles: DATA_CAPTURER

Response:
{
    "id": 1,
    "userId": 3,
    "userName": "John Doe",
    "employeeId": "EMP001",
    "assessmentId": 1,
    "assessmentTitle": "Q1 2024 Data Entry Assessment",
    "totalQuestions": 10,
    "startedAt": "2024-01-15T09:00:00",
    "quarter": "Q1",
    "year": 2024
}
```

#### Submit Assessment

```
POST /api/assessments/{id}/submit
Authorization: Bearer <jwt_token>
Roles: DATA_CAPTURER

Request Body:
{
    "answers": {
        "1": {
            "selectedOptionId": 3
        },
        "2": {
            "textAnswer": "Data validation ensures accuracy"
        }
    }
}

Response:
{
    "id": 1,
    "userId": 3,
    "userName": "John Doe",
    "employeeId": "EMP001",
    "assessmentId": 1,
    "assessmentTitle": "Q1 2024 Data Entry Assessment",
    "score": 85,
    "totalQuestions": 10,
    "correctAnswers": 8,
    "timeTakenMinutes": 45,
    "passed": true,
    "startedAt": "2024-01-15T09:00:00",
    "completedAt": "2024-01-15T09:45:00",
    "quarter": "Q1",
    "year": 2024
}
```

### 4. Results and Reporting

#### Get My Assessment Results (DATA_CAPTURER)

```
GET /api/assessments/my-results
Authorization: Bearer <jwt_token>
Roles: DATA_CAPTURER
```

#### Get Assessment Results (HR/ADMIN)

```
GET /api/assessments/{id}/results
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR
```

#### Get User's Assessment Results (HR/ADMIN)

```
GET /api/assessments/users/{userId}/results
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR
```

#### Get Quarterly Results (HR/ADMIN)

```
GET /api/assessments/results/quarterly?quarter=Q1&year=2024
Authorization: Bearer <jwt_token>
Roles: ADMIN, HR
```

## Automated Scheduling

The system automatically schedules assessments for data capturers with the following features:

### Daily Check (9:00 AM)

- Assigns new assessments to data capturers who haven't completed them for the current quarter
- Creates assessment records for tracking

### Quarter Start (First day at 8:00 AM)

- Logs the start of a new quarter
- Prepares for new assessment assignments

### Weekly Reminders (Monday 10:00 AM)

- Identifies incomplete assessments
- Logs users with pending assessments
- Can be extended to send email notifications

### Quarter End Finalization (Last day at 11:00 PM)

- Marks incomplete assessments as failed
- Sets score to 0 for unfinished assessments
- Ensures quarterly compliance tracking

## Question Types

### MULTIPLE_CHOICE

- Single correct answer from multiple options
- Automatically graded
- Requires `options` array in question creation

### TRUE_FALSE

- Boolean answer type
- Can be automatically graded if correct answer is stored
- Uses `textAnswer` field

### SHORT_ANSWER

- Brief text response
- Requires manual grading
- Uses `textAnswer` field

### ESSAY

- Long-form text response
- Requires manual grading
- Uses `textAnswer` field

## Grading System

### Automatic Grading

- Multiple choice questions are automatically graded
- Score calculated as percentage: (earned points / total points) \* 100
- Pass/fail determined by comparing score to assessment's passing score

### Manual Grading

- Text-based questions require manual review
- Set `isCorrect` field in user answers after review
- Recalculate scores after manual grading

## Security Features

- Role-based access control
- JWT token authentication
- Assessment time limits enforced
- Quarterly restriction prevents multiple attempts
- Audit trail with creation and completion timestamps

## Error Handling

- Assessment not found (404)
- Unauthorized access (403)
- Time limit exceeded (400)
- Already completed assessment (400)
- Invalid question types (400)

## Example Workflow

1. **HR creates assessment** with questions
2. **System automatically assigns** to all data capturers at quarter start
3. **Data capturer starts assessment** (creates result record)
4. **Data capturer submits answers** (calculates score and pass/fail)
5. **System sends reminders** for incomplete assessments
6. **System finalizes** incomplete assessments at quarter end
7. **HR reviews results** and generates reports
