package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppTheme
import com.example.data.preferences.UserPreferencesRepository
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onOpenAbout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val currentTheme by userPreferencesRepository.appTheme.collectAsStateWithLifecycle(initialValue = "DARK")
    val showHistogram by userPreferencesRepository.showHistogram.collectAsStateWithLifecycle(initialValue = true)
    val showLevel by userPreferencesRepository.showLevel.collectAsStateWithLifecycle(initialValue = true)
    val saveRaw by userPreferencesRepository.saveRaw.collectAsStateWithLifecycle(initialValue = false)
    val hapticsEnabled by userPreferencesRepository.hapticsEnabled.collectAsStateWithLifecycle(initialValue = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101014))
            .padding(top = 40.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Settings", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Selector
            Text("THEMES & APPEARANCE", color = Color(0xFFFF6D00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                backgroundColor = Color.White.copy(alpha = 0.08f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Theme Palette", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(AppTheme.values()) { theme ->
                            val isSelected = theme.name == currentTheme
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    coroutineScope.launch { userPreferencesRepository.setAppTheme(theme.name) }
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(theme.primaryHex))
                                        .padding(2.dp)
                                ) {}
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = theme.displayName,
                                    color = if (isSelected) Color(0xFFFF6D00) else Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Camera Defaults
            Text("CAMERA DEFAULTS", color = Color(0xFFFF6D00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                backgroundColor = Color.White.copy(alpha = 0.08f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle("Show Live Histogram", showHistogram) {
                        coroutineScope.launch { userPreferencesRepository.setShowHistogram(it) }
                    }
                    SettingToggle("Show Horizon Level", showLevel) {
                        coroutineScope.launch { userPreferencesRepository.setShowLevel(it) }
                    }
                    SettingToggle("Save DNG RAW + JPEG", saveRaw) {
                        coroutineScope.launch { userPreferencesRepository.setSaveRaw(it) }
                    }
                    SettingToggle("Haptic Feedback", hapticsEnabled) {
                        coroutineScope.launch { userPreferencesRepository.setHapticsEnabled(it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Button
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAbout() },
                cornerRadius = 20.dp,
                backgroundColor = Color.White.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("About Clickit", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("v1.0.0", color = Color.Gray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFF6D00)
            )
        )
    }
}
