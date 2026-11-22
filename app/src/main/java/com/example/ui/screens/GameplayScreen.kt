package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.GameProgress
import com.example.engine.PuzzleStatus
import com.example.i18n.LocalAppStrings
import com.example.ui.ActivePuzzleState
import com.example.ui.GameViewModel
import com.example.ui.components.CoinPill
import com.example.ui.components.GlassCard
import com.example.ui.components.LevelCompleteOverlay
import com.example.ui.components.LevelFailedOverlay
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonPuzzleCanvas
import com.example.ui.components.PauseMenuOverlay
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun GameplayScreen(
    state: ActivePuzzleState,
    progress: GameProgress,
    toastEvents: SharedFlow<String>,
    onBeginStroke: (Int) -> Unit,
    onTryMoveTo: (Int) -> Boolean,
    onLiftFinger: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onUseHint: (String) -> Unit,
    onNextLevel: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onHome: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastEvents) {
        toastEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    BackHandler {
        when (state.status) {
            PuzzleStatus.PAUSED -> onResume()
            PuzzleStatus.READY, PuzzleStatus.DRAWING -> onPause()
            else -> onHome()
        }
    }

    val totalEdges = state.level.edges.size.coerceAtLeast(1)
    val drawnCount = state.drawnEdgeIds.size
    val progressFrac = drawnCount.toFloat() / totalEdges.toFloat()

    NeonGridBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Top HUD
                Row(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NavySurfaceElevated.copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, NavyCardBorder),
                    ) {
                        IconButton(
                            onClick = onPause,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("gameplay_pause_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Pause,
                                contentDescription = strings.pause,
                                tint = TextPrimary,
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (state.level.isDailyChallenge) {
                                strings.dailyChallenge
                            } else {
                                "${strings.levelLabel} ${state.level.id}"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = state.level.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    CoinPill(coins = progress.coins)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress + Timer bar
                GlassCard(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth(),
                    cornerRadius = 16.dp,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${strings.edgesLabel}: $drawnCount / $totalEdges",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (drawnCount == totalEdges) NeonEmerald else TextPrimary,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val mins = state.elapsedSeconds / 60
                                val secs = state.elapsedSeconds % 60
                                Text(
                                    text = "%d:%02d".format(mins, secs),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFrac },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = if (drawnCount == totalEdges) NeonEmerald else NeonCyan,
                            trackColor = NavySurfaceElevated,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Helpful prompt banner
                Text(
                    text = if (state.revealedHintSteps > 0) strings.hintActivePrompt else strings.startDrawingPrompt,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (state.revealedHintSteps > 0) NeonAmber else TextSecondary,
                )

                // Interactive Puzzle Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .testTag("puzzle_canvas_box"),
                    contentAlignment = Alignment.Center,
                ) {
                    NeonPuzzleCanvas(
                        level = state.level,
                        status = state.status,
                        currentVertexId = state.currentVertexId,
                        drawnEdgeIds = state.drawnEdgeIds,
                        hintPath = state.hintPath,
                        revealedHintSteps = state.revealedHintSteps,
                        invalidEdgeFlashId = state.invalidEdgeFlashId,
                        attemptNonce = state.attemptNonce,
                        onBeginStroke = onBeginStroke,
                        onTryMoveTo = onTryMoveTo,
                        onLiftFinger = onLiftFinger,
                    )
                }

                // Bottom Controls: Restart & Hint
                Row(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onRestart,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("gameplay_restart_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NavyCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = NavySurfaceElevated.copy(alpha = 0.7f),
                            contentColor = TextPrimary,
                        ),
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.restart, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = { onUseHint(strings.notEnoughCoins) },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("gameplay_hint_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.65f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = NeonViolet.copy(alpha = 0.14f),
                            contentColor = NeonAmber,
                        ),
                    ) {
                        Icon(Icons.Rounded.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${strings.hint} (${GameViewModel.HINT_COST})", maxLines = 1)
                    }
                }
            }

            // Overlays
            when (state.status) {
                PuzzleStatus.PAUSED -> PauseMenuOverlay(
                    onResume = onResume,
                    onRestart = onRestart,
                    onSettings = onOpenSettings,
                    onHome = onHome,
                )

                PuzzleStatus.COMPLETE -> LevelCompleteOverlay(
                    isDaily = state.level.isDailyChallenge,
                    levelNumber = state.level.id,
                    hasNextLevel = !state.level.isDailyChallenge && state.level.id < 100,
                    result = state.completionResult,
                    onNextLevel = { onNextLevel(state.level.id + 1) },
                    onReplay = onRestart,
                    onHome = onHome,
                )

                PuzzleStatus.FAILED -> LevelFailedOverlay(
                    reason = state.failureReason,
                    coins = progress.coins,
                    onRetry = onRestart,
                    onUseHint = { onUseHint(strings.notEnoughCoins) },
                    onHome = onHome,
                )

                PuzzleStatus.READY, PuzzleStatus.DRAWING -> Unit
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding()
                    .padding(bottom = 74.dp),
            )
        }
    }
}
