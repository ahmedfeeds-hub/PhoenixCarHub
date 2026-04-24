@echo off
REM ═══════════════════════════════════════════════════════════════
REM  Phoenix Car Hub — One-Click APK Builder (Windows)
REM  Double-click or run:  build.bat
REM ═══════════════════════════════════════════════════════════════

setlocal EnableDelayedExpansion

set SDK_DIR=%USERPROFILE%\android-sdk-phoenix
set CMDLINE_URL=https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip

echo.
echo  ██████  ██   ██  ██████  ███████  ███    ██  ██ ██   ██
echo  ██   ██ ██   ██ ██    ██ ██       ████   ██  ██  ██ ██
echo  ██████  ███████ ██    ██ █████    ██ ██  ██  ██   ███
echo  ██      ██   ██ ██    ██ ██       ██  ██ ██  ██  ██ ██
echo  ██      ██   ██  ██████  ███████  ██   ████  ██ ██   ██
echo.
echo  Car Control Hub — APK Builder
echo ────────────────────────────────────────────────────────────
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. Download JDK 17 from:
    echo         https://adoptium.net/temurin/releases/?version=17
    pause & exit /b 1
)
echo [OK] Java found

REM Set ANDROID_HOME
set ANDROID_HOME=%SDK_DIR%
set ANDROID_SDK_ROOT=%SDK_DIR%

REM Download SDK if needed
if not exist "%SDK_DIR%\cmdline-tools\latest\bin\sdkmanager.bat" (
    echo [INFO] Downloading Android SDK command-line tools...
    mkdir "%SDK_DIR%\cmdline-tools" 2>nul
    powershell -Command "Invoke-WebRequest -Uri '%CMDLINE_URL%' -OutFile '%TEMP%\cmdline-tools.zip'"
    powershell -Command "Expand-Archive -Path '%TEMP%\cmdline-tools.zip' -DestinationPath '%TEMP%\sdk-tmp' -Force"
    move "%TEMP%\sdk-tmp\cmdline-tools" "%SDK_DIR%\cmdline-tools\latest"
    echo [OK] SDK tools downloaded
)

REM Install SDK components
if not exist "%SDK_DIR%\platforms\android-34" (
    echo [INFO] Installing Android SDK Platform 34...
    echo y | "%SDK_DIR%\cmdline-tools\latest\bin\sdkmanager.bat" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
    echo [OK] Android SDK installed
)

REM Build
echo.
echo [BUILD] Building Phoenix Car Hub debug APK...
echo.

call gradlew.bat assembleDebug --no-daemon 2>&1

set APK=app\build\outputs\apk\debug\app-debug.apk
if exist "%APK%" (
    echo.
    echo ════════════════════════════════════════════════════════
    echo   SUCCESS! APK is ready:
    echo   %CD%\%APK%
    echo ════════════════════════════════════════════════════════
    echo.
    echo   Install on connected Android device:
    echo   adb install %APK%
    echo.
    explorer app\build\outputs\apk\debug
) else (
    echo.
    echo [ERROR] Build failed. Check the output above.
)

pause
