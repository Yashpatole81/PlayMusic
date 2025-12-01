package com.example.playmusic.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playmusic.data.model.Song
import com.example.playmusic.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> = _currentSong

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentPosition = MutableLiveData<Long>(0L)
    val currentPosition: LiveData<Long> = _currentPosition

    private val _duration = MutableLiveData<Long>(0L)
    val duration: LiveData<Long> = _duration

    private var currentSongIndex = -1
    private var boundService: com.example.playmusic.data.service.MusicService? = null

    fun bindService(service: com.example.playmusic.data.service.MusicService) {
        boundService = service
        Log.d("MusicViewModel", "Service bound successfully")
    }

    fun loadSongs() {
        viewModelScope.launch {
            val list = repository.getAllSongs()
            _songs.postValue(list)
            Log.d("MusicViewModel", "Loaded ${list.size} songs")
        }
    }

    fun playSong(song: Song) {
        if (boundService == null) {
            Log.e("MusicViewModel", "Cannot play song - service not bound!")
            return
        }
        
        Log.d("MusicViewModel", "Playing song: ${song.title}")
        _currentSong.value = song
        _isPlaying.value = true  // Optimistic update
        
        // Use new method that auto-plays when ready
        boundService?.setMediaItemAndPlay(song.uri)
        
        // Update current song index
        val songList = _songs.value
        if (songList != null) {
            currentSongIndex = songList.indexOfFirst { it.id == song.id }
            Log.d("MusicViewModel", "Current index: $currentSongIndex of ${songList.size}")
        }
        
        // Schedule duration refresh
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)  // Give ExoPlayer time to load
            refreshDuration()
        }
    }

    fun togglePlayPause() {
        if (boundService == null) {
            Log.e("MusicViewModel", "Cannot toggle play/pause - service not bound!")
            return
        }
        
        val current = _currentSong.value
        val playing = boundService?.isPlaying() ?: false
        
        Log.d("MusicViewModel", "Toggle play/pause - currently playing: $playing")
        
        if (playing) {
            pause()
        } else {
            // If no song is selected, start with first song
            if (current == null) {
                val songList = _songs.value
                if (!songList.isNullOrEmpty()) {
                    playSong(songList[0])
                    return
                }
            }
            resume()
        }
    }

    fun pause() {
        Log.d("MusicViewModel", "Pausing")
        boundService?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        val current = _currentSong.value
        if (current != null && boundService != null) {
            Log.d("MusicViewModel", "Resuming")
            boundService?.play()
            _isPlaying.value = true
        } else {
            Log.e("MusicViewModel", "Cannot resume - no current song or service not bound")
        }
    }

    fun seekTo(positionMs: Long) {
        Log.d("MusicViewModel", "Seeking to: $positionMs")
        boundService?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun playNext() {
        val songList = _songs.value ?: return
        if (songList.isEmpty()) return
        
        currentSongIndex = (currentSongIndex + 1) % songList.size
        Log.d("MusicViewModel", "Playing next song at index: $currentSongIndex")
        playSong(songList[currentSongIndex])
    }

    fun playPrevious() {
        val songList = _songs.value ?: return
        if (songList.isEmpty()) return
        
        currentSongIndex = if (currentSongIndex <= 0) songList.size - 1 else currentSongIndex - 1
        Log.d("MusicViewModel", "Playing previous song at index: $currentSongIndex")
        playSong(songList[currentSongIndex])
    }

    fun updateProgress() {
        val position = boundService?.getCurrentPosition() ?: 0L
        _currentPosition.value = position
    }

    private fun updateDuration() {
        val dur = boundService?.getDuration() ?: 0L
        _duration.value = dur
        if (dur > 0) {
            Log.d("MusicViewModel", "Duration updated: $dur ms")
        }
    }

    fun refreshDuration() {
        updateDuration()
    }

    fun getCurrentPosition(): Long = boundService?.getCurrentPosition() ?: 0L
    fun getDuration(): Long = boundService?.getDuration() ?: 0L
}
