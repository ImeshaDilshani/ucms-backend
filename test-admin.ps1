# Test script for admin registration and login
Write-Host "Testing admin registration..." -ForegroundColor Yellow

# Test admin registration
$adminRegBody = @{
    username = "testadmin"
    email = "testadmin@example.com"
    password = "testadmin123"
    adminKey = "ADMIN_SECRET_KEY_2025"
} | ConvertTo-Json

try {
    $regResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register/admin" `
        -Method Post `
        -ContentType "application/json" `
        -Body $adminRegBody
    
    Write-Host "Admin registration successful!" -ForegroundColor Green
    Write-Host "Response: $($regResponse | ConvertTo-Json)"
    
    # Test admin login
    Write-Host "`nTesting admin login..." -ForegroundColor Yellow
    
    $loginBody = @{
        username = "testadmin"
        password = "testadmin123"
    } | ConvertTo-Json
    
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody
    
    Write-Host "Admin login successful!" -ForegroundColor Green
    Write-Host "Response: $($loginResponse | ConvertTo-Json)"
    
} catch {
    Write-Host "Error occurred: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

# Test default admin login
Write-Host "`nTesting default admin login..." -ForegroundColor Yellow

$defaultLoginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $defaultLoginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $defaultLoginBody
    
    Write-Host "Default admin login successful!" -ForegroundColor Green
    Write-Host "Response: $($defaultLoginResponse | ConvertTo-Json)"
    
} catch {
    Write-Host "Error occurred: $($_.Exception.Message)" -ForegroundColor Red
}
