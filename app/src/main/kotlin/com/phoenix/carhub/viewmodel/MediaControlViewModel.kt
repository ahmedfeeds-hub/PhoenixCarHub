package com.phoenix.carhub.viewmodel

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.carhub.data.model.MediaState
import com.phoenix.carhub.util.MediaControlUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class MediaControlViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState: StateFlow<MediaState> = _mediaState.asStateFlow()

    private var mediaController: MediaControllerCompat? = null
    private var positionJob: Job? = null

    // ─── MediaController Callbacks ───────────────────────────

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata ?: return
            val albumArt = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
            _mediaState.update { state ->
                state.copy(
                    title    = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
                    artist   = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "",
                    album    = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
                    duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
                    albumArtBytes = albumArt?.toByteArray()
                )
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state ?: return
            val playing = state.state == PlaybackStateCompat.STATE_PLAYING
            _mediaState.update { it.copy(isPlaying = playing, position = state.position) }
            if (playing) startPositionUpdates() else positionJob?.cancel()
        }
    }

    // ─── Attach to active MediaSession ───────────────────────

    fun attachMediaController(controller: MediaControllerCompat) {
        mediaController?.unregisterCallback(controllerCallback)
        mediaController = controller
        controller.registerCallback(controllerCallback)
        // Trigger initial state
        controllerCallback.onMetadataChanged(controller.metadata)
        controllerCallback.onPlaybackStateChanged(controller.playbackState)
    }

    // ─── Launch default music app ────────────────────────────

    fun launchMusicApp(onLaunched: () -> Unit) {
        val packageName = MediaControlUtils.detectMusicApp(context)
        if (packageName == null) {
            // No music app installed
            return
        }
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
            Handler(Looper.getMainLooper()).postDelayed({ onLaunched() }, 500)
        }
    }

    // ─── Playback Controls ───────────────────────────────────

    fun play() {
        mediaController?.transportControls?.play()
    }

    fun pause() {
        mediaController?.transportControls?.pause()
    }

    fun skipNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun skipPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun seekTo(position: Long) {
        mediaController?.transportControls?.seekTo(position)
        _mediaState.update { it.copy(position = position) }
    }

    fun togglePlayPause() {
        if (_mediaState.value.isPlaying) pause() else play()
    }

    // ─── Position Polling ────────────────────────────────────

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (isActive) {
                delay(100)
                val current = mediaController?.playbackState?.position ?: break
                _mediaState.update { it.copy(position = current) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.unregisterCallback(controllerCallback)
        positionJob?.cancel()
    }
}

// ─── Extension: Bitmap to ByteArray ──────────────────────────

private fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}
