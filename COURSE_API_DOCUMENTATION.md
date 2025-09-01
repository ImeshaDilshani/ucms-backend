# Course Management API Endpoints

This document describes the REST API endpoints for managing courses in the University Course Management System (UCMS). All endpoints require authentication, and administrative operations require ADMIN role.

## Base URL
```
http://localhost:8080/api/courses
```

## Authentication
All endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### 1. Create Course (Admin Only)
**POST** `/api/courses`

Creates a new course in the system.

**Required Role:** ADMIN

**Request Body:**
```json
{
  "code": "CS101",
  "title": "Introduction to Computer Science",
  "credits": 3,
  "description": "Basic concepts of computer science including programming fundamentals.",
  "prerequisiteIds": [1, 2] // Optional: Array of course IDs that are prerequisites
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "code": "CS101",
  "title": "Introduction to Computer Science",
  "credits": 3,
  "description": "Basic concepts of computer science including programming fundamentals.",
  "prerequisites": [
    {
      "id": 1,
      "code": "MATH101",
      "title": "Mathematics for Computer Science",
      "credits": 3
    }
  ],
  "createdAt": "2025-08-28T15:30:00",
  "updatedAt": "2025-08-28T15:30:00"
}
```

### 2. Update Course (Admin Only)
**PUT** `/api/courses/{courseId}`

Updates an existing course.

**Required Role:** ADMIN

**Path Parameters:**
- `courseId` (Long): The ID of the course to update

**Request Body:**
```json
{
  "title": "Advanced Computer Science",
  "credits": 4,
  "description": "Updated description for the course.",
  "prerequisiteIds": [1, 2, 3] // Optional: Updated list of prerequisite IDs
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "code": "CS101",
  "title": "Advanced Computer Science",
  "credits": 4,
  "description": "Updated description for the course.",
  "prerequisites": [
    {
      "id": 1,
      "code": "MATH101",
      "title": "Mathematics for Computer Science",
      "credits": 3
    }
  ],
  "createdAt": "2025-08-28T15:30:00",
  "updatedAt": "2025-08-28T16:30:00"
}
```

### 3. Delete Course (Admin Only)
**DELETE** `/api/courses/{courseId}`

Deletes a course from the system. Cannot delete if the course is a prerequisite for other courses.

**Required Role:** ADMIN

**Path Parameters:**
- `courseId` (Long): The ID of the course to delete

**Response:** `204 No Content`

### 4. Get Course by ID
**GET** `/api/courses/{courseId}`

Retrieves a specific course by its ID.

**Required Role:** ADMIN, LECTURER, or STUDENT

**Path Parameters:**
- `courseId` (Long): The ID of the course

**Response:** `200 OK`
```json
{
  "id": 1,
  "code": "CS101",
  "title": "Introduction to Computer Science",
  "credits": 3,
  "description": "Basic concepts of computer science.",
  "prerequisites": [],
  "createdAt": "2025-08-28T15:30:00",
  "updatedAt": "2025-08-28T15:30:00"
}
```

### 5. Get Course by Code
**GET** `/api/courses/code/{code}`

Retrieves a specific course by its course code.

**Required Role:** ADMIN, LECTURER, or STUDENT

**Path Parameters:**
- `code` (String): The course code (e.g., "CS101")

**Response:** `200 OK` - Same format as Get Course by ID

### 6. Get All Courses (Paginated)
**GET** `/api/courses`

Retrieves all courses with pagination and filtering support.

**Required Role:** ADMIN, LECTURER, or STUDENT

**Query Parameters:**
- `code` (String, optional): Filter by course code (partial match)
- `title` (String, optional): Filter by course title (partial match)
- `page` (int, default: 0): Page number (0-based)
- `size` (int, default: 20): Number of items per page
- `sortBy` (String, default: "code"): Field to sort by
- `sortDir` (String, default: "asc"): Sort direction ("asc" or "desc")

**Example:** `/api/courses?code=CS&page=0&size=10&sortBy=title&sortDir=asc`

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "code": "CS101",
      "title": "Introduction to Computer Science",
      "credits": 3,
      "description": "Basic concepts of computer science.",
      "prerequisites": [],
      "createdAt": "2025-08-28T15:30:00",
      "updatedAt": "2025-08-28T15:30:00"
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

### 7. Get All Courses (Simple List)
**GET** `/api/courses/all`

Retrieves all courses as a simple list without pagination.

**Required Role:** ADMIN, LECTURER, or STUDENT

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "code": "CS101",
    "title": "Introduction to Computer Science",
    "credits": 3,
    "description": "Basic concepts of computer science.",
    "prerequisites": [],
    "createdAt": "2025-08-28T15:30:00",
    "updatedAt": "2025-08-28T15:30:00"
  }
]
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed: {code=Course code is required}",
  "instance": "uri=/api/courses"
}
```

### Authentication Error (401 Unauthorized)
```json
{
  "title": "Authentication Failed",
  "status": 401,
  "detail": "Invalid username or password",
  "instance": "uri=/api/courses"
}
```

### Authorization Error (403 Forbidden)
```json
{
  "title": "Access Denied",
  "status": 403,
  "detail": "You don't have permission to access this resource",
  "instance": "uri=/api/courses"
}
```

### Business Logic Error (400 Bad Request)
```json
{
  "title": "Invalid Request",
  "status": 400,
  "detail": "Course with code 'CS101' already exists",
  "instance": "uri=/api/courses"
}
```

### Not Found Error (400 Bad Request)
```json
{
  "title": "Invalid Request",
  "status": 400,
  "detail": "Course not found with ID: 999",
  "instance": "uri=/api/courses/999"
}
```

## Business Rules

1. **Course Code Uniqueness:** Course codes must be unique across the system
2. **Prerequisites:** A course can have multiple prerequisites
3. **Circular Dependencies:** The system prevents circular prerequisite dependencies
4. **Deletion Constraints:** Cannot delete a course that is used as a prerequisite by other courses
5. **Code Formatting:** Course codes are automatically converted to uppercase
6. **Administrative Access:** Only users with ADMIN role can create, update, or delete courses
7. **Read Access:** All authenticated users (ADMIN, LECTURER, STUDENT) can view courses

## Example Usage with cURL

### Create a Course (Admin)
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_jwt_token>" \
  -d '{
    "code": "CS101",
    "title": "Introduction to Computer Science",
    "credits": 3,
    "description": "Basic concepts of computer science."
  }'
```

### Update a Course (Admin)
```bash
curl -X PUT http://localhost:8080/api/courses/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_jwt_token>" \
  -d '{
    "title": "Advanced Computer Science",
    "credits": 4,
    "description": "Updated description."
  }'
```

### Delete a Course (Admin)
```bash
curl -X DELETE http://localhost:8080/api/courses/1 \
  -H "Authorization: Bearer <admin_jwt_token>"
```

### Get All Courses
```bash
curl -X GET "http://localhost:8080/api/courses?page=0&size=10" \
  -H "Authorization: Bearer <jwt_token>"
```
