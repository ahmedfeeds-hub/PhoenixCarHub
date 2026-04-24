# How to Build Phoenix Car Hub APK

## ⚡ Fastest Method: Android Studio (Recommended, ~5 minutes)

### Step 1 — Install Android Studio
Download from: https://developer.android.com/studio  
Install with default settings (it includes JDK + Android SDK automatically).

### Step 2 — Open the Project
1. Open Android Studio
2. Click **"Open"**
3. Navigate to and select the **PhoenixCarHub** folder
4. Click **OK** — Android Studio will sync Gradle automatically

### Step 3 — Wait for Sync (~2-3 min)
- You'll see "Gradle sync in progress…" at the bottom
- It downloads the Gradle wrapper jar and all dependencies automatically
- Wait until it shows "BUILD SUCCESSFUL" in the status bar

### Step 4 — Build the APK
- Go to menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
- Wait ~2 minutes for compilation
- A notification will appear: **"APK(s) generated successfully"**
- Click **"locate"** in the notification to find the APK

### APK Location
```
PhoenixCarHub/app/build/outputs/apk/debug/app-debug.apk
```

### Install on your Android device
- Enable **Developer Options** on your phone:
  - Settings → About Phone → tap "Build Number" 7 times
- Enable **USB Debugging** in Developer Options
- Connect phone via USB cable
- In Android Studio click the **▶ Run** button (or press Shift+F10)

---

## 🖥️ Command Line Method (Linux / macOS)

**Prerequisites:** JDK 17+ installed

```bash
# 1. Make build script executable
chmod +x build.sh

# 2. Run it (downloads SDK automatically)
./build.sh
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🪟 Command Line Method (Windows)

**Prerequisites:** JDK 17+ installed from https://adoptium.net

```bat
REM Double-click build.bat
REM OR run in Command Prompt:
build.bat
```

---

## 🐳 Docker Method (Any OS, no Android Studio needed)

```bash
# Pull pre-configured Android build image
docker run --rm \
  -v "$(pwd)":/project \
  -w /project \
  mingc/android-build-box:latest \
  bash -c "chmod +x gradlew && ./gradlew assembleDebug"
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Installing the APK on Your Car's Android Device

### Method 1: ADB (USB)
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Method 2: File Transfer
1. Copy `app-debug.apk` to your phone via USB/Google Drive/WhatsApp
2. On the Android device: **Settings → Install unknown apps → Allow**
3. Open the APK file on the device to install

### Method 3: Direct Wi-Fi ADB
```bash
# Connect device and phone to same Wi-Fi
adb connect <DEVICE_IP>:5555
adb install app-debug.apk
```

---

## API Keys in This Build

| Service | Key | Status |
|---------|-----|--------|
| Google Maps | `AIzaSyCxUT--lTfPC...` | ✅ Configured |
| Weather (Open-Meteo) | None needed | ✅ Free, no key |

**Both are already embedded in the source — no changes needed.**
