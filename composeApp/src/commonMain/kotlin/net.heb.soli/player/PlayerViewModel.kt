package net.heb.soli.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.heb.soli.stream.StreamType

data class PlayerViewState(
    val streamTitle: String = "No media",
    val streamSubtitle: String? = null,
    val isPlaying: Boolean = false,
    val canSeek: Boolean = false,
    val progress: Long = 0,
    val duration: Long = 0
)

class PlayerViewModel(private val player: Player) : ViewModel() {

    val state = player.getState().map {
        PlayerViewState(
            streamTitle = it.item?.name ?: "No media",
            streamSubtitle = it.item?.name,
            isPlaying = it.isPlaying,
            canSeek = it.item?.type == StreamType.PodcastEpisode,
            progress = it.progress,
            duration = it.duration
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PlayerViewState(),
    )

    fun resume() {
        player.resume()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun seekTo(progress: Long) {
        player.seekTo(progress)
    }
}