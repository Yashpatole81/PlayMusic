package com.example.playmusic.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.playmusic.ui.components.TopBar

@Composable
fun FavouritesScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(title = "Favourites") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Favourites screen (empty)", color = androidx.compose.ui.graphics.Color.White)
        }
    }
}
