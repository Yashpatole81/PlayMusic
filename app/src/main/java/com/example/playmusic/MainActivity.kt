package com.example.playmusic

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playmusic.data.service.MusicService
import com.example.playmusic.navigation.AppNavHost
import com.example.playmusic.ui.theme.PlayMusicTheme
import com.example.playmusic.viewmodel.MusicViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

class MainActivity : ComponentActivity() {
    
    private var musicService: MusicService? = null
    private var serviceBound = false
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as MusicService.LocalBinder
            musicService = localBinder.getService()
            serviceBound = true
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            serviceBound = false
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start and bind to MusicService
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        
        setContent {
            PlayMusicTheme {
                MainContent()
            }
        }
    }
    
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun MainContent() {
        val musicViewModel: MusicViewModel = viewModel()
        
        // Determine which permission to request based on Android version
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        val permissionState = rememberPermissionState(permission)
        
        // Bind service to ViewModel when available
        LaunchedEffect(serviceBound) {
            if (serviceBound && musicService != null) {
                musicViewModel.bindService(musicService!!)
            }
        }
        
        // Request permission on first launch
        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
        
        // Load songs when permission is granted
        LaunchedEffect(permissionState.status.isGranted) {
            if (permissionState.status.isGranted) {
                musicViewModel.loadSongs()
            }
        }
        
        AppNavHost(musicViewModel = musicViewModel)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
    }
}
