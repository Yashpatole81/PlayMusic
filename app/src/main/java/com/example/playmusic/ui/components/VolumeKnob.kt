package com.example.playmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.playmusic.ui.theme.White

@Composable
fun VolumeKnob(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    size: Dp = 120.dp
) {
    val knobSize = size
    Canvas(modifier = Modifier
        .size(knobSize)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                // Simplified: vertical drag changes volume
                val delta = -dragAmount.y / knobSize.toPx()
                val newVolume = (volume + delta).coerceIn(0f, 1f)
                onVolumeChange(newVolume)
            }
        }) {
        // Background circle
        drawCircle(
            color = White.copy(alpha = 0.1f),
            radius = size.toPx() / 2,
            style = Stroke(width = 8f)
        )
        // Indicator line
        val angle = 270f * volume - 135f // map 0..1 to -135..135 degrees
        val radius = size.toPx() / 2 - 12f
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val end = Offset(
            x = center.x + radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat(),
            y = center.y + radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
        )
        drawLine(
            color = White,
            start = center,
            end = end,
            strokeWidth = 8f
        )
    }
}
