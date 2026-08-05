package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun HistogramOverlay(
    luminanceData: IntArray,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .width(130.dp)
            .height(64.dp),
        cornerRadius = 12.dp,
        backgroundColor = Color.Black.copy(alpha = 0.55f)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            if (luminanceData.isEmpty()) return@Canvas
            val maxCount = (luminanceData.maxOrNull() ?: 1).coerceAtLeast(1)
            val stepX = size.width / (luminanceData.size - 1)

            val path = Path()
            path.moveTo(0f, size.height)

            for (i in luminanceData.indices) {
                val x = i * stepX
                val normalizedVal = luminanceData[i].toFloat() / maxCount.toFloat()
                val y = size.height - (normalizedVal * size.height)
                path.lineTo(x, y)
            }
            path.lineTo(size.width, size.height)
            path.close()

            // Draw translucent fill
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.35f)
            )
            // Draw line stroke
            drawPath(
                path = path,
                color = Color(0xFFFF9800),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
