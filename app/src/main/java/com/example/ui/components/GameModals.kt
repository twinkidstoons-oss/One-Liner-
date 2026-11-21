package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.engine.FailureReason
import com.example.i18n.LocalAppStrings
import com.example.ui.CompletionResult
import com.example.ui.GameViewModel
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyDeepest
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LevelCompleteOverlay(
    isDaily: Boolean,
    levelNumber: Int,
    hasNextLevel: Boolean,
    result: CompletionResult?,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit,
) {
    val strings = LocalAppStrings.current
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeepest.copy(alpha = 0.78f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = animateIn,
            enter = fadeIn() + scaleIn(initialScale = 0.85f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
        ) {
            GlassCard(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(),
                borderColor = NeonEmerald,
                containerColor = NavySurface.copy(alpha = 0.96f),
                cornerRadius = 28.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonEmerald.copy(alpha = 0.16f),
                        border = BorderStroke(1.5.dp, NeonEmerald),
                        modifier = Modifier.size(64.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(34.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isDaily) strings.dailyComplete else strings.levelComplete,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = NeonEmerald,
                            fontWeight = FontWeight.Bold,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = if (isDaily) strings.dailyChallenge else "${strings.levelLabel} $levelNumber",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Animated Stars Row
                    val stars = result?.stars ?: 3
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        for (i in 1..3) {
                            val earned = i <= stars
                            val starScale by animateFloatAsState(
                                targetValue = if (animateIn && earned) 1.1f else 0.9f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "star_$i",
                            )
                            Icon(
                                imageVector = if (earned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (earned) NeonAmber else TextMuted,
                                modifier = Modifier
                                    .size(if (i == 2) 52.dp else 44.dp)
                                    .scale(starScale),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Score & Reward Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatBadgeBox(
                            label = strings.scoreLabel,
                            value = "${result?.score ?: 0}",
                            accent = NeonCyan,
                            modifier = Modifier.weight(1f),
                        )
                        StatBadgeBox(
                            label = strings.rewardLabel,
                            value = "+${result?.coinsEarned ?: 0}",
                            accent = NeonAmber,
                            isCoin = true,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    // Newly unlocked achievements
                    if (result != null && result.newlyUnlockedAchievements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NeonViolet.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, NeonViolet.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(22.dp),
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Achievement Unlocked!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonViolet,
                                    )
                                    Text(
                                        text = result.newlyUnlockedAchievements.joinToString { it.title },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextPrimary,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    if (!isDaily && hasNextLevel) {
                        NeonGradientButton(
                            text = strings.nextLevel,
                            onClick = onNextLevel,
                            colors = listOf(NeonEmerald, NeonCyan),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = NavyDeepest,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("next_level_button"),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onReplay,
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("replay_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NavyCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.restart, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = onHome,
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("complete_home_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NavyCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        ) {
                            Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.home, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadgeBox(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    isCoin: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = NavySurfaceElevated.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCoin) {
                    Icon(
                        imageVector = Icons.Rounded.MonetizationOn,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

@Composable
fun LevelFailedOverlay(
    reason: FailureReason?,
    coins: Int,
    onRetry: () -> Unit,
    onUseHint: () -> Unit,
    onHome: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val reasonText = when (reason) {
        FailureReason.LIFTED_FINGER -> strings.failLiftedFinger
        FailureReason.DEAD_END -> strings.failDeadEnd
        FailureReason.RETRACED_EDGE -> strings.failRetrace
        null -> strings.failDeadEnd
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeepest.copy(alpha = 0.78f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 390.dp)
                .fillMaxWidth(),
            borderColor = NeonCoral,
            containerColor = NavySurface.copy(alpha = 0.96f),
            cornerRadius = 28.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    shape = CircleShape,
                    color = NeonCoral.copy(alpha = 0.16f),
                    border = BorderStroke(1.5.dp, NeonCoral),
                    modifier = Modifier.size(62.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = NeonCoral,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.levelFailed,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = NeonCoral,
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = reasonText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(22.dp))

                NeonGradientButton(
                    text = strings.retry,
                    onClick = onRetry,
                    colors = listOf(NeonCyan, NeonEmerald),
                    leadingIcon = {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = NavyDeepest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("retry_button"),
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onUseHint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("failed_hint_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonAmber),
                ) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${strings.hint} (${GameViewModel.HINT_COST} Coins • $coins)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("failed_home_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NavyCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                ) {
                    Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.home)
                }
            }
        }
    }
}

@Composable
fun PauseMenuOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onSettings: () -> Unit,
    onHome: () -> Unit,
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeepest.copy(alpha = 0.80f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        GlassCard(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 360.dp)
                .fillMaxWidth(),
            borderColor = NeonCyan,
            containerColor = NavySurface.copy(alpha = 0.96f),
            cornerRadius = 28.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = strings.pausedTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(modifier = Modifier.height(22.dp))

                NeonGradientButton(
                    text = strings.resume,
                    onClick = onResume,
                    leadingIcon = {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = NavyDeepest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_resume_button"),
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("pause_restart_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NavyCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.restart)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("pause_settings_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NavyCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.settings)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("pause_home_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, NavyCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                ) {
                    Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.home)
                }
            }
        }
    }
}
