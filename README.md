# Blackdot Employee Management System

A robust Spring Boot application for managing employees with quarterly assessment functionality for data capturers.

## 🏗️ Module-Based Architecture

The application follows a modular architecture with clear separation of concerns:

```
src/main/java/com/blackdot/ems/
├── EmployeeManagementSystemApplication.java
├── shared/                           # Shared components across modules
│   ├── entity/                      # JPA entities (User, Role, Assessment, etc.)
│   ├── config/                      # Configuration classes
│   ├── dto/                         # Shared DTOs
│   ├── exception/                   # Custom exceptions
│   └── util/                        # Utility classes
└── module/                          # Business modules
    ├── authentication/              # Authentication & Authorization
    │   ├── controller/              # Auth controllers (login, logout)
    │   ├── service/                 # Auth services (JWT, UserDetails)
    │   ├── repository/              # Auth repositories (RoleRepository)
    │   └── dto/                     # Auth DTOs (LoginRequest, JwtResponse)
    ├── employee/                    # Employee Management
    │   ├── controller/              # Employee CRUD controllers
    │   ├── service/                 # Employee business logic
    │   ├── repository/              # Employee repositories (UserRepository)
    │   └── dto/                     # Employee DTOs
    ├── assessment/                  # Assessment System
    │   ├── controller/              # Assessment controllers
    │   ├── service/                 # Assessment logic & quarterly scheduling
    │   ├── repository/              # Assessment repositories
    │   └── dto/                     # Assessment DTOs
    ├── reporting/                   # Reports & Analytics
    │   ├── controller/              # Report controllers
    │   ├── service/                 # Report generation services
    │   └── dto/                     # Report DTOs
    └── dashboard/                   # Dashboard & UI
        ├── controller/              # Dashboard controllers
        └── service/                 # Dashboard services
```

## 🗄️ Database Schema

The system uses PostgreSQL with Hibernate for automatic table creation:

### Core Tables:

- **users** - Employee information and authentication
- **roles** - Role-based access control
- **user_roles** - Many-to-many user-role mapping
- **assessments** - Assessment definitions
- **questions** - Assessment questions
- **question_options** - Multiple choice options
- **assessment_results** - Completed assessment results
- **user_answers** - Individual user responses
- **quarterly_schedule** - Quarterly assessment scheduling

## 🎯 Key Features

### 1. Role-Based Access Control

- **ADMIN** - Full system access
- **HR** - Employee management and reporting
- **DATA_CAPTURER** - Must take quarterly assessments
- **SUPERVISOR** - Team oversight
- **EMPLOYEE** - Basic access

### 2. Quarterly Assessment System

- Automatic scheduling for data capturers
- Email reminders and notifications
- Time-limited assessments
- Detailed result tracking
- Pass/fail determination

### 3. Employee Management

- Complete CRUD operations
- Employee search and filtering
- Role assignment
- Activity tracking

### 4. Reporting & Analytics

- Quarterly performance reports
- Assessment statistics
- Employee performance tracking
- Export capabilities

## 🚀 Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Database Setup

```sql
CREATE DATABASE blackdot_ems;
CREATE USER ems_user WITH PASSWORD 'ems_password';
GRANT ALL PRIVILEGES ON DATABASE blackdot_ems TO ems_user;
```

### Run Application

```bash
mvn spring-boot:run
```

### Default Users

- **Admin**: username: `admin`, password: `admin123`
- **HR Manager**: username: `hr_manager`, password: `hr123`
- **Data Capturer**: username: `data_capturer1`, password: `dc123`

## � Authentication & Security

### JWT Authentication System

- **JWT Token-based authentication** for stateless security
- **Role-based access control** with method-level security
- **Password encryption** using BCrypt
- **Automatic token validation** on all protected endpoints

### API Endpoints

- `POST /api/auth/signin` - User login
- `POST /api/auth/signout` - User logout
- `GET /api/auth/user` - Get current user info
- `GET /api/test/*` - Test endpoints for role validation

### Web Interface

- **Login Page**: `/login` - User-friendly login interface
- **Dashboard**: `/dashboard` - Role-based dashboard with navigation
- **Home Page**: `/` - Application landing page

### Security Configuration

- **CORS enabled** for cross-origin requests
- **CSRF disabled** for API usage
- **Stateless sessions** with JWT
- **Public endpoints** for authentication and static resources
- **Protected endpoints** requiring valid JWT tokens

## �📋 Configuration

Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/blackdot_ems
spring.jpa.hibernate.ddl-auto=create-drop

# Security
app.jwt.secret=blackdotEmployeeManagementSystemSecretKey2025
app.jwt.expiration=86400000

# Assessment Settings
app.assessment.quarterly-reminder-days=7
app.assessment.grace-period-days=5
```

## 🏢 Internal Server Deployment

Designed for internal company server hosting with:

- PostgreSQL database integration
- JWT authentication
- Email notifications
- File upload support
- Comprehensive logging

## 📦 Dependencies

- **Spring Boot 3.2.0** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database
- **JWT** - Token-based authentication
- **Thymeleaf** - Template engine
- **JasperReports** - Report generation
- **Spring Mail** - Email notifications

## 🧪 Testing

Run tests with:

```bash
mvn test
```

## 📈 Monitoring

Application includes:

- Detailed logging configuration
- Performance monitoring
- Error tracking
- Assessment compliance monitoring
