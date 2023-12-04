package net.heb.soli.player

import net.heb.soli.stream.StreamItem

actual class PlayerBuilder {
    actual fun build(): Player {
        return Player(PlatformPlayer())
    }
}

actual class PlatformPlayer {

    actual fun play(item: StreamItem) {
        println("play")
    }

    actual fun pause() {
        println("pause")
    }

    actual fun stop() {
        println("stop")
    }

    actual fun resume() {
        println("resume")
    }

    actual fun isPlaying(): Boolean {
        return false
    }
}