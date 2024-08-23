package net.heb.soli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        viewModelScope.launch {
            streamRepository.observeStreams().collect {
                _state.value = HomeScreenState(streamItems = it)
            }
        }
    }

    fun startStream(item: StreamItem) {
        player.startStream(item)
    }
}