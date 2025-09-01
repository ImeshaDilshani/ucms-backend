-- MySQL Database Setup for UCMS
-- Run this script in MySQL Workbench or command line

-- Create database
CREATE DATABASE IF NOT EXISTS ucmsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional - you can use root)
-- CREATE USER IF NOT EXISTS 'ucms_user'@'localhost' IDENTIFIED BY 'ucms_password';
-- GRANT ALL PRIVILEGES ON ucmsdb.* TO 'ucms_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Use the database
USE ucmsdb;

-- Show that database is ready
SHOW TABLES;
