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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Achievements
import com.example.data.GameProgress
import com.example.i18n.LocalAppStrings
import com.example.ui.GameViewModel
import com.example.ui.components.CoinPill
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonTopBar
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatisticsScreen(
    progress: GameProgress,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val strings = LocalAppStrings.current
    val todayKey = remember { GameViewModel.todayDateKey() }
    val yesterdayKey = remember { GameViewModel.yesterdayDateKey() }
    val liveStreak = remember(progress.completedDailyDates, todayKey, yesterdayKey) {
        progress.currentDailyStreak(todayKey, yesterdayKey)
    }

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NeonTopBar(
                title = strings.statistics,
                onBack = onBack,
                trailing = { CoinPill(coins = progress.coins) },
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SummaryStatCard(
                        title = strings.levelsCompletedLabel,
                        value = "${progress.completedLevelsCount} / 100",
                        icon = Icons.Rounded.CheckCircle,
                        accent = NeonEmerald,
                        modifier = Modifier.weight(1f),
                    )
                    SummaryStatCard(
                        title = strings.starsLabel,
                        value = "${progress.totalStars} / 300",
                        icon = Icons.Rounded.Star,
                        accent = NeonAmber,
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SummaryStatCard(
                        title = strings.bestStreak,
                        value = "$liveStreak d",
                        icon = Icons.Rounded.LocalFireDepartment,
                        accent = NeonCyan,
                        modifier = Modifier.weight(1f),
                    )
                    SummaryStatCard(
                        title = strings.totalAttempts,
                        value = "${progress.totalAttempts}",
                        icon = Icons.Rounded.EmojiEvents,
                        accent = NeonViolet,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${strings.achievements} (${progress.unlockedAchievements.size}/${Achievements.all.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                )

                Achievements.all.forEach { ach ->
                    val unlocked = progress.unlockedAchievements.contains(ach.id)
                    val currentVal = ach.progressSelector(progress).coerceAtMost(ach.target)
                    val frac = currentVal.toFloat() / ach.target.toFloat()

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (unlocked) NeonEmerald else NavySurfaceElevated,
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = (if (unlocked) NeonEmerald else TextMuted).copy(alpha = 0.16f),
                                        modifier = Modifier.size(40.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (unlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                                                contentDescription = null,
                                                tint = if (unlocked) NeonEmerald else TextMuted,
                                                modifier = Modifier.size(22.dp),
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = ach.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (unlocked) TextPrimary else TextSecondary,
                                        )
                                        Text(
                                            text = ach.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextMuted,
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = NeonAmber.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.45f)),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MonetizationOn,
                                            contentDescription = null,
                                            tint = NeonAmber,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "+${ach.coinReward}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = NeonAmber,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                LinearProgressIndicator(
                                    progress = { frac },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = if (unlocked) NeonEmerald else NeonCyan,
                                    trackColor = NavySurfaceElevated,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$currentVal/${ach.target}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (unlocked) NeonEmerald else TextSecondary,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        modifier = modifier,
        borderColor = accent.copy(alpha = 0.55f),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                ),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}
