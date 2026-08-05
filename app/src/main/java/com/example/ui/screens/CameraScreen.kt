package com.example.ui.screens

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.db.PhotoDao
import com.example.data.model.AspectRatioOption
import com.example.data.model.CameraMode
import com.example.data.model.FilmFilterCatalog
import com.example.data.model.FilterPreset
import com.example.data.model.GridType
import com.example.data.model.PhotoItem
import com.example.ui.components.CameraPreviewView
import com.example.ui.components.FilterSelectorBar
import com.example.ui.components.GlassCard
import com.example.ui.components.GridOverlay
import com.example.ui.components.HistogramOverlay
import com.example.ui.components.LevelIndicatorOverlay
import com.example.ui.components.ProControlPanel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen(
    photoDao: PhotoDao,
    onOpenDrawer: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Camera settings state
    var cameraMode by remember { mutableStateOf(CameraMode.PHOTO) }
    var aspectRatio by remember { mutableStateOf(AspectRatioOption.RATIO_3_4) }
    var gridType by remember { mutableStateOf(GridType.RULE_OF_THIRDS) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(FilmFilterCatalog.filters[0]) }

    // Overlays state
    var showHistogram by remember { mutableStateOf(true) }
    var showLevel by remember { mutableStateOf(true) }
    var showProPanel by remember { mutableStateOf(false) }
    var showFilterBar by remember { mutableStateOf(false) }
    var luminanceData by remember { mutableStateOf(IntArray(32)) }

    // Pro Mode Manual State
    var isoValue by remember { mutableFloatStateOf(0.1f) }
    var shutterSpeedValue by remember { mutableFloatStateOf(0.1f) }
    var evValue by remember { mutableFloatStateOf(0.5f) }
    var wbValue by remember { mutableFloatStateOf(0.4f) }
    var isRawEnabled by remember { mutableStateOf(false) }
    var isPeakingEnabled by remember { mutableStateOf(false) }

    // Shutter capture feedback state
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isShutterFlashing by remember { mutableStateOf(false) }
    var timerCountdown by remember { mutableIntStateOf(0) } // 0 = off, 3, 5, 10
    var activeCountdownSeconds by remember { mutableIntStateOf(0) }

    fun triggerHaptics() {
        try {
            val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }

    fun executeCapture() {
        val capture = imageCapture ?: run {
            Toast.makeText(context, "Camera initializing...", Toast.LENGTH_SHORT).show()
            return
        }

        triggerHaptics()

        val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
        val photoFile = File(photosDir, "CLICKIT_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        coroutineScope.launch {
            isShutterFlashing = true
            delay(120)
            isShutterFlashing = false
        }

        capture.takePicture(
            outputOptions,
            context.mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    coroutineScope.launch {
                        val photoItem = PhotoItem(
                            filePath = photoFile.absolutePath,
                            title = photoFile.name,
                            filterApplied = selectedFilter.name,
                            iso = "ISO ${(isoValue * 1600).toInt() + 100}",
                            shutterSpeed = "1/250s",
                            focalLength = "26mm f/1.8",
                            isRaw = isRawEnabled
                        )
                        photoDao.insertPhoto(photoItem)
                        Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun onShutterClicked() {
        if (timerCountdown > 0) {
            coroutineScope.launch {
                activeCountdownSeconds = timerCountdown
                while (activeCountdownSeconds > 0) {
                    delay(1000)
                    activeCountdownSeconds--
                }
                executeCapture()
            }
        } else {
            executeCapture()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        CameraPreviewView(
            aspectRatioOption = aspectRatio,
            isFrontCamera = isFrontCamera,
            flashMode = flashMode,
            isTorchEnabled = isTorchEnabled,
            selectedFilter = selectedFilter,
            onLuminanceData = { data -> luminanceData = data },
            onCameraReady = { capture -> imageCapture = capture },
            modifier = Modifier.fillMaxSize()
        )

        // Grid Lines Overlay
        GridOverlay(gridType = gridType, modifier = Modifier.fillMaxSize())

        // Horizon Level Indicator Overlay
        if (showLevel) {
            LevelIndicatorOverlay(modifier = Modifier.fillMaxSize())
        }

        // Shutter Screen Flash Animation
        if (isShutterFlashing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        // Timer Countdown Overlay
        if (activeCountdownSeconds > 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$activeCountdownSeconds",
                    color = Color.White,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Top Glass Bar (Header Controls)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 12.dp, end = 12.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp,
                backgroundColor = Color.Black.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Drawer Button
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }

                    // Flash Toggle
                    IconButton(onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    }) {
                        val icon = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                            else -> Icons.Default.FlashOff
                        }
                        Icon(icon, contentDescription = "Flash", tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) Color(0xFFFFB300) else Color.White)
                    }

                    // Timer Toggle
                    IconButton(onClick = {
                        timerCountdown = when (timerCountdown) {
                            0 -> 3
                            3 -> 5
                            5 -> 10
                            else -> 0
                        }
                    }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = if (timerCountdown > 0) Color(0xFFFF6D00) else Color.White)
                            if (timerCountdown > 0) {
                                Text("${timerCountdown}s", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Aspect Ratio Selector
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                aspectRatio = when (aspectRatio) {
                                    AspectRatioOption.RATIO_3_4 -> AspectRatioOption.RATIO_1_1
                                    AspectRatioOption.RATIO_1_1 -> AspectRatioOption.RATIO_16_9
                                    AspectRatioOption.RATIO_16_9 -> AspectRatioOption.RATIO_9_16
                                    else -> AspectRatioOption.RATIO_3_4
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(aspectRatio.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Grid Toggle
                    IconButton(onClick = {
                        gridType = when (gridType) {
                            GridType.NONE -> GridType.RULE_OF_THIRDS
                            GridType.RULE_OF_THIRDS -> GridType.GOLDEN_RATIO
                            GridType.GOLDEN_RATIO -> GridType.SQUARE
                            GridType.SQUARE -> GridType.NONE
                        }
                    }) {
                        Icon(Icons.Default.GridOn, contentDescription = "Grid", tint = if (gridType != GridType.NONE) Color(0xFFFF6D00) else Color.White)
                    }

                    // Settings
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }

            // Secondary Overlays Bar (Histogram / Level toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (showHistogram) {
                    HistogramOverlay(luminanceData = luminanceData)
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                GlassCard(
                    cornerRadius = 16.dp,
                    backgroundColor = Color.Black.copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        IconButton(
                            onClick = { showHistogram = !showHistogram },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("HIST", color = if (showHistogram) Color(0xFFFF6D00) else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { showLevel = !showLevel },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("LEVEL", color = if (showLevel) Color(0xFF29B6F6) else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pro Control Panel Slide Overlay
        AnimatedVisibility(
            visible = showProPanel || cameraMode == CameraMode.PRO,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 170.dp)
        ) {
            ProControlPanel(
                isoValue = isoValue,
                onIsoChange = { isoValue = it },
                shutterSpeedValue = shutterSpeedValue,
                onShutterSpeedChange = { shutterSpeedValue = it },
                evValue = evValue,
                onEvChange = { evValue = it },
                wbValue = wbValue,
                onWbChange = { wbValue = it },
                isRawEnabled = isRawEnabled,
                onRawToggle = { isRawEnabled = it },
                isPeakingEnabled = isPeakingEnabled,
                onPeakingToggle = { isPeakingEnabled = it }
            )
        }

        // Live Filter Bar Overlay
        AnimatedVisibility(
            visible = showFilterBar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp)
        ) {
            FilterSelectorBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // Bottom Camera Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Mode Carousel Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(CameraMode.values()) { mode ->
                    val isSelected = mode == cameraMode
                    Text(
                        text = mode.label.uppercase(),
                        color = if (isSelected) Color(0xFFFF6D00) else Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable { cameraMode = mode }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            }

            // Primary Shutter Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable { onOpenGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White)
                }

                // Main Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onShutterClicked() }
                )

                // Controls Toggles (Filter / Flip Camera)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showFilterBar = !showFilterBar },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (showFilterBar) Color(0xFFFF6D00) else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Filters", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                    }
                }
            }
        }
    }
}
