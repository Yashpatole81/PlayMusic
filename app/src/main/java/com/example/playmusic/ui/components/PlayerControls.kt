package com.example.playmusic.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.playmusic.ui.theme.White
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit
) {
    Row {
        IconButton(onClick = onPrevious) {
            Icon(imageVector = Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = White, modifier = Modifier.size(32.dp))
        }
        IconButton(onClick = onShuffle) {
            Icon(imageVector = Icons.Filled.Shuffle, contentDescription = "Shuffle", tint = White, modifier = Modifier.size(32.dp))
        }
        IconButton(onClick = onPlayPause) {
            val icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
            val desc = if (isPlaying) "Pause" else "Play"
            Icon(imageVector = icon, contentDescription = desc, tint = White, modifier = Modifier.size(48.dp))
        }
        IconButton(onClick = onRepeat) {
            Icon(imageVector = Icons.Filled.Repeat, contentDescription = "Repeat", tint = White, modifier = Modifier.size(32.dp))
        }
        IconButton(onClick = onNext) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = "Next", tint = White, modifier = Modifier.size(32.dp))
        }
    }
}
