# Course Registration API Test Script
# Tests the student course registration functionality

$baseUrl = "http://localhost:8080/api/v1"
$headers = @{"Content-Type" = "application/json"}

Write-Host "=== Testing Course Registration API ===" -ForegroundColor Green

# Step 1: Student Login
Write-Host "`n1. Student Login..." -ForegroundColor Yellow
$loginBody = @{
    username = "student1"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -Headers $headers
    $token = $loginResponse.accessToken
    $authHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }
    Write-Host "✓ Login successful" -ForegroundColor Green
    Write-Host "User: $($loginResponse.userInfo.username)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: View Available Courses
Write-Host "`n2. Viewing available courses for enrollment..." -ForegroundColor Yellow
try {
    $availableCourses = Invoke-RestMethod -Uri "$baseUrl/students/courses/enrollment/available" -Method GET -Headers $authHeaders
    Write-Host "✓ Found $($availableCourses.Count) available courses" -ForegroundColor Green
    
    if ($availableCourses.Count -gt 0) {
        $courseToRegister = $availableCourses[0]
        Write-Host "Course to register: $($courseToRegister.code) - $($courseToRegister.name)" -ForegroundColor Cyan
    } else {
        Write-Host "No available courses found" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Failed to get available courses: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Register for Course
Write-Host "`n3. Registering for course..." -ForegroundColor Yellow
$registrationBody = @{
    courseId = $courseToRegister.id
} | ConvertTo-Json

try {
    $registrationResponse = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/register" -Method POST -Body $registrationBody -Headers $authHeaders
    Write-Host "✓ Successfully registered for course" -ForegroundColor Green
    Write-Host "Registration ID: $($registrationResponse.id)" -ForegroundColor Cyan
    Write-Host "Course: $($registrationResponse.courseCode) - $($registrationResponse.courseTitle)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    
    # Check if it's because already registered
    if ($_.Exception.Message -like "*already registered*") {
        Write-Host "Student already registered for this course, continuing..." -ForegroundColor Yellow
    }
}

# Step 4: View My Registered Courses
Write-Host "`n4. Viewing my registered courses..." -ForegroundColor Yellow
try {
    $myCourses = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/my-courses" -Method GET -Headers $authHeaders
    Write-Host "✓ Found $($myCourses.Count) registered courses" -ForegroundColor Green
    
    foreach ($course in $myCourses) {
        Write-Host "- $($course.courseCode): $($course.courseTitle) ($($course.credits) credits)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "✗ Failed to get registered courses: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Check Registration Status
Write-Host "`n5. Checking registration status for course..." -ForegroundColor Yellow
try {
    $isRegistered = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/check/$($courseToRegister.id)" -Method GET -Headers $authHeaders
    Write-Host "✓ Registration status: $isRegistered" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to check registration status: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 6: Test Duplicate Registration (should fail)
Write-Host "`n6. Testing duplicate registration (should fail)..." -ForegroundColor Yellow
try {
    $duplicateResponse = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/register" -Method POST -Body $registrationBody -Headers $authHeaders
    Write-Host "✗ Duplicate registration should have failed!" -ForegroundColor Red
} catch {
    Write-Host "✓ Duplicate registration properly rejected" -ForegroundColor Green
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Step 7: Unregister from Course
Write-Host "`n7. Unregistering from course..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/unregister/$($courseToRegister.id)" -Method DELETE -Headers $authHeaders
    Write-Host "✓ Successfully unregistered from course" -ForegroundColor Green
} catch {
    Write-Host "✗ Unregistration failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 8: Verify Unregistration
Write-Host "`n8. Verifying unregistration..." -ForegroundColor Yellow
try {
    $isRegisteredAfter = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/check/$($courseToRegister.id)" -Method GET -Headers $authHeaders
    if ($isRegisteredAfter -eq $false) {
        Write-Host "✓ Unregistration verified" -ForegroundColor Green
    } else {
        Write-Host "✗ Unregistration verification failed" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to verify unregistration: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Course Registration API Test Complete ===" -ForegroundColor Green
