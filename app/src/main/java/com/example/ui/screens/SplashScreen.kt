package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.LocalAppStrings
import com.example.ui.components.NeonGridBackground
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val strokeProgress = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        strokeProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1250, easing = FastOutSlowInEasing),
        )
        titleAlpha.animateTo(1f, animationSpec = tween(durationMillis = 400))
        delay(550L)
        onFinished()
    }

    NeonGridBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(164.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // 5-point continuous star path vertices
                    val pts = listOf(
                        Offset(w * 0.50f, h * 0.08f),
                        Offset(w * 0.78f, h * 0.88f),
                        Offset(w * 0.10f, h * 0.38f),
                        Offset(w * 0.90f, h * 0.38f),
                        Offset(w * 0.22f, h * 0.88f),
                        Offset(w * 0.50f, h * 0.08f),
                    )
                    val totalSegments = pts.size - 1
                    val p = strokeProgress.value * totalSegments

                    for (i in 0 until totalSegments) {
                        val segProgress = (p - i).coerceIn(0f, 1f)
                        if (segProgress <= 0f) continue
                        val a = pts[i]
                        val b = pts[i + 1]
                        val end = Offset(
                            a.x + (b.x - a.x) * segProgress,
                            a.y + (b.y - a.y) * segProgress,
                        )
                        drawLine(
                            color = NeonCyan.copy(alpha = 0.28f),
                            start = a,
                            end = end,
                            strokeWidth = 22f,
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            brush = Brush.linearGradient(listOf(NeonCyan, NeonViolet)),
                            start = a,
                            end = end,
                            strokeWidth = 9f,
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            color = Color.White,
                            start = a,
                            end = end,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round,
                        )
                    }

                    // Glowing nodes
                    for (i in 0 until totalSegments) {
                        if (p >= i) {
                            drawCircle(
                                color = NeonEmerald,
                                radius = 8f,
                                center = pts[i],
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.5f,
                                center = pts[i],
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = strings.appTitle,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                ),
                color = TextPrimary,
                modifier = Modifier.alpha(titleAlpha.value.coerceAtLeast(0.4f)),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.appSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = NeonCyan,
                modifier = Modifier.alpha(titleAlpha.value.coerceAtLeast(0.4f)),
            )

            Spacer(modifier = Modifier.height(48.dp))

            LinearProgressIndicator(
                progress = { strokeProgress.value },
                modifier = Modifier
                    .width(210.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = NeonCyan,
                trackColor = NavySurfaceElevated,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = strings.loadingEngine,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
