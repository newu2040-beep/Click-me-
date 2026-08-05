package com.example.ui.components

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.AspectRatioOption
import com.example.data.model.FilterPreset
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraPreviewView(
    aspectRatioOption: AspectRatioOption,
    isFrontCamera: Boolean,
    flashMode: Int, // ImageCapture.FLASH_MODE_OFF / ON / AUTO
    isTorchEnabled: Boolean,
    selectedFilter: FilterPreset,
    onLuminanceData: (IntArray) -> Unit,
    onCameraReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Tap-to-focus ring position
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var showFocusRing by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(isFrontCamera, flashMode, isTorchEnabled) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build().also {
                    previewView?.let { pView ->
                        it.surfaceProvider = pView.surfaceProvider
                    }
                }

                val capture = ImageCapture.Builder()
                    .setFlashMode(flashMode)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
                imageCapture = capture
                onCameraReady(capture)

                // Image analysis for live histogram
                val analyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                    val histogram = computeLuminanceHistogram(imageProxy)
                    onLuminanceData(histogram)
                    imageProxy.close()
                }

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    capture,
                    analyzer
                )

                camera?.cameraControl?.enableTorch(isTorchEnabled)

            } catch (e: Exception) {
                Log.e("CameraPreviewView", "Error binding camera lifecycle", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(aspectRatioOption.ratio)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = this

                        val scaleDetector = ScaleGestureDetector(ctx, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                                val delta = detector.scaleFactor
                                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                                return true
                            }
                        })

                        val gestureDetector = GestureDetector(ctx, object : GestureDetector.SimpleOnGestureListener() {
                            override fun onSingleTapUp(e: MotionEvent): Boolean {
                                val factory = previewView?.meteringPointFactory ?: return false
                                val point = factory.createPoint(e.x, e.y)
                                val action = FocusMeteringAction.Builder(point).build()
                                camera?.cameraControl?.startFocusAndMetering(action)

                                focusPoint = Offset(e.x, e.y)
                                showFocusRing = true
                                return true
                            }
                        })

                        setOnTouchListener { _, event ->
                            scaleDetector.onTouchEvent(event)
                            gestureDetector.onTouchEvent(event)
                            true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Live filter tint preview overlay
            if (selectedFilter.colorOverlayHex != "#00000000") {
                val overlayColor = try {
                    Color(android.graphics.Color.parseColor(selectedFilter.colorOverlayHex))
                } catch (e: Exception) {
                    Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(overlayColor)
                )
            }

            // Tap to focus ring indicator
            focusPoint?.let { pt ->
                if (showFocusRing) {
                    Canvas(
                        modifier = Modifier
                            .size(72.dp)
                            .offset { IntOffset((pt.x - 100).toInt(), (pt.y - 100).toInt()) }
                    ) {
                        drawCircle(
                            color = Color(0xFFFF9800),
                            radius = 32.dp.toPx(),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 4.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

private fun computeLuminanceHistogram(image: ImageProxy): IntArray {
    val plane = image.planes[0]
    val buffer = plane.buffer
    val histogram = IntArray(32)
    val total = buffer.remaining()
    val step = (total / 1000).coerceAtLeast(1) // Sample down for high performance

    var i = 0
    while (i < total) {
        val pixel = buffer.get(i).toInt() and 0xFF
        val bin = (pixel * 32) / 256
        if (bin in 0..31) {
            histogram[bin]++
        }
        i += step
    }
    return histogram
}
