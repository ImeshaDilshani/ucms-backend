# Testing Course Registration API with Postman

This guide walks you through testing the course registration functionality using Postman.

## Prerequisites

1. **Start your Spring Boot application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Make sure it's running on `http://localhost:8080`

2. **Open Postman** and create a new collection called "UCMS Course Registration"

## Step-by-Step Testing Guide

### Step 1: Admin Login (Get JWT Token)

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```

**Expected Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userInfo": {
    "id": 1,
    "username": "admin",
    "email": "admin@university.edu",
    "role": "ADMIN"
  }
}
```

**Action:** Copy the `accessToken` value - you'll need it for authenticated requests.

---

### Step 2: Create/View Available Courses (Admin)

#### Create a Course (if needed)
**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/courses`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer <your_admin_token>
  ```
- **Body (raw JSON):**
  ```json
  {
    "code": "CS101",
    "name": "Introduction to Programming",
    "description": "Basic programming concepts and techniques",
    "credits": 3,
    "department": "Computer Science",
    "maxEnrollments": 50
  }
  ```

#### View All Courses
**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/courses`
- **Headers:**
  ```
  Authorization: Bearer <your_admin_token>
  ```

**Note:** Copy a course ID from the response - you'll need it for registration testing.

---

### Step 3: Student Registration

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/auth/register`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "student001",
    "email": "student001@university.edu",
    "password": "password123",
    "indexNo": "CS/2020/001",
    "firstName": "John",
    "lastName": "Doe",
    "program": "Computer Science",
    "year": 2
  }
  ```

---

### Step 4: Student Login

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "student001",
    "password": "password123"
  }
  ```

**Action:** Copy the student's `accessToken` - you'll use this for course registration.

---

### Step 5: View Available Courses (Student)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/students/courses/enrollment/available`
- **Headers:**
  ```
  Authorization: Bearer <student_token>
  ```

**Expected Response:**
```json
[
  {
    "id": 1,
    "code": "CS101",
    "name": "Introduction to Programming",
    "description": "Basic programming concepts and techniques",
    "credits": 3,
    "department": "Computer Science",
    "maxEnrollments": 50,
    "currentEnrollments": 0,
    "isAvailable": true,
    "prerequisites": []
  }
]
```

---

### Step 6: Register for a Course

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/register`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer <student_token>
  ```
- **Body (raw JSON):**
  ```json
  {
    "courseId": 1
  }
  ```

**Expected Response:**
```json
{
  "id": 1,
  "studentNumber": "CS/2020/001",
  "courseCode": "CS101",
  "courseTitle": "Introduction to Programming",
  "courseName": "Introduction to Programming",
  "credits": 3,
  "department": "Computer Science",
  "courseDescription": "Basic programming concepts and techniques",
  "registeredAt": "2025-09-03T10:30:15.123+05:30",
  "status": "ACTIVE"
}
```

---

### Step 7: View My Registered Courses

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/my-courses`
- **Headers:**
  ```
  Authorization: Bearer <student_token>
  ```

**Expected Response:**
```json
[
  {
    "id": 1,
    "studentNumber": "CS/2020/001",
    "courseCode": "CS101",
    "courseTitle": "Introduction to Programming",
    "courseName": "Introduction to Programming",
    "credits": 3,
    "department": "Computer Science",
    "courseDescription": "Basic programming concepts and techniques",
    "registeredAt": "2025-09-03T10:30:15.123+05:30",
    "status": "ACTIVE"
  }
]
```

---

### Step 8: Check Registration Status

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/check/1`
- **Headers:**
  ```
  Authorization: Bearer <student_token>
  ```

**Expected Response:**
```json
true
```

---

### Step 9: Test Error Cases

#### Try to Register for Same Course Again
**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/register`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer <student_token>
  ```
- **Body (raw JSON):**
  ```json
  {
    "courseId": 1
  }
  ```

**Expected Response:** `400 Bad Request`

#### Try to Register for Non-existent Course
**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/register`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer <student_token>
  ```
- **Body (raw JSON):**
  ```json
  {
    "courseId": 999
  }
  ```

**Expected Response:** `400 Bad Request`

---

### Step 10: Unregister from Course

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/unregister/1`
- **Headers:**
  ```
  Authorization: Bearer <student_token>
  ```

**Expected Response:** `200 OK` (empty body)

---

### Step 11: Admin View Student Registrations

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/students/course-registrations/student/CS/2020/001`
- **Headers:**
  ```
  Authorization: Bearer <admin_token>
  ```

**Expected Response:** Array of student's registrations

---

## Postman Collection Setup

### Environment Variables
Create a Postman environment with these variables:
- `base_url`: `http://localhost:8080/api/v1`
- `admin_token`: (set after admin login)
- `student_token`: (set after student login)

### Collection Structure
```
UCMS Course Registration/
├── Auth/
│   ├── Admin Login
│   ├── Student Register
│   └── Student Login
├── Courses (Admin)/
│   ├── Create Course
│   └── View All Courses
├── Course Registration (Student)/
│   ├── View Available Courses
│   ├── Register for Course
│   ├── View My Courses
│   ├── Check Registration Status
│   └── Unregister from Course
└── Admin Views/
    └── View Student Registrations
```

## Testing Tips

1. **Use Environment Variables:** Set tokens as environment variables to avoid copying them manually
2. **Test in Order:** Follow the step sequence for best results
3. **Check Database:** You can verify changes in your MySQL database
4. **Error Testing:** Test various error scenarios to ensure proper error handling
5. **Multiple Students:** Create multiple students to test different scenarios

## Common Issues & Solutions

1. **401 Unauthorized:** Check if token is valid and properly formatted
2. **403 Forbidden:** Ensure you're using the right role's token
3. **404 Not Found:** Verify course IDs exist in the database
4. **400 Bad Request:** Check request body format and business rules

This comprehensive guide should help you test all aspects of the course registration functionality!
