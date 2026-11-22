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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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

private data class TrailSample(
    val pos: Offset,
    val age: Float = 0f,
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

    val particles = remember(level.id) { mutableStateListOf<SparkParticle>() }
    val dragTrail = remember(level.id, attemptNonce) { mutableStateListOf<TrailSample>() }
    val edgeBloomNanos = remember(level.id, attemptNonce) { mutableStateMapOf<Int, Long>() }
    var frameNowNanos by remember { mutableLongStateOf(0L) }
    var prevDrawnSnapshot by remember(level.id, attemptNonce) { mutableStateOf<Set<Int>>(emptySet()) }

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

    // Record timestamp whenever a new edge becomes drawn so it plays a neon bloom sweep
    LaunchedEffect(drawnEdgeIds, level.id, attemptNonce) {
        val newlyAdded = drawnEdgeIds - prevDrawnSnapshot
        if (newlyAdded.isNotEmpty()) {
            val now = System.nanoTime()
            newlyAdded.forEach { edgeId ->
                edgeBloomNanos[edgeId] = now
            }
        }
        prevDrawnSnapshot = drawnEdgeIds
    }

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

    // Frame animation loop active while particles, drag trail, or edge blooms are animating
    val hasActiveEffects = particles.isNotEmpty() || dragTrail.isNotEmpty() || edgeBloomNanos.isNotEmpty()
    LaunchedEffect(hasActiveEffects) {
        if (!hasActiveEffects) return@LaunchedEffect
        var lastNanos = withFrameNanos { it }
        while (particles.isNotEmpty() || dragTrail.isNotEmpty() || edgeBloomNanos.isNotEmpty()) {
            val now = withFrameNanos { it }
            frameNowNanos = System.nanoTime()
            val dt = ((now - lastNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
            lastNanos = now

            val pIter = particles.listIterator()
            while (pIter.hasNext()) {
                val p = pIter.next()
                p.x += p.vx * dt
                p.y += p.vy * dt
                p.vx *= 0.94f
                p.vy *= 0.94f
                p.life -= dt / p.maxLife
                if (p.life <= 0f) {
                    pIter.remove()
                }
            }

            for (i in dragTrail.lastIndex downTo 0) {
                val updatedAge = dragTrail[i].age + dt * 4.2f
                if (updatedAge >= 1f) {
                    dragTrail.removeAt(i)
                } else {
                    dragTrail[i] = dragTrail[i].copy(age = updatedAge)
                }
            }

            val expiredBlooms = edgeBloomNanos.filterValues { (frameNowNanos - it) > 320_000_000L }.keys.toList()
            expiredBlooms.forEach { edgeBloomNanos.remove(it) }
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

    // Track latest Compose state for pointerInput gesture loop
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
                    dragTrail.clear()
                    dragTrail.add(TrailSample(down.position))
                    spawnNodeSparks(vertexScreenPos(bestId, canvasSize), NeonCyan, count = 10)

                    val activePointerId = down.id
                    var protectedPrevVertexId: Int? = null

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

                        if (dragTrail.isEmpty() || hypot(currPos.x - dragTrail.last().pos.x, currPos.y - dragTrail.last().pos.y) > 6f) {
                            dragTrail.add(TrailSample(currPos))
                            if (dragTrail.size > 18) {
                                dragTrail.removeAt(0)
                            }
                        }

                        if (currentStatusState.value != PuzzleStatus.DRAWING) {
                            continue
                        }

                        val activeVertex = currentVertexState.value ?: continue

                        // Clear protection once finger moves away from the just-departed vertex
                        protectedPrevVertexId?.let { prevV ->
                            val prevVPos = vertexScreenPos(prevV, canvasSize)
                            if (hypot(currPos.x - prevVPos.x, currPos.y - prevVPos.y) > hitRadius * 0.72f) {
                                protectedPrevVertexId = null
                            }
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
                                protectedPrevVertexId = activeVertex
                            }
                            continue
                        }

                        // Check if user deliberately dragged onto an already-drawn neighbor (retracing!)
                        val retraceRadius = hitRadius * 0.46f
                        for (v in level.vertices) {
                            if (v.id == activeVertex || v.id == protectedPrevVertexId) continue
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

            // 3. Draw completed glowing edges (multi-pass neon bloom + completion flash)
            for (edge in level.edges) {
                if (!drawnEdgeIds.contains(edge.id)) continue
                val start = posOf(edge.u)
                val end = posOf(edge.v)
                val isFlashingInvalid = edge.id == invalidEdgeFlashId && status == PuzzleStatus.FAILED
                val glowColor = if (isFlashingInvalid) NeonCoral else NeonEmerald
                val coreColor = if (isFlashingInvalid) Color(0xFFFFB3C1) else Color(0xFFE0FFF6)

                val bloomStart = edgeBloomNanos[edge.id]
                val bloomProgress = if (bloomStart != null && frameNowNanos > bloomStart) {
                    (1f - ((frameNowNanos - bloomStart) / 300_000_000f)).coerceIn(0f, 1f)
                } else {
                    0f
                }

                drawLine(
                    color = glowColor.copy(alpha = 0.22f + 0.25f * bloomProgress),
                    start = start,
                    end = end,
                    strokeWidth = 28f + 16f * bloomProgress,
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
                    strokeWidth = 12f + 3f * bloomProgress,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = coreColor,
                    start = start,
                    end = end,
                    strokeWidth = 4.5f + 1.5f * bloomProgress,
                    cap = StrokeCap.Round,
                )
            }

            // 4. Smooth fading finger ribbon trail
            if (dragTrail.size > 1) {
                val trailPath = Path()
                trailPath.moveTo(dragTrail.first().pos.x, dragTrail.first().pos.y)
                for (i in 1 until dragTrail.size) {
                    val prev = dragTrail[i - 1].pos
                    val curr = dragTrail[i].pos
                    val mid = Offset((prev.x + curr.x) / 2f, (prev.y + curr.y) / 2f)
                    trailPath.quadraticTo(prev.x, prev.y, mid.x, mid.y)
                }
                trailPath.lineTo(dragTrail.last().pos.x, dragTrail.last().pos.y)
                val headAlpha = (1f - dragTrail.last().age).coerceIn(0f, 1f)
                drawPath(
                    path = trailPath,
                    color = NeonCyan.copy(alpha = 0.26f * headAlpha),
                    style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
                drawPath(
                    path = trailPath,
                    color = Color.White.copy(alpha = 0.45f * headAlpha),
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }

            // 5. Active tether from current vertex to finger/mouse with magnetic snap & retrace warning
            val activePos = currentVertexId?.let { posOf(it) }
            val cursor = pointerPos
            var magneticSnapVertexId: Int? = null
            if (status == PuzzleStatus.DRAWING && activePos != null && cursor != null) {
                val validNext = EulerSolver.validNextVertices(level, currentVertexId, drawnEdgeIds)
                val snapPreviewRadius = boardSize * 0.145f
                var nearestValidDist = Float.MAX_VALUE
                for (vId in validNext) {
                    val vp = posOf(vId)
                    val d = hypot(cursor.x - vp.x, cursor.y - vp.y)
                    if (d < snapPreviewRadius && d < nearestValidDist) {
                        nearestValidDist = d
                        magneticSnapVertexId = vId
                    }
                }

                // Detect if cursor is hovering directly over an already-drawn neighbor (retrace warning)
                val nearRetraceTarget = level.vertices.any { v ->
                    if (v.id == currentVertexId || v.id in validNext) return@any false
                    val drawnBetween = EulerSolver.findDrawnEdgeBetween(level, currentVertexId, v.id, drawnEdgeIds)
                    if (drawnBetween != null) {
                        val vp = posOf(v.id)
                        hypot(cursor.x - vp.x, cursor.y - vp.y) < boardSize * 0.12f
                    } else {
                        false
                    }
                }

                val tetherColor = when {
                    magneticSnapVertexId != null -> NeonEmerald
                    nearRetraceTarget -> NeonCoral
                    else -> NeonCyan
                }

                val tetherEnd = magneticSnapVertexId?.let { posOf(it) } ?: cursor
                drawLine(
                    color = tetherColor.copy(alpha = 0.26f),
                    start = activePos,
                    end = tetherEnd,
                    strokeWidth = 20f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonCyan, tetherColor),
                        start = activePos,
                        end = tetherEnd,
                    ),
                    start = activePos,
                    end = tetherEnd,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = activePos,
                    end = tetherEnd,
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = tetherColor.copy(alpha = 0.28f),
                    radius = 24f,
                    center = cursor,
                )
                drawCircle(
                    color = tetherColor.copy(alpha = 0.85f),
                    radius = 10f,
                    center = cursor,
                )
            }

            // 6. Draw vertices/nodes
            val nodeRadius = (boardSize * 0.036f).coerceIn(14f, 24f)
            val validTargets = if (status == PuzzleStatus.DRAWING && currentVertexId != null) {
                EulerSolver.validNextVertices(level, currentVertexId, drawnEdgeIds)
            } else {
                emptySet()
            }

            for (v in level.vertices) {
                val center = posOf(v.id)
                val isCurrent = v.id == currentVertexId
                val isMagneticTarget = v.id == magneticSnapVertexId
                val isValidTarget = v.id in validTargets
                val isOddSuggestedStart = status == PuzzleStatus.READY && oddVertices.contains(v.id)

                if (isCurrent) {
                    drawCircle(
                        color = NeonCyan.copy(alpha = 0.35f * pulse),
                        radius = nodeRadius * 1.9f,
                        center = center,
                    )
                } else if (isMagneticTarget) {
                    drawCircle(
                        color = NeonEmerald.copy(alpha = 0.42f),
                        radius = nodeRadius * 2.1f,
                        center = center,
                    )
                    drawCircle(
                        color = NeonEmerald,
                        radius = nodeRadius * 1.65f,
                        center = center,
                        style = Stroke(width = 3f),
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
                    isMagneticTarget -> NeonEmerald
                    isValidTarget -> NeonSurfaceHighlight(NeonEmerald)
                    else -> VertexIdle
                }
                val borderColor = when {
                    isCurrent -> Color.White
                    isValidTarget || isMagneticTarget -> NeonEmerald
                    isOddSuggestedStart -> NeonViolet
                    else -> VertexBorderIdle
                }

                val drawR = when {
                    isCurrent || isMagneticTarget -> nodeRadius * 1.18f
                    else -> nodeRadius
                }

                drawCircle(
                    color = fillColor,
                    radius = drawR,
                    center = center,
                )
                drawCircle(
                    color = borderColor,
                    radius = drawR,
                    center = center,
                    style = Stroke(width = 4f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = if (isCurrent || isMagneticTarget) 0.95f else 0.35f),
                    radius = nodeRadius * 0.32f,
                    center = center,
                )
            }

            // 7. Draw Hint Step Numbers on vertices when Hint is active
            if (revealedHintSteps > 0 && hintPath.isNotEmpty()) {
                drawHintBadges(
                    hintPath = hintPath,
                    revealedSteps = revealedHintSteps,
                    posOf = ::posOf,
                    nodeRadius = nodeRadius,
                    textMeasurer = textMeasurer,
                )
            }

            // 8. Draw particle sparks
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
    if (hintPath.size < 2 || revealedSteps <= 0) return
    val steps = revealedSteps.coerceAtMost(hintPath.size - 1)

    // 1. Highlight the starting vertex with a clear "START" pill above the node
    val startCenter = posOf(hintPath[0])
    drawCircle(
        color = NeonAmber.copy(alpha = 0.85f),
        radius = nodeRadius * 1.45f,
        center = startCenter,
        style = Stroke(width = 3.5f),
    )
    val startMeasured = textMeasurer.measure(
        text = "START",
        style = TextStyle(
            color = NavyDeepest,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
        ),
    )
    val pillW = startMeasured.size.width + 14f
    val pillH = startMeasured.size.height + 6f
    val pillTop = (startCenter.y - nodeRadius * 1.65f - pillH).coerceAtLeast(4f)
    val pillLeft = (startCenter.x - pillW / 2f).coerceIn(4f, (size.width - pillW - 4f).coerceAtLeast(4f))
    drawRoundRect(
        color = NeonAmber,
        topLeft = Offset(pillLeft, pillTop),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillH / 2f, pillH / 2f),
    )
    drawRoundRect(
        color = NavyDeepest,
        topLeft = Offset(pillLeft, pillTop),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillH / 2f, pillH / 2f),
        style = Stroke(width = 2f),
    )
    drawText(
        textLayoutResult = startMeasured,
        topLeft = Offset(
            pillLeft + (pillW - startMeasured.size.width) / 2f,
            pillTop + (pillH - startMeasured.size.height) / 2f,
        ),
    )

    // 2. Draw strictly sequential step-by-step numbers (1, 2, 3, ..., N) on each revealed edge + arrow
    val placedCenters = mutableListOf<Offset>()
    val badgeRadius = (nodeRadius * 0.78f).coerceIn(13f, 19f)

    for (i in 0 until steps) {
        val a = posOf(hintPath[i])
        val b = posOf(hintPath[i + 1])
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len = hypot(dx, dy).coerceAtLeast(1f)
        val ux = dx / len
        val uy = dy / len
        // Unit normal perpendicular to edge
        val nx = -uy
        val ny = ux

        // Directional arrowhead around 70% along edge
        val tipT = if (len > nodeRadius * 4f) 0.72f else 0.65f
        val tip = Offset(a.x + dx * tipT, a.y + dy * tipT)
        val arrowLen = (nodeRadius * 0.55f).coerceIn(8f, 13f)
        val arrowWing = arrowLen * 0.65f
        val baseCenter = Offset(tip.x - ux * arrowLen, tip.y - uy * arrowLen)
        val arrowPath = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(baseCenter.x + nx * arrowWing, baseCenter.y + ny * arrowWing)
            lineTo(baseCenter.x - nx * arrowWing, baseCenter.y - ny * arrowWing)
            close()
        }
        drawPath(path = arrowPath, color = NeonAmber)

        // Sequential edge step number badge at ~44% along edge (nudge perpendicularly if close to an existing badge)
        var badgeCenter = Offset(a.x + dx * 0.44f, a.y + dy * 0.44f)
        if (placedCenters.any { hypot(it.x - badgeCenter.x, it.y - badgeCenter.y) < badgeRadius * 2.1f }) {
            val cand1 = Offset(badgeCenter.x + nx * badgeRadius * 1.4f, badgeCenter.y + ny * badgeRadius * 1.4f)
            val cand2 = Offset(badgeCenter.x - nx * badgeRadius * 1.4f, badgeCenter.y - ny * badgeRadius * 1.4f)
            val d1 = placedCenters.minOfOrNull { hypot(it.x - cand1.x, it.y - cand1.y) } ?: Float.MAX_VALUE
            val d2 = placedCenters.minOfOrNull { hypot(it.x - cand2.x, it.y - cand2.y) } ?: Float.MAX_VALUE
            badgeCenter = if (d1 >= d2) cand1 else cand2
        }
        placedCenters.add(badgeCenter)

        drawCircle(
            color = NeonAmber,
            radius = badgeRadius,
            center = badgeCenter,
        )
        drawCircle(
            color = NavyDeepest,
            radius = badgeRadius,
            center = badgeCenter,
            style = Stroke(width = 2.2f),
        )
        val label = (i + 1).toString()
        val measured = textMeasurer.measure(
            text = label,
            style = TextStyle(
                color = NavyDeepest,
                fontSize = if (label.length >= 2) 10.sp else 11.sp,
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
