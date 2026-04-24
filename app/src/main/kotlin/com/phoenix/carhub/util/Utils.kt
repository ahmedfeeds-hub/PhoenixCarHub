package com.phoenix.carhub.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

// ─────────────────────────────────────────────
// LocationUtils
// ─────────────────────────────────────────────

object LocationUtils {

    suspend fun reverseGeocode(context: Context, location: Location): String {
        return withContext(Dispatchers.IO) {
            runCatching {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    var result = "Locating…"
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        result = addresses.firstOrNull()?.let { addr ->
                            buildString {
                                addr.locality?.let { append(it) }
                                addr.adminArea?.let {
                                    if (isNotEmpty()) append(", ")
                                    append(it)
                                }
                            }.ifEmpty { addr.getAddressLine(0) ?: "Unknown location" }
                        } ?: "Unknown location"
                    }
                    result
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses?.firstOrNull()?.let { addr ->
                        buildString {
                            addr.locality?.let { append(it) }
                            addr.adminArea?.let {
                                if (isNotEmpty()) append(", ")
                                append(it)
                            }
                        }.ifEmpty { addr.getAddressLine(0) ?: "Unknown location" }
                    } ?: "Unknown location"
                }
            }.getOrDefault("Location unavailable")
        }
    }

    fun buildGoogleMapsLink(location: Location): String {
        return "https://maps.google.com/?q=${location.latitude},${location.longitude}"
    }
}

// ─────────────────────────────────────────────
// MediaControlUtils
// ─────────────────────────────────────────────

object MediaControlUtils {

    fun getVolume(context: Context): Float {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return if (max > 0) current.toFloat() / max.toFloat() else 0f
    }

    fun setVolume(context: Context, fraction: Float) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (fraction.coerceIn(0f, 1f) * max).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun getBrightness(context: Context): Float {
        return runCatching {
            val value = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            value / 255f
        }.getOrDefault(0.5f)
    }

    fun setBrightness(window: Window, fraction: Float) {
        val params = window.attributes
        params.screenBrightness = fraction.coerceIn(0.05f, 1f)
        window.attributes = params
    }

    fun detectMusicApp(context: Context): String? {
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_APP_MUSIC)
        }
        return context.packageManager.resolveActivity(intent, 0)?.activityInfo?.packageName
    }
}

// ─────────────────────────────────────────────
// PermissionHandler
// ─────────────────────────────────────────────

object PermissionHandler {

    val REQUIRED_PERMISSIONS = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.READ_CONTACTS)
        add(Manifest.permission.SEND_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun allPermissionsGranted(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}

// ─────────────────────────────────────────────
// Weather Icon Mapping
// ─────────────────────────────────────────────

object WeatherIconMapper {
    // iconCode is the WMO weather code string from Open-Meteo
    fun iconCodeToEmoji(iconCode: String): String {
        val code = iconCode.toIntOrNull() ?: return "🌡️"
        return when (code) {
            0         -> "☀️"
            1, 2      -> "⛅"
            3         -> "☁️"
            in 45..48 -> "🌫️"
            in 51..67 -> "🌧️"
            in 71..77 -> "❄️"
            in 80..82 -> "🌧️"
            in 95..99 -> "⛈️"
            else      -> "🌡️"
        }
    }

    fun descriptionToEmoji(description: String): String {
        return when {
            description.contains("Clear", ignoreCase = true)      -> "☀️"
            description.contains("Partly", ignoreCase = true)     -> "⛅"
            description.contains("Overcast", ignoreCase = true)   -> "☁️"
            description.contains("Rain", ignoreCase = true)       -> "🌧️"
            description.contains("Shower", ignoreCase = true)     -> "🌧️"
            description.contains("Thunder", ignoreCase = true)    -> "⛈️"
            description.contains("Snow", ignoreCase = true)       -> "❄️"
            description.contains("Fog", ignoreCase = true)        -> "🌫️"
            else                                                   -> "🌡️"
        }
    }
}
