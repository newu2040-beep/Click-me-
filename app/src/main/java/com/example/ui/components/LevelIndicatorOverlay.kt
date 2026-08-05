package com.example.ui.components

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import kotlin.math.atan2

@Composable
fun LevelIndicatorOverlay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var rollAngle by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val ax = event.values[0]
                    val ay = event.values[1]
                    val angle = Math.toDegrees(atan2(ax.toDouble(), ay.toDouble())).toFloat()
                    rollAngle = angle
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelSensor != null) {
            sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val isLevel = kotlin.math.abs(rollAngle) < 1.5f || kotlin.math.abs(rollAngle - 180f) < 1.5f || kotlin.math.abs(rollAngle + 180f) < 1.5f
    val lineColor = if (isLevel) Color(0xFF4CAF50) else Color(0xFFFFD54F)

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = 100f

        // Draw static center reference crosshair
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(cx - 20f, cy),
            end = Offset(cx + 20f, cy),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(cx, cy - 20f),
            end = Offset(cx, cy + 20f),
            strokeWidth = 2f
        )

        // Draw rotating horizon line
        val rad = Math.toRadians(-rollAngle.toDouble())
        val dx = (radius * kotlin.math.cos(rad)).toFloat()
        val dy = (radius * kotlin.math.sin(rad)).toFloat()

        drawLine(
            color = lineColor,
            start = Offset(cx - dx, cy - dy),
            end = Offset(cx + dx, cy + dy),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }
}
