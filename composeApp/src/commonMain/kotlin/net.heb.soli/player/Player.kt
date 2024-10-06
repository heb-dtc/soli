package net.heb.soli.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

expect class PlayerBuilder {
    fun build(): Player
}

data class PlayerState(
    val item: StreamItem? = null,
    val isPlaying: Boolean = false,
    val progress: Long = 0,
    val duration: Long = 0
)

expect class PlatformPlayer {
    fun play(item: StreamItem)
    fun resume()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun getProgress(): Long
    fun getDuration(): Long
    fun seekTo(progress: Long)
}

class Player(private val platformPlayer: PlatformPlayer, private val repository: StreamRepository) {
    private var _state: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState())

    private var progressJob: Job? = null

    private fun startProgressMonitor() {
        progressJob = GlobalScope.launch {
            while (isActive) {
                if (_state.value.item != null && _state.value.isPlaying) {
                    val progress = withContext(Dispatchers.Main) {
                        getProgress()
                    }
                    val duration = withContext(Dispatchers.Main) {
                        getDuration()
                    }
                    _state.value =
                        _state.value.copy(progress = progress, duration = duration)
                    delay(1000)
                }
            }
        }
    }

    private fun stopProgressMonitor() {
        progressJob?.cancel()
        progressJob = null
    }

    fun startStream(item: StreamItem) {
        platformPlayer.play(item)

        _state.value = _state.value.copy(item = item, isPlaying = true, duration = getDuration())
        startProgressMonitor()
    }

    fun resume() {
        if (_state.value.item == null) return

        platformPlayer.resume()
        _state.value = _state.value.copy(isPlaying = true)
        startProgressMonitor()
    }

    fun pause() {
        platformPlayer.pause()
        _state.value = _state.value.copy(isPlaying = false)
        if (_state.value.item is StreamItem.PodcastEpisodeItem) {
            GlobalScope.launch {
                repository.updateEpisodeTimeCode(
                    id = (_state.value.item as StreamItem.PodcastEpisodeItem).id,
                    progress = _state.value.progress
                )
            }
        }
        stopProgressMonitor()
    }

    fun stop() {
        platformPlayer.stop()
        _state.value = _state.value.copy(null, false, 0, 0)
        stopProgressMonitor()
    }

    private fun getProgress(): Long {
        val progress = platformPlayer.getProgress()
        return progress
    }

    fun seekTo(progress: Long) {
        println("Seeking to $progress")
        platformPlayer.seekTo(progress)
    }

    private fun getDuration(): Long {
        val duration = platformPlayer.getDuration()
        return duration
    }

    fun isPlaying(): Boolean {
        return platformPlayer.isPlaying()
    }

    fun getState(): StateFlow<PlayerState> {
        return _state
    }
}