-- Sample data for UCMS development and testing

-- Insert admin user
INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN');

-- Insert sample students
INSERT INTO users (username, email, password_hash, role) VALUES
('alice.smith', 'alice.smith@student.university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'STUDENT'),
('bob.johnson', 'bob.johnson@student.university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'STUDENT');

-- Insert student details
INSERT INTO students (id, index_no, first_name, last_name, program, year) VALUES
((SELECT id FROM users WHERE username = 'alice.smith'), 'CS2021001', 'Alice', 'Smith', 'Computer Science', 3),
((SELECT id FROM users WHERE username = 'bob.johnson'), 'CS2021002', 'Bob', 'Johnson', 'Computer Science', 2);

-- Insert sample lecturer
INSERT INTO users (username, email, password_hash, role) VALUES
('dr.wilson', 'dr.wilson@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'LECTURER');

INSERT INTO lecturers (id, staff_no, department) VALUES
((SELECT id FROM users WHERE username = 'dr.wilson'), 'LEC001', 'Computer Science');

-- Insert sample courses
INSERT INTO courses (code, title, description, credits) VALUES
('CS101', 'Introduction to Computer Science', 'Fundamental concepts of computer science including programming basics, algorithms, and problem solving.', 3),
('CS201', 'Data Structures and Algorithms', 'Advanced data structures and algorithm design and analysis.', 4),
('CS301', 'Database Systems', 'Introduction to database design, SQL, and database management systems.', 3);

-- Insert sample semester
INSERT INTO semesters (name, start_date, end_date, add_drop_start, add_drop_end, is_current) VALUES
('Fall 2024', '2024-09-01', '2024-12-15', '2024-08-25', '2024-09-10', true);

-- Insert sample course offerings
INSERT INTO offerings (course_id, semester_id, lecturer_id, section, capacity, room, schedule) VALUES
((SELECT id FROM courses WHERE code = 'CS101'), (SELECT id FROM semesters WHERE name = 'Fall 2024'), (SELECT id FROM lecturers WHERE staff_no = 'LEC001'), 'A', 30, 'Room 101', 'Mon/Wed/Fri 10:00-11:00 AM'),
((SELECT id FROM courses WHERE code = 'CS201'), (SELECT id FROM semesters WHERE name = 'Fall 2024'), (SELECT id FROM lecturers WHERE staff_no = 'LEC001'), 'A', 25, 'Room 102', 'Tue/Thu 2:00-3:30 PM');

-- Note: Password for all sample accounts is "password" 
-- The hash above corresponds to BCrypt encoded "password"
