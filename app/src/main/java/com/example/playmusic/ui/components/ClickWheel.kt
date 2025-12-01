package com.example.playmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playmusic.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import kotlin.math.atan2
import kotlin.math.PI

@Composable
fun ClickWheel(
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    isPlaying: Boolean,
    onMenuClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onVolumeChange: (Float) -> Unit // Delta change
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer Wheel (Touch Surface)
        Canvas(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(WheelGray)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val center = Offset(size.toPx() / 2, size.toPx() / 2)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                        val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
                        val radius = size.toPx() / 2
                        val innerRadius = radius * 0.35f // Center button area

                        if (distance > innerRadius) {
                            // Wheel areas
                            when {
                                angle in -135.0..-45.0 -> onMenuClick() // Top
                                angle in -45.0..45.0 -> onNextClick()   // Right
                                angle in 45.0..135.0 -> onPlayPauseClick() // Bottom
                                else -> onPrevClick() // Left
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    var lastAngle = 0.0
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.toPx() / 2, size.toPx() / 2)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            lastAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                        },
                        onDrag = { change, _ ->
                            val center = Offset(size.toPx() / 2, size.toPx() / 2)
                            val dx = change.position.x - center.x
                            val dy = change.position.y - center.y
                            val currentAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
                            
                            var delta = currentAngle - lastAngle
                            if (delta > 180) delta -= 360
                            if (delta < -180) delta += 360
                            
                            // Sensitivity factor
                            onVolumeChange((delta / 360f).toFloat())
                            lastAngle = currentAngle
                        }
                    )
                }
        ) {
            // Just background, content is overlayed
        }

        // Labels
        Box(modifier = Modifier.fillMaxSize()) {
            // MENU (Top)
            Text(
                text = "MENU",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
            
            // Prev (Left)
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Prev",
                tint = White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
            )

            // Next (Right)
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Next",
                tint = White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(24.dp)
            )

            // Play/Pause (Bottom)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Center Button
        Box(
            modifier = Modifier
                .size(size * 0.35f)
                .clip(CircleShape)
                .background(WheelInner) // Darker center
                .clickable { onPlayPauseClick() }, // Center usually selects, mapping to Play/Pause for now or could be separate
            contentAlignment = Alignment.Center
        ) {
            // Empty or could have a subtle gradient
        }
    }
}
