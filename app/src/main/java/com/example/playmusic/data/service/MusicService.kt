package com.example.playmusic.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.playmusic.R

class MusicService : Service() {

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null
    private val CHANNEL_ID = "music_service_channel"
    private val NOTIFICATION_ID = 1
    
    private var shouldAutoPlay = false

    // ExoPlayer listener to handle state changes
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.d("MusicService", "Playback state changed: $playbackState")
            when (playbackState) {
                Player.STATE_READY -> {
                    Log.d("MusicService", "Player is ready, shouldAutoPlay: $shouldAutoPlay")
                    if (shouldAutoPlay) {
                        player.play()
                        shouldAutoPlay = false
                    }
                }
                Player.STATE_ENDED -> {
                    Log.d("MusicService", "Playback ended")
                }
                Player.STATE_BUFFERING -> {
                    Log.d("MusicService", "Buffering...")
                }
                Player.STATE_IDLE -> {
                    Log.d("MusicService", "Player idle")
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d("MusicService", "Is playing: $isPlaying")
        }
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        player.addListener(playerListener)
        createNotificationChannel()
        mediaSession = MediaSession.Builder(this, player).setSessionActivity(pendingIntent()).build()
        Log.d("MusicService", "Service created")
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(this, com.example.playmusic.MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PlayMusic")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent())
            .setOngoing(true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("MusicService", "Service bound")
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    // Expose player controls
    fun setMediaItem(uri: String) {
        Log.d("MusicService", "Setting media item: $uri")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }

    // New method: set media item and auto-play when ready
    fun setMediaItemAndPlay(uri: String) {
        Log.d("MusicService", "Setting media item and will auto-play: $uri")
        shouldAutoPlay = true
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        // Listener will handle play() when STATE_READY
    }

    fun play() {
        Log.d("MusicService", "Play called")
        player.play()
    }
    
    fun pause() {
        Log.d("MusicService", "Pause called")
        player.pause()
    }
    
    fun isPlaying(): Boolean = player.isPlaying
    
    fun seekTo(positionMs: Long) {
        Log.d("MusicService", "Seek to: $positionMs")
        player.seekTo(positionMs)
    }
    
    fun getCurrentPosition(): Long = player.currentPosition
    
    fun getDuration(): Long {
        val duration = player.duration
        return if (duration > 0) duration else 0L
    }
    
    fun getPlaybackState(): Int = player.playbackState
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MusicService", "Service destroyed")
        player.removeListener(playerListener)
        player.release()
        mediaSession?.release()
    }
}
