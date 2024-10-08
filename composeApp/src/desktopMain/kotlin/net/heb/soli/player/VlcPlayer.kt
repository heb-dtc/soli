package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

class VlcPlayer {

    private val mediaPlayerComponent = AudioPlayerComponent()

    fun play(item: StreamItem) {
        mediaPlayerComponent.mediaPlayer().media().play(item.uri)
    }

    fun pause() {
        mediaPlayerComponent.mediaPlayer().controls().pause()
    }

    fun stop() {
        mediaPlayerComponent.mediaPlayer().controls().stop()
    }

    fun resume() {
        mediaPlayerComponent.mediaPlayer().controls().play()
    }

    fun seekTo(progress: Long) {
        mediaPlayerComponent.mediaPlayer().controls().skipTime(progress)
    }

    fun getProgress(): Long {
        return mediaPlayerComponent.mediaPlayer().status().time()
    }

    fun getDuration(): Long {
        return mediaPlayerComponent.mediaPlayer().status().length()
    }

    fun isPlaying() = mediaPlayerComponent.mediaPlayer().status().isPlaying
}