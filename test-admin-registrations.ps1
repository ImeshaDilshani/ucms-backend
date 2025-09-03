# Admin Test Script for viewing student registrations
# Tests admin functionality to view student course registrations

$baseUrl = "http://localhost:8080/api/v1"
$headers = @{"Content-Type" = "application/json"}

Write-Host "=== Testing Admin View of Student Registrations ===" -ForegroundColor Green

# Step 1: Admin Login
Write-Host "`n1. Admin Login..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -Headers $headers
    $token = $loginResponse.accessToken
    $authHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $token"
    }
    Write-Host "✓ Admin login successful" -ForegroundColor Green
    Write-Host "User: $($loginResponse.userInfo.username)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: View Student Registrations by Student Number
Write-Host "`n2. Viewing student registrations..." -ForegroundColor Yellow
$studentNumber = "CS/2020/001"  # Change this to an existing student number

try {
    $studentRegistrations = Invoke-RestMethod -Uri "$baseUrl/students/course-registrations/student/$studentNumber" -Method GET -Headers $authHeaders
    Write-Host "✓ Found $($studentRegistrations.Count) registrations for student $studentNumber" -ForegroundColor Green
    
    foreach ($registration in $studentRegistrations) {
        Write-Host "- $($registration.courseCode): $($registration.courseTitle)" -ForegroundColor Cyan
        Write-Host "  Credits: $($registration.credits), Department: $($registration.department)" -ForegroundColor White
        Write-Host "  Registered: $($registration.registeredAt)" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Failed to get student registrations: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Message -like "*404*") {
        Write-Host "Student $studentNumber not found or has no registrations" -ForegroundColor Yellow
    }
}

Write-Host "`n=== Admin Test Complete ===" -ForegroundColor Green
