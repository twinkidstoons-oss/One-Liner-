package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.LevelCatalog
import com.example.engine.EulerSolver
import com.example.engine.PuzzleStatus
import com.example.i18n.LocalAppStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonPuzzleCanvas
import com.example.ui.theme.NavyCardBorder
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
fun OnboardingScreen(
    onComplete: () -> Unit,
) {
    val strings = LocalAppStrings.current
    var page by remember { mutableIntStateOf(0) }

    // Interactive mini triangle puzzle on Page 0!
    val demoLevel = remember { LevelCatalog.getLevel(1) }
    var demoStatus by remember { mutableStateOf(PuzzleStatus.READY) }
    var demoCurrentVertex by remember { mutableStateOf<Int?>(null) }
    var demoDrawnEdges by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var demoNonce by remember { mutableIntStateOf(0) }

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onComplete,
                    modifier = Modifier.testTag("onboarding_skip_button"),
                ) {
                    Text(strings.skip, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            GlassCard(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth(),
                borderColor = when (page) {
                    0 -> NeonCyan
                    1 -> NeonViolet
                    else -> NeonAmber
                },
                cornerRadius = 28.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (page) {
                        0 -> {
                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.size(56.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Gesture,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = strings.onboardingTitle1,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.onboardingBody1,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (demoStatus == PuzzleStatus.COMPLETE) strings.onboardingSolved else strings.onboardingTryIt,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (demoStatus == PuzzleStatus.COMPLETE) NeonEmerald else NeonCyan,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = NavySurfaceElevated.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, NavyCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp),
                            ) {
                                NeonPuzzleCanvas(
                                    level = demoLevel,
                                    status = demoStatus,
                                    currentVertexId = demoCurrentVertex,
                                    drawnEdgeIds = demoDrawnEdges,
                                    hintPath = emptyList(),
                                    revealedHintSteps = 0,
                                    invalidEdgeFlashId = null,
                                    attemptNonce = demoNonce,
                                    onBeginStroke = { vId ->
                                        demoStatus = PuzzleStatus.DRAWING
                                        demoCurrentVertex = vId
                                        demoDrawnEdges = emptySet()
                                    },
                                    onTryMoveTo = { targetId ->
                                        val from = demoCurrentVertex ?: return@NeonPuzzleCanvas false
                                        val edge = EulerSolver.findUndrawnEdge(demoLevel, from, targetId, demoDrawnEdges)
                                        if (edge != null) {
                                            val nextEdges = demoDrawnEdges + edge.id
                                            demoDrawnEdges = nextEdges
                                            demoCurrentVertex = targetId
                                            if (nextEdges.size == demoLevel.edges.size) {
                                                demoStatus = PuzzleStatus.COMPLETE
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                    onLiftFinger = {
                                        if (demoStatus != PuzzleStatus.COMPLETE) {
                                            demoStatus = PuzzleStatus.READY
                                            demoCurrentVertex = null
                                            demoDrawnEdges = emptySet()
                                            demoNonce++
                                        }
                                    },
                                )
                            }
                        }

                        1 -> {
                            Surface(
                                shape = CircleShape,
                                color = NeonCoral.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, NeonCoral.copy(alpha = 0.5f)),
                                modifier = Modifier.size(72.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Block,
                                        contentDescription = null,
                                        tint = NeonCoral,
                                        modifier = Modifier.size(38.dp),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                            Text(
                                text = strings.onboardingTitle2,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.onboardingBody2,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                        }

                        else -> {
                            Surface(
                                shape = CircleShape,
                                color = NeonAmber.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                                modifier = Modifier.size(72.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = NeonAmber,
                                        modifier = Modifier.size(38.dp),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(22.dp))
                            Text(
                                text = strings.onboardingTitle3,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.onboardingBody3,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NeonEmerald)
                                Text(
                                    text = "100 Handcrafted Levels + Daily Mode",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = NeonEmerald,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (i in 0..2) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (page == i) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (page == i) NeonCyan else TextMuted.copy(alpha = 0.5f)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            NeonGradientButton(
                text = if (page < 2) strings.next else strings.getStarted,
                onClick = {
                    if (page < 2) {
                        page++
                    } else {
                        onComplete()
                    }
                },
                colors = if (page < 2) listOf(NeonCyan, NeonViolet) else listOf(NeonEmerald, NeonCyan),
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .testTag("onboarding_next_button"),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
