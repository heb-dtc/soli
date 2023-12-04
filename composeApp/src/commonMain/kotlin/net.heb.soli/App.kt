package net.heb.soli

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamRepository
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val player = koinInject<Player>()
    val repo = koinInject<StreamRepository>()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    MaterialTheme {
        BottomSheetScaffold(
            sheetContent = {
                MiniPlayer(player)
            }
        )
        {
            HomeScreen(repo) {
                player.startStream(it)
            }
        }
    }
}
