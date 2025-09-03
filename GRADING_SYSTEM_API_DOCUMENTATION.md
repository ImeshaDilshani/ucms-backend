# Course Grading System API Documentation

This document describes the API endpoints for the course grading system where lecturers can grade students and students can view their results.

## Base URLs
- **Lecturer Grading:** `http://localhost:8080/api/v1/lecturers/grading`
- **Student Results:** `http://localhost:8080/api/v1/students/results`

## Authentication
All endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Lecturer Endpoints

### 1. Get Gradable Courses
Get all courses available for grading.

**Endpoint:** `GET /courses`  
**Access:** Lecturer only  

**Success Response (200 OK):**
```json
[
    {
        "id": 1,
        "code": "CS101",
        "name": "Introduction to Programming",
        "description": "Basic programming concepts",
        "credits": 3,
        "department": "Computer Science",
        "maxEnrollments": 50,
        "currentEnrollments": 25,
        "active": true
    }
]
```

### 2. Get Enrolled Students for a Course
Get all students enrolled in a specific course for grading.

**Endpoint:** `GET /courses/{courseId}/students`  
**Access:** Lecturer only  

**Success Response (200 OK):**
```json
[
    {
        "studentId": 1,
        "studentNumber": "CS/2020/001",
        "firstName": "John",
        "lastName": "Doe",
        "fullName": "John Doe",
        "email": "john.doe@university.edu",
        "program": "Computer Science",
        "year": 2,
        "hasGrade": false,
        "existingResult": null
    },
    {
        "studentId": 2,
        "studentNumber": "CS/2020/002",
        "firstName": "Jane",
        "lastName": "Smith",
        "fullName": "Jane Smith",
        "email": "jane.smith@university.edu",
        "program": "Computer Science",
        "year": 2,
        "hasGrade": true,
        "existingResult": {
            "id": 1,
            "marks": 85.5,
            "grade": "A",
            "gradeDisplay": "A",
            "remarks": "Excellent work"
        }
    }
]
```

### 3. Submit Grade for a Student
Submit or update grade for a specific student in a course.

**Endpoint:** `POST /submit-grade`  
**Access:** Lecturer only  
**Request Body:**
```json
{
    "studentId": 1,
    "courseId": 1,
    "marks": 85.5,
    "remarks": "Excellent work on all assignments"
}
```

**Success Response (200 OK):**
```json
{
    "id": 1,
    "studentNumber": "CS/2020/001",
    "studentName": "John Doe",
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "credits": 3,
    "marks": 85.5,
    "grade": "A",
    "gradeDisplay": "A",
    "remarks": "Excellent work on all assignments",
    "gradedAt": "2025-09-03T11:30:15.123+05:30",
    "releasedAt": null,
    "isReleased": false,
    "lecturerName": "Dr. Jane Smith"
}
```

**Validation Rules:**
- `marks`: Must be between 0.0 and 100.0
- `studentId` and `courseId`: Required
- Student must be enrolled in the course

**Grade Scale:**
- A+ (85-100): Excellent
- A (80-84): Very Good
- A- (75-79): Good
- B+ (70-74): Above Average
- B (65-69): Average
- B- (60-64): Below Average
- C+ (55-59): Satisfactory
- C (50-54): Pass
- C- (45-49): Marginal Pass
- D (40-44): Poor
- F (0-39): Fail

### 4. Get Course Results
Get all graded results for a specific course.

**Endpoint:** `GET /courses/{courseId}/results`  
**Access:** Lecturer only  

**Success Response (200 OK):**
```json
[
    {
        "id": 1,
        "studentNumber": "CS/2020/001",
        "studentName": "John Doe",
        "courseCode": "CS101",
        "courseName": "Introduction to Programming",
        "credits": 3,
        "marks": 85.5,
        "grade": "A",
        "gradeDisplay": "A",
        "remarks": "Excellent work",
        "gradedAt": "2025-09-03T11:30:15.123+05:30",
        "releasedAt": "2025-09-03T12:00:00.000+05:30",
        "isReleased": true,
        "lecturerName": "Dr. Jane Smith"
    }
]
```

### 5. Release Results for a Course
Make all graded results visible to students.

**Endpoint:** `POST /courses/{courseId}/release-results`  
**Access:** Lecturer only  

**Success Response (200 OK):** Empty response

**Note:** Once released, students can view their results. This action affects all graded students in the course.

## Student Endpoints

### 1. Get My Results
Get all released results for the current student.

**Endpoint:** `GET /my-results`  
**Access:** Student only  

**Success Response (200 OK):**
```json
[
    {
        "id": 1,
        "studentNumber": "CS/2020/001",
        "studentName": "John Doe",
        "courseCode": "CS101",
        "courseName": "Introduction to Programming",
        "credits": 3,
        "marks": 85.5,
        "grade": "A",
        "gradeDisplay": "A",
        "remarks": "Excellent work on all assignments",
        "gradedAt": "2025-09-03T11:30:15.123+05:30",
        "releasedAt": "2025-09-03T12:00:00.000+05:30",
        "isReleased": true,
        "lecturerName": "Dr. Jane Smith"
    }
]
```

**Note:** Only released results are returned to students.

### 2. Get Student Results by Number (Admin/Lecturer)
Get released results for a specific student by their student number.

**Endpoint:** `GET /student/{studentNumber}`  
**Access:** Admin or Lecturer only  

**Success Response (200 OK):**
```json
[
    {
        "id": 1,
        "studentNumber": "CS/2020/001",
        "studentName": "John Doe",
        "courseCode": "CS101",
        "courseName": "Introduction to Programming",
        "credits": 3,
        "marks": 85.5,
        "grade": "A",
        "gradeDisplay": "A",
        "remarks": "Excellent work",
        "gradedAt": "2025-09-03T11:30:15.123+05:30",
        "releasedAt": "2025-09-03T12:00:00.000+05:30",
        "isReleased": true,
        "lecturerName": "Dr. Jane Smith"
    }
]
```

## Lecturer Workflow

1. **Login:** Authenticate as lecturer
2. **View Courses:** `GET /courses` - See available courses
3. **View Students:** `GET /courses/{courseId}/students` - See enrolled students
4. **Grade Students:** `POST /submit-grade` - Submit marks for each student
5. **Review Results:** `GET /courses/{courseId}/results` - Check all grades
6. **Release Results:** `POST /courses/{courseId}/release-results` - Make visible to students

## Student Workflow

1. **Login:** Authenticate as student
2. **View Results:** `GET /my-results` - See released grades

## Business Rules

1. **Grading Access:** Lecturers can grade any active course (simplified model)
2. **Student Enrollment:** Only enrolled students appear in the grading list
3. **Grade Calculation:** Grades are automatically calculated based on marks
4. **Result Release:** Students can only see results after lecturer releases them
5. **Grade Updates:** Lecturers can update grades before or after release
6. **Unique Constraint:** One result per student per course

## Database Schema

The `course_results` table stores:
- Student and course information
- Lecturer who graded
- Marks (0-100) and calculated grade (A+ to F)
- Timestamps for grading and release
- Release status flag

## Error Handling

- `400 Bad Request`: Invalid data, business rule violations
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server errors

## Integration Notes

This grading system integrates with:
- **Course Registration System:** Students must be enrolled to be graded
- **User Management:** Uses existing student and lecturer entities
- **Authentication:** Uses existing JWT authentication

Students can view both their course registrations and results from their dashboard.
