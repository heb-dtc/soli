package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import org.freedesktop.gstreamer.ElementFactory
import org.freedesktop.gstreamer.Gst
import org.freedesktop.gstreamer.elements.PlayBin
import java.net.URI

actual class PlayerBuilder {
    actual fun build(): Player {
        return GstPlayer()
    }
}

class GstPlayer : Player() {

    private val audioPlayBin: PlayBin

    init {
        Gst.init("SOLI")
        audioPlayBin = PlayBin("AudioPlayer")
        //audioPlayBin.setVideoSink(ElementFactory.make("fakesink", "videosink"))
        audioPlayBin.setAudioSink(ElementFactory.make("autoaudiosink", "audiosink"))
    }

    override fun play(item: StreamItem) {
        stop()
        audioPlayBin.setURI(URI.create(item.uri))
        audioPlayBin.play()
    }

    override fun pause() {
        audioPlayBin.pause()
    }

    override fun stop() {
        audioPlayBin.stop()
    }

    override fun resume() {
        audioPlayBin.play()
    }

    override fun isPlaying() = audioPlayBin.isPlaying
}