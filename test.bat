@echo off
rem Compiles the application and the tests, then runs all tests.
cd /d "%~dp0"
call "%~dp0build.bat" || exit /b 1
if exist bin-test rmdir /s /q bin-test
javac --release 11 -encoding UTF-8 -cp bin -d bin-test -sourcepath src\test\java src\test\java\com\sortingapp\TestRunner.java
if errorlevel 1 (
    echo Test build FAILED.
    exit /b 1
)
java -cp "bin;bin-test" com.sortingapp.TestRunner
