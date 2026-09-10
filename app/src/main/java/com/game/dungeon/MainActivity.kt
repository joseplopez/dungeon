package com.game.dungeon

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.game.dungeon.audio.MusicManager
import com.game.dungeon.ui.components.BottomPixelNav
import com.game.dungeon.ui.components.MusicToggleButton
import com.game.dungeon.ui.screens.DungeonScreen
import com.game.dungeon.ui.screens.EquipmentScreen
import com.game.dungeon.ui.screens.InnScreen
import com.game.dungeon.ui.screens.RelicsScreen
import com.game.dungeon.ui.screens.TownScreen
import com.game.dungeon.ui.theme.BgDarkest
import com.game.dungeon.ui.theme.PixelTheme
import com.game.dungeon.ui.viewmodels.DungeonViewModel
import com.game.dungeon.ui.viewmodels.EquipmentViewModel
import com.game.dungeon.ui.viewmodels.InnViewModel
import com.game.dungeon.ui.viewmodels.RelicsViewModel
import com.game.dungeon.ui.viewmodels.TownViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var musicManager: MusicManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        musicManager = MusicManager(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.isStatusBarVisible = false
                systemUiController.isNavigationBarVisible = false
            }

            val navController = rememberNavController()

            // Handle Lifecycle for music pausing/resuming
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> musicManager.pause()
                        Lifecycle.Event.ON_RESUME -> musicManager.resume()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Observe route changes for music
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            var isMuted by remember { mutableStateOf(false) }

            LaunchedEffect(currentRoute, isMuted) {
                musicManager.setMuted(isMuted)
                if (currentRoute == "dungeon") {
                    musicManager.play(R.raw.battle)
                } else {
                    musicManager.play(R.raw.prelude)
                }
            }

            val innViewModel: InnViewModel = hiltViewModel()
            val dungeonViewModel: DungeonViewModel = hiltViewModel()
            val relicsViewModel: RelicsViewModel = hiltViewModel()
            val townViewModel: TownViewModel = hiltViewModel()
            val equipmentViewModel: EquipmentViewModel = hiltViewModel()

            PixelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarkest
                ) {
                    Scaffold(
                        containerColor = BgDarkest
                    ) { padding ->
                        NavHost(
                            navController = navController, 
                            startDestination = "inn",
                            modifier = Modifier.padding(padding),
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }
                        ) {
                            composable("inn") {
                                InnScreen(
                                    onNavigateToDungeon = { party, floor ->
                                        dungeonViewModel.startRun(party, floor)
                                        navController.navigate("dungeon")
                                    },
                                    onNavigateToEquipment = { heroId ->
                                        navController.navigate("equipment/$heroId")
                                    },
                                    navController = navController,
                                    isMuted = isMuted,
                                    onToggleMusic = { isMuted = !isMuted },
                                    viewModel = innViewModel
                                )
                            }
                            composable("equipment/{heroId}") { backStackEntry ->
                                val heroId = backStackEntry.arguments?.getString("heroId") ?: ""
                                EquipmentScreen(
                                    heroId = heroId,
                                    viewModel = equipmentViewModel,
                                    isMuted = isMuted,
                                    onToggleMusic = { isMuted = !isMuted },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("dungeon") {
                                DungeonScreen(
                                    viewModel = dungeonViewModel,
                                    onBack = { navController.popBackStack() },
                                    isMuted = isMuted,
                                    onToggleMusic = { isMuted = !isMuted },
                                    navController = navController
                                )
                            }
                            composable("relics") {
                                RelicsScreen(
                                    navController = navController,
                                    isMuted = isMuted,
                                    onToggleMusic = { isMuted = !isMuted },
                                    viewModel = relicsViewModel
                                )
                            }
                            composable("town") {
                                TownScreen(
                                    navController = navController,
                                    isMuted = isMuted,
                                    onToggleMusic = { isMuted = !isMuted },
                                    viewModel = townViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicManager.stop()
    }
}
