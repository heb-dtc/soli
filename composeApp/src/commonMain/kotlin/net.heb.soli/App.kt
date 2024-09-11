package net.heb.soli

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.Player
import net.heb.soli.podcast.PodcastEpisodesScreen
import net.heb.soli.podcast.PodcastEpisodesScreenViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home")
    data object PodcastEpisodes : Screen(route = "podcast-episodes")
}

@Composable
fun App() {
    KoinContext {
        SoliTheme {
            Soli()
        }
    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun Soli() {

    val player = koinInject<Player>()
    val homeScreenViewModel: HomeScreenViewModel = koinInject()
    val podcastEpisodesScreenViewModel: PodcastEpisodesScreenViewModel = koinInject()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val miniPlayerHeight = 56.dp

    val navController = rememberNavController()
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: Screen.Home.route

    val windowSizeClass = calculateWindowSizeClass()
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        topBar = {
            if (currentScreen == Screen.PodcastEpisodes.route) {
                TopAppBar(
                    title = {
                        Text(
                            "Podcast Episodes",
                            fontSize = if (isCompact) 36.sp else 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //showBottomSheet = true
                    }
            ) {
                MiniPlayer(player)
            }
        },
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeScreenViewModel,
                        modifier = Modifier.padding(innerPadding),
                        navigateToPodcastEpisodes = {
                            navController.navigate(Screen.PodcastEpisodes.route)
                        })
                }

                composable(Screen.PodcastEpisodes.route) {
                    PodcastEpisodesScreen(
                        viewModel = podcastEpisodesScreenViewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }

            //TODO implement a full screen player
//        if (showBottomSheet) {
//            ModalBottomSheet(
//                sheetState = sheetState,
//                sheetMaxWidth = 8000.dp,
//                onDismissRequest = { showBottomSheet = false },
//                content = {
//                    Button(onClick = {
//                        showBottomSheet = false
//                    }) {
//                        Text("Hide bottom sheet")
//                    }
//
//                }
//            )
//        }
        }
    }
}
