# Employee Management API Documentation

## Authentication

All endpoints (except authentication) require a valid JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Authentication Endpoints

### Login

```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Logout

```http
POST /api/auth/signout
Authorization: Bearer <token>
```

### Get Current User

```http
GET /api/auth/user
Authorization: Bearer <token>
```

## Employee Management Endpoints

### Get All Employees

```http
GET /api/employees
Authorization: Bearer <token>
```

**Required Roles:** HR, ADMIN, SUPERVISOR

### Get Active Employees Only

```http
GET /api/employees/active
Authorization: Bearer <token>
```

### Get Employees with Pagination

```http
GET /api/employees/paginated?page=0&size=10&sortBy=id&sortDir=asc
Authorization: Bearer <token>
```

### Search Employees

```http
GET /api/employees/search?q=john
Authorization: Bearer <token>
```

### Get Employees by Role

```http
GET /api/employees/role/DATA_CAPTURER
Authorization: Bearer <token>
```

### Get Employee by ID

```http
GET /api/employees/123
Authorization: Bearer <token>
```

### Get Employee by Employee ID

```http
GET /api/employees/employee-id/EMP001
Authorization: Bearer <token>
```

### Create New Employee

```http
POST /api/employees
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "new_employee",
  "email": "employee@blackdot.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "employeeId": "EMP004",
  "hireDate": "2025-11-05",
  "roles": ["DATA_CAPTURER"]
}
```

**Required Roles:** HR, ADMIN

### Update Employee

```http
PUT /api/employees/123
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "updated@blackdot.com",
  "firstName": "John",
  "lastName": "Smith",
  "employeeId": "EMP004",
  "hireDate": "2025-11-05",
  "isActive": true,
  "roles": ["DATA_CAPTURER", "SUPERVISOR"]
}
```

**Required Roles:** HR, ADMIN

### Deactivate Employee (Soft Delete)

```http
DELETE /api/employees/123
Authorization: Bearer <token>
```

**Required Roles:** HR, ADMIN

### Activate Employee

```http
PUT /api/employees/123/activate
Authorization: Bearer <token>
```

**Required Roles:** HR, ADMIN

### Change Employee Password

```http
PUT /api/employees/123/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

**Required Roles:** HR, ADMIN

### Get Employee Statistics

```http
GET /api/employees/statistics
Authorization: Bearer <token>
```

**Required Roles:** HR, ADMIN, SUPERVISOR

**Response:**

```json
{
  "totalEmployees": 10,
  "activeEmployees": 8,
  "admins": 1,
  "hrStaff": 1,
  "dataCapturers": 3,
  "supervisors": 2,
  "employees": 1
}
```

## Test Endpoints

### Test Role Access

```http
GET /api/test/admin          # ADMIN only
GET /api/test/hr             # HR, ADMIN
GET /api/test/supervisor     # SUPERVISOR, HR, ADMIN
GET /api/test/datacapturer   # DATA_CAPTURER only
GET /api/test/user           # Any authenticated user
GET /api/test/employee-api   # HR, ADMIN, SUPERVISOR
GET /api/test/all            # Public access
```

## Available Roles

- `ROLE_ADMIN` - Full system access
- `ROLE_HR` - Employee and assessment management
- `ROLE_DATA_CAPTURER` - Must take quarterly assessments
- `ROLE_SUPERVISOR` - Team oversight
- `ROLE_EMPLOYEE` - Basic access

## Error Responses

### 400 Bad Request

```json
{
  "status": 400,
  "message": "Username is already taken!",
  "timestamp": "2025-11-05T10:30:00"
}
```

### 401 Unauthorized

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required to access this resource",
  "path": "/api/employees",
  "timestamp": 1699181400000
}
```

### 403 Forbidden

```json
{
  "status": 403,
  "message": "Access denied: Access is denied",
  "timestamp": "2025-11-05T10:30:00"
}
```

### 404 Not Found

```json
{
  "status": 404,
  "message": "Employee not found with id : '123'",
  "timestamp": "2025-11-05T10:30:00"
}
```

### 422 Validation Error

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "username": "size must be between 3 and 50",
    "email": "must be a well-formed email address"
  },
  "timestamp": "2025-11-05T10:30:00"
}
```
