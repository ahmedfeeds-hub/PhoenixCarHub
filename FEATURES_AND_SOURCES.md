# Phoenix Car Control Hub - Complete Features List & Source Code Reference

---

## 🗺️ MAP & NAVIGATION

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Full-Screen Google Maps** | Edge-to-edge map display with pan/zoom/rotate | `MapScreen.kt`, `MainActivity.kt` |
| **Current Location Display** | Shows current GPS coordinates on map | `LocationService.kt`, `MapViewModel.kt` |
| **Reverse Geocoding** | Converts GPS coordinates to address | `Utils.kt` (LocationUtils.reverseGeocode) |
| **GPS Tracking** | Continuous background location updates | `LocationService.kt`, `MainActivity.kt` |
| **Location Permissions** | Runtime permission handling for ACCESS_FINE_LOCATION | `PermissionHandler.kt`, `MainActivity.kt` |
| **Maps API Integration** | Google Maps SDK integration with your API key | `AndroidManifest.xml` (meta-data), `MapScreen.kt` (AndroidView) |
| **Quick Navigation** | One-tap navigation to Home/Work/School via Maps intent | `BottomNavBar.kt` |
| **Saved Locations** | Store 3 addresses (Home, Work, School) | `UserPreferences.kt`, `BottomNavBar.kt` |

---

## 🌤️ WEATHER SYSTEM

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Current Weather Display** | Shows temperature + weather icon in top bar | `TopInfoBar.kt`, `MapViewModel.kt` |
| **2-Hour Forecast** | Displays 4 hourly weather forecasts | `TopInfoBar.kt`, `WeatherRepository.kt` |
| **Open-Meteo API Integration** | Free weather API (no key required) | `WeatherRepository.kt` |
| **WMO Weather Code Mapping** | Converts weather codes to emojis | `WeatherRepository.kt` (wmoCodeToEmoji, wmoCodeToDescription) |
| **Weather Auto-Update** | Updates every 10 minutes or on location change | `MapViewModel.kt` (fetchWeather) |
| **Weather Icon Emojis** | ☀️⛅☁️🌧️⛈️❄️🌫️ icons for conditions | `Utils.kt` (WeatherIconMapper) |
| **Offline Graceful Degradation** | "Weather unavailable" if API fails | `MapViewModel.kt`, `WeatherRepository.kt` |

---

## 🔋 BATTERY STATUS

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Battery Percentage Display** | Shows % only when charging | `TopInfoBar.kt` |
| **Charging Detection** | Detects plugged state via BatteryManager | `MainActivity.kt` (batteryReceiver) |
| **Battery Broadcast Receiver** | Listens to ACTION_BATTERY_CHANGED intent | `MainActivity.kt` |
| **Color-Coded Battery** | Green (>80%), Yellow (20-80%), Red (<20%) | `TopInfoBar.kt`, `Color.kt` |
| **Battery Hidden When Unplugged** | Disappears completely from UI | `TopInfoBar.kt` (if batteryState.isCharging) |

---

## 🎵 MEDIA CONTROLS

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Album Art Display** | Shows album artwork with blur effect | `RightPanel.kt` |
| **Track Information** | Title, artist, album name display | `RightPanel.kt`, `MediaControlViewModel.kt` |
| **Play/Pause Button** | Toggle playback state | `RightPanel.kt` (onPlayPause), `MediaControlViewModel.kt` |
| **Skip Next/Previous** | Navigate between tracks | `RightPanel.kt`, `MediaControlViewModel.kt` (skipNext, skipPrevious) |
| **Seek Bar** | Drag to seek within track | `RightPanel.kt` (Slider), `MediaControlViewModel.kt` (seekTo) |
| **Duration Display** | Shows current/total time (2:30 / 5:00) | `RightPanel.kt` (formatDuration) |
| **MediaSession Integration** | Uses MediaSessionCompat for app control | `MediaControlViewModel.kt` |
| **First-Time Music App Launch** | Detects & launches default music app | `MediaControlViewModel.kt` (launchMusicApp), `Utils.kt` (detectMusicApp) |
| **MediaController Attachment** | Attaches to active music app session | `MediaControlViewModel.kt` (attachMediaController) |
| **Album Art Fallback Gradient** | Shows gradient if no artwork available | `RightPanel.kt` (Box with Brush.linearGradient) |
| **Position Polling** | Real-time position updates (100ms) | `MediaControlViewModel.kt` (startPositionUpdates) |

---

## 🔊 VOLUME CONTROL

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Volume Slider** | 0-100% volume adjustment | `RightPanel.kt` (Slider), `MapViewModel.kt` (setVolume) |
| **System Volume Integration** | Controls STREAM_MUSIC volume via AudioManager | `Utils.kt` (getVolume, setVolume) |
| **Volume Percentage Display** | Shows "0% - 100%" text | `RightPanel.kt` |
| **Real-Time Volume Updates** | Polls system volume every 2 seconds | `MapViewModel.kt` (observeVolume) |

---

## 💡 BRIGHTNESS CONTROL

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Brightness Slider** | Adjusts screen brightness 0-100% | `RightPanel.kt` (Slider), `MapViewModel.kt` (setBrightness) |
| **Window Brightness Control** | Sets WindowManager.LayoutParams.screenBrightness | `Utils.kt` (setBrightness), `MapScreen.kt` (LaunchedEffect) |
| **Brightness Percentage Display** | Shows "0% - 100%" text | `RightPanel.kt` |
| **Adaptive Brightness** | Works across all Android versions | `Utils.kt` |

---

## ☀️🌙 THEME SYSTEM

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Dark Mode Toggle** | Switch between dark/light themes | `MapViewModel.kt` (toggleTheme), `LeftPanel.kt` |
| **Auto Theme Mode** | Follows system dark mode setting | `MapScreen.kt`, `Theme.kt` (isSystemInDarkTheme) |
| **Dark Color Palette** | #121212 bg, #87CEEB (Sky Blue) accent | `Color.kt`, `Theme.kt` |
| **Light Color Palette** | #FFFFFF bg, #00008B (Dark Blue) accent | `Color.kt`, `Theme.kt` |
| **Global Theme Application** | Affects all screens including map | `MainActivity.kt` (PhoenixCarHubTheme wrapper) |
| **DataStore Persistence** | Saves theme preference | `UserPreferences.kt` (themeMode flow) |
| **Material You Colors** | Uses Material3 color scheme | `Theme.kt` (MaterialTheme) |
| **High Contrast Mode** | Optional accessibility feature | `UserPreferences.kt`, `LeftPanel.kt` |

---

## ⚙️ LEFT PANEL - SETUP/SETTINGS

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Slide-In Animation** | Animates from left with slideInHorizontally | `LeftPanel.kt` (AnimatedVisibility) |
| **Wi-Fi Settings** | Opens system Wi-Fi panel (Settings.Panel.ACTION_WIFI) | `LeftPanel.kt` (SetupMenuItem) |
| **Bluetooth Settings** | Opens system Bluetooth panel | `LeftPanel.kt` (SetupMenuItem) |
| **Dark Mode Toggle (In Panel)** | Duplicate of bottom bar toggle | `LeftPanel.kt` (SetupMenuItem) |
| **About Dialog** | Shows version, build #, permissions status | `LeftPanel.kt` (showAboutDialog) |
| **Icon-Only Menu** | Compact design with just icons + labels | `LeftPanel.kt` (SetupMenuItem) |
| **Smooth Close Button** | X icon to close panel | `LeftPanel.kt` (IconButton) |

---

## 🆘 SOS EMERGENCY SYSTEM

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Contact Picker** | System contact picker integration | `LeftPanel.kt` (ActivityResultContracts.PickContact) |
| **Add SOS Contacts** | Store up to 5 emergency contacts | `LeftPanel.kt`, `MapViewModel.kt` (addSosContact) |
| **Remove SOS Contacts** | Delete contacts from list | `LeftPanel.kt`, `MapViewModel.kt` (removeSosContact) |
| **Contact Display** | Shows name + phone number in list | `LeftPanel.kt` (showSosDialog) |
| **Contact Phone Number Extraction** | Fetches phone from ContactsContract | `LeftPanel.kt` (contactPickerLauncher) |
| **DataStore Contact Persistence** | Saves contacts via JSON serialization | `UserPreferences.kt` (sosContacts flow) |
| **SMS Sending** | Sends emergency SMS via SmsManager | `SOSService.kt` (sendSms) |
| **WhatsApp Integration** | Opens WhatsApp share with GPS link | `SOSService.kt` (sendWhatsApp) |
| **GPS Location Link** | Generates Google Maps GPS coordinates link | `Utils.kt` (buildGoogleMapsLink), `SOSService.kt` |
| **Silent SOS Activation** | Sends without toasts/dialogs | `SOSService.kt` (triggerSOS) |
| **SMS Dialog in Left Panel** | Shows SOS contact management UI | `LeftPanel.kt` (showSosDialog AlertDialog) |
| **SOS Trigger Button** | Red "SEND SOS NOW" button in dialog | `LeftPanel.kt` |
| **Permission Handling** | READ_CONTACTS, SEND_SMS permissions | `PermissionHandler.kt`, `MainActivity.kt` |

---

## 🎵 RIGHT PANEL - MEDIA CONTROLS

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Slide-In Animation** | Animates from right with slideInHorizontally | `RightPanel.kt` (AnimatedVisibility) |
| **Album Art Section** | Large album artwork display | `RightPanel.kt` |
| **Album Art Blur** | RenderEffect blur on artwork | `RightPanel.kt` (Brush.linearGradient fallback) |
| **Music Icon Fallback** | 🎵 icon when no artwork available | `RightPanel.kt` (if albumBitmap != null) |
| **Playback Controls Row** | Previous / Play-Pause / Next buttons | `RightPanel.kt` (Row with IconButtons) |
| **Large Play/Pause Button** | 64dp FilledIconButton with accent color | `RightPanel.kt` (FilledIconButton) |
| **Seek Bar** | Interactive slider for track position | `RightPanel.kt` (Slider) |
| **Volume Slider** | System volume control | `RightPanel.kt` (Row with Slider + 🔊) |
| **Brightness Slider** | Window brightness control | `RightPanel.kt` (Row with Slider + ☀️) |
| **Control Labels** | Text showing "Music", "Volume", "Brightness" | `RightPanel.kt` |
| **Smooth Close Button** | X icon to close panel | `RightPanel.kt` (IconButton) |

---

## 🧭 BOTTOM NAVIGATION BAR

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Light/Dark Toggle** | ☀️/🌙 icon on far left | `BottomNavBar.kt` |
| **Google Maps Lists Button** | 📋 icon → opens saved lists in Maps | `BottomNavBar.kt` (ACTION_VIEW with Maps URI) |
| **School Quick-Nav** | 🏫 icon with address picker/nav | `BottomNavBar.kt` |
| **Work Quick-Nav** | 🏢 icon with address picker/nav | `BottomNavBar.kt` |
| **Home Quick-Nav** | 🏠 icon with address picker/nav | `BottomNavBar.kt` |
| **Address Input Dialog** | AlertDialog for first-time address entry | `BottomNavBar.kt` (showAddressDialog) |
| **Saved Address Navigation** | Auto-launch Maps with saved address | `BottomNavBar.kt` (navigateToAddress) |
| **Persistent Address Storage** | Saves via UserPreferences DataStore | `UserPreferences.kt` (homeAddress, workAddress, schoolAddress) |
| **Icon-Only Design** | 56dp large buttons for driving safety | `BottomNavBar.kt` (NavIconButton) |
| **Floating Bar Style** | Rounded corners, shadow, semi-transparent | `BottomNavBar.kt` (RoundedCornerShape, shadow) |
| **Spaced Right-Alignment** | Icons stack right-to-left | `BottomNavBar.kt` (Arrangement.spacedBy) |

---

## 📊 TOP INFO BAR

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Current Location Tag** | 📍 with reverse-geocoded address | `TopInfoBar.kt` |
| **Weather Display** | Current temp + emoji icon | `TopInfoBar.kt` |
| **2-Hour Forecast** | 4 time slots with temps + icons | `TopInfoBar.kt` (weatherState.forecast.take(4)) |
| **Forecast Time Format** | "12:30 PM" format | `WeatherRepository.kt` (SimpleDateFormat) |
| **Battery Percentage** | 🔋 emoji + % (only when charging) | `TopInfoBar.kt` |
| **Color-Coded Battery** | Green/Yellow/Red by percentage | `TopInfoBar.kt` |
| **Auto-Update Location** | Every location change or 30s | `MapViewModel.kt` (onLocationUpdated) |
| **Auto-Update Weather** | Every 10 minutes | `MapViewModel.kt` (fetchWeather with delay) |
| **Semi-Transparent Background** | Dark: 0.8 opacity black | `TopInfoBar.kt` (DarkOverlay, LightOverlay) |
| **High Contrast Text** | White on dark, black on light | `TopInfoBar.kt` |
| **Responsive Layout** | Adapts to any screen width | `TopInfoBar.kt` (fillMaxWidth, weight(1f)) |

---

## 🎨 UI COMPONENTS & STYLING

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Jetpack Compose UI** | All UI in composables (no XML layouts) | `MapScreen.kt`, `LeftPanel.kt`, `RightPanel.kt`, etc. |
| **Material3 Design** | Material Design 3 components | `Theme.kt` (MaterialTheme) |
| **Custom Color Scheme** | Dark/Light with blue accents | `Color.kt` |
| **Typography System** | Roboto fonts, multiple sizes | `Theme.kt` (PhoenixTypography) |
| **Edge-to-Edge Display** | No system UI insets | `MainActivity.kt` (enableEdgeToEdge, WindowCompat) |
| **Rounded Corners** | 12-24dp radius throughout | `LeftPanel.kt`, `RightPanel.kt`, `BottomNavBar.kt` |
| **Smooth Animations** | All panel open/close animated | `LeftPanel.kt`, `RightPanel.kt` (slideInHorizontally, fadeIn) |
| **Shadow Effects** | 8dp elevation on bars | `BottomNavBar.kt` (shadow modifier) |
| **Adaptive Icons** | Launcher icons scale to device shape | `ic_launcher.xml`, `ic_launcher_foreground.xml` |
| **Dark Mode Icons** | Recolored for dark backgrounds | `Color.kt` |
| **Accessibility Touch Targets** | Minimum 48x48dp buttons | `BottomNavBar.kt`, `RightPanel.kt` |

---

## 📱 DATA PERSISTENCE

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **DataStore (Jetpack)** | Modern preference storage | `UserPreferences.kt`, `data/datastore/` |
| **Theme Mode Persistence** | Saves dark/light/auto preference | `UserPreferences.kt` (KEY_THEME_MODE) |
| **High Contrast Preference** | Accessibility option | `UserPreferences.kt` (KEY_HIGH_CONTRAST) |
| **SOS Contacts JSON** | Serializes contact list via Gson | `UserPreferences.kt` (saveSosContacts) |
| **Saved Addresses** | Home, Work, School addresses | `UserPreferences.kt` (homeAddress, workAddress, schoolAddress) |
| **Flow-Based Updates** | Reactive state management | `UserPreferences.kt` (Flow<String>, Flow<List<SOSContact>>) |
| **Migration-Safe Storage** | DataStore handles SharedPreferences → DataStore migration | `UserPreferences.kt` |
| **Encrypted by Default** | DataStore optionally encrypts sensitive data | `UserPreferences.kt` |

---

## 🔐 PERMISSIONS & SECURITY

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Runtime Permission Requests** | Requests at first launch | `MainActivity.kt` (permissionLauncher) |
| **Permission Rationale Dialog** | Explains why each permission is needed | `MainActivity.kt` (PermissionRationaleHandler) |
| **Location Permission Check** | Verifies before GPS access | `PermissionHandler.kt` (hasLocationPermission) |
| **SMS Permission Check** | Verifies before SOS send | `PermissionHandler.kt` (hasSmsPermission) |
| **Contacts Permission Check** | Verifies before contact picker | `PermissionHandler.kt` (hasContactsPermission) |
| **Feature Graceful Degradation** | Disables features if permissions denied | `MapScreen.kt`, `LeftPanel.kt` |
| **All Required Permissions List** | ACCESS_FINE_LOCATION, READ_CONTACTS, SEND_SMS, etc. | `PermissionHandler.kt` (REQUIRED_PERMISSIONS) |
| **Manifest Permissions Declaration** | All 11 permissions declared | `AndroidManifest.xml` |
| **Android 13+ Notification Permission** | POST_NOTIFICATIONS for foreground service | `AndroidManifest.xml`, `MainActivity.kt` |

---

## 🏗️ ARCHITECTURE & FRAMEWORK

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **MVVM Pattern** | Model-View-ViewModel architecture | `viewmodel/`, `data/`, `ui/` |
| **Clean Architecture** | Separation of concerns in layers | Project structure |
| **Hilt Dependency Injection** | Auto-generated DI for ViewModels | `di/AppModule.kt`, `@HiltViewModel` annotations |
| **ViewModel State Management** | StateFlow for UI state | `MapViewModel.kt`, `MediaControlViewModel.kt` |
| **Repository Pattern** | Data layer abstraction | `data/repository/WeatherRepository.kt` |
| **DataStore for Persistence** | Modern Jetpack preference storage | `data/datastore/UserPreferences.kt` |
| **Retrofit Network Client** | HTTP client for API calls | `WeatherRepository.kt` |
| **Coroutine-Based Async** | Launched/collectAsState for async operations | `MapViewModel.kt` |
| **Gradle Version Catalog** | Centralized dependency management | `gradle/libs.versions.toml` |
| **Kotlin DSL** | build.gradle.kts instead of Groovy | `build.gradle.kts`, `app/build.gradle.kts` |

---

## 🌐 NETWORK & API INTEGRATION

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Google Maps API** | Maps display & navigation | `AndroidManifest.xml`, `MapScreen.kt` |
| **Open-Meteo Weather API** | Free weather data (no key) | `WeatherRepository.kt` |
| **Retrofit HTTP Client** | Network requests | `WeatherRepository.kt` (Retrofit.Builder) |
| **Gson JSON Serialization** | Parse API responses | `build.gradle.kts` (gson, converter-gson) |
| **OkHttp Interceptor** | Logging for network debugging | `WeatherRepository.kt` (HttpLoggingInterceptor) |
| **Error Handling** | Try-catch with Result wrappers | `WeatherRepository.kt` (runCatching) |
| **API Response Caching** | Implicit via HTTP headers | `WeatherRepository.kt` |
| **Offline Fallback** | "Unavailable" messages if API fails | `MapViewModel.kt`, `WeatherRepository.kt` |

---

## 🎯 BACKGROUND SERVICES

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **LocationService** | Foreground service for continuous GPS | `service/LocationService.kt` |
| **Foreground Notification** | Persistent notification while tracking | `LocationService.kt` (startForeground) |
| **Service Lifecycle Management** | onCreate, onStart, onDestroy | `MainActivity.kt`, `LocationService.kt` |
| **Service Binding** | Local binding via LocalBinder | `LocationService.kt` (LocalBinder), `MainActivity.kt` (serviceConnection) |
| **Location Callback** | Receives location updates | `LocationService.kt` (LocationCallback) |
| **FusedLocationProvider** | Google's location provider | `LocationService.kt` (getFusedLocationProviderClient) |
| **Location Request Config** | Priority.HIGH_ACCURACY, 5s updates | `LocationService.kt` (LocationRequest.Builder) |

---

## 📋 MANIFEST & BUILD CONFIG

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **API Level 29+ Support** | minSdk = 29 (Android 10) | `build.gradle.kts` |
| **Target API 34** | targetSdk = 34 (Android 14) | `build.gradle.kts` |
| **Kotlin 1.9.22** | Latest Kotlin version | `gradle/libs.versions.toml` |
| **Compose BOM** | Jetpack Compose versions management | `gradle/libs.versions.toml` |
| **AGP 8.2.2** | Android Gradle Plugin version | `gradle/libs.versions.toml` |
| **Java 17 Compilation** | JVM target 17 | `build.gradle.kts` |
| **Landscape Orientation** | Fixed landscape for car display | `AndroidManifest.xml` (screenOrientation=landscape) |
| **Hardware Acceleration** | Enabled for smooth rendering | `AndroidManifest.xml` (hardwareAccelerated=true) |
| **Notch/Cutout Support** | windowLayoutInDisplayCutoutMode=shortEdges | `AndroidManifest.xml` |
| **Debuggable Flag** | Enabled for development | `build.gradle.kts` (isDebuggable) |
| **ProGuard Configuration** | Code obfuscation for release | `app/proguard-rules.pro` |

---

## 🤖 ARTIFICIAL INTELLIGENCE & AUTOMATION

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Hilt Annotations** | @HiltViewModel, @Inject for DI | Throughout ViewModels & classes |
| **Kotlin Coroutines** | Async operations without callbacks | `MapViewModel.kt`, `LocationService.kt` |
| **State Flows** | Reactive state updates | `MapViewModel.kt` (MutableStateFlow, StateFlow) |
| **Lazy Initialization** | Retrofit API client lazy-loaded | `WeatherRepository.kt` (by lazy) |

---

## 🔄 SYSTEM INTEGRATION

| Feature | Description | Source Files |
|---------|-------------|--------------|
| **Intent Handling** | System settings, contacts, navigation intents | Multiple files |
| **Broadcast Receivers** | Battery status updates | `MainActivity.kt` (batteryReceiver) |
| **Content Providers** | Access to contacts & phone settings | `LeftPanel.kt` (ContactsContract) |
| **System Services** | AudioManager, FusedLocationProvider, etc. | `Utils.kt`, `LocationService.kt` |
| **MediaSession** | Headless music control integration | `MediaControlViewModel.kt` |
| **PackageManager** | Detect installed apps (music app, Maps) | `Utils.kt` (detectMusicApp) |
| **Settings Intent** | Open Wi-Fi, Bluetooth, etc. | `LeftPanel.kt` (Settings.Panel.ACTION_*) |

---

## 📊 STATISTICS & SUMMARY

**Total Features: 150+**

| Category | Count |
|----------|-------|
| Map & Navigation | 8 |
| Weather System | 7 |
| Battery | 5 |
| Media Controls | 10 |
| Volume/Brightness | 4 |
| Theme System | 8 |
| Left Panel (Settings) | 7 |
| SOS Emergency | 12 |
| Right Panel (Media UI) | 11 |
| Bottom Navigation | 10 |
| Top Info Bar | 10 |
| UI/Styling | 10 |
| Data Persistence | 7 |
| Permissions/Security | 9 |
| Architecture | 9 |
| Network/API | 8 |
| Background Services | 7 |
| Manifest/Build Config | 10 |
| System Integration | 7 |
| **TOTAL** | **159** |

---

## 📁 FILE STRUCTURE WITH FEATURE OWNERSHIP

```
PhoenixCarHub/
├── app/src/main/kotlin/com/phoenix/carhub/
│   ├── MainActivity.kt                      # Entry point, permissions, battery receiver, location binding
│   ├── PhoenixApplication.kt                # Hilt application initialization
│   │
│   ├── viewmodel/
│   │   ├── MapViewModel.kt                  # Central state mgmt: location, weather, battery, media, theme, SOS, volume, brightness, addresses
│   │   └── MediaControlViewModel.kt         # Media playback control, album art, position polling
│   │
│   ├── ui/
│   │   ├── screens/
│   │   │   └── MapScreen.kt                 # Map + all overlays, edge triggers, panel management
│   │   ├── components/
│   │   │   ├── TopInfoBar.kt                # Location, weather, forecast, battery display
│   │   │   ├── BottomNavBar.kt              # Theme toggle, Lists/School/Work/Home buttons, address dialogs
│   │   │   ├── LeftPanel.kt                 # Wi-Fi, Bluetooth, SOS, Dark Mode, About
│   │   │   └── RightPanel.kt                # Album art, track info, seek, playback, volume, brightness
│   │   └── theme/
│   │       ├── Color.kt                     # Dark/Light color palettes, semantic colors
│   │       └── Theme.kt                     # Material3 theme, typography
│   │
│   ├── data/
│   │   ├── model/Models.kt                  # SOSContact, SavedLocation, WeatherData, BatteryState, MediaState, AppUiState
│   │   ├── datastore/UserPreferences.kt     # Theme, SOS contacts, saved addresses persistence
│   │   └── repository/WeatherRepository.kt  # Open-Meteo API client, current weather, forecast, WMO code mapping
│   │
│   ├── service/
│   │   ├── LocationService.kt               # Foreground GPS service, location callbacks
│   │   ├── SOSService.kt                    # SMS/WhatsApp sending, GPS link generation
│   │   └── WeatherService.kt                # Placeholder for future background tasks
│   │
│   ├── util/
│   │   └── Utils.kt                         # Location reverse geocoding, media controls, permissions, weather icons
│   │
│   └── di/
│       └── AppModule.kt                     # Hilt DI: UserPreferences, WeatherRepository
│
├── .github/workflows/
│   └── build.yml                            # GitHub Actions APK auto-builder
│
├── AndroidManifest.xml                      # Permissions, activities, services, meta-data
├── build.gradle.kts                         # Root Gradle config
├── app/build.gradle.kts                     # App dependencies, API keys, build config
├── gradle/libs.versions.toml                # Centralized dependency versions
│
└── README.md, QUICK_START.md, GITHUB_SETUP.md, HOW_TO_BUILD.md
```

---

## 🎯 KEY INTEGRATION POINTS

### **Feature → ViewModel → Repository → Service Flow**

1. **Location Update Flow**
   - `LocationService` → `MainActivity.serviceConnection` → `MapViewModel.onLocationUpdated()` → `TopInfoBar` displays address
   - `MapViewModel.onLocationUpdated()` → `WeatherRepository.getForecast()` → Weather displayed in `TopInfoBar`

2. **Theme Toggle Flow**
   - `BottomNavBar` (☀️/🌙 button) → `MapViewModel.toggleTheme()` → `UserPreferences.setThemeMode()` → `MainActivity` re-composes with new theme

3. **SOS Activation Flow**
   - `LeftPanel` (SEND SOS NOW button) → `SOSService.triggerSOS()` → SMS via `SmsManager` + WhatsApp intent

4. **Media Control Flow**
   - `RightPanel` (Play button) → `MediaControlViewModel.play()` → `MediaController.transportControls` → Music app

5. **Settings Changes Flow**
   - `LeftPanel` (Wi-Fi, Bluetooth) → System Intent → System Settings
   - `RightPanel` (Volume slider) → `Utils.setVolume()` → `AudioManager`

---

## 💾 SOURCE FILE COUNT

- **Kotlin Classes:** 17
- **Composable Functions:** 8
- **XML Resources:** 6
- **Gradle Config:** 3
- **Markdown Docs:** 5
- **GitHub Workflows:** 1
- **Total Files:** 40+

---

**This document comprehensively maps every feature to its source code location, making it easy to find and modify any functionality.**
