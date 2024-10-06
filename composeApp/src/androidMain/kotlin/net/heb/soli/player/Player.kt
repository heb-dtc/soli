package net.heb.soli.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

actual class PlayerBuilder(private val context: Context, private val repository: StreamRepository) {
    actual fun build(): Player {
        return Player(PlatformPlayer(context = context), repository = repository)
    }
}

actual class PlatformPlayer(context: Context) {

    private val sessionToken =
        SessionToken(context, ComponentName(context, PlayerService::class.java))
    private val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

    private var mediaController: MediaController? = null

    init {
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    actual fun play(item: StreamItem) {
        println("Playing")
        println("Starting radio: ${item.name}")
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle("SOLI")
            .setArtist(item.name)
            //.setArtworkUri()
            .build()

        val source = MediaItem.Builder()
            .setUri(item.uri)
            .setMediaMetadata(mediaMetadata)
            .build()

        mediaController?.setMediaItem(source, /* resetPosition= */ true)
        mediaController?.prepare()
        mediaController?.play()
    }

    actual fun resume() {
        mediaController?.play()
    }

    actual fun pause() {
        println("Pausing")
        mediaController?.pause()
    }

    actual fun stop() {
        println("Stopping")
        mediaController?.release()
    }

    actual fun getProgress(): Long = mediaController?.currentPosition ?: 0

    actual fun getDuration(): Long {
        return mediaController?.duration ?: 0
    }

    actual fun isPlaying(): Boolean = mediaController?.isPlaying == true

    actual fun seekTo(progress: Long) {
        mediaController?.seekTo(progress)
    }
}