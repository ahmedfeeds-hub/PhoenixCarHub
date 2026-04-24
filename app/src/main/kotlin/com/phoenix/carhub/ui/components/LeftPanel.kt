package com.phoenix.carhub.ui.components

import android.content.Intent
import android.location.Location
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenix.carhub.BuildConfig
import com.phoenix.carhub.data.model.SOSContact
import com.phoenix.carhub.data.model.ThemeMode
import com.phoenix.carhub.service.SOSService
import com.phoenix.carhub.util.PermissionHandler
import java.util.UUID

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LeftPanel(
    isOpen: Boolean,
    onClose: () -> Unit,
    themeMode: ThemeMode,
    onToggleTheme: (ThemeMode) -> Unit,
    sosContacts: List<SOSContact>,
    onAddSosContact: (SOSContact) -> Unit,
    onRemoveSosContact: (String) -> Unit,
    currentLocation: Location?,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSosDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Contact picker
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use { c ->
            if (c.moveToFirst()) {
                val nameIdx = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIdx   = c.getColumnIndex(ContactsContract.Contacts._ID)
                val name = if (nameIdx >= 0) c.getString(nameIdx) else "Unknown"
                val contactId = if (idIdx >= 0) c.getString(idIdx) else ""

                // Fetch phone number
                val phoneCursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )
                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        val phoneIdx = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phone = if (phoneIdx >= 0) pc.getString(phoneIdx).replace(" ", "") else ""
                        if (phone.isNotEmpty() && sosContacts.size < 5) {
                            onAddSosContact(
                                SOSContact(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    phoneNumber = phone,
                                    isWhatsApp = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
        exit  = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        val panelBg = if (isDarkTheme)
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        else
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .background(panelBg)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Setup",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close panel")
                    }
                }

                Divider()

                // Wi-Fi
                SetupMenuItem(
                    icon = Icons.Default.Wifi,
                    label = "Wi-Fi Settings",
                    onClick = {
                        val intent = Intent(Settings.Panel.ACTION_WIFI)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                )

                // Bluetooth
                SetupMenuItem(
                    icon = Icons.Default.Bluetooth,
                    label = "Bluetooth Settings",
                    onClick = {
                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                )

                // SOS
                SetupMenuItem(
                    icon = Icons.Default.Emergency,
                    label = "SOS Contacts",
                    onClick = { showSosDialog = true }
                )

                // Dark Mode
                SetupMenuItem(
                    icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    label = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                    onClick = {
                        onToggleTheme(if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK)
                    }
                )

                // About
                SetupMenuItem(
                    icon = Icons.Default.Info,
                    label = "About",
                    onClick = { showAboutDialog = true }
                )
            }
        }
    }

    // ─── SOS Dialog ──────────────────────────────────────────

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text("SOS Emergency Contacts") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (sosContacts.isEmpty()) {
                        Text("No contacts saved. Add up to 5 emergency contacts.")
                    } else {
                        Text("Saved contacts (${sosContacts.size}/5):")
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(sosContacts) { contact ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(contact.name, style = MaterialTheme.typography.bodyLarge)
                                        Text(contact.phoneNumber, style = MaterialTheme.typography.labelSmall)
                                    }
                                    IconButton(onClick = { onRemoveSosContact(contact.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove contact")
                                    }
                                }
                            }
                        }
                    }

                    if (sosContacts.size < 5) {
                        TextButton(
                            onClick = {
                                showSosDialog = false
                                contactPickerLauncher.launch(null)
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Contact")
                        }
                    }

                    if (sosContacts.isNotEmpty() && currentLocation != null) {
                        Button(
                            onClick = {
                                SOSService.triggerSOS(
                                    contacts  = sosContacts,
                                    location  = currentLocation,
                                    launchIntent = { intent ->
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    }
                                )
                                showSosDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SEND SOS NOW")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSosDialog = false }) { Text("Close") }
            }
        )
    }

    // ─── About Dialog ─────────────────────────────────────────

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Phoenix Car Hub") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Version: ${BuildConfig.VERSION_NAME}")
                    Text("Build: ${BuildConfig.VERSION_CODE}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Permissions Status:", style = MaterialTheme.typography.titleMedium)
                    Text("• Location: ${if (PermissionHandler.hasLocationPermission(context)) "✅ Granted" else "❌ Denied"}")
                    Text("• SMS: ${if (PermissionHandler.hasSmsPermission(context)) "✅ Granted" else "❌ Denied"}")
                    Text("• Contacts: ${if (PermissionHandler.hasContactsPermission(context)) "✅ Granted" else "❌ Denied"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Privacy Policy: https://phoenix.example.com/privacy", style = MaterialTheme.typography.labelSmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun SetupMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
