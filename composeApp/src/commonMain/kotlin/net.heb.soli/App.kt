package net.heb.soli

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamRepository
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Soli()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Soli() {

    val player = koinInject<Player>()
    val repo = koinInject<StreamRepository>()

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val miniPlayerHeight = 56.dp

    MaterialTheme {
        BottomSheetScaffold(
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(miniPlayerHeight)
                ) {
                    MiniPlayer(player)
                }
            },
            scaffoldState = scaffoldState,
            sheetPeekHeight = miniPlayerHeight,
            sheetBackgroundColor = Color(0xFFa8dadc),
        )
        {
            HomeScreen(repo) {
                player.startStream(it)
            }
        }
    }
}
