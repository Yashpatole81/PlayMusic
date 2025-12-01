package com.example.playmusic.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playmusic.ui.screens.LibraryScreen
import com.example.playmusic.ui.screens.PlayerScreen
import com.example.playmusic.ui.screens.FavouritesScreen
import com.example.playmusic.ui.screens.PlaylistScreen
import com.example.playmusic.ui.screens.ArtistsScreen
import com.example.playmusic.viewmodel.MusicViewModel

object Screens {
    const val Library = "library"
    const val Player = "player"
    const val Favourites = "favourites"
    const val Playlist = "playlist"
    const val Artists = "artists"
}

@Composable
fun AppNavHost(
    musicViewModel: MusicViewModel,
    startDestination: String = Screens.Library
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screens.Library) { LibraryScreen(navController, musicViewModel) }
        composable(Screens.Player) { PlayerScreen(navController, musicViewModel) }
        composable(Screens.Favourites) { FavouritesScreen(navController) }
        composable(Screens.Playlist) { PlaylistScreen(navController) }
        composable(Screens.Artists) { ArtistsScreen(navController) }
    }
}
