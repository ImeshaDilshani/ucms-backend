# Test script for Student Course Viewing API
# This PowerShell script tests the student course viewing endpoints

$baseUrl = "http://localhost:8080/api/v1"
$studentToken = "YOUR_STUDENT_JWT_TOKEN_HERE"  # Replace with actual student JWT token

$headers = @{
    "Authorization" = "Bearer $studentToken"
    "Content-Type" = "application/json"
}

Write-Host "Testing Student Course Viewing API..." -ForegroundColor Green

# Test 1: Get all available courses (paginated)
Write-Host "`n1. Testing: Get all available courses (paginated)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses?page=0&size=5" -Method Get -Headers $headers
    Write-Host "✅ Success: Retrieved $($response.totalElements) courses (showing $($response.content.Count) per page)" -ForegroundColor Green
    if ($response.content.Count -gt 0) {
        Write-Host "   Sample course: $($response.content[0].code) - $($response.content[0].name)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Get all available courses (simple list)
Write-Host "`n2. Testing: Get all available courses (simple list)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/all" -Method Get -Headers $headers
    Write-Host "✅ Success: Retrieved $($response.Count) courses" -ForegroundColor Green
    if ($response.Count -gt 0) {
        Write-Host "   First course: $($response[0].code) - $($response[0].name)" -ForegroundColor Cyan
        Write-Host "   Available for enrollment: $($response[0].isAvailable)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Search courses by keyword
Write-Host "`n3. Testing: Search courses by keyword 'programming'" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/search?keyword=programming" -Method Get -Headers $headers
    Write-Host "✅ Success: Found $($response.totalElements) courses matching 'programming'" -ForegroundColor Green
    foreach ($course in $response.content) {
        Write-Host "   - $($course.code): $($course.name)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Get courses by department
Write-Host "`n4. Testing: Get courses by department 'Computer Science'" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/department/Computer%20Science" -Method Get -Headers $headers
    Write-Host "✅ Success: Found $($response.Count) courses in Computer Science department" -ForegroundColor Green
    foreach ($course in $response | Select-Object -First 3) {
        Write-Host "   - $($course.code): $($course.name) ($($course.credits) credits)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Get courses by credits
Write-Host "`n5. Testing: Get courses with 3 credits" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/credits/3" -Method Get -Headers $headers
    Write-Host "✅ Success: Found $($response.Count) courses with 3 credits" -ForegroundColor Green
    foreach ($course in $response | Select-Object -First 3) {
        Write-Host "   - $($course.code): $($course.name)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Get courses available for enrollment
Write-Host "`n6. Testing: Get courses available for enrollment" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/enrollment/available" -Method Get -Headers $headers
    Write-Host "✅ Success: Found $($response.Count) courses available for enrollment" -ForegroundColor Green
    foreach ($course in $response | Select-Object -First 3) {
        Write-Host "   - $($course.code): $($course.name) ($($course.currentEnrollments)/$($course.maxEnrollments) enrolled)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Get specific course by ID (assuming course with ID 1 exists)
Write-Host "`n7. Testing: Get course details by ID (ID: 1)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/1" -Method Get -Headers $headers
    Write-Host "✅ Success: Retrieved course details" -ForegroundColor Green
    Write-Host "   Course: $($response.code) - $($response.name)" -ForegroundColor Cyan
    Write-Host "   Department: $($response.department)" -ForegroundColor Cyan
    Write-Host "   Credits: $($response.credits)" -ForegroundColor Cyan
    Write-Host "   Available: $($response.isAvailable)" -ForegroundColor Cyan
    Write-Host "   Prerequisites: $($response.prerequisites.Count)" -ForegroundColor Cyan
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8: Check enrollment availability for course
Write-Host "`n8. Testing: Check enrollment availability for course ID 1" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses/1/enrollment/check" -Method Get -Headers $headers
    Write-Host "✅ Success: Course enrollment available: $response" -ForegroundColor Green
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 9: Filter courses by multiple parameters
Write-Host "`n9. Testing: Filter courses with multiple parameters" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/students/courses?department=Computer%20Science&sortBy=name&sortDir=desc" -Method Get -Headers $headers
    Write-Host "✅ Success: Retrieved filtered and sorted courses" -ForegroundColor Green
    Write-Host "   Total courses: $($response.totalElements)" -ForegroundColor Cyan
    if ($response.content.Count -gt 0) {
        Write-Host "   First course (sorted by name desc): $($response.content[0].name)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Student Course Viewing API Tests Complete ===" -ForegroundColor Green
Write-Host "Replace 'YOUR_STUDENT_JWT_TOKEN_HERE' with an actual student JWT token to run these tests." -ForegroundColor Yellow
