-- UCMS Seed Data

-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password_hash, role) VALUES 
('admin', 'admin@university.edu', '$2a$10$nAB5j9G1c3JHgg7w.XMxWOQe3QZF6JvS9wLjT8WQmOC8Rr5ybqBSi', 'ADMIN');

-- Insert sample lecturer user (password: lecturer123)
INSERT INTO users (username, email, password_hash, role) VALUES 
('john.doe', 'john.doe@university.edu', '$2a$10$H4QdH7RvE3JmLK8c.QmLhO7VbPtGc9NwWlHaE4F5X6I8JbPtS2K9e', 'LECTURER');

-- Insert lecturer details
INSERT INTO lecturers (id, staff_no, department) VALUES 
((SELECT id FROM users WHERE username = 'john.doe'), 'STAFF001', 'Computer Science');

-- Insert sample student users (password: student123)
INSERT INTO users (username, email, password_hash, role) VALUES 
('alice.smith', 'alice.smith@student.university.edu', '$2a$10$V8PtNb4wHx9K3B8QmF7X.e1VbWtGc9NwWlHaE4F5X6I8JbPtS2K9e', 'STUDENT'),
('bob.johnson', 'bob.johnson@student.university.edu', '$2a$10$V8PtNb4wHx9K3B8QmF7X.e1VbWtGc9NwWlHaE4F5X6I8JbPtS2K9e', 'STUDENT');

-- Insert student details
INSERT INTO students (id, index_no, first_name, last_name, program, "year") VALUES 
((SELECT id FROM users WHERE username = 'alice.smith'), 'CS2021001', 'Alice', 'Smith', 'Computer Science', 3),
((SELECT id FROM users WHERE username = 'bob.johnson'), 'CS2021002', 'Bob', 'Johnson', 'Computer Science', 2);

-- Insert sample courses
INSERT INTO courses (code, title, credits, description) VALUES 
('CS101', 'Introduction to Programming', 3, 'Fundamentals of programming using Java'),
('CS201', 'Data Structures', 3, 'Linear and non-linear data structures'),
('CS202', 'Algorithms', 3, 'Algorithm design and analysis'),
('CS301', 'Database Systems', 3, 'Relational database design and SQL'),
('CS401', 'Software Engineering', 4, 'Software development lifecycle and methodologies'),
('MATH101', 'Calculus I', 4, 'Differential and integral calculus'),
('MATH201', 'Linear Algebra', 3, 'Vectors, matrices, and linear transformations');

-- Insert course prerequisites
INSERT INTO course_prerequisites (course_id, prerequisite_id) VALUES 
((SELECT id FROM courses WHERE code = 'CS201'), (SELECT id FROM courses WHERE code = 'CS101')),
((SELECT id FROM courses WHERE code = 'CS202'), (SELECT id FROM courses WHERE code = 'CS201')),
((SELECT id FROM courses WHERE code = 'CS301'), (SELECT id FROM courses WHERE code = 'CS201')),
((SELECT id FROM courses WHERE code = 'CS401'), (SELECT id FROM courses WHERE code = 'CS301'));

-- Insert current semester
INSERT INTO semesters (name, start_date, end_date, add_drop_start, add_drop_end, is_current) VALUES 
('Fall 2025', '2025-09-01', '2025-12-20', '2025-08-25', '2025-09-10', TRUE);

-- Insert sample course offerings for current semester
INSERT INTO offerings (course_id, semester_id, section, lecturer_id, capacity, room, schedule) VALUES 
((SELECT id FROM courses WHERE code = 'CS101'), (SELECT id FROM semesters WHERE name = 'Fall 2025'), 'A', (SELECT id FROM lecturers WHERE staff_no = 'STAFF001'), 30, 'Room 101', '[{"day": "Monday", "time": "10:00-12:00"}, {"day": "Wednesday", "time": "10:00-12:00"}]'),
((SELECT id FROM courses WHERE code = 'CS201'), (SELECT id FROM semesters WHERE name = 'Fall 2025'), 'A', (SELECT id FROM lecturers WHERE staff_no = 'STAFF001'), 25, 'Room 102', '[{"day": "Tuesday", "time": "14:00-16:00"}, {"day": "Thursday", "time": "14:00-16:00"}]'),
((SELECT id FROM courses WHERE code = 'MATH101'), (SELECT id FROM semesters WHERE name = 'Fall 2025'), 'A', (SELECT id FROM lecturers WHERE staff_no = 'STAFF001'), 35, 'Room 201', '[{"day": "Monday", "time": "08:00-10:00"}, {"day": "Wednesday", "time": "08:00-10:00"}, {"day": "Friday", "time": "08:00-10:00"}]');

-- Insert sample enrollments
INSERT INTO enrollments (offering_id, student_id, status) VALUES 
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'CS101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), (SELECT id FROM students WHERE index_no = 'CS2021002'), 'ENROLLED'),
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'MATH101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), (SELECT id FROM students WHERE index_no = 'CS2021001'), 'ENROLLED'),
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'MATH101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), (SELECT id FROM students WHERE index_no = 'CS2021002'), 'ENROLLED');

-- Insert sample assessments
INSERT INTO assessments (offering_id, name, weight_percent, max_score, due_date) VALUES 
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'CS101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), 'Midterm Exam', 40.00, 100.00, '2025-10-15'),
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'CS101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), 'Final Exam', 50.00, 100.00, '2025-12-10'),
((SELECT id FROM offerings WHERE course_id = (SELECT id FROM courses WHERE code = 'CS101') AND semester_id = (SELECT id FROM semesters WHERE name = 'Fall 2025')), 'Assignments', 10.00, 100.00, '2025-11-30');
