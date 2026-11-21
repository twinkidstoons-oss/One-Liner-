package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.stringsFor
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.OneLineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneLineAppRoot()
        }
    }
}

@Composable
fun OneLineAppRoot(
    gameViewModel: GameViewModel = viewModel(),
) {
    val progress by gameViewModel.progress.collectAsStateWithLifecycle()
    val currentScreen by gameViewModel.currentScreen.collectAsStateWithLifecycle()
    val puzzleState by gameViewModel.puzzleState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> gameViewModel.onAppForegroundChanged(true)
                Lifecycle.Event.ON_PAUSE -> gameViewModel.onAppForegroundChanged(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val language = AppLanguage.fromCode(progress.settings.languageCode)
    val strings = stringsFor(language)

    CompositionLocalProvider(LocalAppStrings provides strings) {
        OneLineTheme {
            Crossfade(
                targetState = currentScreen,
                animationSpec = tween(durationMillis = 220),
                label = "screen_transition",
            ) { screen ->
                when (screen) {
                    AppScreen.Splash -> SplashScreen(
                        onFinished = { gameViewModel.finishSplash() },
                    )

                    AppScreen.Onboarding -> OnboardingScreen(
                        onComplete = { gameViewModel.completeOnboarding() },
                    )

                    AppScreen.Home -> HomeScreen(
                        progress = progress,
                        onPlayCurrentLevel = { levelId -> gameViewModel.startLevel(levelId) },
                        onOpenLevels = { gameViewModel.navigateTo(AppScreen.LevelSelect) },
                        onOpenDaily = { gameViewModel.navigateTo(AppScreen.DailyChallenge) },
                        onOpenStats = { gameViewModel.navigateTo(AppScreen.Statistics) },
                        onOpenSettings = { gameViewModel.navigateTo(AppScreen.Settings) },
                        onOpenHowToPlay = { gameViewModel.navigateTo(AppScreen.HowToPlay) },
                    )

                    AppScreen.LevelSelect -> LevelSelectScreen(
                        progress = progress,
                        onSelectLevel = { levelId -> gameViewModel.startLevel(levelId) },
                        onBack = { gameViewModel.navigateBack() },
                    )

                    AppScreen.Gameplay -> GameplayScreen(
                        state = puzzleState,
                        progress = progress,
                        toastEvents = gameViewModel.toastEvents,
                        onBeginStroke = { vId -> gameViewModel.beginStrokeAt(vId) },
                        onTryMoveTo = { vId -> gameViewModel.tryMoveTo(vId) },
                        onLiftFinger = { gameViewModel.onLiftFinger() },
                        onPause = { gameViewModel.pausePuzzle() },
                        onResume = { gameViewModel.resumePuzzle() },
                        onRestart = { gameViewModel.restartPuzzle() },
                        onUseHint = { msg -> gameViewModel.useHint(msg) },
                        onNextLevel = { nextId -> gameViewModel.startLevel(nextId) },
                        onOpenSettings = { gameViewModel.navigateTo(AppScreen.Settings) },
                        onHome = { gameViewModel.navigateHome() },
                    )

                    AppScreen.DailyChallenge -> DailyChallengeScreen(
                        progress = progress,
                        onPlayDaily = { dateKey -> gameViewModel.startDailyChallenge(dateKey) },
                        onBack = { gameViewModel.navigateBack() },
                    )

                    AppScreen.Statistics -> StatisticsScreen(
                        progress = progress,
                        onBack = { gameViewModel.navigateBack() },
                    )

                    AppScreen.Settings -> SettingsScreen(
                        settings = progress.settings,
                        onUpdateSettings = { transform -> gameViewModel.updateSettings(transform) },
                        onResetProgress = { gameViewModel.resetAllProgress() },
                        onBack = { gameViewModel.navigateBack() },
                    )

                    AppScreen.HowToPlay -> HowToPlayScreen(
                        onBack = { gameViewModel.navigateBack() },
                    )
                }
            }
        }
    }
}
