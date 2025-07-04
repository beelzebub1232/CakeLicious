@echo off
echo Starting CakeLicious Server...
echo.

REM Check if build directory exists
if not exist "build" (
    echo Build directory not found. Please compile first using compile.bat
    pause
    exit /b 1
)

REM Start the server
java -cp "build;lib/mysql-connector-j-8.0.33.jar" com.cakelicious.Server

pause