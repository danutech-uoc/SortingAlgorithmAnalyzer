@echo off
rem Compiles the application into the "bin" folder.
cd /d "%~dp0"
if exist bin rmdir /s /q bin
javac --release 11 -encoding UTF-8 -d bin -sourcepath src\main\java src\main\java\com\sortingapp\Main.java
if errorlevel 1 (
    echo Build FAILED.
    exit /b 1
)
echo Build successful. Run the application with run.bat
