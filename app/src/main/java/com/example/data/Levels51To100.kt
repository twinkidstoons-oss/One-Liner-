package com.example.data

import com.example.engine.DifficultyTier
import com.example.engine.Edge
import com.example.engine.PuzzleLevel
import com.example.engine.Vertex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 50 additional verified single-line levels (Levels 51-100) inspired by the
 * iconic shapes in the reference sheets.
 */
object Levels51To100 {

    private fun buildLevel(
        id: Int,
        name: String,
        tier: DifficultyTier,
        parTime: Int,
        pts: List<Pair<Float, Float>>,
        links: List<Pair<Int, Int>>,
    ): PuzzleLevel {
        val vertices = pts.mapIndexed { idx, (x, y) -> Vertex(idx, x, y) }
        val edges = links.mapIndexed { idx, (u, v) -> Edge(idx, u, v) }
        return PuzzleLevel(
            id = id,
            name = name,
            tier = tier,
            vertices = vertices,
            edges = edges,
            parTimeSeconds = parTime,
        )
    }

    private fun ring(
        n: Int,
        radius: Float,
        startAngleDeg: Float = -90f,
        cx: Float = 0.5f,
        cy: Float = 0.5f,
    ): List<Pair<Float, Float>> = (0 until n).map { i ->
        val rad = (startAngleDeg + (360f * i / n)) * (PI / 180.0)
        (cx + radius * cos(rad).toFloat()) to (cy + radius * sin(rad).toFloat())
    }

    private fun cycleEdges(offset: Int, count: Int): List<Pair<Int, Int>> =
        (0 until count).map { i -> (offset + i) to (offset + (i + 1) % count) }

    val levels: List<PuzzleLevel> by lazy { createLevels51To100() }

    private fun createLevels51To100(): List<PuzzleLevel> = listOf(
        // ============================================================
        // TIER 6: ICONIC (Levels 51 - 60)
        // ============================================================
        // 51: Heart Emblem (8V, 10E - 2 odd at 0, 7) -> single stroke heart + inner pulse
        buildLevel(
            51, "Heart Emblem", DifficultyTier.ICONIC, 26,
            pts = listOf(
                0.50f to 0.34f, // 0 cleft
                0.32f to 0.18f, // 1 left top lobe
                0.15f to 0.34f, // 2 left outer
                0.24f to 0.58f, // 3 left lower
                0.50f to 0.84f, // 4 bottom tip
                0.76f to 0.58f, // 5 right lower
                0.85f to 0.34f, // 6 right outer
                0.68f to 0.18f, // 7 right top lobe
            ),
            // Outer heart 0-1-2-3-4-5-6-7-0 (all 2) + inner V 2-0-6? No, 0-4 chord (0:3, 4:3) -> 2 odd!
            // Add inner diamond 1-4? No, 1-3, 3-5, 5-7 ->
            // Wait: outer cycle (8E, all 2) + 0-4 (0:3, 4:3) + 3-5 (3:3, 5:3)? -> 4 odd!
            // Instead: outer cycle + 0-4 + 1-4? No: outer cycle + 0-3, 3-5, 5-0 (triangle inside heart -> 0:4, 3:4, 5:4 -> ALL EVEN!)
            links = cycleEdges(0, 8) + listOf(0 to 3, 3 to 5, 5 to 0),
        ),
        // 52: Infinity Loop (7V, 10E - all even!)
        buildLevel(
            52, "Infinity Loop", DifficultyTier.ICONIC, 26,
            pts = listOf(
                0.50f to 0.50f, // 0 center cross
                0.30f to 0.28f, 0.14f to 0.50f, 0.30f to 0.72f, // 1,2,3 left loop
                0.70f to 0.28f, 0.86f to 0.50f, 0.70f to 0.72f, // 4,5,6 right loop
            ),
            links = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 0,
                0 to 4, 4 to 5, 5 to 6, 6 to 0,
                1 to 4, 3 to 6, 1 to 3, 4 to 6,
            ),
        ),
        // 53: Ribbon Cross (7V, 9E - 2 odd at 1, 2)
        buildLevel(
            53, "Hope Ribbon", DifficultyTier.ICONIC, 28,
            pts = listOf(
                0.50f to 0.16f,
                0.32f to 0.34f, 0.68f to 0.34f,
                0.50f to 0.56f,
                0.24f to 0.84f, 0.76f to 0.84f,
                0.50f to 0.38f,
            ),
            links = listOf(0 to 1, 1 to 3, 3 to 2, 2 to 0, 3 to 4, 4 to 5, 5 to 3, 1 to 6, 6 to 2),
        ),
        // 54: Sealed Envelope (6V, 10E - 2 odd at 0, 1)
        buildLevel(
            54, "Sealed Mail", DifficultyTier.ICONIC, 28,
            pts = listOf(
                0.18f to 0.26f, 0.82f to 0.26f,
                0.50f to 0.52f,
                0.18f to 0.74f, 0.82f to 0.74f,
                0.50f to 0.86f,
            ),
            links = listOf(
                0 to 1, 1 to 4, 4 to 3, 3 to 0,
                0 to 2, 2 to 1, 3 to 2, 2 to 4,
                3 to 5, 5 to 4,
            ),
        ),
        // 55: Beamed Music Note (10V, 12E - 2 odd at 2, 3)
        buildLevel(
            55, "Neon Melody", DifficultyTier.ICONIC, 30,
            pts = listOf(
                0.36f to 0.24f, 0.78f to 0.16f,
                0.36f to 0.36f, 0.78f to 0.28f,
                0.36f to 0.66f, 0.18f to 0.72f, 0.26f to 0.84f,
                0.78f to 0.58f, 0.60f to 0.64f, 0.68f to 0.76f,
            ),
            links = listOf(
                0 to 1, 1 to 3, 2 to 0,
                2 to 4, 4 to 5, 5 to 6, 6 to 4,
                3 to 7, 7 to 8, 8 to 9, 9 to 7,
                4 to 7,
            ),
        ),
        // 56: Paper Airplane (6V, 8E - 2 odd at 0, 4)
        buildLevel(
            56, "Aero Glide", DifficultyTier.ICONIC, 30,
            pts = listOf(
                0.86f to 0.20f, // 0 nose
                0.14f to 0.44f, // 1 left wingtip
                0.42f to 0.54f, // 2 inner fold left
                0.42f to 0.80f, // 3 keel bottom
                0.62f to 0.58f, // 4 inner fold right
                0.74f to 0.74f, // 5 right wingtip
            ),
            links = listOf(0 to 1, 1 to 2, 2 to 0, 0 to 3, 3 to 4, 4 to 0, 4 to 5, 5 to 0),
        ),
        // 57: Lightning Bolt (7V, 10E - 2 odd)
        buildLevel(
            57, "Volt Strike", DifficultyTier.ICONIC, 30,
            pts = listOf(
                0.56f to 0.14f, // 0 top
                0.22f to 0.54f, // 1 left mid
                0.52f to 0.54f, // 2 inner mid
                0.40f to 0.86f, // 3 bottom tip
                0.78f to 0.40f, // 4 right mid
                0.48f to 0.40f, // 5 inner upper
                0.68f to 0.22f, // 6 right shoulder
            ),
            links = cycleEdges(0, 7) + listOf(0 to 5, 5 to 2, 2 to 4),
        ),
        // 58: Crescent Moon (6V, 8E - 2 odd)
        buildLevel(
            58, "Lunar Arc", DifficultyTier.ICONIC, 30,
            pts = listOf(
                0.56f to 0.14f, // 0 top horn
                0.26f to 0.30f, // 1 outer upper
                0.20f to 0.54f, // 2 outer mid
                0.32f to 0.78f, // 3 outer lower
                0.66f to 0.84f, // 4 bottom horn
                0.44f to 0.52f, // 5 inner crescent waist
            ),
            // Outer: 0-1-2-3-4-5-0 (deg 2) + inner chords 1-5, 3-5 -> deg: 1:3, 3:3, 5:4 -> 2 ODD (1, 3)!
            links = cycleEdges(0, 6) + listOf(1 to 5, 3 to 5),
        ),
        // 59: Arrow Emblem (7V, 9E - 2 odd)
        buildLevel(
            59, "Vector Arrow", DifficultyTier.ICONIC, 30,
            pts = listOf(
                0.14f to 0.40f, 0.52f to 0.40f, // 0,1 shaft top
                0.52f to 0.22f, 0.86f to 0.50f, 0.52f to 0.78f, // 2,3,4 arrowhead
                0.52f to 0.60f, 0.14f to 0.60f, // 5,6 shaft bottom
            ),
            // Outline 0-1-2-3-4-5-6-0 (all 2) + inner cross 1-5, 1-3? Wait: 1-5 makes 1:3, 5:3 (2 odd!). Plus 1-3, 5-3 -> 1:4, 5:4, 3:4 -> ALL EVEN!
            links = cycleEdges(0, 7) + listOf(1 to 5, 1 to 3, 5 to 3),
        ),
        // 60: Bowtie Ribbon (7V, 10E - all even!)
        buildLevel(
            60, "Prism Bow", DifficultyTier.ICONIC, 32,
            pts = listOf(
                0.50f to 0.50f, // 0 knot center
                0.20f to 0.24f, 0.14f to 0.50f, 0.20f to 0.76f, // 1,2,3 left wing
                0.80f to 0.24f, 0.86f to 0.50f, 0.80f to 0.76f, // 4,5,6 right wing
            ),
            // Left 0-1-2-3-0 + Right 0-4-5-6-0 + 0-2, 0-5, 1-3? No: 0-2(2:3), add 1-3(1:3,3:3)...
            // Better: 0-1-2-3-0 + 0-4-5-6-0 + 1-3, 4-6 -> wait 1:3,3:3,4:3,6:3 (4 odd).
            // Add 0-2, 0-5 AND 1-3, 4-6? Wait: 2:3, 5:3. Instead:
            // Left triangles: (0,1,2) and (0,2,3) -> 0-1, 1-2, 2-0, 2-3, 3-0 (deg: 0:3, 1:2, 2:3, 3:2) -> Wait 0-1-2-3-0 + 0-2 -> 0:3, 2:3!
            // Right: 0-4-5-6-0 + 0-5 -> 0:6, 2:3, 5:3 (ONLY 2 AND 5 ODD)!
            links = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 0, 0 to 2,
                0 to 4, 4 to 5, 5 to 6, 6 to 0, 0 to 5,
            ),
        ),

        // ============================================================
        // TIER 7: ARTIFACTS (Levels 61 - 70)
        // ============================================================
        // 61: Faceted Diamond Gem (7V, 11E - 2 odd)
        buildLevel(
            61, "Faceted Gem", DifficultyTier.ARTIFACTS, 34,
            pts = listOf(
                0.28f to 0.24f, 0.50f to 0.20f, 0.72f to 0.24f,
                0.14f to 0.44f, 0.50f to 0.46f, 0.86f to 0.44f,
                0.50f to 0.84f,
            ),
            links = listOf(
                0 to 1, 1 to 2, 2 to 5, 5 to 6, 6 to 3, 3 to 0,
                1 to 3, 1 to 5, 3 to 4, 4 to 5, 4 to 6,
            ),
        ),
        // 62: Royal Crown (8V, 12E - 2 odd)
        buildLevel(
            62, "Monarch Crown", DifficultyTier.ARTIFACTS, 34,
            pts = listOf(
                0.14f to 0.30f, 0.32f to 0.50f, 0.50f to 0.20f, 0.68f to 0.50f, 0.86f to 0.30f, // 0..4 peaks/valleys
                0.20f to 0.76f, 0.50f to 0.76f, 0.80f to 0.76f, // 5,6,7 base
            ),
            // 0-1, 1-2, 2-3, 3-4, 4-7, 7-6, 6-5, 5-0 (outer 8V, deg 2)
            // Inner jewel lines: 1-3? Crosses nothing? 1(0.32,0.5), 3(0.68,0.5), 6(0.50,0.76):
            // Triangle (1,3,6) + 2-6? No, 1-5, 3-7 (1:3, 5:3, 3:3, 7:3) + 1-6, 3-6 (1:4, 3:4, 6:4) -> leaves 5(3) and 7(3) ONLY 2 ODD!
            links = cycleEdges(0, 8) + listOf(1 to 5, 3 to 7, 1 to 6, 3 to 6),
        ),
        // 63: Vintage Key (8V, 11E - 2 odd)
        buildLevel(
            63, "Skeleton Key", DifficultyTier.ARTIFACTS, 34,
            pts = listOf(
                0.50f to 0.14f, 0.34f to 0.26f, 0.66f to 0.26f, 0.50f to 0.40f, // 0,1,2,3 key bow diamond
                0.50f to 0.64f, // 4 shaft mid
                0.50f to 0.86f, // 5 shaft tip
                0.70f to 0.64f, 0.70f to 0.80f, // 6,7 key teeth
            ),
            // Bow: 0-1-3-2-0 (deg 2) + inner cross 1-2 (1:3, 2:3) + shaft 3-4, 4-5, 4-6, 6-7, 7-5 ->
            // Wait: 3:3, 4:3, 5:2, 6:2, 7:2 -> 4 odd (1,2,3,4).
            // Add 0-3? Then 0:3, 3:4 -> leaves 0,1,2,4.
            // Better: 0-1-3-2-0 (all 2) + 3-4, 4-5, 4-6, 6-7, 7-5 -> deg: 3:3, 4:3, all others 2 -> ONLY 3 AND 4 ODD!
            // Plus inner bow loop 1-2, 0-2? No, 1-2 makes 1:3, 2:3. Add 1-4? No, 0-1-3-2-0 + 1-2 + 0-3? 0:3,1:3,2:3,3:4.
            // Add 1-3? already. Add 0-1-3-2-0 + 3-4-5-7-6-4 + 1-2? If we connect 1-4 and 2-4? Then 1:3,2:3,4:5!
            // Keep bow 0-1-3-2-0 + 1-2? No: 0-1, 1-2, 2-0, 1-3, 2-3 (0:2, 1:3, 2:3, 3:2) + 3-4, 4-6, 6-7, 7-5, 5-4 -> 3:3, 4:3 -> 4 odd.
            // Fix: add 1-2? Wait: 0-1-3-2-0 + 3-4-6-7-5-4 -> 3:3, 4:3 (ONLY 2 ODD!). To make bow richer: add 0-1.. wait 0-3? Then 0:3, 3:4 -> ONLY 0 AND 4 ODD!
            // Let's check 0(0.5,0.14) to 3(0.5,0.40) -> no node in between!
            // Degrees with: 0-1, 1-3, 3-2, 2-0, 0-3, 3-4, 4-6, 6-7, 7-5, 5-4:
            // 0:3, 1:2, 2:2, 3:4, 4:3, 5:2, 6:2, 7:2 -> ONLY 0 AND 4 ODD!
            links = listOf(
                0 to 1, 1 to 3, 3 to 2, 2 to 0, 0 to 3,
                3 to 4, 4 to 6, 6 to 7, 7 to 5, 5 to 4,
            ),
        ),
        // 64: Victory Trophy (9V, 12E - 2 odd)
        buildLevel(
            64, "Champion Cup", DifficultyTier.ARTIFACTS, 36,
            pts = listOf(
                0.24f to 0.18f, 0.76f to 0.18f, // 0,1 rim
                0.14f to 0.36f, 0.86f to 0.36f, // 2,3 handles
                0.32f to 0.52f, 0.68f to 0.52f, // 4,5 cup base
                0.50f to 0.66f, // 6 stem
                0.28f to 0.84f, 0.72f to 0.84f, // 7,8 pedestal
            ),
            // Cup: 0-1, 0-4, 4-5, 5-1 (all 2)
            // Handles: 0-2, 2-4, 1-3, 3-5 -> 0:3, 1:3, 4:3, 5:3 -> + 0-5? No.
            // Instead: 0-1, 0-2, 2-4, 4-5, 5-3, 3-1 (outer cup+handles = 6 cycle!) + inner cup walls 0-4, 1-5 -> 0:3, 1:3, 4:3, 5:3.
            // Connect stem: 4-6, 5-6, 6-7, 7-8, 8-6 -> 4:4, 5:4, 6:4, 7:2, 8:2 -> ONLY 0 AND 1 ODD!
            links = listOf(
                0 to 1, 0 to 2, 2 to 4, 4 to 5, 5 to 3, 3 to 1,
                0 to 4, 1 to 5,
                4 to 6, 5 to 6, 6 to 7, 7 to 8, 8 to 6,
            ),
        ),
        // 65: Liberty Bell (8V, 11E - 2 odd)
        buildLevel(
            65, "Resonant Bell", DifficultyTier.ARTIFACTS, 36,
            pts = listOf(
                0.50f to 0.14f, // 0 top crown
                0.28f to 0.30f, 0.72f to 0.30f, // 1,2 upper bell
                0.24f to 0.58f, 0.76f to 0.58f, // 3,4 waist
                0.14f to 0.76f, 0.86f to 0.76f, // 5,6 flared rim
                0.50f to 0.86f, // 7 clapper
            ),
            // Outer bell 0-1-3-5-6-4-2-0 (deg 2)
            // Clapper V: 5-7, 7-6 -> 5:3, 6:3, 7:2 -> + 1-2? (1:3, 2:3 -> 4 odd).
            // Instead of 1-2, add 0-7? Crosses 1-2 if 1-2 exists. Add 3-4, 3-7, 4-7?
            // With outer (0..6) + 5-7, 7-6 + 3-4 -> 3:3, 4:3, 5:3, 6:3 (4 odd).
            // Add 3-7, 4-7 -> 3:4, 4:4, 7:4, leaving ONLY 5(3) and 6(3) ODD!
            links = listOf(
                0 to 1, 1 to 3, 3 to 5, 5 to 6, 6 to 4, 4 to 2, 2 to 0,
                5 to 7, 7 to 6, 3 to 4, 3 to 7, 4 to 7,
            ),
        ),
        // 66: Space Rocket (8V, 12E - 2 odd)
        buildLevel(
            66, "Nova Rocket", DifficultyTier.ARTIFACTS, 36,
            pts = listOf(
                0.50f to 0.12f, // 0 nose
                0.34f to 0.36f, 0.66f to 0.36f, // 1,2 upper body
                0.34f to 0.68f, 0.66f to 0.68f, // 3,4 lower body
                0.16f to 0.82f, 0.84f to 0.82f, // 5,6 side fins
                0.50f to 0.86f, // 7 thruster
            ),
            // Hull: 0-1, 1-3, 3-4, 4-2, 2-0, 1-2 (deg: 0:2, 1:3, 2:3, 3:2, 4:2)
            // Fins & thruster: 1-5, 5-3, 2-6, 6-4, 3-7, 7-4 ->
            // deg: 0:2, 1:4, 2:4, 3:4, 4:4, 5:2, 6:2, 7:2 -> ALL EVEN!
            links = listOf(
                0 to 1, 1 to 3, 3 to 4, 4 to 2, 2 to 0, 1 to 2,
                1 to 5, 5 to 3, 2 to 6, 6 to 4, 3 to 7, 7 to 4,
            ),
        ),
        // 67: Knight Shield (8V, 12E - all even!)
        buildLevel(
            67, "Aegis Shield", DifficultyTier.ARTIFACTS, 38,
            pts = listOf(
                0.20f to 0.22f, 0.50f to 0.16f, 0.80f to 0.22f, // 0,1,2 top rim
                0.22f to 0.54f, 0.50f to 0.48f, 0.78f to 0.54f, // 3,4,5 mid
                0.50f to 0.86f, // 6 bottom tip
            ),
            // Outer shield 0-1-2-5-6-3-0 (deg 2)
            // Inner cross: 1-4, 4-6, 3-4, 4-5 -> deg: 1:3, 3:3, 5:3, 6:3, 4:4 -> 4 odd!
            // Connect 1-3, 1-5? -> 1:5, 3:4, 5:4 -> 1(5) and 6(3) ONLY 2 ODD!
            links = listOf(
                0 to 1, 1 to 2, 2 to 5, 5 to 6, 6 to 3, 3 to 0,
                1 to 4, 4 to 6, 3 to 4, 4 to 5, 1 to 3, 1 to 5,
            ),
        ),
        // 68: Castle Rook (9V, 15E - 2 odd at 3, 4)
        buildLevel(
            68, "Citadel Rook", DifficultyTier.ARTIFACTS, 38,
            pts = listOf(
                0.20f to 0.22f, 0.50f to 0.14f, 0.80f to 0.22f,
                0.32f to 0.38f, 0.68f to 0.38f,
                0.32f to 0.68f, 0.68f to 0.68f,
                0.18f to 0.84f, 0.82f to 0.84f,
            ),
            links = listOf(
                0 to 1, 1 to 2, 2 to 4, 4 to 6, 6 to 8, 8 to 7, 7 to 5, 5 to 3, 3 to 0,
                3 to 4, 5 to 6, 3 to 6, 4 to 5, 1 to 3, 1 to 4,
            ),
        ),
        // 69: Hero Sword (8V, 11E - 2 odd)
        buildLevel(
            69, "Blade Crest", DifficultyTier.ARTIFACTS, 38,
            pts = listOf(
                0.50f to 0.12f, // 0 tip
                0.40f to 0.28f, 0.60f to 0.28f, // 1,2 upper blade
                0.42f to 0.62f, 0.58f to 0.62f, // 3,4 hilt top
                0.22f to 0.66f, 0.78f to 0.66f, // 5,6 crossguard tips
                0.50f to 0.88f, // 7 pommel
            ),
            // Blade: 0-1, 1-3, 3-4, 4-2, 2-0 (all 2)
            // Crossguard & pommel: 3-5, 5-7, 7-6, 6-4 -> 3:3, 4:3, 5:2, 6:2, 7:2 -> 2 ODD (3, 4)!
            // Plus fuller spine 0-3? No, 1-4, 2-3? Crosses cleanly! 1-4, 2-3 -> 1:3, 2:3, 3:4, 4:4 (2 odd: 1, 2)!
            links = listOf(
                0 to 1, 1 to 3, 3 to 4, 4 to 2, 2 to 0,
                3 to 5, 5 to 7, 7 to 6, 6 to 4,
                1 to 4, 2 to 3,
            ),
        ),
        // 70: Locked Padlock (8V, 14E - all even!)
        buildLevel(
            70, "Vault Lock", DifficultyTier.ARTIFACTS, 40,
            pts = listOf(
                0.34f to 0.40f, 0.66f to 0.40f,
                0.34f to 0.16f, 0.66f to 0.16f,
                0.20f to 0.46f, 0.80f to 0.46f,
                0.20f to 0.84f, 0.80f to 0.84f,
            ),
            links = listOf(
                0 to 2, 2 to 3, 3 to 1, 1 to 0,
                0 to 4, 4 to 6, 6 to 7, 7 to 5, 5 to 1,
                0 to 7, 1 to 6, 4 to 5, 4 to 7, 5 to 6,
            ),
        ),

        // ============================================================
        // TIER 8: NATURE (Levels 71 - 80)
        // ============================================================
        // 71: Forest Leaf (7V, 11E - 2 odd)
        buildLevel(
            71, "Emerald Leaf", DifficultyTier.NATURE, 38,
            pts = listOf(
                0.76f to 0.16f, // 0 leaf tip
                0.42f to 0.22f, 0.24f to 0.48f, // 1,2 upper blade
                0.28f to 0.72f, // 3 stem base
                0.56f to 0.72f, 0.78f to 0.46f, // 4,5 lower blade
                0.52f to 0.46f, // 6 midrib node
            ),
            // Outline: 0-1-2-3-4-5-0 (all 2)
            // Midrib & veins: 3-6, 6-0, 1-6, 6-4 (3:3, 0:3, 1:3, 4:3, 6:4 -> 4 odd).
            // Omit 1-6, keep 3-6, 6-0, 2-6, 6-5? No: 3-6, 6-0 (3:3, 0:3) + 2-6, 6-4 (2:3, 4:3).
            // Add 2-4? No, just 3-6, 6-0 + 1-6, 6-5? No, 1-6, 6-4 -> 3,0,1,4 odd.
            // Best: Outline (6E) + 3-6, 6-0 (3:3, 0:3) + 1-6, 6-2 (1:3, 2:3 -> wait 1-2 already, forms triangle (1,2,6) so 1:3,2:3)!
            // Add 4-6, 5-6 -> 4:3, 5:3.
            // Wait: if we ONLY add 3-6, 6-0, 1-6, 6-5 -> 1:3, 5:3, 3:3, 0:3. Add 1-5? Crosses 6!
            // If we add 3-6, 6-0 -> ONLY 3 AND 0 ARE ODD! And add 1-6, 6-4? No, 1-2? already. Add 1-4? Crosses 6.
            // Add 1-6, 6-5 -> 1:3, 5:3. Put vein 1-5? Crosses 6. Put 1-0? already.
            // Add 2-6, 6-4 AND 2-4? Wait: 2-4 doesn't cross 6 (6 is at 0.52,0.46; 2(0.24,0.48) to 4(0.56,0.72) goes BELOW 6)!
            // Let's check! Outline + 3-6, 6-0 + 2-6, 6-4, 2-4 -> deg: 0:3, 1:2, 2:4, 3:3, 4:4, 5:2, 6:4 -> ONLY 0 AND 3 ODD!
            links = cycleEdges(0, 6) + listOf(3 to 6, 6 to 0, 2 to 6, 6 to 4, 2 to 4),
        ),
        // 72: Neon Butterfly (9V, 15E - 2 odd at 0, 2)
        buildLevel(
            72, "Monarch Wings", DifficultyTier.NATURE, 42,
            pts = listOf(
                0.50f to 0.27f, 0.50f to 0.48f, 0.50f to 0.76f, // 0,1,2 thorax/body
                0.16f to 0.18f, 0.14f to 0.50f, 0.24f to 0.78f, // 3,4,5 left wing
                0.84f to 0.18f, 0.86f to 0.50f, 0.76f to 0.78f, // 6,7,8 right wing
            ),
            links = listOf(
                0 to 1, 1 to 2,
                0 to 3, 3 to 4, 4 to 1, 1 to 5, 5 to 2,
                0 to 6, 6 to 7, 7 to 1, 1 to 8, 8 to 2,
                3 to 1, 6 to 1, 3 to 6,
            ),
        ),
        // 73: Ocean Fish (8V, 11E - 2 odd)
        buildLevel(
            73, "Cyber Koi", DifficultyTier.NATURE, 40,
            pts = listOf(
                0.86f to 0.50f, // 0 nose
                0.62f to 0.26f, 0.34f to 0.34f, // 1,2 top body
                0.14f to 0.22f, 0.20f to 0.50f, 0.14f to 0.78f, // 3,4,5 tail fin
                0.34f to 0.66f, 0.62f to 0.74f, // 6,7 bottom body
            ),
            // Body+tail: 0-1-2-4-6-7-0 (deg 2) + tail loop 2-3-4-5-6 -> Wait:
            // Outline: 0-1-2-3-4-5-6-7-0 (all 2)
            // Tail crossover: 2-4, 4-6 (2:3, 4:4, 6:3 -> 2 odd!)
            // Gill line: 1-7 (1:3, 7:3 -> 4 odd). Add 1-4? Crosses. Add 2-6! (2:4, 6:4 -> leaves 1, 7 ODD!)
            links = cycleEdges(0, 8) + listOf(2 to 4, 4 to 6, 1 to 7, 2 to 6),
        ),
        // 74: Whisker Cat (9V, 12E - 2 odd)
        buildLevel(
            74, "Cyber Cat", DifficultyTier.NATURE, 42,
            pts = listOf(
                0.24f to 0.16f, 0.76f to 0.16f, // 0,1 ear tips
                0.22f to 0.44f, 0.50f to 0.34f, 0.78f to 0.44f, // 2,3,4 head top/sides
                0.36f to 0.58f, 0.64f to 0.58f, // 5,6 cheeks/eyes
                0.50f to 0.66f, // 7 nose
                0.50f to 0.84f, // 8 chin
            ),
            // Ears/head: 0-2, 0-3, 3-1, 1-4, 2-8, 8-4 (all 2)
            // Nose/eyes triangles: 2-5, 5-7, 7-6, 6-4, 3-5, 3-6, 5-6?
            // Check deg: 0:2, 1:2, 2:3, 3:4, 4:3, 5:4, 6:4, 7:2, 8:2 -> 2 ODD (2, 4)!
            // Add 7-8? Then 7:3, 8:3 (4 odd). Keep clean without 7-8 -> ONLY 2 AND 4 ODD!
            links = listOf(
                0 to 2, 0 to 3, 3 to 1, 1 to 4, 2 to 8, 8 to 4,
                2 to 5, 5 to 7, 7 to 6, 6 to 4, 3 to 5, 3 to 6, 5 to 6,
            ),
        ),
        // 75: Origami Rabbit (9V, 12E - 2 odd)
        buildLevel(
            75, "Neon Rabbit", DifficultyTier.NATURE, 42,
            pts = listOf(
                0.42f to 0.14f, 0.62f to 0.16f, // 0,1 ears
                0.48f to 0.38f, 0.68f to 0.40f, // 2,3 head
                0.24f to 0.54f, 0.58f to 0.60f, // 4,5 back / chest
                0.16f to 0.74f, 0.36f to 0.80f, 0.72f to 0.80f, // 6 tail, 7,8 feet
            ),
            // Ears: 0-2, 2-1, 1-3, 3-2 (Wait: 0-2, 1-2? 0 would be deg 1! Connect 0-1: 0-1-3-2-0 -> all 2! Plus 1-2 -> 1:3, 2:3)
            // Body: 2-4, 4-6, 6-7, 7-5, 5-3, 4-5, 5-8, 8-3 ->
            // Check deg: 0:2, 1:3, 2:4, 3:4, 4:3, 5:4, 6:2, 7:2, 8:2 -> 2 ODD (1, 4)!
            links = listOf(
                0 to 1, 1 to 3, 3 to 2, 2 to 0, 1 to 2,
                2 to 4, 4 to 6, 6 to 7, 7 to 5, 5 to 3,
                4 to 5, 5 to 8, 8 to 3,
            ),
        ),
        // 76: Sacred Elephant (10V, 13E - 2 odd)
        buildLevel(
            76, "Titan Elephant", DifficultyTier.NATURE, 44,
            pts = listOf(
                0.20f to 0.38f, 0.46f to 0.24f, 0.72f to 0.28f, // 0 rump, 1 back, 2 head
                0.86f to 0.54f, 0.76f to 0.68f, // 3,4 trunk tip
                0.62f to 0.54f, 0.56f to 0.82f, // 5 ear/neck, 6 front leg
                0.38f to 0.66f, 0.24f to 0.82f, // 7 belly, 8 back leg
                0.48f to 0.46f, // 9 ear center
            ),
            // Outer: 0-1, 1-2, 2-3, 3-4, 4-5, 5-6, 6-7, 7-8, 8-0 (9V cycle, deg 2)
            // Inner ear/body: 1-9, 9-2, 9-5, 9-7 -> deg: 1:3, 2:3, 5:3, 7:3, 9:4 -> 4 odd!
            // Add 2-5 -> 2:4, 5:4 -> leaves ONLY 1(3) and 7(3) ODD!
            links = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 6, 6 to 7, 7 to 8, 8 to 0,
                1 to 9, 9 to 2, 9 to 5, 9 to 7, 2 to 5,
            ),
        ),
        // 77: Blooming Lotus (10V, 16E - all even!)
        buildLevel(
            77, "Lotus Bloom", DifficultyTier.NATURE, 44,
            pts = listOf(
                0.50f to 0.16f, // 0 center petal tip
                0.36f to 0.42f, 0.64f to 0.42f, // 1,2 inner waist
                0.22f to 0.28f, 0.78f to 0.28f, // 3,4 mid petal tips
                0.14f to 0.56f, 0.86f to 0.56f, // 5,6 side petal tips
                0.34f to 0.72f, 0.66f to 0.72f, // 7,8 base corners
                0.50f to 0.84f, // 9 root
            ),
            // Center petal: 0-1-9-2-0 (deg 2)
            // Mid petals: 3-1, 3-7, 4-2, 4-8 -> (1:3, 2:3, 3:2, 4:2, 7:1, 8:1)
            // Side petals: 5-7, 5-1, 6-8, 6-2 -> (1:4, 2:4, 5:2, 6:2, 7:2, 8:2)
            // Base connections: 7-9, 9-8 -> (7:3, 8:3, 9:4) + 7-8? (7:4, 8:4!) -> ALL EVEN!
            links = listOf(
                0 to 1, 1 to 9, 9 to 2, 2 to 0,
                3 to 1, 3 to 7, 4 to 2, 4 to 8,
                5 to 7, 5 to 1, 6 to 8, 6 to 2,
                7 to 9, 9 to 8, 7 to 8,
            ),
        ),
        // 78: Six-Petal Flower (13V, 21E - all even!)
        buildLevel(
            78, "Star Blossom", DifficultyTier.NATURE, 46,
            pts = listOf(0.50f to 0.50f) + ring(6, 0.38f, -90f) + ring(6, 0.24f, -60f),
            links = (0 until 6).flatMap { i ->
                val tip = 1 + i
                val vRight = 7 + i
                val vLeft = 7 + (i + 5) % 6
                listOf(0 to vRight, vRight to tip, tip to vLeft)
            } + listOf(7 to 8, 9 to 10, 11 to 12),
        ),
        // 79: Geometric Tree (10V, 16E - 2 odd at 0, 4)
        buildLevel(
            79, "Fractal Tree", DifficultyTier.NATURE, 46,
            pts = listOf(
                0.50f to 0.14f, // 0 canopy peak
                0.24f to 0.34f, 0.76f to 0.34f, // 1,2 upper canopy
                0.16f to 0.58f, 0.50f to 0.44f, 0.84f to 0.58f, // 3,4,5 lower canopy + core
                0.42f to 0.64f, 0.58f to 0.64f, // 6,7 trunk top
                0.38f to 0.86f, 0.62f to 0.86f, // 8,9 trunk roots
            ),
            links = listOf(
                0 to 1, 1 to 3, 3 to 6, 7 to 5, 5 to 2, 2 to 0,
                0 to 4, 1 to 4, 2 to 4, 4 to 6, 4 to 7, 1 to 6, 2 to 7,
                6 to 8, 8 to 9, 9 to 7,
            ),
        ),
        // 80: Scallop Shell (9V, 14E - 2 odd)
        buildLevel(
            80, "Pearl Shell", DifficultyTier.NATURE, 46,
            pts = listOf(
                0.50f to 0.82f, // 0 hinge base
                0.34f to 0.74f, 0.66f to 0.74f, // 1,2 base wings
                0.16f to 0.50f, 0.28f to 0.26f, 0.50f to 0.16f, 0.72f to 0.26f, 0.84f to 0.50f, // 3..7 scalloped rim
                0.50f to 0.52f, // 8 pearl center
            ),
            // Rim: 0-1, 1-3, 3-4, 4-5, 5-6, 6-7, 7-2, 2-0 (all 2)
            // Ribs: 0-8, 8-4, 8-5, 8-6 -> 0:3, 4:3, 5:3, 6:3, 8:4.
            // Connect 4-6? Crosses 5! Connect 3-8, 7-8 -> 3:3, 7:3, 8:6.
            // Add 1-8, 2-8 -> 1:3, 2:3, 8:8. Wait: if we connect 8 to ONLY 4, 5, 6 and 0 -> 0,4,5,6 odd.
            // Connect 1-4, 2-6 -> 1:3, 4:4, 2:3, 6:4 -> leaves 0,1,2,5 odd.
            // Connect 1-8, 2-8? 1:3, 2:3, 8:6. Wait: add 1-2? (1:4, 2:4) -> passes right across 0-8!
            // Put 8 at (0.50, 0.44), ribs from 0 to 3, 7 and 0-8, 8-4, 8-6, 4-6? No:
            // Rim (0..7) + 0-4, 0-6, 4-8, 8-6, 8-5, 0-5? No: 0-8 passes through!
            // Put 8 at (0.50, 0.52), connect 0-3? 0-1 already. Connect 1-4, 2-6, 4-8, 8-6, 5-8, 0-8 ->
            // deg: 0:3, 1:3, 2:3, 3:2, 4:3, 5:3, 6:3, 7:2, 8:4.
            // Pair 1-5? No! Pair 1-8, 2-8 -> 1:4, 2:4, 8:6! Then odd: 0(3), 4(3), 5(3), 6(3).
            // Pair 4-5? already edge! Omit 5-8, keep 0-8, 1-8, 2-8, 4-8, 6-8 -> 0:3, 1:4, 2:4, 4:4, 5:2, 6:4, 8:5 -> ONLY 0 AND 8 ODD!
            links = listOf(
                0 to 1, 1 to 3, 3 to 4, 4 to 5, 5 to 6, 6 to 7, 7 to 2, 2 to 0,
                1 to 4, 2 to 6,
                0 to 8, 1 to 8, 2 to 8, 4 to 8, 6 to 8,
            ),
        ),

        // ============================================================
        // TIER 9: WONDER (Levels 81 - 90)
        // ============================================================
        // 81: Rain Umbrella (9V, 13E - 2 odd)
        buildLevel(
            81, "Storm Canopy", DifficultyTier.WONDER, 46,
            pts = listOf(
                0.50f to 0.16f, // 0 finial top
                0.24f to 0.28f, 0.76f to 0.28f, // 1,2 canopy shoulders
                0.14f to 0.50f, 0.38f to 0.50f, 0.62f to 0.50f, 0.86f to 0.50f, // 3,4,5,6 canopy hem
                0.50f to 0.76f, 0.62f to 0.84f, // 7,8 J-handle
            ),
            // Canopy: 0-1, 1-3, 3-4, 4-5, 5-6, 6-2, 2-0 (deg 2)
            // Ribs: 0-4, 0-5 (0:4, 4:3, 5:3)
            // Handle: 4-7, 7-8, 8-5 -> 4:4, 5:4, 7:2, 8:2 -> ALL EVEN!
            // Plus 1-4, 2-5 -> 1:3, 2:3, 4:5, 5:5? No, add 1-4, 4-2? No: 1-4, 2-5 makes 1,2,4,5 odd.
            // Add ONLY 1-4, 2-5? Wait: omit 4-5? No!
            // Just add 1-5? Crosses 0-4. Add 0-3, 0-6? No, 0-1-3 already.
            // Connect 1-4, 2-5 AND remove 0-4, 0-5 -> 1:3, 2:3, 4:4, 5:4 -> ONLY 1 AND 2 ODD!
            links = listOf(
                0 to 1, 1 to 3, 3 to 4, 4 to 5, 5 to 6, 6 to 2, 2 to 0,
                1 to 4, 2 to 5, 4 to 7, 7 to 8, 8 to 5,
            ),
        ),
        // 82: Ice Cream Cone (9V, 16E - 2 odd at 3, 5)
        buildLevel(
            82, "Gelato Cone", DifficultyTier.WONDER, 46,
            pts = listOf(
                0.50f to 0.14f, // 0 cherry top
                0.28f to 0.28f, 0.72f to 0.28f, // 1,2 upper scoop
                0.24f to 0.46f, 0.50f to 0.46f, 0.76f to 0.46f, // 3,4,5 cone rim
                0.36f to 0.66f, 0.64f to 0.66f, // 6,7 cone mid
                0.50f to 0.88f, // 8 cone tip
            ),
            links = listOf(
                0 to 1, 1 to 3, 3 to 4, 4 to 5, 5 to 2, 2 to 0, 1 to 2,
                3 to 6, 6 to 8, 8 to 7, 7 to 5,
                4 to 6, 4 to 7, 6 to 7, 1 to 4, 2 to 4,
            ),
        ),
        // 83: Idea Lightbulb (9V, 13E - 2 odd)
        buildLevel(
            83, "Spark Bulb", DifficultyTier.WONDER, 48,
            pts = listOf(
                0.50f to 0.14f, // 0 top
                0.24f to 0.28f, 0.76f to 0.28f, // 1,2 sides
                0.34f to 0.56f, 0.50f to 0.40f, 0.66f to 0.56f, // 3,4,5 filament/neck
                0.36f to 0.72f, 0.64f to 0.72f, // 6,7 screw base
                0.50f to 0.86f, // 8 contact tip
            ),
            // Bulb: 0-1, 1-3, 3-6, 6-8, 8-7, 7-5, 5-2, 2-0 (all 2)
            // Filament & base: 3-4, 4-5, 3-5, 6-7 -> 3:4, 5:4, 4:2, 6:3, 7:3 -> 2 ODD (6, 7)!
            // Plus 0-4, 1-4, 2-4? Wait: 0-4 makes 0:3, 4:3; + 1-4, 2-4 makes 1:3, 2:3, 4:5 (too many).
            // Just 1-4, 2-4 -> 1:3, 2:3, 4:4! Wait: then 1,2,6,7 odd. Add 1-6? Crosses 3!
            // Keep clean: 0-4, 4-1? No, 1-4, 2-4 + 3-6, 5-7 already -> instead add 0-4, 4-3? No:
            // Add 1-4, 2-4, 3-4, 4-5, 3-5, 6-7 -> 1:3, 2:3, 3:4, 4:6, 5:4, 6:3, 7:3.
            // Remove 6-7 -> 6:2, 7:2 -> ONLY 1 AND 2 ODD!
            links = listOf(
                0 to 1, 1 to 3, 3 to 6, 6 to 8, 8 to 7, 7 to 5, 5 to 2, 2 to 0,
                1 to 4, 2 to 4, 3 to 4, 4 to 5, 3 to 5,
            ),
        ),
        // 84: Sea Anchor (9V, 12E - 2 odd)
        buildLevel(
            84, "Harbor Anchor", DifficultyTier.WONDER, 48,
            pts = listOf(
                0.50f to 0.14f, 0.40f to 0.24f, 0.60f to 0.24f, // 0,1,2 ring
                0.28f to 0.38f, 0.50f to 0.38f, 0.72f to 0.38f, // 3,4,5 crossbar
                0.18f to 0.64f, 0.82f to 0.64f, // 6,7 flukes
                0.50f to 0.86f, // 8 crown
            ),
            // Ring: 0-1, 1-4, 4-2, 2-0 (deg 2)
            // Crossbar & shaft: 3-4, 4-5, 4-8 (3:1, 5:1, 4:5, 8:1)
            // Flukes: 6-8, 8-7, 3-6, 5-7 -> 3:2, 5:2, 6:2, 7:2, 8:3 -> 4:5, 8:3 -> 2 ODD (4, 8)!
            // Plus 1-3, 2-5 -> 1:3, 3:3, 2:3, 5:3 (4 odd). Keep clean without 1-3, 2-5 -> ONLY 4 AND 8 ODD!
            links = listOf(
                0 to 1, 1 to 4, 4 to 2, 2 to 0,
                3 to 4, 4 to 5, 4 to 8,
                3 to 6, 6 to 8, 8 to 7, 7 to 5,
            ),
        ),
        // 85: Retro Camera (10V, 16E - all even!)
        buildLevel(
            85, "Lens Capture", DifficultyTier.WONDER, 50,
            pts = listOf(
                0.36f to 0.20f, 0.64f to 0.20f, // 0,1 prism top
                0.16f to 0.32f, 0.84f to 0.32f, // 2,3 body top corners
                0.16f to 0.78f, 0.84f to 0.78f, // 4,5 body bottom corners
                0.50f to 0.38f, 0.34f to 0.55f, 0.50f to 0.72f, 0.66f to 0.55f, // 6,7,8,9 lens diamond
            ),
            // Body: 0-1, 1-3, 3-5, 5-4, 4-2, 2-0 (all 2)
            // Lens: 6-7-8-9-6 (all 2)
            // Connectors: 2-7, 7-4, 3-9, 9-5 -> 2:3, 4:3, 3:3, 5:3, 7:4, 9:4 -> 4 odd!
            // Connect 0-6, 1-6 -> 0:3, 1:3, 6:4. Still 0,1,2,4,3,5 odd.
            // Fix: 2-6, 3-6, 4-8, 5-8 -> 2:3,3:3,4:3,5:3,6:4,8:4. Pair 2-4(already), 3-5(already)!
            //Wait: 2(0.16,0.32) already connects to 4(0.16,0.78)!
            // So just add lens connectors: 0-6, 6-1, 4-8, 8-5 -> 0:3, 1:3, 4:3, 5:3 -> + 0-2? already!
            // Even cleaner: Body 2-3-5-4-2 + Prism 0-1-3? Wait:
            // Outer: 0-1-3-5-4-2-0 (deg 2)
            // Lens: 6-7-8-9-6 (deg 2)
            // Bridges: 2-7, 7-4 (7:4, 2:3, 4:3) -> wait 2-4 IS ALREADY AN EDGE, so (2,4,7) is a triangle? Yes! Wait 2:3, 4:3!
            // To make 2 & 4 even, connect 2-6 and 4-8? No, then 6:3, 8:3 -> add 6-8 -> 6:4, 8:4!
            // Let's check:
            // Outer: 0-1-3-5-4-2-0 (all 2)
            // Lens: 6-7-8-9-6 (all 2)
            // Spokes: 0-6, 1-6, 4-8, 5-8 (0:3, 1:3, 4:3, 5:3, 6:4, 8:4) ->
            // Add 2-7, 3-9 -> 2:3, 7:3, 3:3, 9:3. No!
            // Add ONLY: 6-8, 7-9? Crosses inside lens! Wait: 0-6, 1-6, 2-7, 4-7, 3-9, 5-9, 7-8? No:
            // Keep prism simple: 0-6, 1-6 (0:3, 1:3, 6:4) -> add 0-3? No, 2-6, 3-6!
            // Wait: 2-6, 3-6 (2:3,3:3,6:4), 4-8, 5-8 (4:3,5:3,8:4) + 2-7, 4-7 (2:4,4:4,7:4) + 3-9, 5-9 (3:4,5:4,9:4) -> ALL DEGREE 4 OR 2! ALL EVEN!
            links = listOf(
                0 to 1, 1 to 3, 3 to 5, 5 to 4, 4 to 2, 2 to 0,
                6 to 7, 7 to 8, 8 to 9, 9 to 6,
                2 to 6, 3 to 6, 4 to 8, 5 to 8,
                2 to 7, 4 to 7, 3 to 9, 5 to 9,
            ),
        ),
        // 86: Cool Glasses (10V, 14E - 2 odd)
        buildLevel(
            86, "Cyber Shades", DifficultyTier.WONDER, 50,
            pts = listOf(
                0.22f to 0.24f, 0.78f to 0.24f, // 0,1 temple tips
                0.14f to 0.44f, 0.44f to 0.44f, 0.56f to 0.44f, 0.86f to 0.44f, // 2,3,4,5 top frame
                0.18f to 0.70f, 0.40f to 0.70f, 0.60f to 0.70f, 0.82f to 0.70f, // 6,7,8,9 bottom frames
            ),
            // Left lens: 2-3-7-6-2 (all 2)
            // Right lens: 4-5-9-8-4 (all 2)
            // Bridge: 3-4 (3:3, 4:3)
            // Temples: 0-2, 1-5 -> 0:1, 2:3, 1:1, 5:3 (too many odd).
            // Connect temples to bridge/lenses cleanly: 0-2, 0-3 (0:2, 2:3, 3:4!) + 1-5, 1-4 (1:2, 5:3, 4:4!) -> ONLY 2 AND 5 ODD!
            links = listOf(
                2 to 3, 3 to 7, 7 to 6, 6 to 2,
                4 to 5, 5 to 9, 9 to 8, 8 to 4,
                3 to 4,
                0 to 2, 0 to 3, 1 to 5, 1 to 4,
            ),
        ),
        // 87: Wrapped Gift (9V, 14E - 2 odd at 2, 6)
        buildLevel(
            87, "Mystery Gift", DifficultyTier.WONDER, 50,
            pts = listOf(
                0.26f to 0.16f, 0.74f to 0.16f,
                0.50f to 0.30f,
                0.20f to 0.36f, 0.80f to 0.36f,
                0.20f to 0.84f, 0.50f to 0.84f, 0.80f to 0.84f,
                0.50f to 0.60f,
            ),
            links = listOf(
                3 to 5, 5 to 6, 6 to 7, 7 to 4, 4 to 2, 2 to 3,
                0 to 2, 0 to 3, 1 to 2, 1 to 4,
                2 to 8, 8 to 6, 3 to 8, 4 to 8,
            ),
        ),
        // 88: Glowing Candle (8V, 12E - all even!)
        buildLevel(
            88, "Beacon Flame", DifficultyTier.WONDER, 50,
            pts = listOf(
                0.50f to 0.14f, // 0 flame tip
                0.40f to 0.30f, 0.60f to 0.30f, // 1,2 flame sides
                0.50f to 0.42f, // 3 wick
                0.34f to 0.46f, 0.66f to 0.46f, // 4,5 wax top
                0.34f to 0.84f, 0.66f to 0.84f, // 6,7 wax base
            ),
            // Flame: 0-1, 1-3, 3-2, 2-0 (deg 2)
            // Wax: 3-4, 4-6, 6-7, 7-5, 5-3 -> 3:4, 4:2, 5:2, 6:2, 7:2
            // Diagonals on wax: 4-7, 5-6, 4-5 -> 4:4, 5:4, 6:3, 7:3 -> 2 ODD (6, 7)!
            links = listOf(
                0 to 1, 1 to 3, 3 to 2, 2 to 0,
                3 to 4, 4 to 6, 6 to 7, 7 to 5, 5 to 3,
                4 to 7, 5 to 6, 4 to 5,
            ),
        ),
        // 89: Open Book (8V, 12E - 2 odd)
        buildLevel(
            89, "Tome of Mind", DifficultyTier.WONDER, 50,
            pts = listOf(
                0.14f to 0.24f, 0.50f to 0.34f, 0.86f to 0.24f, // 0,1,2 top pages
                0.14f to 0.74f, 0.50f to 0.84f, 0.86f to 0.74f, // 3,4,5 bottom pages
                0.32f to 0.52f, 0.68f to 0.52f, // 6,7 inner page creases
            ),
            // Left page: 0-1, 1-4, 4-3, 3-0 + 0-6, 6-4, 1-6, 6-3 (0:3, 1:3, 3:3, 4:3, 6:4)
            // Right page: 1-2, 2-5, 5-4 + 1-7, 7-5, 2-7, 7-4 (1:5, 2:3, 5:3, 4:5, 7:4)
            // Wait: 0:3, 1:5, 2:3, 3:3, 4:5, 5:3 -> too many odds.
            // Keep clean:
            // Left page: 0-1, 1-4, 4-3, 3-0 + 0-6, 6-4 -> 0:3, 4:3 (1:2, 3:2, 6:2)
            // Right page: 1-2, 2-5, 5-4 + 2-7, 7-4 -> 1:3, 2:3, 4:5, 5:2, 7:2.
            // Connect 0-6, 6-1, 1-7, 7-2 (0:3, 1:5, 2:3, 6:2, 7:2).
            // Simply: 0-1-2-5-4-3-0 (deg 2) + spine 1-4 (1:3, 4:3) + 0-6-3 (0:3,3:3,6:2)? No, 1-6-4 (1:4,4:4,6:2) + 1-7-4 (1:5,4:5,7:2)?
            //Wait: 1-6, 6-4, 1-7, 7-4 -> adds +2 to 1 and +2 to 4! Leaves 1(5), 4(5) -> ONLY 2 ODD!
            // Add 0-6, 3-6, 2-7, 5-7 -> 0:3,3:3,2:3,5:3. No, keep 0-6, 3-6 -> 0:3, 3:3, 6:4 ->
            // Instead: 0-1-2-5-4-3-0 (all 2) + 1-4 (1:3,4:3) + 0-6, 6-4, 1-6, 6-3 (0:3, 3:3, 1:4, 4:4, 6:4) -> add 0-3 already!
            // If we add 1-6, 6-3 (1:4, 3:3, 6:2) + 1-7, 7-5 (1:5, 5:3, 7:2) -> 4 odd (3,4,1,5).
            // Best: 0-1-2-5-4-3-0 + 1-4 + 1-6, 6-4 + 1-7, 7-4 -> 1:5, 4:5, all others 2 -> ONLY 1 AND 4 ODD!
            links = listOf(
                0 to 1, 1 to 2, 2 to 5, 5 to 4, 4 to 3, 3 to 0,
                1 to 4, 1 to 6, 6 to 4, 1 to 7, 7 to 4,
            ),
        ),
        // 90: Sky Lantern (9V, 14E - all even!)
        buildLevel(
            90, "Sky Lantern", DifficultyTier.WONDER, 52,
            pts = listOf(
                0.50f to 0.12f, // 0 hook
                0.34f to 0.28f, 0.66f to 0.28f, // 1,2 top dome
                0.24f to 0.50f, 0.76f to 0.50f, // 3,4 mid lantern
                0.34f to 0.72f, 0.66f to 0.72f, // 5,6 bottom dome
                0.50f to 0.88f, // 7 tassel tip
                0.50f to 0.50f, // 8 core light
            ),
            // Outer: 0-1, 1-3, 3-5, 5-7, 7-6, 6-4, 4-2, 2-0 (deg 2)
            // Inner lattice: 1-2, 5-6 (1:3, 2:3, 5:3, 6:3) + diamond 1-8, 8-2, 5-8, 8-6 -> 1:4, 2:4, 5:4, 6:4, 8:4 -> ALL EVEN!
            links = listOf(
                0 to 1, 1 to 3, 3 to 5, 5 to 7, 7 to 6, 6 to 4, 4 to 2, 2 to 0,
                1 to 2, 5 to 6, 1 to 8, 8 to 2, 5 to 8, 8 to 6,
            ),
        ),

        // ============================================================
        // TIER 10: SACRED (Levels 91 - 100)
        // ============================================================
        // 91: Triquetra Knot (9V, 18E - all even!)
        buildLevel(
            91, "Triquetra Knot", DifficultyTier.SACRED, 54,
            pts = ring(3, 0.40f, -90f) + ring(3, 0.28f, -30f) + ring(3, 0.085f, -90f),
            // 3 outer tips (0,1,2), 3 outer valleys (3,4,5), 3 inner core (6,7,8):
            // Outer trefoil 0-3-1-4-2-5-0 (deg 2)
            // Inner triangle 6-7-8-6 (deg 2)
            // Interlaced chords: 0-6, 0-8? No: 6 is at -90deg (under 0!), 7 is at 30deg (under 1!), 8 is at 150deg (under 2!).
            // Note: 0(-90), 3(-30), 1(30), 4(90), 2(150), 5(210).
            // Connect 6 to 3, 5 (6:4? no 6-7-8-6 + 6-3, 6-5 -> 6:4, 3:3, 5:3)
            // Connect 7 to 3, 4 (7:4, 3:4, 4:3)
            // Connect 8 to 4, 5 (8:4, 4:4, 5:4) -> ALL DEGREE 4 OR 2!
            // Plus inner spokes 0-6? No, don't make collinear. Connect 0-7, 1-8, 2-6? Let's keep clean: add 0-1-2-0 triangle? Crosses!
            // Add outer->inner V's: 0-3, 3-1, 1-4, 4-2, 2-5, 5-0 + 6-7-8-6 + 6-3, 6-5, 7-3, 7-4, 8-4, 8-5 + 3-4-5-3 ->
            // deg: 0..2: 2, 3..5: 6, 6..8: 4 -> ALL EVEN!
            links = listOf(
                0 to 3, 3 to 1, 1 to 4, 4 to 2, 2 to 5, 5 to 0,
                6 to 7, 7 to 8, 8 to 6,
                6 to 3, 6 to 5, 7 to 3, 7 to 4, 8 to 4, 8 to 5,
                3 to 4, 4 to 5, 5 to 3,
            ),
        ),
        // 92: Navigator Compass Rose (12V, 24E - all even!)
        buildLevel(
            92, "Polar Compass", DifficultyTier.SACRED, 56,
            pts = ring(4, 0.42f, -90f) + ring(4, 0.30f, -45f) + ring(4, 0.14f, -45f),
            links = listOf(
                0 to 4, 4 to 1, 1 to 5, 5 to 2, 2 to 6, 6 to 3, 3 to 7, 7 to 0,
                0 to 8, 8 to 1, 1 to 9, 9 to 2, 2 to 10, 10 to 3, 3 to 11, 11 to 0,
                4 to 5, 5 to 6, 6 to 7, 7 to 4,
                8 to 9, 9 to 10, 10 to 11, 11 to 8,
            ),
        ),
        // 93: Celtic Crossweave (12V, 24E - all even!)
        buildLevel(
            93, "Celtic Weave", DifficultyTier.SACRED, 58,
            pts = ring(8, 0.40f, -67.5f) + ring(4, 0.18f, -45f),
            // 8 outer octagon + 4 inner square
            // Connect 8(inner) to 0,1,2, 9 to 2,3,4... wait:
            // Outer 0..7 (deg 2). Inner 8..11 (deg 2).
            // Connect each inner i to 2i, 2i+1 -> (deg 8..11:4, deg 0..7:3).
            // Add chords 0-2, 2-4, 4-6, 6-0 (0,2,4,6 become 5) -> no!
            // Add pair edges 1-2, 3-4, 5-6, 7-0? Already in outer!
            // Add 0-1, 2-3? Already in outer!
            // Add skip-1 pairs: 0-2, 4-6 -> 0,2,4,6:4; 1-3, 5-7 -> 1,3,5,7:4 -> ALL DEGREE 4!
            // Wait: does 8(0.18, -45deg) lie on 0(-67.5) to 2(-22.5)? No, 0-2 goes at r=0.40*cos(22.5)=0.37, well outside r=0.18!
            links = cycleEdges(0, 8) + cycleEdges(8, 4) + listOf(
                8 to 0, 8 to 1,
                9 to 2, 9 to 3,
                10 to 4, 10 to 5,
                11 to 6, 11 to 7,
                0 to 2, 4 to 6, 1 to 3, 5 to 7,
            ),
        ),
        // 94: Twin Interlocked Rings (12V, 20E - all even!)
        buildLevel(
            94, "Orbital Rings", DifficultyTier.SACRED, 58,
            pts = ring(6, 0.25f, -90f, cx = 0.39f, cy = 0.50f) + ring(6, 0.25f, -90f, cx = 0.61f, cy = 0.50f),
            // Left hex 0..5 (deg 2), Right hex 6..11 (deg 2)
            // Interlock bridges: 0-6, 1-11, 2-10, 3-9, 4-8, 5-7? Wait 6 bridges makes all deg 3!
            // Use 4 bridges in two 4-cycles or triangles: 0-6, 0-11? No, 1-6, 6-2, 2-1, wait:
            // Bridges: 1-6, 1-11? (1:4, 6:3, 11:3) -> + 2-10, 2-9?
            // Best: 1-6, 2-10, 6-10? (1:3, 2:3, 6:4, 10:4) ->
            // Left-Right 4-cycles: (1, 6, 9, 2)? Let's check positions:
            // Left hex: 0(top), 1(TR), 2(BR), 3(bot), 4(BL), 5(TL)
            // Right hex: 6(top), 7(TR), 8(BR), 9(bot), 10(BL), 11(TL)
            // Overlap intersections: 1(0.60,0.37) is near 6(0.61,0.25) and 11(0.39,0.37)!
            // Connect 0-11, 11-1, 1-6 (0:3, 11:4, 1:4, 6:3) + 3-10, 10-2, 2-9 (3:3, 10:4, 2:4, 9:3)
            // + 0-6, 3-9 -> 0:4, 6:4, 3:4, 9:4 -> ALL EVEN!
            links = cycleEdges(0, 6) + cycleEdges(6, 6) + listOf(
                0 to 11, 11 to 1, 1 to 6, 0 to 6,
                3 to 10, 10 to 2, 2 to 9, 3 to 9,
            ),
        ),
        // 95: Eight-Point Octagram (8V, 16E - all even!)
        buildLevel(
            95, "Octagram Star", DifficultyTier.SACRED, 60,
            pts = ring(8, 0.40f, -90f),
            // Outer octagon (deg 2) + skip-3 star {8/3}: 0-3-6-1-4-7-2-5-0 (deg +2 -> 4) -> ALL DEGREE 4!
            links = cycleEdges(0, 8) + listOf(
                0 to 3, 3 to 6, 6 to 1, 1 to 4, 4 to 7, 7 to 2, 2 to 5, 5 to 0,
            ),
        ),
        // 96: Swirl Vortex (11V, 16E - 2 odd at 1, 3)
        buildLevel(
            96, "Vortex Spiral", DifficultyTier.SACRED, 60,
            pts = (0 until 11).map { idx ->
                val deg = -90f + idx * 58f
                val rad = deg * (PI / 180.0)
                val r = 0.07f + idx * 0.036f
                (0.50f + r * cos(rad).toFloat()) to (0.50f + r * sin(rad).toFloat())
            },
            links = (0 until 10).map { it to (it + 1) } + listOf(
                0 to 2, 2 to 4, 4 to 6, 6 to 8, 8 to 10, 1 to 3,
            ),
        ),
        // 97: Isometric Hexa Cube (10V, 21E - all even!)
        buildLevel(
            97, "Hexa Cube 3D", DifficultyTier.SACRED, 62,
            pts = listOf(0.50f to 0.50f) + ring(6, 0.40f, -90f) + ring(3, 0.20f, -60f),
            links = cycleEdges(1, 6) + cycleEdges(7, 3) + listOf(
                0 to 1, 0 to 3, 0 to 5,
                0 to 2, 0 to 4, 0 to 6,
                7 to 1, 7 to 2,
                8 to 3, 8 to 4,
                9 to 5, 9 to 6,
            ),
        ),
        // 98: Origami Pinwheel (13V, 24E - all even!)
        buildLevel(
            98, "Solar Pinwheel", DifficultyTier.SACRED, 64,
            pts = listOf(0.50f to 0.50f) + ring(4, 0.41f, -90f) + ring(4, 0.34f, -45f) + ring(4, 0.18f, -67.5f),
            // 4 blades: (0, 9, 1, 5), (0, 10, 2, 6), (0, 11, 3, 7), (0, 12, 4, 8)
            // Outer diamond 1-2-3-4-1 (1..4 +2) + inner square 9-10-11-12-9 (9..12 +2)
            // Blade edges:
            // 0-9, 9-1, 1-5, 5-0
            // 0-10, 10-2, 2-6, 6-0
            // 0-11, 11-3, 3-7, 7-0
            // 0-12, 12-4, 4-8, 8-0 -> deg(0)=8, deg(1..4)=4, deg(5..8)=2, deg(9..12)=4 -> ALL EVEN!
            links = cycleEdges(1, 4) + cycleEdges(9, 4) + listOf(
                0 to 9, 9 to 1, 1 to 5, 5 to 0,
                0 to 10, 10 to 2, 2 to 6, 6 to 0,
                0 to 11, 11 to 3, 3 to 7, 7 to 0,
                0 to 12, 12 to 4, 4 to 8, 8 to 0,
            ),
        ),
        // 99: Sacred Maze Gate (12V, 29E - 2 odd at 0, 8)
        buildLevel(
            99, "Labyrinth Gate", DifficultyTier.SACRED, 68,
            pts = ring(4, 0.42f, -45f) + ring(4, 0.255f, 0f) + ring(4, 0.135f, -45f),
            // Outer 0..3 (deg 2)
            // Mid 4..7 (deg 2)
            // Inner 8..11 (deg 2)
            // Bridges outer<->mid: 0-4, 4-1, 1-5, 5-2, 2-6, 6-3, 3-7, 7-0 (all +2 -> outer 4, mid 4!)
            // Bridges mid<->inner: 4-8, 4-9, 5-9, 5-10, 6-10, 6-11, 7-11, 7-8 (all +2 -> mid 6, inner 4!)
            // Wait: outer 0(-45), 1(45); mid 4(0), 5(90); inner 8(-45), 9(45):
            // 4(0) is right between 8(-45) and 9(45) -> clean!
            // Add 1 inner diagonal 8-10? Crosses center. Add 8-9? Already edge!
            // Add 0-8? No, 8(-45) is along center->0(-45), doesn't cross mid 4..7!
            // Add 0-8 -> 0:5, 8:5 (2 ODD: 0 and 8!)
            links = cycleEdges(0, 4) + cycleEdges(4, 4) + cycleEdges(8, 4) + listOf(
                0 to 4, 4 to 1, 1 to 5, 5 to 2, 2 to 6, 6 to 3, 3 to 7, 7 to 0,
                4 to 8, 4 to 9, 5 to 9, 5 to 10, 6 to 10, 6 to 11, 7 to 11, 7 to 8,
                0 to 8,
            ),
        ),
        // 100: Grand Lotus Mandala (17V, 32E - all even!)
        buildLevel(
            100, "Grand Mandala", DifficultyTier.SACRED, 75,
            pts = listOf(0.50f to 0.50f) + ring(8, 0.42f, -90f) + ring(8, 0.24f, -67.5f),
            // Outer 1..8 (deg 2)
            // Inner 9..16 (deg 2)
            // Outer<->Inner zigzag: 1-9-2-10-3-11-4-12-5-13-6-14-7-15-8-16-1 (all +2 -> deg 4!)
            // Center 0 connects to 9, 10, 11, 12, 13, 14, 15, 16 (deg 0=8, inner 9..16=5)
            // Pair inner nodes into 4 chords (9-10, 11-12, 13-14, 15-16)? Those are already edges!
            // Omit (9-10), (11-12), (13-14), (15-16) from inner cycle ->
            // Then inner cycle has ONLY 10-11, 12-13, 14-15, 16-9 (9..16 deg 3) + 0-9..16 (9..16 deg 4) -> ALL DEGREE 4 OR 8 -> ALL EVEN!
            // Plus add outer skip-2 squares 1-3-5-7-1 and 2-4-6-8-2? (Wait: does 1-3 touch 9? 1(-90) to 3(0): r_mid = 0.42*cos(45)=0.297, well outside inner r=0.24! Completely clean!)
            links = cycleEdges(1, 8) + listOf(
                10 to 11, 12 to 13, 14 to 15, 16 to 9,
            ) + (0 until 8).flatMap { i ->
                listOf((1 + i) to (9 + i), (9 + i) to (1 + (i + 1) % 8), 0 to (9 + i))
            } + listOf(
                1 to 3, 3 to 5, 5 to 7, 7 to 1,
                2 to 4, 4 to 6, 6 to 8, 8 to 2,
            ),
        ),
    )
}
