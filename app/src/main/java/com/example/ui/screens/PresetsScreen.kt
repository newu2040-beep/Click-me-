package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.PresetDao
import com.example.data.model.PresetItem
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun PresetsScreen(
    presetDao: PresetDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val presets by presetDao.getAllPresets().collectAsStateWithLifecycle(initialValue = emptyList())

    var showCreateDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }
    var grainValue by remember { mutableFloatStateOf(0.2f) }
    var tempValue by remember { mutableFloatStateOf(0.1f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101014))
            .padding(top = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Saved Presets", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }

            if (presets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No custom presets created yet", color = Color.Gray, fontSize = 15.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(presets, key = { it.id }) { preset ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            cornerRadius = 16.dp,
                            backgroundColor = Color.White.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(preset.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Filter: ${preset.filterType} • Grain: ${(preset.grain * 100).toInt()}%", color = Color.Gray, fontSize = 12.sp)
                                }

                                Row {
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            presetDao.updatePreset(preset.copy(isFavorite = !preset.isFavorite))
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (preset.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (preset.isFavorite) Color.Red else Color.White
                                        )
                                    }

                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            presetDao.deletePreset(preset.id)
                                            Toast.makeText(context, "Preset deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Add Preset
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = Color(0xFFFF6D00),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Preset")
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = Color(0xFF1C1C22),
                title = { Text("Create Preset", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPresetName,
                            onValueChange = { newPresetName = it },
                            label = { Text("Preset Name", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Film Grain: ${(grainValue * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                        Slider(value = grainValue, onValueChange = { grainValue = it }, colors = SliderDefaults.colors(thumbColor = Color(0xFFFF6D00)))

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Warmth: ${(tempValue * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                        Slider(value = tempValue, onValueChange = { tempValue = it }, colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD54F)))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPresetName.isNotBlank()) {
                                coroutineScope.launch {
                                    presetDao.insertPreset(
                                        PresetItem(
                                            name = newPresetName,
                                            grain = grainValue,
                                            temperature = tempValue
                                        )
                                    )
                                    newPresetName = ""
                                    showCreateDialog = false
                                    Toast.makeText(context, "Preset saved!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
                    ) {
                        Text("Save Preset", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}
