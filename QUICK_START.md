# 🚀 Quick Start — Get Your APK in 5 Steps

## Option A: GitHub Actions (No Installation Needed) ⭐ RECOMMENDED

1. **Create free GitHub account:** https://github.com/signup
2. **Create new repository** named `PhoenixCarHub`
3. **Upload** this folder's contents to your new repo
4. **Wait** 5-10 minutes (GitHub builds automatically)
5. **Download** APK from the **Releases** page

✅ **Done! Zero installation needed.**

👉 **Full guide:** See `GITHUB_SETUP.md` in this folder

---

## Option B: Docker (If You Have Docker Installed)

```bash
docker run --rm -v "%cd%":/project -w /project mingc/android-build-box:latest bash -c "chmod +x gradlew && ./gradlew assembleDebug"
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

---

## Option C: Android Studio (Most Visual)

1. **Download:** https://developer.android.com/studio (install it)
2. **Open Project:** File → Open → select `PhoenixCarHub` folder
3. **Build:** Build → Build APK(s)
4. **Download:** Click "Locate" when done

---

## Install APK on Your Car's Android Device

**Easiest:** 
1. Email yourself the APK
2. On phone: Settings → Install unknown apps → Allow
3. Open APK → Install

**Or use ADB:**
```bash
adb install app-debug.apk
```

---

## Your API Key is Already Configured ✅

- **Google Maps:** Included
- **Weather:** Free (Open-Meteo, no key needed)

**No changes needed — just build!**
