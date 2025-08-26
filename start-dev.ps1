# ===================================
# CRM System - Development Mode (H2)
# ===================================

Write-Host "Launch Develop/Debug verison of crm-system" -ForegroundColor Green
Write-Host "   - DB: H2 (in-memory)" -ForegroundColor Yellow
Write-Host "   - H2 Console: http://localhost:8080/h2-console" -ForegroundColor Yellow
Write-Host "   - API URL: http://localhost:8080/api" -ForegroundColor Yellow
Write-Host "   - Profile: h2" -ForegroundColor Yellow
Write-Host ""

if ($env:SPRING_PROFILES_ACTIVE) {
    Remove-Item Env:SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue
}

Write-Host "Launching..." -ForegroundColor Cyan
.\gradlew.bat bootRun --args='--spring.profiles.active=h2'

Write-Host ""
Write-Host "crm-system stopped" -ForegroundColor Red
