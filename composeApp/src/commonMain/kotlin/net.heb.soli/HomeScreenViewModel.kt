package net.heb.soli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

data class HomeScreenState(
    val streamItems: List<StreamItem> = emptyList()
)

class HomeScreenViewModel(
    private val streamRepository: StreamRepository,
    private val player: Player
) : ViewModel() {

    private var _state = MutableStateFlow(HomeScreenState())
    val state = _state

    private val streams = combine(
        streamRepository.observeRadios(),
        streamRepository.observeAmbientStream(),
        streamRepository.observerPodcastFeeds(),
        streamRepository.observeTracks()
    ) { radios, ambients, podcasts, tracks ->
        radios + ambients + podcasts + tracks
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            streamRepository.sync()
        }

        viewModelScope.launch {
            streams.collect {
                _state.value = HomeScreenState(streamItems = it)
            }
        }
    }

    fun startStream(item: StreamItem) {
        player.startStream(item)
    }
}