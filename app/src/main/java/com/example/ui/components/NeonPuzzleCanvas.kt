package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.engine.EulerSolver
import com.example.engine.PuzzleLevel
import com.example.engine.PuzzleStatus
import com.example.ui.theme.EdgeUndrawn
import com.example.ui.theme.EdgeUndrawnGlow
import com.example.ui.theme.NavyDeepest
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VertexBorderIdle
import com.example.ui.theme.VertexIdle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private data class SparkParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val color: Color,
    val radius: Float,
)

@Composable
fun NeonPuzzleCanvas(
    level: PuzzleLevel,
    status: PuzzleStatus,
    currentVertexId: Int?,
    drawnEdgeIds: Set<Int>,
    hintPath: List<Int>,
    revealedHintSteps: Int,
    invalidEdgeFlashId: Int?,
    attemptNonce: Int,
    onBeginStroke: (Int) -> Unit,
    onTryMoveTo: (Int) -> Boolean,
    onLiftFinger: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var pointerPos by remember(level.id, attemptNonce) { mutableStateOf<Offset?>(null) }
    var prevPointerPos by remember(level.id, attemptNonce) { mutableStateOf<Offset?>(null) }

    val particles = remember { mutableStateListOf<SparkParticle>() }
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "canvas_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "node_pulse",
    )

    // Spawn celebration burst when level completes
    LaunchedEffect(status, level.id, attemptNonce) {
        if (status == PuzzleStatus.COMPLETE && canvasSize.width > 0) {
            val cx = canvasSize.width / 2f
            val cy = canvasSize.height / 2f
            val colors = listOf(NeonCyan, NeonEmerald, NeonAmber, NeonViolet)
            repeat(52) { i ->
                val angle = (2.0 * PI * i / 52.0) + Random.nextDouble(-0.15, 0.15)
                val speed = 180f + Random.nextFloat() * 440f
                particles.add(
                    SparkParticle(
                        x = cx,
                        y = cy,
                        vx = (cos(angle) * speed).toFloat(),
                        vy = (sin(angle) * speed).toFloat(),
                        life = 1f,
                        maxLife = 0.8f + Random.nextFloat() * 0.5f,
                        color = colors[i % colors.size],
                        radius = 4f + Random.nextFloat() * 5f,
                    ),
                )
            }
        }
    }

    // Particle physics loop active only while particles exist
    LaunchedEffect(particles.size) {
        if (particles.isEmpty()) return@LaunchedEffect
        var lastNanos = withFrameNanos { it }
        while (particles.isNotEmpty()) {
            val now = withFrameNanos { it }
            val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
            lastNanos = now
            val iterator = particles.listIterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx * dt
                p.y += p.vy * dt
                p.vx *= 0.94f
                p.vy *= 0.94f
                p.life -= dt / p.maxLife
                if (p.life <= 0f) {
                    iterator.remove()
                }
            }
        }
    }

    fun boardMetrics(size: IntSize): Triple<Float, Float, Float> {
        val boardSize = min(size.width, size.height) * 0.88f
        val offsetX = (size.width - boardSize) / 2f
        val offsetY = (size.height - boardSize) / 2f
        return Triple(boardSize, offsetX, offsetY)
    }

    fun vertexScreenPos(vertexId: Int, size: IntSize): Offset {
        val v = level.vertices.firstOrNull { it.id == vertexId } ?: return Offset.Zero
        val (boardSize, offsetX, offsetY) = boardMetrics(size)
        return Offset(offsetX + v.x * boardSize, offsetY + v.y * boardSize)
    }

    fun spawnNodeSparks(center: Offset, color: Color, count: Int = 14) {
        repeat(count) { i ->
            val angle = (2.0 * PI * i / count) + Random.nextDouble(-0.2, 0.2)
            val speed = 110f + Random.nextFloat() * 200f
            particles.add(
                SparkParticle(
                    x = center.x,
                    y = center.y,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat(),
                    life = 1f,
                    maxLife = 0.45f + Random.nextFloat() * 0.25f,
                    color = color,
                    radius = 3f + Random.nextFloat() * 3.5f,
                ),
            )
        }
    }

    // Track current state inside rememberUpdatedState-like refs for gesture loop
    val currentStatusState = remember { mutableStateOf(status) }.apply { value = status }
    val currentVertexState = remember { mutableStateOf(currentVertexId) }.apply { value = currentVertexId }
    val currentDrawnState = remember { mutableStateOf(drawnEdgeIds) }.apply { value = drawnEdgeIds }

    val oddVertices = remember(level.id) { EulerSolver.oddDegreeVertices(level).toSet() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .pointerInput(level.id, attemptNonce) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                    val st = currentStatusState.value
                    if (st != PuzzleStatus.READY) return@awaitEachGesture

                    val (boardSize, _, _) = boardMetrics(canvasSize)
                    if (boardSize <= 0f) return@awaitEachGesture
                    val hitRadius = (boardSize * 0.115f).coerceIn(64f, 130f)

                    // Snap down to nearest vertex within generous touch radius
                    var bestId: Int? = null
                    var bestDist = Float.MAX_VALUE
                    for (v in level.vertices) {
                        val pos = vertexScreenPos(v.id, canvasSize)
                        val d = hypot(down.position.x - pos.x, down.position.y - pos.y)
                        if (d <= hitRadius && d < bestDist) {
                            bestDist = d
                            bestId = v.id
                        }
                    }
                    if (bestId == null) return@awaitEachGesture

                    down.consume()
                    onBeginStroke(bestId)
                    pointerPos = down.position
                    prevPointerPos = down.position
                    spawnNodeSparks(vertexScreenPos(bestId, canvasSize), NeonCyan, count = 10)

                    var activePointerId = down.id
                    var lastLeftVertexId: Int? = null

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull { it.id == activePointerId }
                            ?: event.changes.firstOrNull()
                            ?: break

                        if (!change.pressed) {
                            change.consume()
                            pointerPos = null
                            prevPointerPos = null
                            if (currentStatusState.value == PuzzleStatus.DRAWING) {
                                onLiftFinger()
                            }
                            break
                        }

                        change.consume()
                        val prevPos = prevPointerPos ?: change.position
                        val currPos = change.position
                        pointerPos = currPos
                        prevPointerPos = currPos

                        if (currentStatusState.value != PuzzleStatus.DRAWING) {
                            continue
                        }

                        val activeVertex = currentVertexState.value ?: continue
                        val activePos = vertexScreenPos(activeVertex, canvasSize)
                        val distFromActive = hypot(currPos.x - activePos.x, currPos.y - activePos.y)
                        if (distFromActive > hitRadius * 0.55f) {
                            lastLeftVertexId = activeVertex
                        }

                        val drawn = currentDrawnState.value
                        val validNext = EulerSolver.validNextVertices(level, activeVertex, drawn)

                        // Check valid undrawn neighbors first using segment-to-point distance for crisp swipe tracking
                        val snapRadius = hitRadius * 0.85f
                        var matchedValid: Int? = null
                        var bestValidDist = Float.MAX_VALUE
                        for (candidateId in validNext) {
                            val vPos = vertexScreenPos(candidateId, canvasSize)
                            val segDist = pointToSegmentDistance(vPos, prevPos, currPos)
                            if (segDist <= snapRadius && segDist < bestValidDist) {
                                bestValidDist = segDist
                                matchedValid = candidateId
                            }
                        }

                        if (matchedValid != null) {
                            val moved = onTryMoveTo(matchedValid)
                            if (moved) {
                                spawnNodeSparks(vertexScreenPos(matchedValid, canvasSize), NeonEmerald, count = 14)
                                lastLeftVertexId = activeVertex
                            }
                            continue
                        }

                        // Check if user intentionally dragged straight into an already-drawn neighbor (retracing!)
                        val retraceRadius = hitRadius * 0.48f
                        for (v in level.vertices) {
                            if (v.id == activeVertex || v.id == lastLeftVertexId) continue
                            val alreadyDrawnEdge = EulerSolver.findDrawnEdgeBetween(level, activeVertex, v.id, drawn)
                            if (alreadyDrawnEdge != null) {
                                val vPos = vertexScreenPos(v.id, canvasSize)
                                val d = hypot(currPos.x - vPos.x, currPos.y - vPos.y)
                                if (d <= retraceRadius) {
                                    onTryMoveTo(v.id)
                                    break
                                }
                            }
                        }
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val (boardSize, offsetX, offsetY) = boardMetrics(
                IntSize(size.width.toInt(), size.height.toInt()),
            )
            if (boardSize <= 0f) return@Canvas

            fun posOf(vertexId: Int): Offset {
                val v = level.vertices[vertexId]
                return Offset(offsetX + v.x * boardSize, offsetY + v.y * boardSize)
            }

            // 1. Draw subtle undrawn guide edges
            for (edge in level.edges) {
                if (drawnEdgeIds.contains(edge.id)) continue
                val start = posOf(edge.u)
                val end = posOf(edge.v)
                drawLine(
                    color = EdgeUndrawnGlow.copy(alpha = 0.55f),
                    start = start,
                    end = end,
                    strokeWidth = 14f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = EdgeUndrawn,
                    start = start,
                    end = end,
                    strokeWidth = 6f,
                    cap = StrokeCap.Round,
                )
            }

            // 2. Draw Hint guide path (if revealed)
            if (revealedHintSteps > 0 && hintPath.size > 1) {
                val steps = revealedHintSteps.coerceAtMost(hintPath.size - 1)
                for (i in 0 until steps) {
                    val a = posOf(hintPath[i])
                    val b = posOf(hintPath[i + 1])
                    drawLine(
                        color = NeonAmber.copy(alpha = 0.30f + 0.15f * pulse),
                        start = a,
                        end = b,
                        strokeWidth = 14f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = NeonAmber.copy(alpha = 0.85f),
                        start = a,
                        end = b,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                    )
                }
            }

            // 3. Draw completed glowing edges (multi-pass neon bloom)
            for (edge in level.edges) {
                if (!drawnEdgeIds.contains(edge.id)) continue
                val start = posOf(edge.u)
                val end = posOf(edge.v)
                val isFlashingInvalid = edge.id == invalidEdgeFlashId && status == PuzzleStatus.FAILED
                val glowColor = if (isFlashingInvalid) NeonCoral else NeonEmerald
                val coreColor = if (isFlashingInvalid) Color(0xFFFFB3C1) else Color(0xFFE0FFF6)

                drawLine(
                    color = glowColor.copy(alpha = 0.22f),
                    start = start,
                    end = end,
                    strokeWidth = 28f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonCyan, glowColor),
                        start = start,
                        end = end,
                    ),
                    start = start,
                    end = end,
                    strokeWidth = 12f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = coreColor,
                    start = start,
                    end = end,
                    strokeWidth = 4.5f,
                    cap = StrokeCap.Round,
                )
            }

            // 4. Active tether from current vertex to finger/mouse
            val activePos = currentVertexId?.let { posOf(it) }
            val cursor = pointerPos
            if (status == PuzzleStatus.DRAWING && activePos != null && cursor != null && currentVertexId != null) {
                val validNext = EulerSolver.validNextVertices(level, currentVertexId, drawnEdgeIds)
                // Check if cursor is hovering near a valid neighbor for magnetic glow feedback
                val nearValid = validNext.any { vId ->
                    val vp = posOf(vId)
                    hypot(cursor.x - vp.x, cursor.y - vp.y) < boardSize * 0.14f
                }
                val tetherColor = if (nearValid) NeonEmerald else NeonCyan
                drawLine(
                    color = tetherColor.copy(alpha = 0.25f),
                    start = activePos,
                    end = cursor,
                    strokeWidth = 18f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tetherColor.copy(alpha = 0.85f),
                    start = activePos,
                    end = cursor,
                    strokeWidth = 7f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = tetherColor.copy(alpha = 0.35f),
                    radius = 18f,
                    center = cursor,
                )
            }

            // 5. Draw vertices/nodes
            val nodeRadius = (boardSize * 0.036f).coerceIn(14f, 24f)
            val validTargets = if (status == PuzzleStatus.DRAWING && currentVertexId != null) {
                EulerSolver.validNextVertices(level, currentVertexId, drawnEdgeIds)
            } else {
                emptySet()
            }

            for (v in level.vertices) {
                val center = posOf(v.id)
                val isCurrent = v.id == currentVertexId
                val isValidTarget = v.id in validTargets
                val isOddSuggestedStart = status == PuzzleStatus.READY && oddVertices.contains(v.id)

                // Outer halo for active, valid target, or odd-degree start clue
                if (isCurrent) {
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.35f * pulse),
                        radius = nodeRadius * 1.9f,
                        center = center,
                    )
                } else if (isValidTarget) {
                    drawCircle(
                        color = NeonEmerald.copy(alpha = 0.25f * pulse),
                        radius = nodeRadius * 1.55f,
                        center = center,
                    )
                } else if (isOddSuggestedStart) {
                    drawCircle(
                        color = NeonViolet.copy(alpha = 0.28f * pulse),
                        radius = nodeRadius * 1.5f,
                        center = center,
                        style = Stroke(width = 3f),
                    )
                }

                val fillColor = when {
                    isCurrent -> NeonCyan
                    isValidTarget -> NeonSurfaceHighlight(NeonEmerald)
                    else -> VertexIdle
                }
                val borderColor = when {
                    isCurrent -> Color.White
                    isValidTarget -> NeonEmerald
                    isOddSuggestedStart -> NeonViolet
                    else -> VertexBorderIdle
                }

                drawCircle(
                    color = fillColor,
                    radius = if (isCurrent) nodeRadius * 1.15f else nodeRadius,
                    center = center,
                )
                drawCircle(
                    color = borderColor,
                    radius = if (isCurrent) nodeRadius * 1.15f else nodeRadius,
                    center = center,
                    style = Stroke(width = 4f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = if (isCurrent) 0.95f else 0.35f),
                    radius = nodeRadius * 0.32f,
                    center = center,
                )
            }

            // 6. Draw Hint Step Numbers on vertices when Hint is active
            if (revealedHintSteps > 0 && hintPath.isNotEmpty()) {
                drawHintBadges(
                    hintPath = hintPath,
                    revealedSteps = revealedHintSteps,
                    posOf = ::posOf,
                    nodeRadius = nodeRadius,
                    textMeasurer = textMeasurer,
                )
            }

            // 7. Draw particle sparks
            for (p in particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                    radius = p.radius * p.life.coerceIn(0.2f, 1f),
                    center = Offset(p.x, p.y),
                )
            }
        }
    }
}

private fun NeonSurfaceHighlight(accent: Color): Color =
    Color(
        red = accent.red * 0.25f + 0.08f,
        green = accent.green * 0.25f + 0.14f,
        blue = accent.blue * 0.25f + 0.24f,
        alpha = 1f,
    )

private fun DrawScope.drawHintBadges(
    hintPath: List<Int>,
    revealedSteps: Int,
    posOf: (Int) -> Offset,
    nodeRadius: Float,
    textMeasurer: TextMeasurer,
) {
    // Show first few step indices clearly without overcrowding: show step #1 on start node and up to next 4 nodes
    val shownCount = (revealedSteps + 1).coerceAtMost(hintPath.size)
    val firstVisitStep = LinkedHashMap<Int, Int>()
    for (stepIdx in 0 until shownCount) {
        val vId = hintPath[stepIdx]
        if (!firstVisitStep.containsKey(vId)) {
            firstVisitStep[vId] = stepIdx + 1
        }
    }
    for ((vId, stepNum) in firstVisitStep) {
        val center = posOf(vId)
        val badgeCenter = Offset(center.x + nodeRadius * 0.95f, center.y - nodeRadius * 0.95f)
        val badgeRadius = (nodeRadius * 0.72f).coerceAtLeast(13f)
        drawCircle(
            color = NeonAmber,
            radius = badgeRadius,
            center = badgeCenter,
        )
        drawCircle(
            color = NavyDeepest,
            radius = badgeRadius,
            center = badgeCenter,
            style = Stroke(width = 2f),
        )
        val label = stepNum.toString()
        val measured = textMeasurer.measure(
            text = label,
            style = TextStyle(
                color = NavyDeepest,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
        )
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(
                badgeCenter.x - measured.size.width / 2f,
                badgeCenter.y - measured.size.height / 2f,
            ),
        )
    }
}

private fun pointToSegmentDistance(p: Offset, a: Offset, b: Offset): Float {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val lenSq = dx * dx + dy * dy
    if (lenSq < 0.0001f) return hypot(p.x - a.x, p.y - a.y)
    val t = (((p.x - a.x) * dx + (p.y - a.y) * dy) / lenSq).coerceIn(0f, 1f)
    val projX = a.x + t * dx
    val projY = a.y + t * dy
    return hypot(p.x - projX, p.y - projY)
}

@Composable
fun MiniPuzzlePreview(
    level: PuzzleLevel,
    modifier: Modifier = Modifier,
    lineColor: Color = NeonCyan,
    nodeColor: Color = TextPrimary,
) {
    Canvas(modifier = modifier) {
        val boardSize = min(size.width, size.height) * 0.84f
        val offsetX = (size.width - boardSize) / 2f
        val offsetY = (size.height - boardSize) / 2f

        fun posOf(id: Int): Offset {
            val v = level.vertices[id]
            return Offset(offsetX + v.x * boardSize, offsetY + v.y * boardSize)
        }

        for (e in level.edges) {
            val a = posOf(e.u)
            val b = posOf(e.v)
            drawLine(
                color = lineColor.copy(alpha = 0.25f),
                start = a,
                end = b,
                strokeWidth = 7f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = lineColor.copy(alpha = 0.9f),
                start = a,
                end = b,
                strokeWidth = 2.6f,
                cap = StrokeCap.Round,
            )
        }
        val r = (boardSize * 0.032f).coerceIn(2.5f, 5.5f)
        for (v in level.vertices) {
            drawCircle(
                color = nodeColor,
                radius = r,
                center = posOf(v.id),
            )
        }
    }
}
