package net.heb.soli.player

import net.heb.soli.stream.StreamItem

actual class PlayerBuilder {
    actual fun build(): Player {
        return IosPlayer()
    }
}

class IosPlayer : Player {
    override fun startStream(item: StreamItem) {
        println("startRadio: $item")
    }

    override fun play() {
        println("play")
    }

    override fun pause() {
        println("pause")
    }

    override fun stop() {
        println("stop")
    }

    override fun isPlaying(): Boolean {
        return false
    }
}