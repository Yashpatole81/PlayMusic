package com.example.playmusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.playmusic.ui.theme.White
import com.example.playmusic.ui.theme.Black
import com.example.playmusic.data.model.Song
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow

@Composable
fun SongItem(song: Song, onClick: () -> Unit, isSelected: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(if (isSelected) White.copy(alpha = 0.1f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = song.title, color = White)
            Text(text = song.artist, color = White.copy(alpha = 0.7f), style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
