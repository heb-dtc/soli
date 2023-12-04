package net.heb.soli.player

import net.heb.soli.stream.StreamItem
import org.freedesktop.gstreamer.ElementFactory
import org.freedesktop.gstreamer.Gst
import org.freedesktop.gstreamer.elements.PlayBin
import java.net.URI

actual class PlayerBuilder {
    actual fun build(): Player {
        return Player(PlatformPlayer())
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
        audioPlayBin.play()
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

    actual fun isPlaying() = audioPlayBin.isPlaying
}