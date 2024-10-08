package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository


actual class PlayerBuilder(private val repository: StreamRepository) {
    actual fun build(): Player {
        return Player(PlatformPlayer(), repository)
    }
}

actual class PlatformPlayer {

    private val player = VlcPlayer()

    actual fun play(item: StreamItem) {
        stop()
        player.play(item)
    }

    actual fun pause() {
        player.pause()
    }

    actual fun stop() {
        player.stop()
    }

    actual fun resume() {
        player.resume()
    }

    actual fun seekTo(progress: Long) {
        player.seekTo(progress)
    }

    actual fun getProgress(): Long {
        return player.getProgress()
    }

    actual fun getDuration(): Long {
        return player.getDuration()
    }

    actual fun isPlaying() = player.isPlaying()
}