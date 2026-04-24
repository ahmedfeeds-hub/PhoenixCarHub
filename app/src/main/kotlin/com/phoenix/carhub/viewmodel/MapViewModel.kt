package com.phoenix.carhub.viewmodel

import android.content.Context
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.carhub.data.datastore.UserPreferences
import com.phoenix.carhub.data.model.*
import com.phoenix.carhub.data.repository.WeatherRepository
import com.phoenix.carhub.util.LocationUtils
import com.phoenix.carhub.util.MediaControlUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // ─── UI State ────────────────────────────────────────────

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    // ─── Saved Addresses ─────────────────────────────────────

    val homeAddress   = userPreferences.homeAddress.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val workAddress   = userPreferences.workAddress.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val schoolAddress = userPreferences.schoolAddress.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // ─── Init ────────────────────────────────────────────────

    init {
        observeThemeAndContacts()
        observeVolume()
    }

    private fun observeThemeAndContacts() {
        viewModelScope.launch {
            combine(
                userPreferences.themeMode,
                userPreferences.sosContacts,
                userPreferences.useHighContrast
            ) { theme, contacts, highContrast ->
                Triple(theme, contacts, highContrast)
            }.collect { (theme, contacts, highContrast) ->
                _uiState.update {
                    it.copy(
                        themeMode = theme,
                        sosContacts = contacts,
                        useHighContrast = highContrast
                    )
                }
            }
        }
    }

    private fun observeVolume() {
        viewModelScope.launch {
            while (isActive) {
                val vol = MediaControlUtils.getVolume(context)
                val bri = MediaControlUtils.getBrightness(context)
                _uiState.update { it.copy(volume = vol, brightness = bri) }
                delay(2000)
            }
        }
    }

    // ─── Panel Toggle ────────────────────────────────────────

    fun toggleLeftPanel() {
        _uiState.update {
            it.copy(
                isLeftPanelOpen = !it.isLeftPanelOpen,
                isRightPanelOpen = false
            )
        }
    }

    fun toggleRightPanel() {
        _uiState.update {
            it.copy(
                isRightPanelOpen = !it.isRightPanelOpen,
                isLeftPanelOpen = false
            )
        }
    }

    fun closeAllPanels() {
        _uiState.update { it.copy(isLeftPanelOpen = false, isRightPanelOpen = false) }
    }

    // ─── Location ────────────────────────────────────────────

    fun onLocationUpdated(location: Location) {
        viewModelScope.launch {
            val address = LocationUtils.reverseGeocode(context, location)
            _uiState.update { it.copy(currentAddress = address) }
            fetchWeather(location.latitude, location.longitude)
        }
    }

    // ─── Weather ─────────────────────────────────────────────

    private var weatherJob: Job? = null

    private fun fetchWeather(lat: Double, lon: Double) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _uiState.update { it.copy(weatherState = it.weatherState.copy(isLoading = true)) }

            val currentResult  = weatherRepository.getCurrentWeather(lat, lon)
            val forecastResult = weatherRepository.getForecast(lat, lon)

            _uiState.update { state ->
                state.copy(
                    weatherState = WeatherState(
                        current  = currentResult.getOrNull(),
                        forecast = forecastResult.getOrNull() ?: emptyList(),
                        isLoading = false,
                        error = if (currentResult.isFailure) "Weather unavailable" else null
                    )
                )
            }
        }
    }

    // ─── Battery ─────────────────────────────────────────────

    fun updateBatteryState(isCharging: Boolean, percentage: Int) {
        _uiState.update {
            it.copy(batteryState = BatteryState(isCharging = isCharging, percentage = percentage))
        }
    }

    // ─── Media ───────────────────────────────────────────────

    fun updateMediaState(mediaState: MediaState) {
        _uiState.update { it.copy(mediaState = mediaState) }
    }

    // ─── Theme ───────────────────────────────────────────────

    fun toggleTheme() {
        viewModelScope.launch {
            val current = _uiState.value.themeMode
            val next = when (current) {
                ThemeMode.DARK  -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.AUTO  -> ThemeMode.DARK
            }
            userPreferences.setThemeMode(next)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { userPreferences.setThemeMode(mode) }
    }

    // ─── SOS Contacts ────────────────────────────────────────

    fun addSosContact(contact: SOSContact) {
        viewModelScope.launch {
            val current = _uiState.value.sosContacts
            if (current.size < 5) {
                userPreferences.saveSosContacts(current + contact)
            }
        }
    }

    fun removeSosContact(contactId: String) {
        viewModelScope.launch {
            val updated = _uiState.value.sosContacts.filter { it.id != contactId }
            userPreferences.saveSosContacts(updated)
        }
    }

    // ─── Volume / Brightness ─────────────────────────────────

    fun setVolume(fraction: Float) {
        MediaControlUtils.setVolume(context, fraction)
        _uiState.update { it.copy(volume = fraction) }
    }

    fun setBrightness(fraction: Float) {
        _uiState.update { it.copy(brightness = fraction) }
        // Window-level brightness is handled in UI layer
    }

    // ─── Saved Addresses ─────────────────────────────────────

    fun saveHomeAddress(address: String) {
        viewModelScope.launch { userPreferences.saveHomeAddress(address) }
    }

    fun saveWorkAddress(address: String) {
        viewModelScope.launch { userPreferences.saveWorkAddress(address) }
    }

    fun saveSchoolAddress(address: String) {
        viewModelScope.launch { userPreferences.saveSchoolAddress(address) }
    }
}
