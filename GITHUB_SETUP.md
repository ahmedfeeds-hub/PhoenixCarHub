# Build APK Using GitHub Actions (Cloud-Based — No Installation Needed)

This guide shows you how to automatically build the APK using GitHub's free cloud servers. **No need to install Android Studio or Java!**

---

## Step-by-Step Instructions

### Step 1: Create a Free GitHub Account (2 minutes)
1. Go to: **https://github.com/signup**
2. Enter email, create password, choose username
3. Verify your email
4. **Done!**

---

### Step 2: Create a New Repository

1. Log in to GitHub
2. Click **+** (top right) → **New repository**
3. Fill in:
   - **Repository name:** `PhoenixCarHub`
   - **Description:** (optional) "Phoenix Car Control Hub Android App"
   - **Visibility:** `Public` (required for free GitHub Actions)
4. Click **Create repository**

---

### Step 3: Upload Your Project Files

#### **Method A: Upload via GitHub Web UI (Easiest)**

1. On your new repo page, click **Add file** → **Upload files**
2. Drag-and-drop the extracted `PhoenixCarHub` folder contents, OR:
   - Select all files from inside the folder
   - Drag them into the upload area
3. Scroll down, click **Commit changes**

#### **Method B: Using Git Command Line (If you know Git)**

```bash
git clone https://github.com/YOUR_USERNAME/PhoenixCarHub.git
cd PhoenixCarHub
# Copy all project files here
git add .
git commit -m "Initial commit: Phoenix Car Hub"
git push origin main
```

---

### Step 4: GitHub Actions Automatically Builds Your APK

1. Go to your repo
2. Click **Actions** tab
3. You'll see **"Build Phoenix APK"** workflow running
4. Wait for the green ✅ checkmark (takes 5-10 minutes first time)

---

### Step 5: Download Your APK

#### **While Build is Running:**
1. Click on the **"Build Phoenix APK"** workflow run
2. Scroll down to **"Artifacts"** section
3. Click **phoenix-car-hub-debug** to download

#### **After Build Completes:**
1. Go to **Releases** section (right sidebar)
2. Click the latest release
3. Download **app-debug.apk**

---

## Install APK on Your Phone

### **Option 1: Direct USB Installation**

```bash
# If you have ADB (Android Debug Bridge) installed:
adb install app-debug.apk
```

### **Option 2: File Transfer (Easiest)**

1. Download the APK to your computer
2. Email it to yourself or use Google Drive
3. On your Android phone:
   - **Settings** → **Install unknown apps** → Allow the browser/file manager
   - Download/open the APK
   - Tap **Install**

### **Option 3: WhatsApp/Telegram**

1. Send the APK file to yourself via WhatsApp/Telegram
2. On your phone, tap the file → Install

---

## Automatic Future Builds

Every time you push code changes to GitHub, a new APK is automatically built!

```bash
git add .
git commit -m "Your changes here"
git push origin main
```

Check the **Actions** tab to see the new build running.

---

## Troubleshooting

### Build Shows Red ❌

Common causes:
- **Syntax error in code** — Check the error message in the Actions log
- **Internet timeout** — GitHub will retry automatically
- **License issue** — Already handled in the workflow

Click the failed run to see detailed logs.

### Can't Download APK

Make sure the repo is **Public** (not Private):
- Go to repo **Settings** → **Visibility** → **Public**

(Private repos can't use free GitHub Actions)

---

## What's Happening Behind the Scenes

The workflow (`.github/workflows/build.yml`) does this automatically:

1. ✅ Downloads Android SDK
2. ✅ Installs build tools
3. ✅ Compiles your Kotlin code
4. ✅ Builds the APK
5. ✅ Uploads it for download

All on GitHub's servers — you don't install anything!

---

## Need Help?

- **GitHub Actions docs:** https://docs.github.com/en/actions
- **Android build issues:** Check the Actions log for error messages
- **ADB/installation issues:** See Android's guide: https://developer.android.com/studio/command-line/adb
