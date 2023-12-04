package net.heb.soli.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.heb.soli.stream.StreamItem

expect class PlayerBuilder {
    fun build(): Player
}

data class PlayerState(val item: StreamItem?, val isPlaying: Boolean)

expect class PlatformPlayer {
    fun play(item: StreamItem)
    fun resume()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
}

class Player(private val platformPlayer: PlatformPlayer) {
    private var _state: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState(null, false))

    fun startStream(item: StreamItem) {
        _state.value = PlayerState(item, true)
        platformPlayer.play(item)
    }

    fun resume() {
        if (_state.value.item == null) return

        _state.value = _state.value.copy(isPlaying = true)
        platformPlayer.resume()
    }

    fun pause() {
        _state.value = _state.value.copy(isPlaying = false)
        platformPlayer.pause()
    }

    fun stop() {
        _state.value = PlayerState(null, false)
        platformPlayer.stop()
    }

    fun isPlaying(): Boolean {
        return platformPlayer.isPlaying()
    }

    fun getState(): StateFlow<PlayerState> {
        return _state
    }
}