package net.heb.soli.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiniPlayer(player: Player) {
    val state = player.getState().collectAsState()

    MiniPlayer(state.value, player::stop, player::resume)
}

@Composable
fun MiniPlayer(state: PlayerState, onStop: () -> Unit, onPlay: () -> Unit) {
    Row(
        modifier = Modifier.padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${state.item?.name}".uppercase(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Icon(
            imageVector = if (state.isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
                .clickable {
                    if (state.isPlaying) {
                        onStop()
                    } else {
                        onPlay()
                    }
                },
        )
    }
}
