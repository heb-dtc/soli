package net.heb.soli

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.heb.soli.stream.StreamItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>(),
    modifier: Modifier = Modifier,
    navigateToPodcastEpisodes: (id: Long) -> Unit
) {
    val state = viewModel.state.collectAsState()
    val openAddYoutubeVideoDialog = remember { mutableStateOf(false) }

    if (openAddYoutubeVideoDialog.value) {
        var url by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = {
                openAddYoutubeVideoDialog.value = false
            }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        "Add Youtube Video",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("video url") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("video name") }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                viewModel.addYoutubeVideo(name, url)
                                openAddYoutubeVideoDialog.value = false
                            }
                        ) {
                            Text(
                                text = "Confirm",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    HomeScreen(
        state = state.value,
        modifier = modifier,
        addYoutubeVideo = {
            openAddYoutubeVideoDialog.value = true
        },
        onStartStream = viewModel::startStream,
        navigateToPodcastEpisodes = navigateToPodcastEpisodes
    )
}

@Composable
fun HomeScreen(
    state: HomeScreenState,
    modifier: Modifier = Modifier,
    addYoutubeVideo: () -> Unit,
    onStartStream: (StreamItem) -> Unit,
    navigateToPodcastEpisodes: (id: Long) -> Unit,
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
            items = state.streamItems.filterIsInstance<StreamItem.RadioItem>(),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Ambient", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filterIsInstance<StreamItem.AmbientItem>(),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Youtube",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            OutlinedButton(
                onClick = { addYoutubeVideo() },
                shape = CircleShape,
                modifier = Modifier.padding(start = 4.dp),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        ItemGrid(
            items = state.streamItems.filterIsInstance<StreamItem.YoutubeItem>(),
            onClick = {
//                if ((it as StreamItem.YoutubeItem).downloaded) {
//                    //onStartStream(it)
//                }
            },
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
            items = state.streamItems.filterIsInstance<StreamItem.PodcastFeedItem>(),
            onClick = {
                navigateToPodcastEpisodes(it.id)
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
            items = state.streamItems.filterIsInstance<StreamItem.SpotifyPlaylistItem>(),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Songs", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        ItemGrid(
            items = state.streamItems.filterIsInstance<StreamItem.SongItem>(),
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
            StreamItem(
                streamItem = media,
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = onClick
            )
        }
    }
}

@Composable
fun StreamItem(
    streamItem: StreamItem,
    modifier: Modifier,
    onClick: (StreamItem) -> Unit
) {
    val borderColor = when (streamItem) {
        is StreamItem.YoutubeItem -> {
            if (streamItem.downloaded) MaterialTheme.colorScheme.secondary
            else blue
        }

        else -> MaterialTheme.colorScheme.secondary
    }

    Card(shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        border = BorderStroke(2.dp, borderColor),
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
