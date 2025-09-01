-- Add missing updated_at column to assessments table
ALTER TABLE assessments ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
