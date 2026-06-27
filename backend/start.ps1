# User Management System - 一键启动脚本
# 用法: 右键 → "使用 PowerShell 运行"

$projectDir = "C:\Users\32668\dev\UserManagement_Spring"
Set-Location $projectDir

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  User Management System" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Start MySQL if not running
Write-Host "`n[1/3] Checking MySQL..." -ForegroundColor Yellow
$env:Path += ";C:\Program Files\MySQL\MySQL Server 8.4\bin"
$mysqlOk = $false
try {
    mysql -u root -proot -e "SELECT 1" 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  MySQL: Running" -ForegroundColor Green
        $mysqlOk = $true
    }
} catch {}

if (-not $mysqlOk) {
    Write-Host "  MySQL: Starting..." -ForegroundColor Yellow
    Start-Process "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" -ArgumentList "--datadir=D:\MySQL_Data" -WindowStyle Hidden
    Start-Sleep 4
    try {
        mysql -u root -proot -e "SELECT 1" 2>$null | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  MySQL: Started OK" -ForegroundColor Green
        }
    } catch {
        Write-Host "  MySQL: FAILED! Run this as Administrator to install service" -ForegroundColor Red
        pause
        exit 1
    }
}

# 2. Find Maven
Write-Host "`n[2/3] Finding Maven..." -ForegroundColor Yellow
$mvn = $null
$ideaMvn = "D:\IntelliJ IDEA Community Edition\plugins\maven\lib\maven3\bin\mvn.cmd"
if (Test-Path $ideaMvn) {
    $mvn = $ideaMvn
    Write-Host "  Using IDEA's Maven" -ForegroundColor Green
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvn = "mvn"
    Write-Host "  Using system Maven" -ForegroundColor Green
} else {
    Write-Host "  Maven not found. Please open this project in IDEA first to download dependencies." -ForegroundColor Red
    Write-Host "  Then run this script again." -ForegroundColor Red
    pause
    exit 1
}

# 3. Build and run
Write-Host "`n[3/3] Building and starting..." -ForegroundColor Yellow
& $mvn -f "$projectDir\pom.xml" spring-boot:run -q 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "`n  Build failed. Trying to download dependencies first..." -ForegroundColor Red
    & $mvn -f "$projectDir\pom.xml" dependency:resolve 2>&1
    Write-Host "`n  Now try opening the project in IDEA and running UserManagementApplication.java" -ForegroundColor Yellow
}
pause
