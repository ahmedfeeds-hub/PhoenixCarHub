# Phoenix Car Control Hub

An Android launcher/overlay app for in-car navigation with:
- Full-screen Google Maps navigation
- Live weather (OpenWeather API)
- Media playback controls
- SOS emergency contact system
- Home / Work / School quick-navigation
- Dark / Light theme toggle

---

## Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog (2023.1.1) or newer |
| JDK | 17 |
| Android SDK | API 29–34 |
| Gradle | 8.x (via wrapper) |

---

## API Keys Required

### 1. Google Maps API Key

1. Go to https://console.cloud.google.com/
2. Create a project → Enable **Maps SDK for Android** and **Navigation SDK for Android**
3. Create an API key
4. Restrict to your app's package: `com.phoenix.carhub`

### 2. OpenWeather API Key

1. Sign up at https://openweathermap.org/api
2. Use the **free tier** — Current Weather + 5-day Forecast
3. Copy your API key

---

## Setup Instructions

1. **Clone / Extract** the project
2. **Copy** `local.properties.template` → `local.properties`
3. **Fill in** your SDK path and both API keys in `local.properties`
4. Open in Android Studio
5. Sync Gradle (`File > Sync Project with Gradle Files`)

---

## Build Commands

```bash
# Debug APK (install directly on device)
./gradlew assembleDebug

# Release APK (requires signing config)
./gradlew assembleRelease

# Install debug directly on connected device
./gradlew installDebug

# Run all unit tests
./gradlew test

# Clean build
./gradlew clean assembleDebug
```

Output APK location:
- Debug:   `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

---

## First Launch

On first run, the app requests:
1. **Location** (ACCESS_FINE_LOCATION) — for GPS + weather
2. **Contacts** (READ_CONTACTS) — for SOS contact picker
3. **SMS** (SEND_SMS) — for emergency SOS messages
4. **Notifications** (Android 13+) — for foreground service notification

All permissions are individually gracefully degraded if denied.

---

## Feature Reference

### Top Info Bar
- Shows current address (reverse-geocoded)
- Current weather + 2-hour forecast from OpenWeather
- Battery % only when charging

### Left Panel (⚙ button)
- Wi-Fi / Bluetooth → system settings panels
- SOS → add up to 5 emergency contacts, trigger to send GPS location via SMS + WhatsApp
- Dark/Light mode toggle
- About → version, permission status

### Right Panel (🎵 button)
- Album art, track title, artist
- Seek bar with duration
- Previous / Play-Pause / Next controls
- Volume slider (system audio)
- Brightness slider (window brightness)

### Bottom Bar
- ☀️/🌙 — theme toggle
- 📋 — opens Google Maps saved lists
- 🏫 / 🏢 / 🏠 — School / Work / Home quick-navigation (set once, tap to navigate)

---

## Architecture

```
MVVM + Clean Architecture
├── ViewModels      (MapViewModel, MediaControlViewModel)
├── Repositories    (WeatherRepository)
├── DataStore       (UserPreferences — themes, SOS contacts, saved addresses)
├── Services        (LocationService foreground, SOSService, WeatherService)
├── Hilt DI         (AppModule)
└── Compose UI      (screens/, components/, theme/)
```

---

## Known Limitations

- Google Navigation SDK turn-by-turn requires a paid Maps Platform account beyond the free tier
- WhatsApp SOS opens the WhatsApp share dialog; silent background send is not possible without WhatsApp Business API
- MediaSession attach works best with apps that publish an active MediaSession (Spotify, YouTube Music, Google Play Music). Some apps restrict external controller access
- Album art blur requires Android 12+ (RenderEffect); older devices show a gradient fallback
- `WRITE_SETTINGS` permission must be granted manually on some OEM devices (Settings → Apps → Special app access → Modify system settings)

---

## Permissions Summary

| Permission | Used For |
|-----------|---------|
| ACCESS_FINE_LOCATION | GPS for map + weather |
| READ_CONTACTS | SOS contact picker |
| SEND_SMS | Emergency SOS SMS |
| INTERNET | Weather API, Maps |
| FOREGROUND_SERVICE | Location service notification |
| QUERY_ALL_PACKAGES | Detect installed music app |
| VIBRATE | Haptic feedback |
| POST_NOTIFICATIONS | Android 13+ foreground service |
