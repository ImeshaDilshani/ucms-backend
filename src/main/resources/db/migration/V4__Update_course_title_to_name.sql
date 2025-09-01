-- Migration to rename 'title' column to 'name' in courses table
-- and add new columns for enhanced course management

-- Add new columns first
ALTER TABLE courses 
ADD COLUMN name VARCHAR(255),
ADD COLUMN department VARCHAR(100),
ADD COLUMN max_enrollments INT DEFAULT 0,
ADD COLUMN current_enrollments INT DEFAULT 0,
ADD COLUMN is_active BOOLEAN DEFAULT true;

-- Copy data from title to name
UPDATE courses SET name = title WHERE name IS NULL;

-- Make name column NOT NULL after copying data
ALTER TABLE courses MODIFY COLUMN name VARCHAR(255) NOT NULL;

-- Make other new columns NOT NULL with appropriate defaults
ALTER TABLE courses MODIFY COLUMN department VARCHAR(100) NOT NULL DEFAULT 'General';
ALTER TABLE courses MODIFY COLUMN max_enrollments INT NOT NULL DEFAULT 50;
ALTER TABLE courses MODIFY COLUMN current_enrollments INT NOT NULL DEFAULT 0;
ALTER TABLE courses MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT true;

-- Drop the old title column (optional - comment out if you want to keep it temporarily)
-- ALTER TABLE courses DROP COLUMN title;
