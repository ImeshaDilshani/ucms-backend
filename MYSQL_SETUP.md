# MySQL Setup Guide for UCMS Backend

## Prerequisites

1. **Install MySQL Server**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use MySQL Installer for Windows
   - Or install via package manager (Linux/Mac)

2. **Install MySQL Workbench** (Optional but recommended)
   - Download from: https://dev.mysql.com/downloads/workbench/

## Setup Steps

### Step 1: Start MySQL Server
- Windows: Start MySQL service from Services or MySQL Workbench
- Linux: `sudo systemctl start mysql`
- Mac: `brew services start mysql`

### Step 2: Create Database
Option A - Using MySQL Command Line:
```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE ucmsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

Option B - Run the provided script:
```bash
mysql -u root -p < database_setup.sql
```

### Step 3: Configure Application Properties
The application.properties has been updated with MySQL configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ucmsdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

**Important**: Update the password if your MySQL root account has a password:
```properties
spring.datasource.password=your_mysql_root_password
```

### Step 4: Run the Application
```bash
./mvnw spring-boot:run
```

## Database Schema

The application uses Flyway migrations to create the database schema automatically:

- `V1__Initial_schema.sql` - Creates all tables and indexes
- `V2__Sample_data.sql` - Inserts sample data for testing

## Sample Accounts

After migration, you'll have these test accounts:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | password | ADMIN | System administrator |
| alice.smith | password | STUDENT | Sample student (CS Year 3) |
| bob.johnson | password | STUDENT | Sample student (CS Year 2) |
| dr.wilson | password | LECTURER | Sample lecturer |

## Troubleshooting

### Connection Issues
1. Check if MySQL server is running
2. Verify database exists: `SHOW DATABASES;`
3. Check username/password in application.properties
4. Ensure MySQL is listening on port 3306

### Permission Issues
If you get access denied errors:
```sql
CREATE USER 'ucms_user'@'localhost' IDENTIFIED BY 'ucms_password';
GRANT ALL PRIVILEGES ON ucmsdb.* TO 'ucms_user'@'localhost';
FLUSH PRIVILEGES;
```

Then update application.properties:
```properties
spring.datasource.username=ucms_user
spring.datasource.password=ucms_password
```

### Migration Issues
If migrations fail, you can reset:
```sql
DROP DATABASE ucmsdb;
CREATE DATABASE ucmsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Verification

1. Check tables are created:
```sql
USE ucmsdb;
SHOW TABLES;
```

2. Check sample data:
```sql
SELECT COUNT(*) FROM users;
SELECT username, role FROM users;
```

## Development vs Production

- **Development**: Use root user for simplicity
- **Production**: Create dedicated MySQL user with limited privileges
- **Security**: Use environment variables for database credentials

## MySQL Workbench (Optional)

For GUI database management:
1. Open MySQL Workbench
2. Create connection: localhost:3306
3. Username: root (or your MySQL user)
4. Test connection and connect
5. Browse ucmsdb database

This setup provides persistent storage, better performance for production, and professional database management capabilities.
