# Student Course Registration API Documentation

This document describes the API endpoints for student course registration functionality.

## Base URL
```
http://localhost:8080/api/v1/students/course-registrations
```

## Authentication
All endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### 1. Register for a Course
Register the current student for a specific course.

**Endpoint:** `POST /register`  
**Access:** Student only  
**Request Body:**
```json
{
    "courseId": 1
}
```

**Success Response (200 OK):**
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
    "registeredAt": "2025-09-03T09:52:16.489+05:30",
    "status": "ACTIVE"
}
```

**Error Responses:**
- `400 Bad Request`: Registration failed (course full, already registered, course inactive)
- `404 Not Found`: Course not found
- `401 Unauthorized`: Not authenticated or not a student

### 2. Unregister from a Course
Remove the current student's registration from a specific course.

**Endpoint:** `DELETE /unregister/{courseId}`  
**Access:** Student only  

**Success Response (200 OK):** Empty response

**Error Responses:**
- `400 Bad Request`: Student not registered for this course
- `404 Not Found`: Course not found
- `401 Unauthorized`: Not authenticated or not a student

### 3. Get My Registered Courses
Get all courses the current student is registered for.

**Endpoint:** `GET /my-courses`  
**Access:** Student only  

**Success Response (200 OK):**
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
        "registeredAt": "2025-09-03T09:52:16.489+05:30",
        "status": "ACTIVE"
    },
    {
        "id": 2,
        "studentNumber": "CS/2020/001",
        "courseCode": "CS102",
        "courseTitle": "Data Structures",
        "courseName": "Data Structures",
        "credits": 4,
        "department": "Computer Science",
        "courseDescription": "Introduction to data structures and algorithms",
        "registeredAt": "2025-09-03T10:15:20.123+05:30",
        "status": "ACTIVE"
    }
]
```

### 4. Check Registration Status
Check if the current student is registered for a specific course.

**Endpoint:** `GET /check/{courseId}`  
**Access:** Student only  

**Success Response (200 OK):**
```json
true
```
or
```json
false
```

### 5. Get Registered Courses by Student Number
Get all courses a specific student is registered for (Admin/Lecturer access).

**Endpoint:** `GET /student/{studentNumber}`  
**Access:** Admin or Lecturer only  

**Success Response (200 OK):**
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
        "registeredAt": "2025-09-03T09:52:16.489+05:30",
        "status": "ACTIVE"
    }
]
```

## Integration with Existing Course APIs

You can combine these registration endpoints with your existing course viewing endpoints:

### View Available Courses for Registration
Use the existing endpoint to see available courses:
```
GET /api/v1/students/courses/enrollment/available
```

### Check Course Availability
Use the existing endpoint to check if a course is available:
```
GET /api/v1/students/courses/{courseId}/enrollment/check
```

## Usage Flow

1. **Student Login**: Authenticate to get JWT token
2. **View Available Courses**: `GET /api/v1/students/courses/enrollment/available`
3. **Register for Course**: `POST /api/v1/students/course-registrations/register`
4. **View My Courses**: `GET /api/v1/students/course-registrations/my-courses`
5. **Unregister if needed**: `DELETE /api/v1/students/course-registrations/unregister/{courseId}`

## Business Rules

1. **Capacity Check**: Students cannot register if the course is full (`currentEnrollments >= maxEnrollments`)
2. **Duplicate Registration**: Students cannot register for the same course twice
3. **Active Courses Only**: Students can only register for active courses
4. **Automatic Updates**: Course enrollment counts are automatically updated when students register/unregister

## Database Schema

The `student_course_registrations` table stores:
- Student ID and student number (index_no)
- Course ID, code, and title
- Registration timestamp
- Status (ACTIVE/DROPPED)
- Unique constraint on (student_id, course_id)

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK`: Success
- `400 Bad Request`: Business logic errors
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server errors
