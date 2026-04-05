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
::
::  FIX NOTES (why a staging dir is required)
::  ------------------------------------------
::  jpackage copies every file in --input into the app\ bundle.
::  When --dest is a *subdirectory* of --input (e.g. target\exe-image
::  inside target\) jpackage walks into its own output while scanning,
::  producing a circular copy.  On Windows this causes the root-level
::  JAR files to be silently omitted from app\, so the EXE launcher
::  cannot find its classpath and fails immediately.
::
::  The fix is to copy only the fat JAR into a dedicated staging
::  directory (target\jpackage-input\) and use *that* as --input.
::  The staging dir is a sibling of exe-image, never nested inside it.
::  This also prevents the thin JAR from appearing in --input, which
::  would otherwise create a duplicate app.classpath= entry in the
::  generated TomatoTimer.cfg.
:: ============================================================
setlocal EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "TARGET_DIR=%SCRIPT_DIR%target"
set "DEST_DIR=%TARGET_DIR%\exe-image"
:: Staging dir: sibling of exe-image, never nested inside DEST_DIR
set "STAGING_DIR=%TARGET_DIR%\jpackage-input"
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

:: ------------------------------------------------------------
:: 4. Dry-run: print diagnostics and exit without building
:: ------------------------------------------------------------
if "%DRY_RUN%"=="1" (
    echo [DRY-RUN] JAR        : %JAR_FILE%
    echo [DRY-RUN] JAR name   : %JAR_NAME%
    echo [DRY-RUN] JPACKAGE   : %JPACKAGE_EXE%
    echo [DRY-RUN] STAGING    : %STAGING_DIR%  ^(fat JAR only^)
    echo [DRY-RUN] DEST       : %DEST_DIR%
    echo [DRY-RUN] Output EXE : %EXE_PATH%
    echo [DRY-RUN] Command:
    echo           "%JPACKAGE_EXE%"
    echo               --type app-image
    echo               --name %APP_NAME%
    echo               --input "%STAGING_DIR%"
    echo               --main-jar "%JAR_NAME%"
    echo               --main-class %MAIN_CLASS%
    echo               --dest "%DEST_DIR%"
    echo               --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED"
    echo               --java-options "--add-opens=java.base/java.io=ALL-UNNAMED"
    echo [DRY-RUN] Note: = ^(not space^) in --add-opens is required -- jpackage splits
    echo [DRY-RUN]       space-separated java-options values into broken cfg lines.
    echo [DRY-RUN] Note: jpackage auto-injects -Djpackage.app-version from --app-version.
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
:: 6. Prepare staging directory – copy ONLY the fat JAR
::    (prevents duplicate app.classpath= in TomatoTimer.cfg and
::     avoids copying the Maven build output tree into the bundle)
:: ------------------------------------------------------------
echo Preparing staging directory ...
if exist "%STAGING_DIR%" rmdir /s /q "%STAGING_DIR%" 2>nul
mkdir "%STAGING_DIR%"

echo Copying fat JAR to staging: %JAR_NAME% ...
copy /y "%JAR_FILE%" "%STAGING_DIR%\%JAR_NAME%" >nul
if errorlevel 1 (
    echo [ERROR] Failed to copy fat JAR to staging directory "%STAGING_DIR%".
    rmdir /s /q "%STAGING_DIR%" 2>nul
    exit /b 3
)

:: ------------------------------------------------------------
:: 7. Build the app-image with jpackage
:: ------------------------------------------------------------
echo Building TomatoTimer app-image ...
echo JAR      : %JAR_FILE%
echo JPACKAGE : %JPACKAGE_EXE%
echo STAGING  : %STAGING_DIR%
echo DEST     : %DEST_DIR%
echo.

:: IMPORTANT – use = (not a space) between --add-opens and its argument.
::
:: jpackage on JDK 21.x (and earlier) splits the value of --java-options on
:: every space when writing TomatoTimer.cfg.  A space-separated form like
::   --java-options "--add-opens java.base/java.lang=ALL-UNNAMED"
:: produces two *separate* lines in the cfg:
::   java-options=--add-opens                      <- value-less: JVM rejects this
::   java-options=java.base/java.lang=ALL-UNNAMED  <- unrecognised: ignored
:: The JVM's JNI invocation API treats each java-options line as one standalone
:: argument, so it never reassembles the two-token form and aborts with:
::   Error: --add-opens requires a <module>/<package>=<target-module> specification
::
:: The single-token = form is accepted by all JDKs >= 9 and is never split:
::   --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED"
:: produces the correct single cfg line:
::   java-options=--add-opens=java.base/java.lang=ALL-UNNAMED
"%JPACKAGE_EXE%" ^
    --type app-image ^
    --name %APP_NAME% ^
    --input "%STAGING_DIR%" ^
    --main-jar "%JAR_NAME%" ^
    --main-class %MAIN_CLASS% ^
    --dest "%DEST_DIR%" ^
    --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" ^
    --java-options "--add-opens=java.base/java.io=ALL-UNNAMED"

set "JPACKAGE_EXIT=%errorlevel%"

:: ------------------------------------------------------------
:: 8. Clean up staging directory (always, even on failure)
:: ------------------------------------------------------------
rmdir /s /q "%STAGING_DIR%" 2>nul

if "%JPACKAGE_EXIT%" NEQ "0" (
    echo [ERROR] jpackage failed ^(exit code %JPACKAGE_EXIT%^). See output above for details.
    exit /b 4
)

:: ------------------------------------------------------------
:: 9. Done
:: ------------------------------------------------------------
echo.
echo [OK] Build succeeded.
echo      EXE: %EXE_PATH%
echo.
echo Tip: right-click TomatoTimer.exe -^> "Pin to taskbar" for quick access.

endlocal
exit /b 0

