@echo off
rem Builds (if needed) and starts the application.
cd /d "%~dp0"
if not exist bin\com\sortingapp\Main.class call "%~dp0build.bat" || exit /b 1
java -cp bin com.sortingapp.Main
