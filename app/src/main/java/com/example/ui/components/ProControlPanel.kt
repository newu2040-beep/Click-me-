package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProControlPanel(
    isoValue: Float,
    onIsoChange: (Float) -> Unit,
    shutterSpeedValue: Float,
    onShutterSpeedChange: (Float) -> Unit,
    evValue: Float,
    onEvChange: (Float) -> Unit,
    wbValue: Float,
    onWbChange: (Float) -> Unit,
    isRawEnabled: Boolean,
    onRawToggle: (Boolean) -> Unit,
    isPeakingEnabled: Boolean,
    onPeakingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Toggles Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PRO CONTROLS",
                    color = Color(0xFFFF9800),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = isRawEnabled,
                    onClick = { onRawToggle(!isRawEnabled) },
                    label = { Text("RAW + JPEG", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF6D00),
                        selectedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = isPeakingEnabled,
                    onClick = { onPeakingToggle(!isPeakingEnabled) },
                    label = { Text("Focus Peaking", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF29B6F6),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ISO Slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ISO: ${(isoValue * 3200).toInt().coerceAtLeast(100)}", color = Color.White, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                Slider(
                    value = isoValue,
                    onValueChange = onIsoChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF6D00), activeTrackColor = Color(0xFFFF6D00))
                )
            }

            // Shutter Speed Slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                val speedStr = if (shutterSpeedValue < 0.5f) "1/${(1f / (shutterSpeedValue.coerceAtLeast(0.001f) * 1000)).toInt()}s" else "${(shutterSpeedValue * 2).toInt()}s"
                Text("Shutter: $speedStr", color = Color.White, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                Slider(
                    value = shutterSpeedValue,
                    onValueChange = onShutterSpeedChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF6D00), activeTrackColor = Color(0xFFFF6D00))
                )
            }

            // Exposure Compensation EV Slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                val evFormatted = String.format("%.1f EV", (evValue - 0.5f) * 4f)
                Text("EV: $evFormatted", color = Color.White, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                Slider(
                    value = evValue,
                    onValueChange = onEvChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF6D00), activeTrackColor = Color(0xFFFF6D00))
                )
            }

            // White Balance Kelvin Slider
            Row(verticalAlignment = Alignment.CenterVertically) {
                val kelvin = (2500 + wbValue * 7500).toInt()
                Text("WB: ${kelvin}K", color = Color.White, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                Slider(
                    value = wbValue,
                    onValueChange = onWbChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD54F), activeTrackColor = Color(0xFFFFD54F))
                )
            }
        }
    }
}
