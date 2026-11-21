package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.i18n.LocalAppStrings
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGridBackground
import com.example.ui.components.NeonTopBar
import com.example.ui.theme.EdgeUndrawn
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val strings = LocalAppStrings.current

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NeonTopBar(title = strings.howToPlay, onBack = onBack)

            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                RuleDiagramCard(
                    title = strings.rule1Title,
                    description = strings.rule1Desc,
                    accent = NeonCyan,
                ) {
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val a = Offset(size.width * 0.5f, size.height * 0.14f)
                        val b = Offset(size.width * 0.16f, size.height * 0.82f)
                        val c = Offset(size.width * 0.84f, size.height * 0.82f)
                        drawLine(NeonEmerald, a, b, strokeWidth = 6f, cap = StrokeCap.Round)
                        drawLine(NeonCyan, b, c, strokeWidth = 6f, cap = StrokeCap.Round)
                        drawLine(EdgeUndrawn, c, a, strokeWidth = 5f, cap = StrokeCap.Round)
                        listOf(a, b, c).forEach {
                            drawCircle(NeonCyan, radius = 7f, center = it)
                        }
                    }
                }

                RuleDiagramCard(
                    title = strings.rule2Title,
                    description = strings.rule2Desc,
                    accent = NeonAmber,
                ) {
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val p1 = Offset(size.width * 0.16f, size.height * 0.72f)
                        val p2 = Offset(size.width * 0.5f, size.height * 0.24f)
                        val p3 = Offset(size.width * 0.84f, size.height * 0.72f)
                        drawLine(NeonCyan, p1, p2, strokeWidth = 6f, cap = StrokeCap.Round)
                        drawLine(
                            color = NeonAmber,
                            start = p2,
                            end = p3,
                            strokeWidth = 5f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
                        )
                        drawCircle(NeonCyan, radius = 8f, center = p2)
                        drawCircle(Color.White, radius = 3.5f, center = p2)
                        listOf(p1, p3).forEach { drawCircle(EdgeUndrawn, radius = 6f, center = it) }
                    }
                }

                RuleDiagramCard(
                    title = strings.rule3Title,
                    description = strings.rule3Desc,
                    accent = NeonCoral,
                ) {
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val a = Offset(size.width * 0.16f, size.height * 0.5f)
                        val b = Offset(size.width * 0.84f, size.height * 0.5f)
                        drawLine(NeonEmerald, a, b, strokeWidth = 7f, cap = StrokeCap.Round)
                        val cx = size.width * 0.5f
                        val cy = size.height * 0.5f
                        drawLine(NeonCoral, Offset(cx - 12f, cy - 12f), Offset(cx + 12f, cy + 12f), strokeWidth = 5f, cap = StrokeCap.Round)
                        drawLine(NeonCoral, Offset(cx + 12f, cy - 12f), Offset(cx - 12f, cy + 12f), strokeWidth = 5f, cap = StrokeCap.Round)
                        drawCircle(NeonEmerald, radius = 7f, center = a)
                        drawCircle(NeonEmerald, radius = 7f, center = b)
                    }
                }

                RuleDiagramCard(
                    title = strings.rule4Title,
                    description = strings.rule4Desc,
                    accent = NeonViolet,
                ) {
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val top = Offset(size.width * 0.5f, size.height * 0.16f)
                        val left = Offset(size.width * 0.2f, size.height * 0.78f)
                        val right = Offset(size.width * 0.8f, size.height * 0.78f)
                        drawLine(EdgeUndrawn, top, left, strokeWidth = 5f)
                        drawLine(EdgeUndrawn, top, right, strokeWidth = 5f)
                        drawLine(EdgeUndrawn, left, right, strokeWidth = 5f)
                        drawLine(EdgeUndrawn, top, Offset(size.width * 0.5f, size.height * 0.78f), strokeWidth = 5f)
                        // Highlight odd nodes with violet ring
                        drawCircle(NeonViolet, radius = 12f, center = top, style = Stroke(3f))
                        drawCircle(NeonCyan, radius = 6f, center = top)
                        drawCircle(NeonViolet, radius = 12f, center = Offset(size.width * 0.5f, size.height * 0.78f), style = Stroke(3f))
                        drawCircle(NeonCyan, radius = 6f, center = Offset(size.width * 0.5f, size.height * 0.78f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RuleDiagramCard(
    title: String,
    description: String,
    accent: Color,
    diagram: @Composable () -> Unit,
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accent.copy(alpha = 0.6f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            diagram()
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}
