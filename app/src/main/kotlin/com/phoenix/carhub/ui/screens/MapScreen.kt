package com.phoenix.carhub.ui.screens

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.navigation.NavigationView
import com.google.android.libraries.navigation.StylingOptions
import com.phoenix.carhub.data.model.ThemeMode
import com.phoenix.carhub.ui.components.*
import com.phoenix.carhub.util.PermissionHandler
import com.phoenix.carhub.viewmodel.MapViewModel
import com.phoenix.carhub.viewmodel.MediaControlViewModel
import com.phoenix.carhub.viewmodel.NavigationViewModel

@Composable
fun MapScreen(
    viewModel:      MapViewModel           = hiltViewModel(),
    mediaViewModel: MediaControlViewModel  = hiltViewModel(),
    navViewModel:   NavigationViewModel    = hiltViewModel()
) {
    val context  = LocalContext.current
    val activity = context as? android.app.Activity

    val uiState    by viewModel.uiState.collectAsStateWithLifecycle()
    val mediaState by mediaViewModel.mediaState.collectAsStateWithLifecycle()
    val navState   by navViewModel.navState.collectAsStateWithLifecycle()

    val homeAddress   by viewModel.homeAddress.collectAsStateWithLifecycle()
    val workAddress   by viewModel.workAddress.collectAsStateWithLifecycle()
    val schoolAddress by viewModel.schoolAddress.collectAsStateWithLifecycle()

    val isDark = when (uiState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK  -> true
        ThemeMode.AUTO  -> isSystemInDarkTheme()
    }

    val navigationView = remember { NavigationView(context) }

    LaunchedEffect(Unit) {
        activity?.let { navViewModel.initNavigator(it) }
    }

    LaunchedEffect(uiState.brightness) {
        activity?.window?.let { win ->
            val p = win.attributes
            p.screenBrightness = uiState.brightness.coerceIn(0.05f, 1f)
            win.attributes = p
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // LAYER 1 — Full-screen NavigationView (Navigation SDK embedded)
        AndroidView(
            factory  = { navigationView },
            modifier = Modifier.fillMaxSize(),
            update   = { navView ->
                navView.getMapAsync { googleMap ->
                    try {
                        if (PermissionHandler.hasLocationPermission(context)) {
                            googleMap.isMyLocationEnabled = true
                        }
                    } catch (_: SecurityException) {}

                    googleMap.uiSettings.apply {
                        isZoomControlsEnabled     = false
                        isCompassEnabled          = false
                        isMyLocationButtonEnabled = false
                        isMapToolbarEnabled       = false
                    }

                    // Tap on map -> pick destination -> show confirmation sheet
                    googleMap.setOnMapClickListener { latLng ->
                        if (!uiState.isLeftPanelOpen && !uiState.isRightPanelOpen) {
                            viewModel.closeAllPanels()
                            navViewModel.onMapTapped(latLng)
                        }
                    }
                    // Long press also works
                    googleMap.setOnMapLongClickListener { latLng ->
                        viewModel.closeAllPanels()
                        navViewModel.onMapTapped(latLng)
                    }

                    if (isDark) {
                        runCatching {
                            navView.setStylingOptions(
                                StylingOptions.builder()
                                    .primaryDayModeThemeColor(0xFF87CEEB.toInt())
                                    .headerDistanceValueTextColor(0xFFFFFFFF.toInt())
                                    .build()
                            )
                        }
                    }
                }
            }
        )

        // LAYER 2 — Top Info Bar
        TopInfoBar(
            currentAddress = uiState.currentAddress,
            weatherState   = uiState.weatherState,
            batteryState   = uiState.batteryState,
            isDarkTheme    = isDark,
            modifier       = Modifier.align(Alignment.TopCenter).fillMaxWidth()
        )

        // LAYER 3 — Navigation Status Bar (during active guidance)
        NavigationStatusBar(
            isNavigating     = navState.isNavigating,
            destinationLabel = navState.activeDestinationLabel,
            onStopNavigation = { navViewModel.stopNavigation() },
            modifier         = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 52.dp)
        )

        // LAYER 4 — Left panel + trigger
        Box(modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()) {
            LeftPanel(
                isOpen             = uiState.isLeftPanelOpen,
                onClose            = viewModel::closeAllPanels,
                themeMode          = uiState.themeMode,
                onToggleTheme      = viewModel::setThemeMode,
                sosContacts        = uiState.sosContacts,
                onAddSosContact    = viewModel::addSosContact,
                onRemoveSosContact = viewModel::removeSosContact,
                currentLocation    = null,
                isDarkTheme        = isDark
            )
            if (!uiState.isLeftPanelOpen) {
                EdgeTriggerButton("⚙", viewModel::toggleLeftPanel, isDark,
                    Modifier.align(Alignment.CenterStart))
            }
        }

        // LAYER 5 — Right panel + trigger
        Box(modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()) {
            if (!uiState.isRightPanelOpen) {
                EdgeTriggerButton("🎵", viewModel::toggleRightPanel, isDark,
                    Modifier.align(Alignment.CenterEnd))
            }
            RightPanel(
                isOpen             = uiState.isRightPanelOpen,
                onClose            = viewModel::closeAllPanels,
                mediaState         = mediaState,
                volume             = uiState.volume,
                brightness         = uiState.brightness,
                onPlayPause        = mediaViewModel::togglePlayPause,
                onSkipNext         = mediaViewModel::skipNext,
                onSkipPrev         = mediaViewModel::skipPrevious,
                onSeek             = { pos -> mediaViewModel.seekTo(pos.toLong()) },
                onVolumeChange     = viewModel::setVolume,
                onBrightnessChange = viewModel::setBrightness,
                onLaunchMusicApp   = { mediaViewModel.launchMusicApp {} },
                isDarkTheme        = isDark,
                modifier           = Modifier.align(Alignment.CenterEnd)
            )
        }

        // LAYER 6 — Bottom Nav Bar
        // Home/Work/School navigate IN-APP via navViewModel — no external Maps
        BottomNavBar(
            themeMode           = uiState.themeMode,
            onToggleTheme       = viewModel::toggleTheme,
            homeAddress         = homeAddress,
            workAddress         = workAddress,
            schoolAddress       = schoolAddress,
            onSaveHomeAddress   = viewModel::saveHomeAddress,
            onSaveWorkAddress   = viewModel::saveWorkAddress,
            onSaveSchoolAddress = viewModel::saveSchoolAddress,
            onNavigateHome      = { navViewModel.onSavedPlaceTapped(homeAddress,   "🏠 Home")   },
            onNavigateWork      = { navViewModel.onSavedPlaceTapped(workAddress,   "🏢 Work")   },
            onNavigateSchool    = { navViewModel.onSavedPlaceTapped(schoolAddress, "🏫 School") },
            onOpenMapsList      = {
                runCatching {
                    val i = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://maps.google.com/?q=saved")
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(i)
                }
            },
            isDarkTheme         = isDark,
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )

        // LAYER 7 — Destination Confirmation Sheet
        // Slides up from bottom when destination is picked
        DestinationSheet(
            destination = navState.pendingDestination,
            onConfirm   = { navViewModel.confirmNavigation() },
            onDismiss   = { navViewModel.cancelPendingDestination() },
            isDarkTheme = isDark,
            modifier    = Modifier.align(Alignment.BottomCenter)
        )
    }

    DisposableEffect(Unit) {
        navigationView.onCreate(Bundle())
        navigationView.onStart()
        navigationView.onResume()
        onDispose {
            navigationView.onPause()
            navigationView.onStop()
            navigationView.onDestroy()
        }
    }
}

@Composable
fun EdgeTriggerButton(emoji: String, onClick: () -> Unit, isDark: Boolean, modifier: Modifier = Modifier) {
    val bg = if (isDark)
        androidx.compose.ui.graphics.Color(0xCC121212)
    else
        androidx.compose.ui.graphics.Color(0xCCFFFFFF)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(36.dp).height(64.dp)
            .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
    }
}
