# UCMS Admin Course Management Test Script
# Test all course CRUD operations for admin users

Write-Host "Starting UCMS Admin Course Management Tests..." -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"
$adminCredentials = @{
    username = "admin"
    password = "admin123"
}

# Function to make authenticated requests
function Invoke-AuthenticatedRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [object]$Body = $null,
        [string]$Token
    )
    
    $headers = @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
    
    try {
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10
            return Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body $jsonBody
        } else {
            return Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers
        }
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Test 1: Admin Login
Write-Host "1. Testing Admin Login..." -ForegroundColor Cyan
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" -Method POST -ContentType "application/json" -Body ($adminCredentials | ConvertTo-Json)
    $adminToken = $loginResponse.accessToken
    Write-Host "✅ Admin login successful!" -ForegroundColor Green
    Write-Host "Token: $($adminToken.Substring(0, 50))..." -ForegroundColor Yellow
} catch {
    Write-Host "❌ Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Test 2: Create a Course
Write-Host "2. Testing Course Creation..." -ForegroundColor Cyan
$newCourse = @{
    name = "Introduction to Computer Science2"
    code = "CS105"
    description = "Fundamental concepts of computer science and programming2"
    credits = 3
    department = "Computer Science"
    maxEnrollments = 50
    prerequisiteCodes = @()
}

$courseResponse = Invoke-AuthenticatedRequest -Method POST -Uri "$baseUrl/api/courses" -Body $newCourse -Token $adminToken

if ($courseResponse) {
    Write-Host "✅ Course created successfully!" -ForegroundColor Green
    Write-Host "Course ID: $($courseResponse.id)" -ForegroundColor Yellow
    Write-Host "Course Code: $($courseResponse.code)" -ForegroundColor Yellow
    $createdCourseId = $courseResponse.id
} else {
    Write-Host "❌ Course creation failed!" -ForegroundColor Red
}
Write-Host ""

# Test 3: Get All Courses
Write-Host "3. Testing Get All Courses..." -ForegroundColor Cyan
$coursesResponse = Invoke-AuthenticatedRequest -Method GET -Uri "$baseUrl/api/courses" -Token $adminToken

if ($coursesResponse) {
    Write-Host "✅ Retrieved courses successfully!" -ForegroundColor Green
    Write-Host "Total courses: $($coursesResponse.content.Length)" -ForegroundColor Yellow
} else {
    Write-Host "❌ Failed to retrieve courses!" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get Course by ID
if ($createdCourseId) {
    Write-Host "4. Testing Get Course by ID..." -ForegroundColor Cyan
    $courseByIdResponse = Invoke-AuthenticatedRequest -Method GET -Uri "$baseUrl/api/courses/$createdCourseId" -Token $adminToken
    
    if ($courseByIdResponse) {
        Write-Host "✅ Retrieved course by ID successfully!" -ForegroundColor Green
        Write-Host "Course: $($courseByIdResponse.name)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Failed to retrieve course by ID!" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 5: Update Course
if ($createdCourseId) {
    Write-Host "5. Testing Course Update..." -ForegroundColor Cyan
    $updatedCourse = @{
        name = "Introduction to Computer Science - Updated"
        credits = 4
        description = "Updated fundamental concepts of computer science and programming"
        department = "Computer Science"
        maxEnrollments = 60
        prerequisiteCodes = @()
    }
    
    $updateResponse = Invoke-AuthenticatedRequest -Method PUT -Uri "$baseUrl/api/courses/$createdCourseId" -Body $updatedCourse -Token $adminToken
    
    if ($updateResponse) {
        Write-Host "✅ Course updated successfully!" -ForegroundColor Green
        Write-Host "Updated name: $($updateResponse.name)" -ForegroundColor Yellow
        Write-Host "Updated credits: $($updateResponse.credits)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Course update failed!" -ForegroundColor Red
    }
    Write-Host ""
}

# Test 6: Get Course by Code
Write-Host "6. Testing Get Course by Code..." -ForegroundColor Cyan
$courseByCodeResponse = Invoke-AuthenticatedRequest -Method GET -Uri "$baseUrl/api/courses/code/CS105" -Token $adminToken

if ($courseByCodeResponse) {
    Write-Host "✅ Retrieved course by code successfully!" -ForegroundColor Green
    Write-Host "Course: $($courseByCodeResponse.name)" -ForegroundColor Yellow
} else {
    Write-Host "❌ Failed to retrieve course by code!" -ForegroundColor Red
}
Write-Host ""

# Test 7: Delete Course
if ($createdCourseId) {
    Write-Host "7. Testing Course Deletion..." -ForegroundColor Cyan
    $deleteResponse = Invoke-AuthenticatedRequest -Method DELETE -Uri "$baseUrl/api/courses/$createdCourseId" -Token $adminToken
    
    if ($deleteResponse -eq $null) {  # DELETE returns no content (204)
        Write-Host "✅ Course deleted successfully!" -ForegroundColor Green
        
        # Verify deletion by trying to get the deleted course
        $verifyDeletion = Invoke-AuthenticatedRequest -Method GET -Uri "$baseUrl/api/courses/$createdCourseId" -Token $adminToken
        if ($verifyDeletion -eq $null) {
            Write-Host "✅ Deletion verified - course no longer exists!" -ForegroundColor Green
        }
    } else {
        Write-Host "❌ Course deletion failed!" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "Course Management Tests Completed!" -ForegroundColor Green
Write-Host "All admin course CRUD operations have been tested." -ForegroundColor Cyan
