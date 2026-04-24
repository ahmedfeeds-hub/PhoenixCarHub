package com.phoenix.carhub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
// Color Schemes
// ─────────────────────────────────────────────

private val PhoenixDarkColorScheme = darkColorScheme(
    primary            = DarkAccent,
    onPrimary          = DarkBackground,
    secondary          = DarkAccent,
    onSecondary        = DarkBackground,
    background         = DarkBackground,
    onBackground       = DarkTextPrimary,
    surface            = DarkSurface,
    onSurface          = DarkTextPrimary,
    surfaceVariant     = DarkSurface,
    onSurfaceVariant   = DarkTextSecondary,
    error              = ErrorRed,
    outline            = DarkTextSecondary
)

private val PhoenixLightColorScheme = lightColorScheme(
    primary            = LightAccent,
    onPrimary          = LightBackground,
    secondary          = LightAccent,
    onSecondary        = LightBackground,
    background         = LightBackground,
    onBackground       = LightTextPrimary,
    surface            = LightSurface,
    onSurface          = LightTextPrimary,
    surfaceVariant     = LightSurface,
    onSurfaceVariant   = LightTextSecondary,
    error              = ErrorRed,
    outline            = LightTextSecondary
)

// ─────────────────────────────────────────────
// Typography
// ─────────────────────────────────────────────

val PhoenixTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 20.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 16.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 12.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 11.sp),
)

// ─────────────────────────────────────────────
// Theme Composable
// ─────────────────────────────────────────────

@Composable
fun PhoenixCarHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PhoenixDarkColorScheme else PhoenixLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = PhoenixTypography,
        content     = content
    )
}
