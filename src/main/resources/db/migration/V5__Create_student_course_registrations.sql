-- Create student course registrations table
CREATE TABLE student_course_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    student_number VARCHAR(32) NOT NULL,
    course_code VARCHAR(16) NOT NULL,
    course_title VARCHAR(255) NOT NULL,
    registered_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    status ENUM('ACTIVE', 'DROPPED') NOT NULL DEFAULT 'ACTIVE',
    UNIQUE KEY unique_student_course (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_student_number (student_number),
    INDEX idx_course_code (course_code),
    INDEX idx_status (status)
);
