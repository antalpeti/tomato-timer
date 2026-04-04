@echo off
:: ============================================================
::  TomatoTimer.bat  --  Windows launcher for the fat JAR
::
::  Usage:
::    TomatoTimer.bat             -- launch the app
::    TomatoTimer.bat --dry-run   -- verify environment only
::
::  The script locates the newest target\*-fat.jar, checks
::  that javaw is on the PATH, then starts the app without a
::  console window via "start".
::
::  Double-click use: prefer TomatoTimer.vbs which starts
::  this batch hidden so no console flickers on screen.
:: ============================================================
setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "TARGET_DIR=%SCRIPT_DIR%target"
set "DRY_RUN=0"

if /i "%~1"=="--dry-run" set "DRY_RUN=1"

:: ------------------------------------------------------------
:: 1. Locate newest fat JAR inside target\
:: ------------------------------------------------------------
set "JAR_FILE="
for /f "delims=" %%F in ('dir /b /o-d "%TARGET_DIR%\*-fat.jar" 2^>nul') do (
    if not defined JAR_FILE (
        set "JAR_FILE=%TARGET_DIR%\%%F"
    )
)

if not defined JAR_FILE (
    echo [ERROR] No fat JAR found in "%TARGET_DIR%".
    echo         Build the project first with:  mvn package
    exit /b 1
)

:: ------------------------------------------------------------
:: 2. Locate javaw on the PATH
:: ------------------------------------------------------------
set "JAVAW_EXE="
for /f "delims=" %%X in ('where javaw 2^>nul') do (
    if not defined JAVAW_EXE (
        set "JAVAW_EXE=%%X"
    )
)

if not defined JAVAW_EXE (
    echo [ERROR] javaw not found on PATH.
    echo         Install Java 21+ and make sure its bin\ is on PATH.
    echo         Verify with:  where javaw
    exit /b 2
)

:: ------------------------------------------------------------
:: 3. Dry-run: print diagnostics and exit without launching
:: ------------------------------------------------------------
if "%DRY_RUN%"=="1" (
    echo [DRY-RUN] JAR    : %JAR_FILE%
    echo [DRY-RUN] JAVAW  : %JAVAW_EXE%
    echo [DRY-RUN] Command: "%JAVAW_EXE%" -jar "%JAR_FILE%"
    echo [DRY-RUN] OK -- environment looks good, no process was started.
    exit /b 0
)

:: ------------------------------------------------------------
:: 4. Launch -- "start" detaches the process so this batch
::    exits immediately; javaw runs without a console window.
:: ------------------------------------------------------------
echo Launching TomatoTimer ...
echo JAR   : %JAR_FILE%
echo JAVAW : %JAVAW_EXE%

start "" "%JAVAW_EXE%" -jar "%JAR_FILE%"

endlocal
exit /b 0

