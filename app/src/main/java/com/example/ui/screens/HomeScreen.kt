package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameProgress
import com.example.data.LevelCatalog
import com.example.engine.DifficultyTier
import com.example.i18n.LocalAppStrings
import com.example.ui.GameViewModel
import com.example.ui.components.CoinPill
import com.example.ui.components.GlassCard
import com.example.ui.components.MiniPuzzlePreview
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.NeonGridBackground
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyDeepest
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    progress: GameProgress,
    onPlayCurrentLevel: (Int) -> Unit,
    onOpenLevels: () -> Unit,
    onOpenDaily: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHowToPlay: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val currentLevelId = progress.highestUnlockedLevel.coerceIn(1, 100)
    val currentLevel = remember(currentLevelId) { LevelCatalog.getLevel(currentLevelId) }
    val todayKey = remember { GameViewModel.todayDateKey() }
    val yesterdayKey = remember { GameViewModel.yesterdayDateKey() }
    val liveDailyStreak = remember(progress.completedDailyDates, todayKey, yesterdayKey) {
        progress.currentDailyStreak(todayKey, yesterdayKey)
    }
    val isDailyDoneToday = progress.completedDailyDates.contains(todayKey)

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Bar: Brand + Coins + Quick Settings/Stats
            Row(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = strings.appTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        ),
                        color = TextPrimary,
                    )
                    Text(
                        text = "${progress.completedLevelsCount}/100 ${strings.levelsCompletedLabel} • ${progress.totalStars} ★",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonCyan,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CoinPill(coins = progress.coins)
                    Surface(
                        shape = CircleShape,
                        color = NavySurfaceElevated.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, NavyCardBorder),
                    ) {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("home_settings_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = strings.settings,
                                tint = TextPrimary,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hero Play Card
            GlassCard(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                borderColor = NeonCyan,
                cornerRadius = 26.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonCyan.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                        ) {
                            Text(
                                text = "${strings.levelLabel.uppercase()} $currentLevelId",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            )
                        }

                        Text(
                            text = tierTitle(currentLevel.tier, strings),
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonViolet,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        MiniPuzzlePreview(
                            level = currentLevel,
                            modifier = Modifier.size(168.dp),
                            lineColor = NeonCyan,
                        )
                    }

                    Text(
                        text = currentLevel.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                    Text(
                        text = "${currentLevel.vertices.size} Nodes • ${currentLevel.edges.size} ${strings.edgesLabel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    NeonGradientButton(
                        text = "${strings.continueLevel} $currentLevelId",
                        onClick = { onPlayCurrentLevel(currentLevelId) },
                        colors = listOf(NeonCyan, NeonEmerald),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = NavyDeepest,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_play_button"),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Challenge Banner Card
            GlassCard(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .testTag("home_daily_card"),
                borderColor = if (isDailyDoneToday) NeonEmerald else NeonMagenta,
                onClick = onOpenDaily,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = (if (isDailyDoneToday) NeonEmerald else NeonMagenta).copy(alpha = 0.16f),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (isDailyDoneToday) NeonEmerald else NeonMagenta,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.dailyChallenge,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                            )
                            Text(
                                text = if (isDailyDoneToday) strings.completedToday else strings.dailyRewardHint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDailyDoneToday) NeonEmerald else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    if (liveDailyStreak > 0) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonAmber.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$liveDailyStreak",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NeonAmber,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2-column Menu Grid: Levels, Statistics, How to Play, Settings
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    HomeTileCard(
                        title = strings.levelSelect,
                        subtitle = "${progress.completedLevelsCount} / 100",
                        icon = Icons.Rounded.GridView,
                        accent = NeonCyan,
                        progressFraction = progress.completedLevelsCount / 100f,
                        onClick = onOpenLevels,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_levels_button"),
                    )
                    HomeTileCard(
                        title = strings.statistics,
                        subtitle = "${progress.totalStars} / 300 ★",
                        icon = Icons.Rounded.BarChart,
                        accent = NeonAmber,
                        progressFraction = progress.totalStars / 300f,
                        onClick = onOpenStats,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_stats_button"),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    HomeTileCard(
                        title = strings.howToPlay,
                        subtitle = "Euler Guide",
                        icon = Icons.AutoMirrored.Rounded.HelpOutline,
                        accent = NeonViolet,
                        progressFraction = null,
                        onClick = onOpenHowToPlay,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_howtoplay_button"),
                    )
                    HomeTileCard(
                        title = strings.settings,
                        subtitle = "Audio & Lang",
                        icon = Icons.Rounded.Settings,
                        accent = NeonEmerald,
                        progressFraction = null,
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_settings_tile"),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun HomeTileCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    progressFraction: Float?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier,
        borderColor = accent.copy(alpha = 0.55f),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accent.copy(alpha = 0.16f),
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            if (progressFraction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressFraction.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50)),
                    color = accent,
                    trackColor = NavySurfaceElevated,
                )
            }
        }
    }
}

fun tierTitle(tier: DifficultyTier, strings: com.example.i18n.AppStrings): String = when (tier) {
    DifficultyTier.FOUNDATIONS -> strings.tierFoundations
    DifficultyTier.INTERSECTIONS -> strings.tierIntersections
    DifficultyTier.GEOMETRY -> strings.tierGeometry
    DifficultyTier.OVERLAPS -> strings.tierOverlaps
    DifficultyTier.MASTERMIND -> strings.tierMastermind
    DifficultyTier.ICONIC -> strings.tierIconic
    DifficultyTier.ARTIFACTS -> strings.tierArtifacts
    DifficultyTier.NATURE -> strings.tierNature
    DifficultyTier.WONDER -> strings.tierWonder
    DifficultyTier.SACRED -> strings.tierSacred
    DifficultyTier.DAILY -> strings.tierDaily
}
