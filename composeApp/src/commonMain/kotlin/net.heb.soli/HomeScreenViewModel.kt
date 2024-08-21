package net.heb.soli

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamRepository

data class HomeScreenState(
    val streamItems: List<StreamItem> = emptyList()
)

class HomeScreenViewModel(private val streamRepository: StreamRepository) : ViewModel() {

    private var _state = MutableStateFlow(HomeScreenState())
    val state = _state

    init {
        GlobalScope.launch {
            _state.value = HomeScreenState(streamItems = streamRepository.getStreams())
        }
    }
}