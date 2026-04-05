@echo off
:: ============================================================
::  BuildTomatoTimerExe.bat  --  jpackage app-image builder
::
::  Usage:
::    BuildTomatoTimerExe.bat             -- build the app-image
::    BuildTomatoTimerExe.bat --dry-run   -- print paths and
::                                           jpackage command,
::                                           do not build
::
::  Produces:
::    target\exe-image\TomatoTimer\TomatoTimer.exe
::
::  Requirements:
::    * JDK 14+  (jpackage is bundled with the JDK)
::    * A fat JAR must exist under target\  (run: mvn package)
::
::  The EXE produced here is a good candidate to pin to the
::  Windows Taskbar: right-click TomatoTimer.exe -> Pin to taskbar.
:: ============================================================
setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "TARGET_DIR=%SCRIPT_DIR%target"
set "DEST_DIR=%TARGET_DIR%\exe-image"
set "APP_NAME=TomatoTimer"
set "MAIN_CLASS=com.tomatotimer.Launcher"
set "DRY_RUN=0"

if /i "%~1"=="--dry-run" set "DRY_RUN=1"

:: ------------------------------------------------------------
:: 1. Locate newest fat JAR inside target\
:: ------------------------------------------------------------
set "JAR_FILE="
set "JAR_NAME="
for /f "delims=" %%F in ('dir /b /o-d "%TARGET_DIR%\*-fat.jar" 2^>nul') do (
    if not defined JAR_FILE (
        set "JAR_FILE=%TARGET_DIR%\%%F"
        set "JAR_NAME=%%F"
    )
)

if not defined JAR_FILE (
    echo [ERROR] No fat JAR found in "%TARGET_DIR%".
    echo         Build the project first with:  mvn package
    exit /b 1
)

:: ------------------------------------------------------------
:: 2. Locate jpackage on the PATH
:: ------------------------------------------------------------
set "JPACKAGE_EXE="
for /f "delims=" %%X in ('where jpackage 2^>nul') do (
    if not defined JPACKAGE_EXE (
        set "JPACKAGE_EXE=%%X"
    )
)

if not defined JPACKAGE_EXE (
    echo [ERROR] jpackage not found on PATH.
    echo         jpackage ships with JDK 14+.
    echo         Make sure the JDK bin\ directory is on PATH.
    echo         Verify with:  where jpackage
    exit /b 2
)

:: ------------------------------------------------------------
:: 3. Resolve final EXE path (for display / dry-run)
:: ------------------------------------------------------------
set "EXE_PATH=%DEST_DIR%\%APP_NAME%\%APP_NAME%.exe"

set JPACKAGE_CMD="%JPACKAGE_EXE%" ^
    --type app-image ^
    --name %APP_NAME% ^
    --input "%TARGET_DIR%" ^
    --main-jar "%JAR_NAME%" ^
    --main-class %MAIN_CLASS% ^
    --dest "%DEST_DIR%"

:: ------------------------------------------------------------
:: 4. Dry-run: print diagnostics and exit without building
:: ------------------------------------------------------------
if "%DRY_RUN%"=="1" (
    echo [DRY-RUN] JAR        : %JAR_FILE%
    echo [DRY-RUN] JAR name   : %JAR_NAME%
    echo [DRY-RUN] JPACKAGE   : %JPACKAGE_EXE%
    echo [DRY-RUN] DEST       : %DEST_DIR%
    echo [DRY-RUN] Output EXE : %EXE_PATH%
    echo [DRY-RUN] Command:
    echo           "%JPACKAGE_EXE%"
    echo               --type app-image
    echo               --name %APP_NAME%
    echo               --input "%TARGET_DIR%"
    echo               --main-jar "%JAR_NAME%"
    echo               --main-class %MAIN_CLASS%
    echo               --dest "%DEST_DIR%"
    echo [DRY-RUN] OK -- environment looks good, no build was started.
    exit /b 0
)

:: ------------------------------------------------------------
:: 5. Remove previous app-image output (best-effort)
:: ------------------------------------------------------------
if exist "%DEST_DIR%\%APP_NAME%" (
    echo Removing previous app-image: "%DEST_DIR%\%APP_NAME%" ...
    rmdir /s /q "%DEST_DIR%\%APP_NAME%" 2>nul
)

:: ------------------------------------------------------------
:: 6. Build the app-image with jpackage
:: ------------------------------------------------------------
echo Building TomatoTimer app-image ...
echo JAR      : %JAR_FILE%
echo JPACKAGE : %JPACKAGE_EXE%
echo DEST     : %DEST_DIR%
echo.

"%JPACKAGE_EXE%" ^
    --type app-image ^
    --name %APP_NAME% ^
    --input "%TARGET_DIR%" ^
    --main-jar "%JAR_NAME%" ^
    --main-class %MAIN_CLASS% ^
    --dest "%DEST_DIR%"

if errorlevel 1 (
    echo [ERROR] jpackage failed. See output above for details.
    exit /b 3
)

:: ------------------------------------------------------------
:: 7. Done
:: ------------------------------------------------------------
echo.
echo [OK] Build succeeded.
echo      EXE: %EXE_PATH%
echo.
echo Tip: right-click TomatoTimer.exe -^> "Pin to taskbar" for quick access.

endlocal
exit /b 0

