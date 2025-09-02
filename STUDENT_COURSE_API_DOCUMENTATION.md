# Student Course API Documentation

This document describes the REST API endpoints available for students to view course details in the UCMS system.

## Base URL
```
/api/v1/students/courses
```

## Authentication
All endpoints require authentication with `STUDENT` role.

## Endpoints

### 1. Get All Available Courses (Paginated)
**GET** `/api/v1/students/courses`

Get all active courses with pagination and filtering options.

**Query Parameters:**
- `code` (optional): Filter by course code (partial match)
- `name` (optional): Filter by course name (partial match)
- `department` (optional): Filter by department (partial match)
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size
- `sortBy` (optional, default: "code"): Sort field
- `sortDir` (optional, default: "asc"): Sort direction ("asc" or "desc")

**Example Request:**
```
GET /api/v1/students/courses?department=Computer Science&page=0&size=10
```

**Response:** 
```json
{
  "content": [
    {
      "id": 1,
      "code": "CS101",
      "name": "Introduction to Programming",
      "description": "Basic programming concepts and techniques",
      "credits": 3,
      "department": "Computer Science",
      "maxEnrollments": 50,
      "currentEnrollments": 30,
      "isAvailable": true,
      "prerequisites": []
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### 2. Get All Available Courses (Simple List)
**GET** `/api/v1/students/courses/all`

Get all active courses without pagination.

**Example Response:**
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
    "currentEnrollments": 30,
    "isAvailable": true,
    "prerequisites": []
  }
]
```

### 3. Get Course Details by ID
**GET** `/api/v1/students/courses/{courseId}`

Get detailed information about a specific course by its ID.

**Path Parameters:**
- `courseId`: Course ID

**Example Request:**
```
GET /api/v1/students/courses/1
```

### 4. Get Course Details by Code
**GET** `/api/v1/students/courses/code/{code}`

Get detailed information about a specific course by its code.

**Path Parameters:**
- `code`: Course code

**Example Request:**
```
GET /api/v1/students/courses/code/CS101
```

### 5. Search Courses
**GET** `/api/v1/students/courses/search`

Search courses by keyword in course name or description.

**Query Parameters:**
- `keyword` (required): Search keyword
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size
- `sortBy` (optional, default: "code"): Sort field
- `sortDir` (optional, default: "asc"): Sort direction

**Example Request:**
```
GET /api/v1/students/courses/search?keyword=programming
```

### 6. Get Courses by Department
**GET** `/api/v1/students/courses/department/{department}`

Get all active courses in a specific department.

**Path Parameters:**
- `department`: Department name

**Example Request:**
```
GET /api/v1/students/courses/department/Computer Science
```

### 7. Get Courses by Credits
**GET** `/api/v1/students/courses/credits/{credits}`

Get all active courses with a specific credit value.

**Path Parameters:**
- `credits`: Credit value

**Example Request:**
```
GET /api/v1/students/courses/credits/3
```

### 8. Get Courses Available for Enrollment
**GET** `/api/v1/students/courses/enrollment/available`

Get all courses that still have enrollment capacity (currentEnrollments < maxEnrollments).

**Example Response:**
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
    "currentEnrollments": 30,
    "isAvailable": true,
    "prerequisites": []
  }
]
```

### 9. Get Courses Available for Enrollment by Department
**GET** `/api/v1/students/courses/enrollment/available/department/{department}`

Get courses available for enrollment in a specific department.

**Path Parameters:**
- `department`: Department name

### 10. Check Course Enrollment Availability
**GET** `/api/v1/students/courses/{courseId}/enrollment/check`

Check if a specific course is available for enrollment.

**Path Parameters:**
- `courseId`: Course ID

**Example Response:**
```json
true
```

## Response Format

### StudentCourseViewResponse
```json
{
  "id": 1,
  "code": "CS101",
  "name": "Introduction to Programming",
  "description": "Basic programming concepts and techniques",
  "credits": 3,
  "department": "Computer Science",
  "maxEnrollments": 50,
  "currentEnrollments": 30,
  "isAvailable": true,
  "prerequisites": [
    {
      "id": 2,
      "code": "MATH101",
      "name": "Calculus I",
      "credits": 4
    }
  ]
}
```

### Key Fields:
- `isAvailable`: Boolean indicating if the course has enrollment capacity
- `prerequisites`: Array of prerequisite courses with basic information
- Only active courses are returned to students
- Current and maximum enrollment numbers are provided for capacity planning

## Error Responses

### 404 Not Found
```json
{
  "error": "Course not found or not available: CS999"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied. Student role required."
}
```

## Notes

1. Only **active** courses are visible to students
2. Students can view all course details including prerequisites and enrollment status
3. The `isAvailable` field helps students identify courses they can potentially enroll in
4. All endpoints support CORS for web applications
5. Pagination is available for large result sets
6. Search functionality covers both course names and descriptions
