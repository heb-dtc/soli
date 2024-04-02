package net.heb.soli

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

data class HomeScreenState(
    val streamItems: List<StreamItem> = emptyList()
)

class HomeScreenViewModel(private val streamRepository: StreamRepository) : ViewModel() {

    private var _state = MutableStateFlow(HomeScreenState())
    val state = _state

    init {
        _state.value = HomeScreenState(streamItems = streamRepository.getStreams())
    }
}