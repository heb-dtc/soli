package net.heb.soli.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import net.heb.soli.stream.StreamItem

actual class PlayerBuilder(private val context: Context) {
    actual fun build(): Player {
        return Player(PlatformPlayer(context = context))
    }
}

actual class PlatformPlayer(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        //.setWakeMode(C.WAKE_MODE_LOCAL)
        .build().apply {
            playWhenReady = true
        }

    actual fun play(item: StreamItem) {
        println("Playing")
        println("Starting radio: ${item.name}")
        val source = MediaItem.Builder()
            .setUri(item.uri)
            //.setMediaMetadata(mediaMetadata)
            .build()

        exoPlayer.setMediaItem(source)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    actual fun resume() {
        exoPlayer.play()
    }

    actual fun pause() {
        println("Pausing")
        exoPlayer.pause()
    }

    actual fun stop() {
        println("Stopping")
        exoPlayer.release()
    }

    actual fun isPlaying() = exoPlayer.isPlaying
}