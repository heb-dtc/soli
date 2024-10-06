package net.heb.soli.podcast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.heb.soli.stream.StreamItem
import net.heb.soli.toDuration
import org.koin.compose.viewmodel.koinNavViewModel

@Composable
fun PodcastEpisodesScreen(
    viewModel: PodcastEpisodesScreenViewModel = koinNavViewModel<PodcastEpisodesScreenViewModel>(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsState()

    PodcastEpisodesScreen(
        state = state.value,
        modifier = modifier,
        onStartStream = viewModel::startStream,
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun PodcastEpisodesScreen(
    state: PodcastEpisodesScreenScreenState,
    modifier: Modifier = Modifier,
    onStartStream: (StreamItem) -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass()
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isLarge = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    val colNumber = if (isCompact) {
        1
    } else {
        if (isLarge) {
            4
        } else {
            2
        }
    }

    LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(colNumber)) {
        items(state.streamItems) { streamItem ->
            PodcastEpisodeCard(streamItem = streamItem, onClick = onStartStream)
        }
    }
}

@Composable
fun PodcastEpisodeCard(
    streamItem: StreamItem.PodcastEpisodeItem,
    modifier: Modifier = Modifier,
    onClick: (StreamItem) -> Unit
) {
    Card(shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
            .padding(8.dp)
            .clickable {
                onClick(streamItem)
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = streamItem.name.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.weight(1f))

            if (streamItem.played) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${streamItem.timeCode.toDuration()} / ${streamItem.duration.toDuration()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = "played",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                Text(
                    text = streamItem.duration.toDuration(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}