package com.phoenix.carhub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenix.carhub.data.model.ThemeMode

@Composable
fun BottomNavBar(
    themeMode:           ThemeMode,
    onToggleTheme:       () -> Unit,
    homeAddress:         String,
    workAddress:         String,
    schoolAddress:       String,
    onSaveHomeAddress:   (String) -> Unit,
    onSaveWorkAddress:   (String) -> Unit,
    onSaveSchoolAddress: (String) -> Unit,
    // In-app navigation callbacks — triggered by navViewModel, not Intents
    onNavigateHome:      () -> Unit,
    onNavigateWork:      () -> Unit,
    onNavigateSchool:    () -> Unit,
    onOpenMapsList:      () -> Unit,
    isDarkTheme:         Boolean,
    modifier:            Modifier = Modifier
) {
    var showAddressDialog by remember { mutableStateOf<String?>(null) }
    var dialogInput       by remember { mutableStateOf("") }

    val overlayColor = if (isDarkTheme)
        androidx.compose.ui.graphics.Color(0xD9121212)
    else
        androidx.compose.ui.graphics.Color(0xD9FFFFFF)

    Row(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(overlayColor)
            .padding(horizontal = 24.dp)
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Theme toggle
        NavIconButton(
            emoji   = if (isDarkTheme) "☀️" else "🌙",
            label   = if (isDarkTheme) "Light" else "Dark",
            onClick = onToggleTheme
        )

        Spacer(modifier = Modifier.weight(1f))

        // Right group: Lists, School, Work, Home
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // 📋 Google Maps Lists (only item that still opens Maps app)
            NavIconButton(emoji = "📋", label = "Lists", onClick = onOpenMapsList)

            // 🏫 School
            NavIconButton(
                emoji   = "🏫",
                label   = "School",
                onClick = {
                    if (schoolAddress.isEmpty()) {
                        showAddressDialog = "school"
                        dialogInput = ""
                    } else {
                        onNavigateSchool()      // ← in-app navigation
                    }
                }
            )

            // 🏢 Work
            NavIconButton(
                emoji   = "🏢",
                label   = "Work",
                onClick = {
                    if (workAddress.isEmpty()) {
                        showAddressDialog = "work"
                        dialogInput = ""
                    } else {
                        onNavigateWork()        // ← in-app navigation
                    }
                }
            )

            // 🏠 Home
            NavIconButton(
                emoji   = "🏠",
                label   = "Home",
                onClick = {
                    if (homeAddress.isEmpty()) {
                        showAddressDialog = "home"
                        dialogInput = ""
                    } else {
                        onNavigateHome()        // ← in-app navigation
                    }
                }
            )
        }
    }

    // Address input dialog (first-time setup for each saved place)
    if (showAddressDialog != null) {
        val label = when (showAddressDialog) {
            "home"   -> "Home"
            "work"   -> "Work"
            "school" -> "School"
            else     -> ""
        }
        AlertDialog(
            onDismissRequest = { showAddressDialog = null },
            title = { Text("Set $label Address") },
            text  = {
                Column {
                    Text("Enter your $label address:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value       = dialogInput,
                        onValueChange = { dialogInput = it },
                        placeholder = { Text("e.g. 123 Main Street, City") },
                        singleLine  = false,
                        maxLines    = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (dialogInput.isNotBlank()) {
                        when (showAddressDialog) {
                            "home"   -> { onSaveHomeAddress(dialogInput.trim());   onNavigateHome()   }
                            "work"   -> { onSaveWorkAddress(dialogInput.trim());   onNavigateWork()   }
                            "school" -> { onSaveSchoolAddress(dialogInput.trim()); onNavigateSchool() }
                        }
                    }
                    showAddressDialog = null
                }) { Text("Save & Navigate") }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun NavIconButton(emoji: String, label: String, onClick: () -> Unit) {
    TextButton(
        onClick         = onClick,
        modifier        = Modifier.size(64.dp),
        contentPadding  = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text     = label,
                fontSize = 9.sp,
                color    = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
