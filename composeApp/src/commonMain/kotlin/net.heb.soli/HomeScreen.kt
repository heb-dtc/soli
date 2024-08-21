package net.heb.soli

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.heb.soli.stream.Stream
import net.heb.soli.stream.StreamItem

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel, onStartStream: (StreamItem) -> Unit) {
    val state = viewModel.state.collectAsState()

    HomeScreen(
        state = state.value,
        onStartStream = onStartStream
    )
}

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onStartStream: (StreamItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Radios", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        ItemRow(
            items = state.streamItems.filter {
                !it.name.contains("dikro", ignoreCase = true) && !it.name.contains(
                    "compile2noel",
                    ignoreCase = true
                )
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text("Ambiant", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        ItemRow(
            items = state.streamItems.takeLast(2),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Podcast",
            Modifier.padding(8.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        ItemRow(
            items = listOf(StreamItem(id = 1092, name = "No items", uri = "")),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text(
            "Spotify",
            Modifier.padding(8.dp),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        ItemRow(
            items = listOf(StreamItem(id = 1092, name = "No items", uri = "")),
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text("Songs", Modifier.padding(8.dp), fontSize = 48.sp, fontWeight = FontWeight.Bold)
        ItemRow(
            items = state.streamItems.takeWhile {
                it.name.contains("dikro", ignoreCase = true) || it.name.contains(
                    "compile2noel",
                    ignoreCase = true
                )
            },
            onClick = onStartStream,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
    }
}

@Composable
fun ItemRow(items: List<StreamItem>, onClick: (StreamItem) -> Unit, modifier: Modifier = Modifier) {
    val colors = listOf(0xFFe63946, 0xFFf1faee, 0xFFa8dadc, 0xFF457b9d, 0xFF1d3557)

    Row(
        modifier = modifier.padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        items.forEachIndexed { index, media ->
            val colorIndex = if (index >= colors.size) (colors.indices).random() else index

            RadioItem(
                streamItem = media,
                color = Color(colors[colorIndex]),
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = onClick
            )
        }
    }
}

@Composable
internal fun StreamItemList(medias: List<StreamItem>, onClick: (StreamItem) -> Unit) {
    val colors = listOf(0xFFe63946, 0xFFf1faee, 0xFFa8dadc, 0xFF457b9d, 0xFF1d3557)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        itemsIndexed(items = medias) { index, media ->
            val colorIndex = if (index >= colors.size) (colors.indices).random() else index

            val modifier = if (index % 2 == 0) {
                Modifier.padding(start = 0.dp, end = 4.dp, top = 8.dp, bottom = 0.dp)
            } else {
                Modifier.padding(start = 4.dp, end = 0.dp, top = 8.dp, bottom = 0.dp)
            }

            RadioItem(
                media, Color(colors[colorIndex]),
                modifier, onClick
            )
        }
    }
}

@Composable
fun RadioItem(
    streamItem: StreamItem,
    color: Color,
    modifier: Modifier,
    onClick: (StreamItem) -> Unit
) {
    Card(shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
        modifier = modifier
            .height(140.dp)
            .width(140.dp)
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
            color = Color.Black
        )
    }
}
