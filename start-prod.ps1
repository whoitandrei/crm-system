# ===================================
# CRM System - Production Mode (Postgres)
# ===================================

Write-Host "Launch Production version of crm-system" -ForegroundColor Green
Write-Host "   - Postgres DB" -ForegroundColor Yellow
Write-Host "   - API URL: http://localhost:8080/api" -ForegroundColor Yellow
Write-Host "   - Profile: postgres" -ForegroundColor Yellow
Write-Host ""

if ($env:SPRING_PROFILES_ACTIVE) {
    Remove-Item Env:SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue
}

Write-Host "Starting Docker containers..." -ForegroundColor Cyan
& docker-compose up -d

$originalTitle = $host.UI.RawUI.WindowTitle
$host.UI.RawUI.WindowTitle = "CRM System - Press Ctrl+C to stop"

try {
    Write-Host "Launching Spring Boot application..." -ForegroundColor Cyan
    $process = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "bootRun --args='--spring.profiles.active=postgres'" -PassThru -NoNewWindow

    Wait-Process -Id $process.Id

} catch {
    Write-Host "Application interrupted" -ForegroundColor Yellow
} finally {
    Write-Host "Stopping Docker containers..." -ForegroundColor Cyan
    & docker-compose down

    $host.UI.RawUI.WindowTitle = $originalTitle
    Write-Host "crm-system stopped" -ForegroundColor Red
    Write-Host "Note: Data preserved. To delete data run: docker-compose down -v" -ForegroundColor Yellow
}