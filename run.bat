@echo off
title User Management System
cd /d "%~dp0"

set CP=lib\sqlite-jdbc.jar;lib\slf4j-api-2.0.9.jar;lib\slf4j-nop-2.0.9.jar

:: Download missing jars
if not exist "lib\sqlite-jdbc.jar" (
    echo Downloading sqlite-jdbc...
    powershell -Command "Invoke-WebRequest 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar' -OutFile 'lib\sqlite-jdbc.jar'"
)
if not exist "lib\slf4j-api-2.0.9.jar" (
    echo Downloading slf4j-api...
    powershell -Command "Invoke-WebRequest 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar' -OutFile 'lib\slf4j-api-2.0.9.jar'"
)
if not exist "lib\slf4j-nop-2.0.9.jar" (
    echo Downloading slf4j-nop...
    powershell -Command "Invoke-WebRequest 'https://repo1.maven.org/maven2/org/slf4j/slf4j-nop/2.0.9/slf4j-nop-2.0.9.jar' -OutFile 'lib\slf4j-nop-2.0.9.jar'"
)

echo ========================================
echo   User Management System
echo ========================================

:: Compile
echo Compiling...
if not exist "out" mkdir out
javac -encoding UTF-8 -cp "%CP%" -d out src\Main.java src\db\Database.java src\model\User.java src\dao\UserDAO.java src\ui\LoginFrame.java src\ui\MainFrame.java src\ui\UserDialog.java
if %errorlevel% neq 0 (
    echo COMPILE ERROR!
    pause
    exit /b 1
)
echo Compile OK.

:: Run
echo Starting...
java -cp "out;%CP%" Main
pause
