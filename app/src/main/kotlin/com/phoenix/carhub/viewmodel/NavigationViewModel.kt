package com.phoenix.carhub.viewmodel

import android.app.Activity
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.SimulationOptions
import com.google.android.libraries.navigation.Waypoint
import com.phoenix.carhub.util.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Navigation State ─────────────────────────────────────────

data class NavigationUiState(
    val isNavigating: Boolean = false,
    val navigatorReady: Boolean = false,
    val pendingDestination: PendingDestination? = null,   // awaiting user confirmation
    val activeDestinationLabel: String = "",
    val error: String? = null
)

data class PendingDestination(
    val latLng: LatLng,
    val label: String,   // reverse-geocoded address or saved label
    val isFromSavedPlace: Boolean = false
)

// ─── ViewModel ────────────────────────────────────────────────

@HiltViewModel
class NavigationViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _navState = MutableStateFlow(NavigationUiState())
    val navState: StateFlow<NavigationUiState> = _navState.asStateFlow()

    private var navigator: Navigator? = null

    // ─── Initialize Navigator ────────────────────────────────

    fun initNavigator(activity: Activity) {
        NavigationApi.getNavigator(
            activity,
            object : NavigationApi.NavigatorListener {
                override fun onNavigatorReady(nav: Navigator) {
                    navigator = nav
                    _navState.update { it.copy(navigatorReady = true, error = null) }

                    // Listen for arrival at destination
                    nav.addArrivalListener {
                        _navState.update { it.copy(isNavigating = false, activeDestinationLabel = "") }
                    }
                }

                override fun onError(errorCode: Int) {
                    val msg = when (errorCode) {
                        NavigationApi.ErrorCode.NOT_AUTHORIZED ->
                            "Navigation SDK not authorized. Ensure your API key has Navigation SDK enabled."
                        NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED ->
                            "Please accept Google Maps Terms of Service."
                        NavigationApi.ErrorCode.NETWORK_ERROR ->
                            "Network error initializing navigation."
                        else -> "Navigation error (code $errorCode)"
                    }
                    _navState.update { it.copy(error = msg, navigatorReady = false) }
                }
            }
        )
    }

    // ─── Destination Selection from Map Tap ──────────────────

    /**
     * Called when user taps/long-presses on the map.
     * Reverse-geocodes the LatLng and presents a confirmation bottom sheet.
     */
    fun onMapTapped(latLng: LatLng) {
        if (_navState.value.isNavigating) return  // ignore taps during active navigation
        viewModelScope.launch {
            val location = Location("map_tap").apply {
                latitude  = latLng.latitude
                longitude = latLng.longitude
            }
            val label = LocationUtils.reverseGeocode(context, location)
            _navState.update {
                it.copy(
                    pendingDestination = PendingDestination(
                        latLng = latLng,
                        label  = label,
                        isFromSavedPlace = false
                    )
                )
            }
        }
    }

    /**
     * Called from bottom nav buttons (Home, Work, School) when address is already saved.
     * Geocodes the address string and shows the confirmation sheet.
     */
    fun onSavedPlaceTapped(address: String, label: String) {
        if (address.isBlank()) return
        viewModelScope.launch {
            // Use Geocoder to turn address string → LatLng
            val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
            runCatching {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(address, 1) { results ->
                        results.firstOrNull()?.let { addr ->
                            _navState.update {
                                it.copy(
                                    pendingDestination = PendingDestination(
                                        latLng = LatLng(addr.latitude, addr.longitude),
                                        label  = label,
                                        isFromSavedPlace = true
                                    )
                                )
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val results = geocoder.getFromLocationName(address, 1)
                    results?.firstOrNull()?.let { addr ->
                        _navState.update {
                            it.copy(
                                pendingDestination = PendingDestination(
                                    latLng = LatLng(addr.latitude, addr.longitude),
                                    label  = label,
                                    isFromSavedPlace = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // ─── User Confirms Destination ───────────────────────────

    fun confirmNavigation() {
        val pending = _navState.value.pendingDestination ?: return
        val nav     = navigator ?: run {
            _navState.update { it.copy(error = "Navigator not ready. Please wait.") }
            return
        }

        val waypoint = runCatching {
            Waypoint.builder()
                .setLatLng(pending.latLng.latitude, pending.latLng.longitude)
                .setTitle(pending.label)
                .build()
        }.getOrElse {
            _navState.update { it.copy(error = "Invalid destination.") }
            return
        }

        val pendingRoute = nav.setDestination(waypoint)
        pendingRoute.addOnResultListener { result ->
            when (result) {
                Navigator.RouteStatus.OK -> {
                    nav.startGuidance()
                    _navState.update {
                        it.copy(
                            isNavigating            = true,
                            activeDestinationLabel  = pending.label,
                            pendingDestination      = null,
                            error                   = null
                        )
                    }
                }
                Navigator.RouteStatus.ROUTE_CANCELED -> {
                    _navState.update { it.copy(pendingDestination = null) }
                }
                else -> {
                    _navState.update {
                        it.copy(
                            error              = "Could not find route: $result",
                            pendingDestination = null
                        )
                    }
                }
            }
        }
    }

    // ─── User Cancels Destination Sheet ──────────────────────

    fun cancelPendingDestination() {
        _navState.update { it.copy(pendingDestination = null) }
    }

    // ─── Stop Navigation ─────────────────────────────────────

    fun stopNavigation() {
        navigator?.stopGuidance()
        navigator?.clearDestinations()
        _navState.update { it.copy(isNavigating = false, activeDestinationLabel = "") }
    }

    // ─── Cleanup ─────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        navigator?.removeArrivalListeners()
        navigator?.cleanup()
    }
}
