@echo off
echo Compiling CakeLicious...

REM Create build directory if it doesn't exist
if not exist "build" mkdir build

REM Compile Java files
javac -d build -cp "lib/mysql-connector-j-8.0.33.jar;." src/com/cakelicious/database/*.java src/com/cakelicious/models/*.java src/com/cakelicious/services/*.java src/com/cakelicious/handlers/*.java src/com/cakelicious/Server.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the server, execute:
    echo java -cp "build;lib/mysql-connector-j-8.0.33.jar" com.cakelicious.Server
) else (
    echo Compilation failed!
)

pause