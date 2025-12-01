package com.example.playmusic.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.playmusic.ui.components.PlayerControls
import com.example.playmusic.ui.components.VolumeKnob
import com.example.playmusic.ui.components.TopBar
import com.example.playmusic.viewmodel.MusicViewModel
import com.example.playmusic.util.FormatUtils
import com.example.playmusic.ui.theme.White
import com.example.playmusic.ui.theme.Black
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(navController: NavController, musicViewModel: MusicViewModel) {
    val currentSong = musicViewModel.currentSong.observeAsState().value
    val isPlaying = musicViewModel.isPlaying.observeAsState(false).value
    val position = musicViewModel.currentPosition.observeAsState(0L).value
    val duration = musicViewModel.duration.observeAsState(0L).value
    var volume by remember { mutableStateOf(0.5f) }
    var userIsSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableStateOf(0L) }
    
    // Debug logging
    LaunchedEffect(currentSong) {
        android.util.Log.d("PlayerScreen", "ViewModel instance: ${musicViewModel.hashCode()}")
        android.util.Log.d("PlayerScreen", "Current song: ${currentSong?.title ?: "NULL"}")
        android.util.Log.d("PlayerScreen", "Is playing: $isPlaying")
    }

    // Progress tracking
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            if (!userIsSeeking) {
                musicViewModel.updateProgress()
                // Refresh duration periodically (it might not be available immediately)
                if (duration == 0L) {
                    musicViewModel.refreshDuration()
                }
            }
        }
    }

    // Rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "albumRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Scaffold(
        topBar = { TopBar(title = "Now Playing") },
        containerColor = Black
    ) { paddingValues ->
        if (currentSong == null) {
            // Show loading briefly or message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading song...", 
                        color = White,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Rotating Album Art
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .rotate(if (isPlaying) angle else 0f)
                        .clip(CircleShape)
                        .border(2.dp, White, CircleShape)
                        .background(White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "♫", fontSize = 80.sp, color = White.copy(alpha = 0.5f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Song Info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentSong.title,
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = currentSong.artist,
                        color = White.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                }

                // Seek Bar
                Column {
                    val currentDuration = if (duration > 0L) duration else 1000L
                    val displayPosition = if (userIsSeeking) seekPosition else position
                    
                    Slider(
                        value = displayPosition.toFloat().coerceIn(0f, currentDuration.toFloat()),
                        onValueChange = { newValue ->
                            userIsSeeking = true
                            seekPosition = newValue.toLong()
                        },
                        onValueChangeFinished = {
                            musicViewModel.seekTo(seekPosition)
                            userIsSeeking = false
                        },
                        valueRange = 0f..currentDuration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = White,
                            activeTrackColor = White,
                            inactiveTrackColor = White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = FormatUtils.formatDuration(displayPosition), color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text(text = FormatUtils.formatDuration(currentDuration), color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                // Controls
                PlayerControls(
                    isPlaying = isPlaying,
                    onPlayPause = { musicViewModel.togglePlayPause() },
                    onNext = { musicViewModel.playNext() },
                    onPrevious = { musicViewModel.playPrevious() },
                    onShuffle = { /* TODO: Implement shuffle */ },
                    onRepeat = { /* TODO: Implement repeat */ }
                )

                // Volume Knob
                VolumeKnob(volume = volume, onVolumeChange = { volume = it })
            }
        }
    }
}
