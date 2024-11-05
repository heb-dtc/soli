package net.heb.soli

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import net.heb.soli.di.viewModelModule
import net.heb.soli.player.MiniPlayer
import net.heb.soli.player.ModalPlayer
import net.heb.soli.player.Player
import net.heb.soli.podcast.PodcastEpisodesScreen
import net.heb.soli.stream.StreamRepository
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.module.rememberKoinModules

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home")
    data object PodcastEpisodes : Screen(route = "podcast-episodes/{feedId}")
}

@Composable
fun App() {
    KoinContext {

        rememberKoinModules {
            listOf(
                viewModelModule(),
            )
        }

        SoliTheme {
            Soli()
        }
    }
}

data class TopAppBarState(
    val appBar: @Composable () -> Unit,
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun Soli() {
    val player = koinInject<Player>()
    val streamRepository = koinInject<StreamRepository>()

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

    val openAddYoutubeVideoDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var topAppBarState by remember { mutableStateOf(TopAppBarState(appBar = {})) }

    Scaffold(
        topBar = {
            topAppBarState.appBar()
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showBottomSheet = true
                    }
            ) {
                MiniPlayer()
            }
        },
    )
    { innerPadding ->

        if (openAddYoutubeVideoDialog.value) {
            var url by remember { mutableStateOf("") }
            var name by remember { mutableStateOf("") }

            Dialog(
                onDismissRequest = {
                    openAddYoutubeVideoDialog.value = false
                }) {
                Card(
                    modifier = Modifier
                        //.fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
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
                                unfocusedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("video name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        streamRepository.addYoutubeVideo(name, url)
                                    }
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

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    LaunchedEffect(Unit) {
                        topAppBarState = TopAppBarState(
                            appBar = {
                                TopAppBar(
                                    title = {},
                                    actions = {
                                        IconButton(onClick = {
                                            openAddYoutubeVideoDialog.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = "Add youtube video",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        IconButton(onClick = {

                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Sync,
                                                contentDescription = "Sync",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    },
                                )
                            }
                        )
                    }

                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        navigateToPodcastEpisodes = { id ->
                            navController.navigate(
                                Screen.PodcastEpisodes.route.replace(
                                    "{feedId}",
                                    id.toString()
                                )
                            )
                        })
                }

                composable(
                    Screen.PodcastEpisodes.route,
                    arguments = listOf(
                        navArgument("feedId") { type = NavType.LongType },
                    ),
                ) {
                    LaunchedEffect(Unit) {
                        topAppBarState = TopAppBarState(
                            appBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            "Podcast Episodes",
                                            fontSize = if (isCompact) 36.sp else 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
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
                        )
                    }

                    PodcastEpisodesScreen(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    sheetMaxWidth = 8000.dp,
                    onDismissRequest = { showBottomSheet = false },
                    content = {
                        ModalPlayer()
                    }
                )
            }
        }
    }
}

fun setTopAppBar(any: Any) {

}
