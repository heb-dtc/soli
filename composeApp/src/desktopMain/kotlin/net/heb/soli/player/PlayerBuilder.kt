package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository
import org.freedesktop.gstreamer.ElementFactory
import org.freedesktop.gstreamer.Gst
import org.freedesktop.gstreamer.StateChangeReturn
import org.freedesktop.gstreamer.elements.PlayBin
import java.net.URI
import java.util.concurrent.TimeUnit

actual class PlayerBuilder(private val repository: StreamRepository) {
    actual fun build(): Player {
        return Player(PlatformPlayer(), repository)
    }
}

actual class PlatformPlayer {

    private val audioPlayBin: PlayBin

    init {
        Gst.init("SOLI")
        audioPlayBin = PlayBin("AudioPlayer")
        //audioPlayBin.setVideoSink(ElementFactory.make("fakesink", "videosink"))
        audioPlayBin.setAudioSink(ElementFactory.make("autoaudiosink", "audiosink"))
    }

    actual fun play(item: StreamItem) {
        stop()
        audioPlayBin.setURI(URI.create(item.uri))
        val stateChange = audioPlayBin.play()

        if (stateChange == StateChangeReturn.FAILURE) {
            throw IllegalStateException("Failed to play audio")
        }
    }

    actual fun pause() {
        audioPlayBin.pause()
    }

    actual fun stop() {
        audioPlayBin.stop()
    }

    actual fun resume() {
        audioPlayBin.play()
    }

    actual fun seekTo(progress: Long) {
        val success = audioPlayBin.seek(progress, TimeUnit.MILLISECONDS)
        if (!success) {
            println("Failed to seek")
        }
    }

    actual fun getProgress(): Long {
        return audioPlayBin.queryPosition(TimeUnit.MILLISECONDS)
    }

    actual fun getDuration(): Long {
        return audioPlayBin.queryDuration(TimeUnit.MILLISECONDS)
    }

    actual fun isPlaying() = audioPlayBin.isPlaying
}