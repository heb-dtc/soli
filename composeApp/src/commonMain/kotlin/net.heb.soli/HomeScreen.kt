package net.heb.soli

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.heb.soli.stream.StreamItem
import net.heb.soli.stream.StreamType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>(),
    modifier: Modifier = Modifier,
    navigateToPodcastEpisodes: (id: Long) -> Unit
) {
    val state = viewModel.state.collectAsState()

    HomeScreen(
        state = state.value,
        modifier = modifier,
        onStartStream = viewModel::startStream,
        navigateToPodcastEpisodes = navigateToPodcastEpisodes
    )
}

@Composable
fun HomeScreen(
    state: HomeScreenState,
    modifier: Modifier = Modifier,
    onStartStream: (StreamItem) -> Unit,
    navigateToPodcastEpisodes: (id: Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Radios",
            Modifier.padding(8.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filter {
                it.type == StreamType.Radio
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Ambient", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filter {
                it.type == StreamType.Ambient
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Podcast",
            Modifier.padding(8.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filter {
                it.type == StreamType.PodcastFeed
            },
            onClick = {
                it.remoteId?.let { feedId ->
                    navigateToPodcastEpisodes(feedId)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Spotify",
            Modifier.padding(8.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filter {
                it.type == StreamType.SpotifyPlaylist
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Songs", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filter {
                it.type == StreamType.Song
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
    }
}

@Composable
fun ItemGrid(
    items: List<StreamItem>,
    onClick: (StreamItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        items.forEachIndexed { index, media ->
            RadioItem(
                streamItem = media,
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = onClick
            )
        }
    }
}

@Composable
internal fun StreamItemList(medias: List<StreamItem>, onClick: (StreamItem) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        itemsIndexed(items = medias) { index, media ->
            val modifier = if (index % 2 == 0) {
                Modifier.padding(start = 0.dp, end = 4.dp, top = 8.dp, bottom = 0.dp)
            } else {
                Modifier.padding(start = 4.dp, end = 0.dp, top = 8.dp, bottom = 0.dp)
            }

            RadioItem(
                media,
                modifier, onClick
            )
        }
    }
}

@Composable
fun RadioItem(
    streamItem: StreamItem,
    modifier: Modifier,
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
            .clickable {
                onClick(streamItem)
            }
    ) {
        Text(
            streamItem.name.uppercase(),
            Modifier
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
