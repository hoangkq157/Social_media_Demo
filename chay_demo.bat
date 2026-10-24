@echo off
chcp 65001 >nul
title SpringBook - Demo nhom 2
cd /d "%~dp0"
if exist "C:\lab-tools\jdk21\bin\java.exe" (
  set "JAVA_HOME=C:\lab-tools\jdk21"
) else (
  where java >nul 2>&1
  if errorlevel 1 (
    echo [!] Khong tim thay Java. Vui long cai JDK 21+ hoac copy vao C:\lab-tools\jdk21.
    pause & exit /b 1
  )
)
echo ============================================
echo   DANG BAT UNG DUNG SpringBook (demo Nhom 2)
echo   Sau khi thay dong: Tomcat started on port 8080
echo   - Mo trinh duyet:  http://localhost:8080
echo   - Tai khoan: an / binh / chi  -  mat khau 123456
echo   - Xem CSDL:       http://localhost:8080/h2-console
echo   - TAT DEMO: nhan Ctrl+C (hoac dong cua so nay)
echo ============================================
call mvnw.cmd spring-boot:run
pause
