# Test Admin Course Management - Complete Flow
Write-Host "=== Testing Admin Course Management ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/api/v1"

# Step 1: Login as admin to get JWT token
Write-Host "`n1. Logging in as admin..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -ContentType "application/json" -Body $loginData
    $token = $loginResponse.accessToken
    Write-Host "✓ Login successful!" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0,50))..." -ForegroundColor Cyan
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Create Authorization header
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Step 3: Create a new course
Write-Host "`n2. Creating a new course..." -ForegroundColor Yellow
$courseData = @{
    name = "Introduction to Computer Science"
    code = "CS105"
    description = "Fundamental concepts of computer science and programming"
    credits = 3
    department = "Computer Science"
    maxEnrollments = 50
    prerequisiteCodes = @()
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/courses" -Method POST -Headers $headers -Body $courseData
    Write-Host "✓ Course created successfully!" -ForegroundColor Green
    Write-Host "Course ID: $($createResponse.id)" -ForegroundColor Cyan
    $courseId = $createResponse.id
} catch {
    Write-Host "✗ Course creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    exit 1
}

# Step 4: View all courses
Write-Host "`n3. Viewing all courses..." -ForegroundColor Yellow
try {
    $coursesResponse = Invoke-RestMethod -Uri "$baseUrl/courses" -Method GET -Headers $headers
    Write-Host "✓ Retrieved $($coursesResponse.Count) courses" -ForegroundColor Green
    $coursesResponse | ForEach-Object {
        Write-Host "- $($_.code): $($_.name)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "✗ Failed to retrieve courses: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Update the course
if ($courseId) {
    Write-Host "`n4. Updating the course..." -ForegroundColor Yellow
    $updateData = @{
        name = "Advanced Computer Science"
        code = "CS105"
        description = "Advanced concepts of computer science and programming"
        credits = 4
        department = "Computer Science"
        maxEnrollments = 40
        prerequisiteCodes = @()
    } | ConvertTo-Json

    try {
        $updateResponse = Invoke-RestMethod -Uri "$baseUrl/courses/$courseId" -Method PUT -Headers $headers -Body $updateData
        Write-Host "✓ Course updated successfully!" -ForegroundColor Green
        Write-Host "Updated Name: $($updateResponse.name)" -ForegroundColor Cyan
    } catch {
        Write-Host "✗ Course update failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    # Step 6: View specific course
    Write-Host "`n5. Viewing specific course..." -ForegroundColor Yellow
    try {
        $courseResponse = Invoke-RestMethod -Uri "$baseUrl/courses/$courseId" -Method GET -Headers $headers
        Write-Host "✓ Course retrieved successfully!" -ForegroundColor Green
        Write-Host "Course: $($courseResponse.code) - $($courseResponse.name)" -ForegroundColor Cyan
    } catch {
        Write-Host "✗ Failed to retrieve course: $($_.Exception.Message)" -ForegroundColor Red
    }

    # Step 7: Delete the course
    Write-Host "`n6. Deleting the course..." -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri "$baseUrl/courses/$courseId" -Method DELETE -Headers $headers
        Write-Host "✓ Course deleted successfully!" -ForegroundColor Green
    } catch {
        Write-Host "✗ Course deletion failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
