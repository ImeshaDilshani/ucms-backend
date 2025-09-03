# Test script for Lecturer Grading System
# Tests lecturer functionality to grade students and release results

$baseUrl = "http://localhost:8080/api/v1"
$headers = @{"Content-Type" = "application/json"}

Write-Host "=== Testing Lecturer Grading System ===" -ForegroundColor Green

# Step 1: Create and Login Lecturer
Write-Host "`n1. Creating and logging in lecturer..." -ForegroundColor Yellow

# Create lecturer (you may need to adjust this based on your lecturer registration)
$lecturerRegBody = @{
    username = "lecturer001"
    email = "lecturer001@university.edu"
    password = "password123"
    staffNo = "LEC001"
    firstName = "Dr. Jane"
    lastName = "Smith"
    department = "Computer Science"
    lecturerSecretKey = "LECTURER_SECRET_2024"
} | ConvertTo-Json

try {
    # Try to register lecturer (might fail if already exists)
    $regResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register/lecturer" -Method POST -Body $lecturerRegBody -Headers $headers -ErrorAction SilentlyContinue
} catch {
    Write-Host "Lecturer might already exist, proceeding to login..." -ForegroundColor Yellow
}

# Login lecturer
$lecturerLoginBody = @{
    username = "lecturer001"
    password = "password123"
} | ConvertTo-Json

try {
    $lecturerLoginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $lecturerLoginBody -Headers $headers
    $lecturerToken = $lecturerLoginResponse.accessToken
    $lecturerHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $lecturerToken"
    }
    Write-Host "✓ Lecturer login successful" -ForegroundColor Green
    Write-Host "User: $($lecturerLoginResponse.userInfo.username)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Lecturer login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Get Gradable Courses
Write-Host "`n2. Getting gradable courses..." -ForegroundColor Yellow
try {
    $courses = Invoke-RestMethod -Uri "$baseUrl/lecturers/grading/courses" -Method GET -Headers $lecturerHeaders
    Write-Host "✓ Found $($courses.Count) gradable courses" -ForegroundColor Green
    
    if ($courses.Count -gt 0) {
        $courseId = $courses[0].id
        Write-Host "Using course: $($courses[0].code) - $($courses[0].name)" -ForegroundColor Cyan
    } else {
        Write-Host "No courses available for grading" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "✗ Failed to get courses: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Get Enrolled Students
Write-Host "`n3. Getting enrolled students for course..." -ForegroundColor Yellow
try {
    $students = Invoke-RestMethod -Uri "$baseUrl/lecturers/grading/courses/$courseId/students" -Method GET -Headers $lecturerHeaders
    Write-Host "✓ Found $($students.Count) enrolled students" -ForegroundColor Green
    
    foreach ($student in $students) {
        Write-Host "- $($student.studentNumber): $($student.fullName)" -ForegroundColor Cyan
        if ($student.hasGrade) {
            Write-Host "  Already has grade: $($student.existingResult.gradeDisplay)" -ForegroundColor Yellow
        }
    }
    
    if ($students.Count -eq 0) {
        Write-Host "No students enrolled in this course" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "✗ Failed to get enrolled students: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Submit Grade for First Student
Write-Host "`n4. Submitting grade for first student..." -ForegroundColor Yellow
$firstStudent = $students[0]
$gradeSubmission = @{
    studentId = $firstStudent.studentId
    courseId = $courseId
    marks = 85.5
    remarks = "Excellent work on all assignments"
} | ConvertTo-Json

try {
    $gradeResult = Invoke-RestMethod -Uri "$baseUrl/lecturers/grading/submit-grade" -Method POST -Body $gradeSubmission -Headers $lecturerHeaders
    Write-Host "✓ Grade submitted successfully" -ForegroundColor Green
    Write-Host "Student: $($gradeResult.studentName)" -ForegroundColor Cyan
    Write-Host "Marks: $($gradeResult.marks)" -ForegroundColor Cyan
    Write-Host "Grade: $($gradeResult.gradeDisplay)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Failed to submit grade: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Get Course Results
Write-Host "`n5. Getting all course results..." -ForegroundColor Yellow
try {
    $courseResults = Invoke-RestMethod -Uri "$baseUrl/lecturers/grading/courses/$courseId/results" -Method GET -Headers $lecturerHeaders
    Write-Host "✓ Found $($courseResults.Count) course results" -ForegroundColor Green
    
    foreach ($result in $courseResults) {
        Write-Host "- $($result.studentNumber): $($result.gradeDisplay) ($($result.marks)%)" -ForegroundColor Cyan
        Write-Host "  Released: $($result.isReleased)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Failed to get course results: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 6: Release Results
Write-Host "`n6. Releasing results..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/lecturers/grading/courses/$courseId/release-results" -Method POST -Headers $lecturerHeaders
    Write-Host "✓ Results released successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to release results: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Lecturer Grading Test Complete ===" -ForegroundColor Green
