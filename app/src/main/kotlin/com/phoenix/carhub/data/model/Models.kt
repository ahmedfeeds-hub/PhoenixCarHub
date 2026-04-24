package com.phoenix.carhub.data.model

import java.util.UUID

// ─────────────────────────────────────────────
// SOS Contact
// ─────────────────────────────────────────────

data class SOSContact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String,       // E.164 format e.g. "+919876543210"
    val isWhatsApp: Boolean = false
)

// ─────────────────────────────────────────────
// Saved Location
// ─────────────────────────────────────────────

data class SavedLocation(
    val label: String,             // "Home" | "Work" | "School"
    val address: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────
// Weather Models
// ─────────────────────────────────────────────

data class WeatherData(
    val temperature: Double,       // Celsius
    val description: String,
    val iconCode: String,          // OpenWeather icon code
    val timestamp: Long = System.currentTimeMillis()
)

data class ForecastItem(
    val time: String,              // e.g. "12:30 PM"
    val temperature: Double,
    val iconCode: String
)

data class WeatherState(
    val current: WeatherData? = null,
    val forecast: List<ForecastItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ─────────────────────────────────────────────
// Battery State
// ─────────────────────────────────────────────

data class BatteryState(
    val isCharging: Boolean = false,
    val percentage: Int = 0
)

// ─────────────────────────────────────────────
// Media State
// ─────────────────────────────────────────────

data class MediaState(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0L,
    val position: Long = 0L,
    val albumArtBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaState) return false
        return isPlaying == other.isPlaying &&
               title == other.title &&
               artist == other.artist &&
               album == other.album &&
               duration == other.duration &&
               position == other.position
    }

    override fun hashCode(): Int {
        var result = isPlaying.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        return result
    }
}

// ─────────────────────────────────────────────
// Theme Mode
// ─────────────────────────────────────────────

enum class ThemeMode { LIGHT, DARK, AUTO }

// ─────────────────────────────────────────────
// App UI State
// ─────────────────────────────────────────────

data class AppUiState(
    val isLeftPanelOpen: Boolean = false,
    val isRightPanelOpen: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val useHighContrast: Boolean = false,
    val currentAddress: String = "Locating…",
    val batteryState: BatteryState = BatteryState(),
    val weatherState: WeatherState = WeatherState(),
    val mediaState: MediaState = MediaState(),
    val sosContacts: List<SOSContact> = emptyList(),
    val volume: Float = 0.5f,
    val brightness: Float = 0.5f,
    val isNavigationActive: Boolean = false,
    val navigationStepInfo: NavigationStepInfo? = null,
    val selectedDestination: DestinationPoint? = null
)

// ─────────────────────────────────────────────
// Navigation Models
// ─────────────────────────────────────────────

data class DestinationPoint(
    val latitude: Double,
    val longitude: Double,
    val address: String = "Selected Location"
)

data class NavigationStepInfo(
    val currentStep: String = "",
    val distanceToStep: String = "",
    val distanceToDestination: String = "",
    val timeToDestination: String = "",
    val distanceValue: Double = 0.0,
    val timeValue: Long = 0L,
    val nextStep: String = ""
)
