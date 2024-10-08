package net.heb.soli.podcast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.heb.soli.db.PodcastFeedEntity
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

data class PodcastEpisodesScreenScreenState(
    val streamItems: List<StreamItem.PodcastEpisodeItem> = emptyList(),
    val feedName: String = ""
)

class PodcastEpisodesScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val streamRepository: StreamRepository,
    private val player: Player
) : ViewModel() {

    private var podcastId = savedStateHandle.get<Long>("feedId")!!

    private var _state = MutableStateFlow(PodcastEpisodesScreenScreenState())
    val state = _state

    init {
        viewModelScope.launch {
            streamRepository.getPodcastFeed(podcastId)?.let {
                _state.value = state.value.copy(feedName = it.name)
            }
        }
        viewModelScope.launch {
            streamRepository.observePodcastEpisodes(podcastId).collect {
                _state.value = PodcastEpisodesScreenScreenState(streamItems = it)
            }
        }

        viewModelScope.launch {
            streamRepository.fetchPodcastEpisodes(podcastId)
        }
    }

    fun startStream(item: StreamItem) {
        player.startStream(item)

        if (item is StreamItem.PodcastEpisodeItem) {
            if (item.timeCode > 0) {
                player.seekTo(item.timeCode)
            }
        }

        viewModelScope.launch {
            streamRepository.updateEpisodePlayedStatus(id = item.id, played = true)
        }
    }
}