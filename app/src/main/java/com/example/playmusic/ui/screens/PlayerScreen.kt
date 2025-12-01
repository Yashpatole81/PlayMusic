package com.example.playmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.playmusic.ui.components.ClickWheel
import com.example.playmusic.viewmodel.MusicViewModel
import com.example.playmusic.util.FormatUtils
import com.example.playmusic.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(navController: NavController, musicViewModel: MusicViewModel) {
    val currentSong = musicViewModel.currentSong.observeAsState().value
    val isPlaying = musicViewModel.isPlaying.observeAsState(false).value
    val position = musicViewModel.currentPosition.observeAsState(0L).value
    val duration = musicViewModel.duration.observeAsState(0L).value
    var userIsSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableStateOf(0L) }
    
    // Progress tracking
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            if (!userIsSeeking) {
                musicViewModel.updateProgress()
                if (duration == 0L) musicViewModel.refreshDuration()
            }
        }
    }

    Scaffold(
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
        ) {
            // Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Now Playing",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (currentSong == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
            } else {
                // Content Area (Album Art + Info)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Album Art Placeholder
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♫", fontSize = 40.sp, color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Song Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSong.title,
                                color = TextWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentSong.artist,
                                color = TextGray,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "1 of 1", // Placeholder for playlist index
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Progress Bar
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
                            thumbColor = Color.Transparent, // Hide thumb for cleaner look or keep small
                            activeTrackColor = AccentBlue,
                            inactiveTrackColor = WheelGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = FormatUtils.formatDuration(displayPosition), color = TextGray, fontSize = 12.sp)
                        Text(text = "-${FormatUtils.formatDuration(currentDuration - displayPosition)}", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            // Click Wheel Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                ClickWheel(
                    isPlaying = isPlaying,
                    onMenuClick = { navController.popBackStack() },
                    onPlayPauseClick = { musicViewModel.togglePlayPause() },
                    onNextClick = { musicViewModel.playNext() },
                    onPrevClick = { musicViewModel.playPrevious() },
                    onVolumeChange = { /* Volume change logic if needed */ }
                )
            }
        }
    }
}
