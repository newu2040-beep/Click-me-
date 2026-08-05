package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.data.model.GridType

@Composable
fun GridOverlay(
    gridType: GridType,
    modifier: Modifier = Modifier
) {
    if (gridType == GridType.NONE) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeColor = Color.White.copy(alpha = 0.35f)
        val w = size.width
        val h = size.height

        when (gridType) {
            GridType.RULE_OF_THIRDS -> {
                // Vertical lines
                drawLine(strokeColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1f)
                drawLine(strokeColor, Offset(2 * w / 3f, 0f), Offset(2 * w / 3f, h), strokeWidth = 1f)
                // Horizontal lines
                drawLine(strokeColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1f)
                drawLine(strokeColor, Offset(0f, 2 * h / 3f), Offset(w, 2 * h / 3f), strokeWidth = 1f)
            }
            GridType.GOLDEN_RATIO -> {
                val phi = 0.6180339887f
                val x1 = w * (1f - phi)
                val x2 = w * phi
                val y1 = h * (1f - phi)
                val y2 = h * phi

                drawLine(strokeColor, Offset(x1, 0f), Offset(x1, h), strokeWidth = 1f)
                drawLine(strokeColor, Offset(x2, 0f), Offset(x2, h), strokeWidth = 1f)
                drawLine(strokeColor, Offset(0f, y1), Offset(w, y1), strokeWidth = 1f)
                drawLine(strokeColor, Offset(0f, y2), Offset(w, y2), strokeWidth = 1f)
            }
            GridType.SQUARE -> {
                val minDim = kotlin.math.min(w, h)
                val top = (h - minDim) / 2f
                val bottom = top + minDim
                drawLine(strokeColor, Offset(0f, top), Offset(w, top), strokeWidth = 1.5f)
                drawLine(strokeColor, Offset(0f, bottom), Offset(w, bottom), strokeWidth = 1.5f)
            }
            else -> {}
        }
    }
}
