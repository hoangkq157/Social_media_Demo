@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
title SpringBook - Tat demo nhom 2
cd /d "%~dp0"

echo ============================================
echo   DANG TAT UNG DUNG SpringBook (demo Nhom 2)
echo   Tim va tat process dang giu port 8080
echo ============================================
echo.

set "FOUND=0"
set "PREV="
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /R /C:":8080 .*LISTENING"') do (
    if not "%%p"=="!PREV!" (
        set "FOUND=1"
        echo Tim thay process PID %%p dang giu port 8080 - dang tat...
        taskkill /F /PID %%p >nul 2>&1
    )
    set "PREV=%%p"
)

if "%FOUND%"=="0" (
    echo [i] Khong co process nao dang chay tren port 8080.
    echo     Ung dung da tat hoac chua duoc bat.
) else (
    %SystemRoot%\System32\ping.exe -n 3 -w 1000 127.0.0.1 >nul
    netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul 2>&1
    if errorlevel 1 (
        echo [OK] Da tat ung dung. Port 8080 da duoc giai phong.
    ) else (
        echo [!] Van con process tren port 8080 - chay lai file nay mot lan nua.
    )
)

echo.
pause
