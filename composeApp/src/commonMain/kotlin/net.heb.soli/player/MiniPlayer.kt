package net.heb.soli.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(
            modifier = Modifier.weight(1f)
                .wrapContentWidth(Alignment.Start),
        ) {
            Text(
                text = "${state.item?.name}".uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

        }
        Row(
            modifier = Modifier.weight(1f)
                .wrapContentWidth(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
                    .clickable {
                        if (state.isPlaying) {
                            onStop()
                        } else {
                            onPlay()
                        }
                    },
            )

            Icon(
                Icons.Filled.Stop,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
                    .clickable {
                        onStop()
                    },
            )
        }
    }
}
