-- Initial database schema for UCMS
-- This migration creates all necessary tables for the University Course Management System

-- Users table (parent table for inheritance)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'LECTURER', 'STUDENT') NOT NULL
);

-- Students table (inherits from users)
CREATE TABLE students (
    id BIGINT PRIMARY KEY,
    index_no VARCHAR(32) UNIQUE,
    first_name VARCHAR(64),
    last_name VARCHAR(64),
    program VARCHAR(64),
    year INT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- Lecturers table (inherits from users)
CREATE TABLE lecturers (
    id BIGINT PRIMARY KEY,
    staff_no VARCHAR(32) UNIQUE,
    department VARCHAR(64),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- Courses table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    code VARCHAR(16) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    credits INT NOT NULL
);

-- Course prerequisites (many-to-many)
CREATE TABLE course_prerequisites (
    course_id BIGINT NOT NULL,
    prerequisite_id BIGINT NOT NULL,
    PRIMARY KEY (course_id, prerequisite_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Semesters table
CREATE TABLE semesters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    name VARCHAR(32) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    add_drop_start DATE NOT NULL,
    add_drop_end DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE
);

-- Course offerings table
CREATE TABLE offerings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    lecturer_id BIGINT,
    section VARCHAR(8) NOT NULL,
    capacity INT NOT NULL,
    room VARCHAR(32),
    schedule TEXT,
    UNIQUE KEY unique_offering (course_id, semester_id, section),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE SET NULL
);

-- Student enrollments
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    student_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    dropped_at TIMESTAMP(6) NULL,
    status ENUM('ENROLLED', 'DROPPED', 'WAITLISTED') NOT NULL DEFAULT 'ENROLLED',
    UNIQUE KEY unique_enrollment (student_id, offering_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
);

-- Assessments table
CREATE TABLE assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    offering_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    max_score DECIMAL(8,2) NOT NULL,
    weight_percent DECIMAL(5,2) NOT NULL,
    due_date DATE,
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
);

-- Grades table
CREATE TABLE grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    student_id BIGINT NOT NULL,
    assessment_id BIGINT NOT NULL,
    score DECIMAL(8,2),
    graded_at TIMESTAMP(6),
    is_locked BOOLEAN DEFAULT FALSE,
    UNIQUE KEY unique_grade (student_id, assessment_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

-- Refresh tokens for JWT
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP(6) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_students_index_no ON students(index_no);
CREATE INDEX idx_lecturers_staff_no ON lecturers(staff_no);
CREATE INDEX idx_courses_code ON courses(code);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_offering ON enrollments(offering_id);
CREATE INDEX idx_grades_student ON grades(student_id);
CREATE INDEX idx_grades_assessment ON grades(assessment_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at);
