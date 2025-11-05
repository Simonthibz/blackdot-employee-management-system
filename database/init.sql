-- Database Initialization Script for Blackdot Employee Management System
-- ========================================================================

-- Create database if it doesn't exist
-- Note: This script runs in the context of the default database

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_ADMIN', 'System Administrator with full access'),
    ('ROLE_HR', 'Human Resources with employee and assessment management'),
    ('ROLE_DATA_CAPTURER', 'Data entry personnel for assessments'),
    ('ROLE_EMPLOYEE', 'Regular employee with basic access')
ON CONFLICT (name) DO NOTHING;

-- Create users table (will be managed by Hibernate)
-- This is handled by JPA/Hibernate DDL generation

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_employee_id ON users(employee_id);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_assessment_results_user ON assessment_results(user_id);
CREATE INDEX IF NOT EXISTS idx_assessment_results_quarter ON assessment_results(quarter, year);

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Create default admin user (password will be: admin123)
-- The password hash is for 'admin123' using BCrypt
INSERT INTO users (username, email, password, first_name, last_name, employee_id, department, position, hire_date, is_active, created_at, updated_at)
VALUES (
    'admin',
    'admin@blackdot.com',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPHx4B2X6',
    'System',
    'Administrator',
    'EMP001',
    'IT',
    'System Administrator',
    CURRENT_DATE,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'admin@blackdot.com' 
AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Create sample HR user (password: hr123)
INSERT INTO users (username, email, password, first_name, last_name, employee_id, department, position, hire_date, is_active, created_at, updated_at)
VALUES (
    'hr_manager',
    'hr@blackdot.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'HR',
    'Manager',
    'EMP002',
    'Human Resources',
    'HR Manager',
    CURRENT_DATE,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Assign HR role to HR user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'hr@blackdot.com' 
AND r.name = 'ROLE_HR'
ON CONFLICT DO NOTHING;

-- Create sample data capturer (password: data123)
INSERT INTO users (username, email, password, first_name, last_name, employee_id, department, position, hire_date, is_active, created_at, updated_at)
VALUES (
    'data_capturer',
    'datacapturer@blackdot.com',
    '$2a$10$VEjxzHkHsCy8.LXr7NJh8.Xh8H8k8HjX8HjX8HjX8HjX8HjX8HjX8H',
    'Data',
    'Capturer',
    'EMP003',
    'Administration',
    'Data Capturer',
    CURRENT_DATE,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Assign data capturer role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'datacapturer@blackdot.com' 
AND r.name = 'ROLE_DATA_CAPTURER'
ON CONFLICT DO NOTHING;

-- Create sample employees
INSERT INTO users (username, email, password, first_name, last_name, employee_id, department, position, hire_date, is_active, created_at, updated_at)
VALUES 
    ('john_doe', 'john.doe@blackdot.com', '$2a$10$VEjxzHkHsCy8.LXr7NJh8.Xh8H8k8HjX8HjX8HjX8HjX8HjX8HjX8H', 'John', 'Doe', 'EMP004', 'Sales', 'Sales Representative', CURRENT_DATE, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('jane_smith', 'jane.smith@blackdot.com', '$2a$10$VEjxzHkHsCy8.LXr7NJh8.Xh8H8k8HjX8HjX8HjX8HjX8HjX8HjX8H', 'Jane', 'Smith', 'EMP005', 'Marketing', 'Marketing Coordinator', CURRENT_DATE, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('mike_wilson', 'mike.wilson@blackdot.com', '$2a$10$VEjxzHkHsCy8.LXr7NJh8.Xh8H8k8HjX8HjX8HjX8HjX8HjX8HjX8H', 'Mike', 'Wilson', 'EMP006', 'IT', 'Software Developer', CURRENT_DATE, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sarah_brown', 'sarah.brown@blackdot.com', '$2a$10$VEjxzHkHsCy8.LXr7NJh8.Xh8H8k8HjX8HjX8HjX8HjX8HjX8HjX8H', 'Sarah', 'Brown', 'EMP007', 'Finance', 'Financial Analyst', CURRENT_DATE, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Assign employee role to all sample employees
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email IN ('john.doe@blackdot.com', 'jane.smith@blackdot.com', 'mike.wilson@blackdot.com', 'sarah.brown@blackdot.com')
AND r.name = 'ROLE_EMPLOYEE'
ON CONFLICT DO NOTHING;

-- Display summary
SELECT 'Database initialization completed successfully' as status;
SELECT 'Default admin user: admin@blackdot.com / admin123' as admin_credentials;
SELECT 'Default HR user: hr@blackdot.com / hr123' as hr_credentials;