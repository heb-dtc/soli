package net.heb.soli

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.Player
import net.heb.soli.podcast.PodcastEpisodesScreen
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home")
    data object PodcastEpisodes : Screen(route = "podcast-episodes")
}

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
    val homeScreenViewModel: HomeScreenViewModel = koinInject()

    val scaffoldState = rememberBottomSheetScaffoldState()

    val miniPlayerHeight = 56.dp

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
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
            { innerPadding ->
                HomeScreen(homeScreenViewModel, Modifier.padding(innerPadding)) {
                    player.startStream(it)
                }
            }
        }

        composable(Screen.PodcastEpisodes.route) {
            PodcastEpisodesScreen()
        }
    }

//    MaterialTheme {
//        BottomSheetScaffold(
//            sheetContent = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(miniPlayerHeight)
//                ) {
//                    MiniPlayer(player)
//                }
//            },
//            scaffoldState = scaffoldState,
//            sheetPeekHeight = miniPlayerHeight,
//            sheetBackgroundColor = Color(0xFFD2D4FF),
//        )
//        { innerPadding ->
//            HomeScreen(homeScreenViewModel, Modifier.padding(innerPadding)) {
//                player.startStream(it)
//            }
//        }
//    }
}
