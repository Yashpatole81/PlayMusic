package com.example.playmusic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import com.example.playmusic.ui.components.TopBar
import com.example.playmusic.ui.components.SongItem
import com.example.playmusic.viewmodel.MusicViewModel
import androidx.compose.foundation.layout.Box
import com.example.playmusic.navigation.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun LibraryScreen(navController: NavController, musicViewModel: MusicViewModel) {
    Scaffold(
        topBar = { TopBar(title = "Songs") }
    ) { paddingValues ->
        val songs = musicViewModel.songs.observeAsState()
        if (songs.value == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                items(songs.value!!) { song ->
                    SongItem(song = song, onClick = {
                        android.util.Log.d("LibraryScreen", "Song clicked: ${song.title}")
                        
                        // Set song and navigate - LiveData should propagate
                        musicViewModel.playSong(song)
                        navController.navigate(Screens.Player)
                    })
                }
            }
        }
    }
}
