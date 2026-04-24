package com.phoenix.carhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phoenix.carhub.data.model.BatteryState
import com.phoenix.carhub.data.model.WeatherState
import com.phoenix.carhub.ui.theme.BatteryGreen
import com.phoenix.carhub.ui.theme.BatteryRed
import com.phoenix.carhub.ui.theme.BatteryYellow
import com.phoenix.carhub.ui.theme.DarkOverlay
import com.phoenix.carhub.util.WeatherIconMapper

@Composable
fun TopInfoBar(
    currentAddress: String,
    weatherState: WeatherState,
    batteryState: BatteryState,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val overlayColor = if (isDarkTheme) DarkOverlay else androidx.compose.ui.graphics.Color(0xCCFFFFFF)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(overlayColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ─── Left: Current Location ──────────────────────────
        Text(
            text = "📍 $currentAddress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        // ─── Center: Weather ─────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1.5f)
        ) {
            weatherState.current?.let { weather ->
                val emoji = WeatherIconMapper.descriptionToEmoji(weather.description)
                Text(
                    text = "$emoji ${weather.temperature.toInt()}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp
                )
            } ?: Text(
                text = weatherState.error ?: if (weatherState.isLoading) "Loading…" else "Weather unavailable",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            // 2-hour forecast
            if (weatherState.forecast.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weatherState.forecast.take(4).forEach { item ->
                        val emoji = WeatherIconMapper.iconCodeToEmoji(item.iconCode)
                        Text(
                            text = "${item.time}: ${item.temperature.toInt()}°$emoji",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ─── Right: Battery (only when charging) ─────────────
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (batteryState.isCharging) {
                val batteryColor = when {
                    batteryState.percentage > 80 -> BatteryGreen
                    batteryState.percentage > 20 -> BatteryYellow
                    else -> BatteryRed
                }
                Text(
                    text = "🔋 ${batteryState.percentage}%",
                    fontSize = 10.sp,
                    color = batteryColor
                )
            }
        }
    }
}
