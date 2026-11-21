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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.GameProgress
import com.example.data.LevelCatalog
import com.example.i18n.LocalAppStrings
import com.example.ui.GameViewModel
import com.example.ui.components.CoinPill
import com.example.ui.components.GlassCard
import com.example.ui.components.MiniPuzzlePreview
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonTopBar
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyDeepest
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DailyChallengeScreen(
    progress: GameProgress,
    onPlayDaily: (String) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val strings = LocalAppStrings.current
    val todayKey = remember { GameViewModel.todayDateKey() }
    val last7Days = remember { GameViewModel.recent7DateKeys() }
    val todaysPuzzle = remember(todayKey) { LevelCatalog.generateDailyPuzzle(todayKey) }
    val isCompletedToday = progress.completedDailyDates.contains(todayKey)

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NeonTopBar(
                title = strings.dailyChallenge,
                onBack = onBack,
                trailing = { CoinPill(coins = progress.coins) },
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Streak & 7-day Calendar Strip
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonAmber.copy(alpha = 0.6f),
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(26.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${progress.dailyStreak} ${strings.streakDays}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = NeonAmber,
                                )
                            }
                            Text(
                                text = "${progress.completedDailyDates.size} ${strings.dailyCompletedCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            last7Days.forEach { dayKey ->
                                val done = progress.completedDailyDates.contains(dayKey)
                                val isToday = dayKey == todayKey
                                val dayNum = dayKey.takeLast(2)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when {
                                            done -> NeonEmerald.copy(alpha = 0.22f)
                                            isToday -> NeonMagenta.copy(alpha = 0.2f)
                                            else -> NavySurfaceElevated
                                        },
                                        border = BorderStroke(
                                            1.5.dp,
                                            when {
                                                done -> NeonEmerald
                                                isToday -> NeonMagenta
                                                else -> NavyCardBorder
                                            },
                                        ),
                                        modifier = Modifier.size(36.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (done) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = null,
                                                    tint = NeonEmerald,
                                                    modifier = Modifier.size(18.dp),
                                                )
                                            } else {
                                                Text(
                                                    text = dayNum,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = if (isToday) TextPrimary else TextMuted,
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isToday) "TODAY" else dayNum,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isToday) NeonMagenta else TextMuted,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Today's Featured Daily Puzzle Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isCompletedToday) NeonEmerald else NeonMagenta,
                    cornerRadius = 26.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = null,
                                tint = if (isCompletedToday) NeonEmerald else NeonMagenta,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = todayKey,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isCompletedToday) NeonEmerald else NeonMagenta,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        MiniPuzzlePreview(
                            level = todaysPuzzle,
                            modifier = Modifier.size(190.dp),
                            lineColor = if (isCompletedToday) NeonEmerald else NeonMagenta,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${todaysPuzzle.vertices.size} Nodes • ${todaysPuzzle.edges.size} ${strings.edgesLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonAmber.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MonetizationOn,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isCompletedToday) "+15 Replay Bonus" else "+75 Daily Bonus",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = NeonAmber,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        NeonGradientButton(
                            text = if (isCompletedToday) strings.completedToday else strings.playTodaysPuzzle,
                            onClick = { onPlayDaily(todayKey) },
                            colors = if (isCompletedToday) listOf(NeonEmerald, NeonCyan) else listOf(NeonMagenta, NeonCyan),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = NavyDeepest,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("daily_play_button"),
                        )
                    }
                }
            }
        }
    }
}
