package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import org.freedesktop.gstreamer.ElementFactory
import org.freedesktop.gstreamer.Gst
import org.freedesktop.gstreamer.StateChangeReturn
import org.freedesktop.gstreamer.elements.PlayBin
import java.net.URI
import java.util.concurrent.TimeUnit

class GstreamerPlayer {
    private val audioPlayBin: PlayBin

    init {
        Gst.init("SOLI")
        audioPlayBin = PlayBin("AudioPlayer")
        //audioPlayBin.setVideoSink(ElementFactory.make("fakesink", "videosink"))
        audioPlayBin.setAudioSink(ElementFactory.make("autoaudiosink", "audiosink"))
    }

    fun play(item: StreamItem) {
        stop()
        audioPlayBin.setURI(URI.create(item.uri))
        val stateChange = audioPlayBin.play()

        if (stateChange == StateChangeReturn.FAILURE) {
            throw IllegalStateException("Failed to play audio")
        }
    }

    fun pause() {
        audioPlayBin.pause()
    }

    fun stop() {
        audioPlayBin.stop()
    }

    fun resume() {
        audioPlayBin.play()
    }

    fun seekTo(progress: Long) {
        val success = audioPlayBin.seek(progress, TimeUnit.MILLISECONDS)
        if (!success) {
            println("Failed to seek")
        }
    }

    fun getProgress(): Long {
        return audioPlayBin.queryPosition(TimeUnit.MILLISECONDS)
    }

    fun getDuration(): Long {
        return audioPlayBin.queryDuration(TimeUnit.MILLISECONDS)
    }

    fun isPlaying() = audioPlayBin.isPlaying
}