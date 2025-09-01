# MySQL Setup Instructions for UCMS

## Prerequisites
1. Install MySQL Server (version 8.0 or later recommended)
2. Install MySQL Workbench (optional but recommended for GUI)

## Option 1: Command Line Setup
```bash
# Connect to MySQL as root
mysql -u root -p

# Run the setup script
source setup-mysql.sql

# Or copy-paste these commands:
CREATE DATABASE IF NOT EXISTS ucmsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'ucms_user'@'localhost' IDENTIFIED BY 'ucms_password';
GRANT ALL PRIVILEGES ON ucmsdb.* TO 'ucms_user'@'localhost';
FLUSH PRIVILEGES;
```

## Option 2: MySQL Workbench Setup
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open the setup-mysql.sql file
4. Execute the script

## Option 3: XAMPP/phpMyAdmin Setup
1. Start XAMPP and ensure MySQL is running
2. Open http://localhost/phpmyadmin
3. Click "New" to create database
4. Database name: ucmsdb
5. Collation: utf8mb4_unicode_ci
6. Click "Create"

## Verify Setup
After creating the database, verify by connecting:
```bash
mysql -u ucms_user -p ucmsdb
# Enter password: ucms_password
```

## Application Configuration
The application is configured to use:
- **Host**: localhost
- **Port**: 3306 (MySQL default)
- **Database**: ucmsdb
- **Username**: ucms_user
- **Password**: ucms_password

## Security Notes
- Change default passwords in production
- Use environment variables for sensitive data
- Consider creating separate users for different environments
