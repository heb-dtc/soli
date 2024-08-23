package net.heb.soli.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

data class PodcastEpisodesScreenScreenState(
    val streamItems: List<StreamItem> = emptyList()
)

class PodcastEpisodesScreenViewModel(
    private val streamRepository: StreamRepository,
    private val player: Player
) : ViewModel() {

    private var _state = MutableStateFlow(PodcastEpisodesScreenScreenState())
    val state = _state

    init {
        viewModelScope.launch {
            streamRepository.observePodcastEpisodes("555224").collect {
                _state.value = PodcastEpisodesScreenScreenState(streamItems = it)
            }
        }
    }

    fun startStream(item: StreamItem) {
        player.startStream(item)
    }
}