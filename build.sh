#!/bin/bash
# ═══════════════════════════════════════════════════════════════
#  Phoenix Car Hub — One-Click APK Builder (Linux / macOS)
#  Run:  chmod +x build.sh && ./build.sh
# ═══════════════════════════════════════════════════════════════

set -e
CYAN='\033[0;36m'; GREEN='\033[0;32m'; RED='\033[0;31m'; NC='\033[0m'
log()  { echo -e "${CYAN}[Phoenix]${NC} $1"; }
ok()   { echo -e "${GREEN}[✓]${NC} $1"; }
fail() { echo -e "${RED}[✗]${NC} $1"; exit 1; }

SDK_DIR="$HOME/android-sdk-phoenix"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
GRADLE_VERSION="8.6"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

# ─── 1. Check Java 17+ ──────────────────────────────────────────
log "Checking Java..."
if ! command -v java &>/dev/null; then
    fail "Java not found. Install JDK 17: https://adoptium.net"
fi
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
[ "$JAVA_VER" -ge 17 ] 2>/dev/null || fail "Java 17+ required (found $JAVA_VER). Install from: https://adoptium.net"
ok "Java $JAVA_VER found"

# ─── 2. Download Android SDK ────────────────────────────────────
if [ ! -d "$SDK_DIR/cmdline-tools/latest/bin" ]; then
    log "Downloading Android SDK command-line tools..."
    mkdir -p "$SDK_DIR/cmdline-tools"
    curl -L "$CMDLINE_TOOLS_URL" -o /tmp/cmdline-tools.zip
    unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools-tmp
    mv /tmp/cmdline-tools-tmp/cmdline-tools "$SDK_DIR/cmdline-tools/latest"
    rm -rf /tmp/cmdline-tools-tmp /tmp/cmdline-tools.zip
    ok "SDK tools downloaded"
fi

export ANDROID_HOME="$SDK_DIR"
export ANDROID_SDK_ROOT="$SDK_DIR"
export PATH="$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/platform-tools:$PATH"

# ─── 3. Accept licenses & install SDK components ───────────────
if [ ! -d "$SDK_DIR/platforms/android-34" ]; then
    log "Installing Android SDK platform 34 and build tools..."
    yes | sdkmanager --licenses > /dev/null 2>&1 || true
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
    ok "Android SDK installed"
fi

# ─── 4. Download Gradle wrapper (if not cached) ────────────────
if [ ! -f "$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin/*/gradle-${GRADLE_VERSION}/bin/gradle" ] 2>/dev/null; then
    log "Gradle wrapper will be auto-downloaded on first build..."
fi

# ─── 5. Build APK ───────────────────────────────────────────────
log "Building Phoenix Car Hub debug APK..."
cd "$(dirname "$0")"

export JAVA_HOME="${JAVA_HOME:-$(java -XshowSettings:property -version 2>&1 | grep 'java.home' | awk '{print $3}')}"

chmod +x gradlew
./gradlew assembleDebug \
    -Pandroid.sdk.dir="$SDK_DIR" \
    --no-daemon \
    --stacktrace 2>&1 | tee build.log

APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    SIZE=$(du -sh "$APK" | cut -f1)
    ok "APK built successfully!"
    echo ""
    echo -e "${GREEN}════════════════════════════════════════${NC}"
    echo -e "${GREEN}  APK ready: $APK${NC}"
    echo -e "${GREEN}  Size: $SIZE${NC}"
    echo -e "${GREEN}════════════════════════════════════════${NC}"
    echo ""
    echo "Install on connected Android device:"
    echo "  adb install $APK"
else
    fail "Build failed. Check build.log for details."
fi
