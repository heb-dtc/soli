package net.heb.soli.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MiniPlayer(viewModel: PlayerViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()

    MiniPlayer(state.value, viewModel::stop, viewModel::resume)
}

@Composable
fun MiniPlayer(state: PlayerViewState, onStop: () -> Unit, onPlay: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = state.streamTitle.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            if (state.canSeek) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp)
                        .height(4.dp),
                    progress = { state.progress.toFloat() / state.duration.toFloat() },
                    strokeCap = StrokeCap.Square,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp)
                        .height(4.dp),
                    progress = { 100f },
                    strokeCap = StrokeCap.Square,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                        .clickable {
                            onStop()
                        },
                )
            }
        }
    }
}
