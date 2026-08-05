package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.PhotoDao
import com.example.data.model.FilmFilterCatalog
import com.example.data.model.PhotoItem
import com.example.ui.components.FilterSelectorBar
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch
import java.io.File

data class EditState(
    val exposure: Float = 0f,
    val contrast: Float = 0f,
    val highlights: Float = 0f,
    val shadows: Float = 0f,
    val temperature: Float = 0f,
    val tint: Float = 0f,
    val saturation: Float = 0f,
    val vignette: Float = 0f,
    val grain: Float = 0f,
    val filterName: String = "Natural"
)

@Composable
fun PhotoEditorScreen(
    photoId: Long,
    photoDao: PhotoDao,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var photo by remember { mutableStateOf<PhotoItem?>(null) }

    var currentState by remember { mutableStateOf(EditState()) }
    val history = remember { mutableStateListOf<EditState>() }
    var historyIndex by remember { mutableStateOf(0) }

    var copiedState by remember { mutableStateOf<EditState?>(null) }
    var isHoldingBefore by remember { mutableStateOf(false) }
    var selectedFilterPreset by remember { mutableStateOf(FilmFilterCatalog.filters[0]) }

    LaunchedEffect(photoId) {
        photo = photoDao.getPhotoById(photoId)
        photo?.let {
            val initial = EditState(filterName = it.filterApplied)
            currentState = initial
            history.clear()
            history.add(initial)
            historyIndex = 0
            selectedFilterPreset = FilmFilterCatalog.filters.find { f -> f.name == it.filterApplied } ?: FilmFilterCatalog.filters[0]
        }
    }

    fun pushState(newState: EditState) {
        if (newState == currentState) return
        currentState = newState
        while (history.size > historyIndex + 1) {
            history.removeLast()
        }
        history.add(newState)
        historyIndex = history.size - 1
    }

    fun undo() {
        if (historyIndex > 0) {
            historyIndex--
            currentState = history[historyIndex]
        }
    }

    fun redo() {
        if (historyIndex < history.size - 1) {
            historyIndex++
            currentState = history[historyIndex]
        }
    }

    val currentPhoto = photo ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101014))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Editor Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { undo() }, enabled = historyIndex > 0) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = if (historyIndex > 0) Color.White else Color.Gray)
                    }
                    IconButton(onClick = { redo() }, enabled = historyIndex < history.size - 1) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo", tint = if (historyIndex < history.size - 1) Color.White else Color.Gray)
                    }
                    IconButton(onClick = {
                        copiedState = currentState
                        Toast.makeText(context, "Edits copied!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Edits", tint = Color.White)
                    }
                    IconButton(onClick = {
                        copiedState?.let {
                            pushState(it)
                            Toast.makeText(context, "Edits pasted!", Toast.LENGTH_SHORT).show()
                        }
                    }, enabled = copiedState != null) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "Paste Edits", tint = if (copiedState != null) Color.White else Color.Gray)
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updated = currentPhoto.copy(filterApplied = currentState.filterName)
                            photoDao.updatePhoto(updated)
                            Toast.makeText(context, "Edits saved!", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Image Preview Canvas with Press-to-Compare
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHoldingBefore = true
                                tryAwaitRelease()
                                isHoldingBefore = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = File(currentPhoto.filePath),
                    contentDescription = "Edited Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                if (isHoldingBefore) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("ORIGINAL", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Live Filter Bar
            FilterSelectorBar(
                selectedFilter = selectedFilterPreset,
                onFilterSelected = { preset ->
                    selectedFilterPreset = preset
                    pushState(currentState.copy(filterName = preset.name))
                }
            )

            // Parameter Adjustment Sliders
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF18181E))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EditorSlider("Exposure", currentState.exposure, -1f..1f) {
                    pushState(currentState.copy(exposure = it))
                }
                EditorSlider("Contrast", currentState.contrast, -1f..1f) {
                    pushState(currentState.copy(contrast = it))
                }
                EditorSlider("Highlights", currentState.highlights, -1f..1f) {
                    pushState(currentState.copy(highlights = it))
                }
                EditorSlider("Shadows", currentState.shadows, -1f..1f) {
                    pushState(currentState.copy(shadows = it))
                }
                EditorSlider("Temperature", currentState.temperature, -1f..1f) {
                    pushState(currentState.copy(temperature = it))
                }
                EditorSlider("Tint", currentState.tint, -1f..1f) {
                    pushState(currentState.copy(tint = it))
                }
                EditorSlider("Saturation", currentState.saturation, -1f..1f) {
                    pushState(currentState.copy(saturation = it))
                }
                EditorSlider("Vignette", currentState.vignette, 0f..1f) {
                    pushState(currentState.copy(vignette = it))
                }
                EditorSlider("Grain", currentState.grain, 0f..1f) {
                    pushState(currentState.copy(grain = it))
                }
            }
        }
    }
}

@Composable
private fun EditorSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, modifier = Modifier.width(90.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = Color(0xFFFF6D00), activeTrackColor = Color(0xFFFF6D00))
        )
        Text(String.format("%.1f", value), color = Color.Gray, fontSize = 11.sp, modifier = Modifier.width(36.dp))
    }
}
