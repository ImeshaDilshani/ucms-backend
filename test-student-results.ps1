# Test script for Student Results System
# Tests student functionality to view released results

$baseUrl = "http://localhost:8080/api/v1"
$headers = @{"Content-Type" = "application/json"}

Write-Host "=== Testing Student Results System ===" -ForegroundColor Green

# Step 1: Student Login
Write-Host "`n1. Student login..." -ForegroundColor Yellow
$studentLoginBody = @{
    username = "student001"
    password = "password123"
} | ConvertTo-Json

try {
    $studentLoginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $studentLoginBody -Headers $headers
    $studentToken = $studentLoginResponse.accessToken
    $studentHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $studentToken"
    }
    Write-Host "✓ Student login successful" -ForegroundColor Green
    Write-Host "User: $($studentLoginResponse.userInfo.username)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Student login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Make sure student001 exists or run the course registration test first" -ForegroundColor Yellow
    exit 1
}

# Step 2: Get My Results
Write-Host "`n2. Getting my results..." -ForegroundColor Yellow
try {
    $myResults = Invoke-RestMethod -Uri "$baseUrl/students/results/my-results" -Method GET -Headers $studentHeaders
    Write-Host "✓ Found $($myResults.Count) released results" -ForegroundColor Green
    
    if ($myResults.Count -eq 0) {
        Write-Host "No results released yet. Run the lecturer grading test first." -ForegroundColor Yellow
    } else {
        Write-Host "`nMy Course Results:" -ForegroundColor Cyan
        foreach ($result in $myResults) {
            Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray
            Write-Host "Course: $($result.courseCode) - $($result.courseName)" -ForegroundColor White
            Write-Host "Credits: $($result.credits)" -ForegroundColor White
            Write-Host "Marks: $($result.marks)%" -ForegroundColor Yellow
            Write-Host "Grade: $($result.gradeDisplay)" -ForegroundColor Green
            if ($result.remarks) {
                Write-Host "Remarks: $($result.remarks)" -ForegroundColor Cyan
            }
            Write-Host "Lecturer: $($result.lecturerName)" -ForegroundColor Gray
            Write-Host "Released: $($result.releasedAt)" -ForegroundColor Gray
        }
        Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray
    }
} catch {
    Write-Host "✗ Failed to get student results: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Student Results Test Complete ===" -ForegroundColor Green
