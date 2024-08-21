package net.heb.soli

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.Player
import net.heb.soli.stream.StreamRepository
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
    val homeScreenViewModel = HomeScreenViewModel(repo)

    val scaffoldState = rememberBottomSheetScaffoldState()

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
            sheetBackgroundColor = Color(0xFFD2D4FF),
        )
        {
            HomeScreen(homeScreenViewModel) {
                player.startStream(it)
            }
        }
    }
}
