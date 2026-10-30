@echo off
rem Creates the Moodle submission files in the "submission" folder next to this project:
rem   <ID1>_<ID2>_SortingAssignment.zip  - source code, README, sample data (no .class files)
rem   SortingAlgorithmAnalyzer.jar       - runnable application (java -jar SortingAlgorithmAnalyzer.jar)
rem Usage: package.bat T0XXXXX T0YYYYY
setlocal
if "%~2"=="" (
    echo Usage: package.bat STUDENT1_ID STUDENT2_ID
    exit /b 1
)
cd /d "%~dp0"
call "%~dp0build.bat" || exit /b 1

set OUT=%~dp0..\submission
if not exist "%OUT%" mkdir "%OUT%"
set ZIP=%OUT%\%~1_%~2_SortingAssignment.zip
if exist "%ZIP%" del "%ZIP%"

jar --create --file "%OUT%\SortingAlgorithmAnalyzer.jar" --main-class com.sortingapp.Main -C bin .
if errorlevel 1 exit /b 1

cd ..
tar -a -c -f "%ZIP%" --exclude "bin" --exclude "bin-test" --exclude "*.class" SortingAlgorithmAnalyzer
if errorlevel 1 exit /b 1

echo.
echo Created:
echo   %ZIP%
echo   %OUT%\SortingAlgorithmAnalyzer.jar
