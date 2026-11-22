package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.GameProgress
import com.example.data.LevelCatalog
import com.example.engine.DifficultyTier
import com.example.engine.PuzzleLevel
import com.example.i18n.LocalAppStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.MiniPuzzlePreview
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonTopBar
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LevelSelectScreen(
    progress: GameProgress,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val strings = LocalAppStrings.current
    val tiers = listOf(
        DifficultyTier.FOUNDATIONS,
        DifficultyTier.INTERSECTIONS,
        DifficultyTier.GEOMETRY,
        DifficultyTier.OVERLAPS,
        DifficultyTier.MASTERMIND,
        DifficultyTier.ICONIC,
        DifficultyTier.ARTIFACTS,
        DifficultyTier.NATURE,
        DifficultyTier.WONDER,
        DifficultyTier.SACRED,
    )
    var selectedTier by remember { mutableStateOf<DifficultyTier?>(null) }

    val filteredLevels = remember(selectedTier) {
        if (selectedTier == null) LevelCatalog.levels else LevelCatalog.levels.filter { it.tier == selectedTier }
    }

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            NeonTopBar(
                title = strings.levelSelect,
                onBack = onBack,
                trailing = {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = NavySurfaceElevated.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${progress.totalStars} / 300",
                                style = MaterialTheme.typography.labelLarge,
                                color = NeonAmber,
                            )
                        }
                    }
                },
            )

            // Tier filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedTier == null,
                        onClick = { selectedTier = null },
                        label = { Text("All (100)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            labelColor = TextSecondary,
                        ),
                    )
                }
                items(tiers) { tier ->
                    val rangeLabel = when (tier) {
                        DifficultyTier.FOUNDATIONS -> "1-10"
                        DifficultyTier.INTERSECTIONS -> "11-20"
                        DifficultyTier.GEOMETRY -> "21-30"
                        DifficultyTier.OVERLAPS -> "31-40"
                        DifficultyTier.MASTERMIND -> "41-50"
                        DifficultyTier.ICONIC -> "51-60"
                        DifficultyTier.ARTIFACTS -> "61-70"
                        DifficultyTier.NATURE -> "71-80"
                        DifficultyTier.WONDER -> "81-90"
                        DifficultyTier.SACRED -> "91-100"
                        DifficultyTier.DAILY -> ""
                    }
                    FilterChip(
                        selected = selectedTier == tier,
                        onClick = { selectedTier = if (selectedTier == tier) null else tier },
                        label = { Text("${tierTitle(tier, strings)} ($rangeLabel)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            labelColor = TextSecondary,
                        ),
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 104.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(filteredLevels, key = { it.id }) { level ->
                    val stars = progress.levelStars[level.id] ?: 0
                    val isCompleted = stars > 0
                    val isUnlocked = level.id <= progress.highestUnlockedLevel || isCompleted
                    val isCurrent = level.id == progress.highestUnlockedLevel

                    LevelGridCell(
                        level = level,
                        stars = stars,
                        isCompleted = isCompleted,
                        isUnlocked = isUnlocked,
                        isCurrent = isCurrent,
                        onClick = {
                            if (isUnlocked) onSelectLevel(level.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelGridCell(
    level: PuzzleLevel,
    stars: Int,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when {
        isCurrent -> NeonCyan
        isCompleted -> NeonEmerald.copy(alpha = 0.7f)
        isUnlocked -> NavyCardBorder
        else -> NavyCardBorder.copy(alpha = 0.35f)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("level_card_${level.id}"),
        borderColor = borderColor,
        containerColor = if (isUnlocked) NavySurface.copy(alpha = 0.88f) else NavySurface.copy(alpha = 0.42f),
        cornerRadius = 18.dp,
        onClick = if (isUnlocked) onClick else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = level.id.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = when {
                        isCurrent -> NeonCyan
                        isCompleted -> NeonEmerald
                        isUnlocked -> TextPrimary
                        else -> TextMuted
                    },
                )
                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Locked",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isUnlocked) {
                    MiniPuzzlePreview(
                        level = level,
                        modifier = Modifier.fillMaxSize(),
                        lineColor = if (isCompleted) NeonEmerald else NeonCyan,
                        nodeColor = TextPrimary,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = TextMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(26.dp),
                    )
                }
            }

            Text(
                text = level.name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isUnlocked) TextSecondary else TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (i in 1..3) {
                    Icon(
                        imageVector = if (i <= stars) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = if (i <= stars) NeonAmber else TextMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}
