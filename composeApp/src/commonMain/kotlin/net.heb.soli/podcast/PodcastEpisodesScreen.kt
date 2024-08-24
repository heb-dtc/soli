package net.heb.soli.podcast

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.heb.soli.stream.StreamItem

@Composable
fun PodcastEpisodesScreen(
    viewModel: PodcastEpisodesScreenViewModel,
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
    streamItem: StreamItem,
    modifier: Modifier = Modifier,
    onClick: (StreamItem) -> Unit
) {
    Card(shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
            .padding(8.dp)
            .clickable {
                onClick(streamItem)
            }
    ) {
        Text(
            streamItem.name.uppercase(),
            Modifier
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Black
        )
    }
}