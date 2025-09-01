-- MySQL Database Setup for UCMS
-- Run these commands in MySQL Command Line or MySQL Workbench

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS ucmsdb 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 2. Create a dedicated user for the application (recommended for security)
CREATE USER IF NOT EXISTS 'ucms_user'@'localhost' IDENTIFIED BY 'ucms_password';

-- 3. Grant privileges to the user
GRANT ALL PRIVILEGES ON ucmsdb.* TO 'ucms_user'@'localhost';

-- 4. Refresh privileges
FLUSH PRIVILEGES;

-- 5. Verify the database was created
SHOW DATABASES;

-- 6. Use the database
USE ucmsdb;

-- 7. Show tables (should be empty initially)
SHOW TABLES;
