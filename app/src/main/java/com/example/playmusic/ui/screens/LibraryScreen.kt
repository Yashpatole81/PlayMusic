package com.example.playmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.playmusic.ui.components.SongItem
import com.example.playmusic.viewmodel.MusicViewModel
import com.example.playmusic.navigation.Screens
import com.example.playmusic.ui.theme.*

@Composable
fun LibraryScreen(navController: NavController, musicViewModel: MusicViewModel) {
    Scaffold(
        containerColor = Black,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WheelGray) // Header bar background
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Music",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
        ) {
            val songs = musicViewModel.songs.observeAsState()
            
            if (songs.value == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(songs.value!!) { song ->
                        SongItem(song = song, onClick = {
                            musicViewModel.playSong(song)
                            navController.navigate(Screens.Player)
                        })
                    }
                }
            }
        }
    }
}
