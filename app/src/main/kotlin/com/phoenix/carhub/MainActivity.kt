package com.phoenix.carhub

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.BatteryManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phoenix.carhub.data.model.ThemeMode
import com.phoenix.carhub.service.LocationService
import com.phoenix.carhub.ui.screens.MapScreen
import com.phoenix.carhub.ui.theme.PhoenixCarHubTheme
import com.phoenix.carhub.util.PermissionHandler
import com.phoenix.carhub.viewmodel.MapViewModel
import com.phoenix.carhub.viewmodel.MediaControlViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()
    private val mediaViewModel: MediaControlViewModel by viewModels()

    // ─── Location Service Binding ─────────────────────────────
    private var locationService: LocationService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val lb = binder as? LocationService.LocalBinder ?: return
            locationService = lb.getService()
            locationService?.onLocationUpdate = { location ->
                mapViewModel.onLocationUpdated(location)
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
        }
    }

    // ─── Battery Broadcast Receiver ───────────────────────────
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val status   = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val plugged  = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
                val level    = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale    = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                val pct      = (level * 100 / scale.coerceAtLeast(1))
                val charging = plugged != 0 &&
                        status != BatteryManager.BATTERY_STATUS_DISCHARGING
                mapViewModel.updateBatteryState(charging, pct)
            }
        }
    }

    // ─── Permission Launcher ──────────────────────────────────
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val locationGranted = results[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (locationGranted) startLocationService()
        // Feature degradation handled per-feature in composables
    }

    // ─── Lifecycle ────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (PermissionHandler.allPermissionsGranted(this)) {
            startLocationService()
        } else {
            permissionLauncher.launch(PermissionHandler.REQUIRED_PERMISSIONS)
        }

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        setContent {
            val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (uiState.themeMode) {
                ThemeMode.DARK  -> true
                ThemeMode.LIGHT -> false
                ThemeMode.AUTO  -> systemDark
            }

            PhoenixCarHubTheme(darkTheme = isDark) {
                PermissionRationaleHandler(
                    allGranted = PermissionHandler.allPermissionsGranted(this),
                    onRequestPermissions = {
                        permissionLauncher.launch(PermissionHandler.REQUIRED_PERMISSIONS)
                    }
                )
                MapScreen(
                    viewModel      = mapViewModel,
                    mediaViewModel = mediaViewModel
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        runCatching { unbindService(serviceConnection) }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { unregisterReceiver(batteryReceiver) }
    }

    // ─── Helpers ─────────────────────────────────────────────

    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        startForegroundService(intent)
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

// ─── Permission Rationale Composable ─────────────────────────

@Composable
private fun PermissionRationaleHandler(
    allGranted: Boolean,
    onRequestPermissions: () -> Unit
) {
    var showDialog by remember { mutableStateOf(!allGranted) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Permissions Required") },
            text = {
                Text(
                    "Phoenix Car Hub needs:\n\n" +
                    "• Location — to show your position and route\n" +
                    "• Contacts — for SOS emergency contacts\n" +
                    "• SMS — to send emergency alerts\n\n" +
                    "These are only used for the features you activate."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onRequestPermissions()
                }) { Text("Grant Permissions") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Later") }
            }
        )
    }
}
