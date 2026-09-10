package com.game.dungeon.audio

import android.content.Context
import android.media.MediaPlayer
import com.game.dungeon.R

class MusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null
    private var isMuted: Boolean = false

    fun play(resId: Int) {
        if (currentResId == resId) return

        stop()

        currentResId = resId
        if (isMuted) return
        
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            start()
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            mediaPlayer?.pause()
        } else {
            if (mediaPlayer != null) {
                mediaPlayer?.start()
            } else {
                currentResId?.let { res ->
                    currentResId = null // Force restart
                    play(res)
                }
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        if (!isMuted) {
            mediaPlayer?.start()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        currentResId = null
    }
}
