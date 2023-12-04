package net.heb.soli.player

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MiniPlayer(player: Player) {
    val state = player.getState().collectAsState()

    MiniPlayer(state.value)
}

@Composable
fun MiniPlayer(state: PlayerState) {
    Row(
        modifier = Modifier.padding(4.dp)
    ) {
        Text("${state.item?.name}")
    }
}