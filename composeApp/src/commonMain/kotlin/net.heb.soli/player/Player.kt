package net.heb.soli.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.heb.soli.stream.StreamItem

expect class PlayerBuilder {
    fun build(): Player
}

data class PlayerState(val item: StreamItem?, val isPlaying: Boolean)

abstract class Player {
    private var _state: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState(null, false))

    fun startStream(item: StreamItem) {
        _state.value = PlayerState(item, false)
        play(item)
    }

    abstract fun play(item: StreamItem)
    abstract fun resume()
    abstract fun pause()
    abstract fun stop()
    abstract fun isPlaying(): Boolean
    fun getState(): StateFlow<PlayerState> {
        return _state
    }
}