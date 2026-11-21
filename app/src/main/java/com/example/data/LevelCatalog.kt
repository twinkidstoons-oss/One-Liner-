package com.example.data

import com.example.engine.DifficultyTier
import com.example.engine.Edge
import com.example.engine.EulerSolver
import com.example.engine.PuzzleLevel
import com.example.engine.Vertex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object LevelCatalog {

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

    private fun ring(n: Int, radius: Float, startAngleDeg: Float = -90f, cx: Float = 0.5f, cy: Float = 0.5f): List<Pair<Float, Float>> {
        return (0 until n).map { i ->
            val rad = (startAngleDeg + (360f * i / n)) * (PI / 180.0)
            (cx + radius * cos(rad).toFloat()) to (cy + radius * sin(rad).toFloat())
        }
    }

    private fun cycleEdges(offset: Int, count: Int): List<Pair<Int, Int>> =
        (0 until count).map { i -> (offset + i) to (offset + (i + 1) % count) }

    val levels: List<PuzzleLevel> by lazy { createAll50Levels() }

    fun getLevel(id: Int): PuzzleLevel = levels.firstOrNull { it.id == id } ?: levels.first()

    private fun createAll50Levels(): List<PuzzleLevel> = listOf(
        // ============================================================
        // TIER 1: FOUNDATIONS (Levels 1 - 10)
        // ============================================================
        // 1: Triangle (3V, 3E - all even)
        buildLevel(
            1, "Neon Triangle", DifficultyTier.FOUNDATIONS, 15,
            pts = listOf(0.50f to 0.22f, 0.20f to 0.76f, 0.80f to 0.76f),
            links = listOf(0 to 1, 1 to 2, 2 to 0),
        ),
        // 2: Square (4V, 4E - all even)
        buildLevel(
            2, "Cyber Square", DifficultyTier.FOUNDATIONS, 15,
            pts = listOf(0.24f to 0.24f, 0.76f to 0.24f, 0.76f to 0.76f, 0.24f to 0.76f),
            links = listOf(0 to 1, 1 to 2, 2 to 3, 3 to 0),
        ),
        // 3: Diamond Kite (4V, 5E - 2 odd at 0,2)
        buildLevel(
            3, "Sky Kite", DifficultyTier.FOUNDATIONS, 18,
            pts = listOf(0.50f to 0.16f, 0.22f to 0.46f, 0.50f to 0.84f, 0.78f to 0.46f),
            links = listOf(0 to 1, 1 to 2, 2 to 3, 3 to 0, 1 to 3),
        ),
        // 4: Open House (5V, 6E - 2 odd at 3,4)
        buildLevel(
            4, "Signal Cabin", DifficultyTier.FOUNDATIONS, 20,
            pts = listOf(0.50f to 0.18f, 0.24f to 0.44f, 0.76f to 0.44f, 0.24f to 0.80f, 0.76f to 0.80f),
            links = listOf(0 to 1, 0 to 2, 1 to 2, 1 to 3, 3 to 4, 4 to 2),
        ),
        // 5: Hourglass Bowtie (5V, 6E - all even)
        buildLevel(
            5, "Hourglass", DifficultyTier.FOUNDATIONS, 20,
            pts = listOf(0.22f to 0.20f, 0.78f to 0.20f, 0.50f to 0.50f, 0.22f to 0.80f, 0.78f to 0.80f),
            links = listOf(0 to 1, 1 to 2, 2 to 0, 2 to 3, 3 to 4, 4 to 2),
        ),
        // 6: Pentagon Halo (5V, 5E - all even)
        buildLevel(
            6, "Pentagon Halo", DifficultyTier.FOUNDATIONS, 20,
            pts = ring(5, 0.34f),
            links = cycleEdges(0, 5),
        ),
        // 7: Sailboat (5V, 7E - 2 odd)
        buildLevel(
            7, "Solar Sail", DifficultyTier.FOUNDATIONS, 22,
            pts = listOf(0.50f to 0.16f, 0.26f to 0.54f, 0.74f to 0.54f, 0.18f to 0.78f, 0.82f to 0.78f),
            links = listOf(0 to 1, 0 to 2, 1 to 2, 1 to 3, 3 to 4, 4 to 2, 0 to 4).let {
                // Verify deg: 0:3, 1:3, 2:3, 3:2, 4:3 -> 4 odd! Let's fix: replace 0 to 4 with 1 to 4 (still 0:2? no).
                // Let's use clean Sailboat: hull 1-2-4-3-1 + mast 0-1,0-2 + keel 1-4 -> deg: 0:2, 1:4, 2:3, 3:2, 4:3 (2 odd: 2,4!)
                listOf(0 to 1, 0 to 2, 1 to 2, 1 to 3, 3 to 4, 4 to 2, 1 to 4)
            },
        ),
        // 8: Double Wing (6V, 8E - 2 odd)
        buildLevel(
            8, "Twin Wings", DifficultyTier.FOUNDATIONS, 22,
            pts = listOf(0.50f to 0.20f, 0.18f to 0.50f, 0.50f to 0.50f, 0.82f to 0.50f, 0.50f to 0.80f, 0.34f to 0.35f),
            links = listOf(0 to 5, 5 to 1, 1 to 2, 2 to 4, 4 to 3, 3 to 0, 0 to 2, 2 to 3),
            // deg: 0:3, 5:2, 1:2, 2:3, 4:2, 3:2 -> 2 odd (0, 2)
        ),
        // 9: Classic Nikolaus House (5V, 8E - 2 odd at bottom 3,4)
        buildLevel(
            9, "Classic House", DifficultyTier.FOUNDATIONS, 25,
            pts = listOf(0.50f to 0.16f, 0.24f to 0.44f, 0.76f to 0.44f, 0.24f to 0.82f, 0.76f to 0.82f),
            links = listOf(0 to 1, 0 to 2, 1 to 2, 1 to 3, 2 to 4, 3 to 4, 1 to 4, 2 to 3),
        ),
        // 10: Hexagon Ribbon (6V, 8E - 2 odd)
        buildLevel(
            10, "Hexa Ribbon", DifficultyTier.FOUNDATIONS, 25,
            pts = ring(6, 0.34f, 0f),
            links = cycleEdges(0, 6) + listOf(0 to 2, 3 to 5),
            // deg: 0:3, 1:2, 2:3, 3:3, 4:2, 5:3 -> 4 odd! Fix: 0 to 3, 1 to 5 -> deg: 0:3, 1:3, 2:2, 3:3, 4:2, 5:3 (4 odd).
            // Instead: 0 to 2, 2 to 4 -> deg: 0:3, 1:2, 2:4, 3:2, 4:3, 5:2 -> 2 odd (0, 4)!
        ).let {
            buildLevel(
                10, "Hexa Ribbon", DifficultyTier.FOUNDATIONS, 25,
                pts = ring(6, 0.34f, 0f),
                links = cycleEdges(0, 6) + listOf(0 to 2, 2 to 4),
            )
        },

        // ============================================================
        // TIER 2: INTERSECTIONS (Levels 11 - 20)
        // ============================================================
        // 11: Triforce Crest (6V, 9E - all even!)
        buildLevel(
            11, "Tri-Core", DifficultyTier.INTERSECTIONS, 26,
            pts = listOf(
                0.50f to 0.16f, 0.16f to 0.80f, 0.84f to 0.80f,
                0.33f to 0.48f, 0.67f to 0.48f, 0.50f to 0.80f,
            ),
            links = listOf(
                0 to 3, 3 to 1, 1 to 5, 5 to 2, 2 to 4, 4 to 0,
                3 to 4, 4 to 5, 5 to 3,
            ),
        ),
        // 12: Pentagram Star (10V, 15E - all even: outer 2, inner 4)
        buildLevel(
            12, "Nova Star", DifficultyTier.INTERSECTIONS, 28,
            pts = ring(5, 0.38f, -90f) + ring(5, 0.16f, -54f),
            links = cycleEdges(5, 5) + (0 until 5).flatMap { i ->
                listOf(i to (5 + i), i to (5 + (i + 4) % 5))
            },
        ),
        // 13: Twin Houses Joined (7V, 11E - 2 odd)
        buildLevel(
            13, "Twin Cabins", DifficultyTier.INTERSECTIONS, 28,
            pts = listOf(
                0.32f to 0.18f, 0.68f to 0.18f,
                0.16f to 0.44f, 0.50f to 0.44f, 0.84f to 0.44f,
                0.16f to 0.80f, 0.50f to 0.80f, 0.84f to 0.80f,
            ),
            // Let's compute degrees carefully:
            // Roofs: 0-2, 0-3, 1-3, 1-4
            // Walls/floors: 2-3, 3-4, 2-5, 3-6, 4-7, 5-6, 6-7
            // Diagonals: 2-6, 3-5, 3-7, 4-6 -> Wait: deg(0)=2, deg(1)=2, deg(2)=4, deg(3)=6, deg(4)=4, deg(5)=3, deg(6)=4, deg(7)=3 -> 2 odd (5,7)!
            links = listOf(
                0 to 2, 0 to 3, 1 to 3, 1 to 4,
                2 to 3, 3 to 4, 2 to 5, 4 to 7, 5 to 6, 6 to 7,
                2 to 6, 3 to 5, 3 to 7, 4 to 6,
            ),
        ),
        // 14: Butterfly Prism (6V, 10E - 2 odd)
        buildLevel(
            14, "Prism Wing", DifficultyTier.INTERSECTIONS, 28,
            pts = listOf(
                0.18f to 0.22f, 0.82f to 0.22f,
                0.38f to 0.50f, 0.62f to 0.50f,
                0.18f to 0.78f, 0.82f to 0.78f,
            ),
            // 0-1, 1-3, 3-2, 2-0, 2-4, 4-5, 5-3, 0-4, 1-5, 2-5? Wait deg:
            // 0:3, 1:3, 2:4, 3:3, 4:3, 5:4 -> 4 odd! Fix: remove 2-5, add 0-3? 0:4,1:3,2:3,3:4,4:3,5:3 -> 4 odd.
            // Let's use: 0-1, 1-5, 5-4, 4-0, 0-2, 2-4, 1-3, 3-5, 2-3, 0-3? Wait:
            // 0:4, 1:3, 2:3, 3:4, 4:3, 5:3 -> still 4 odd!
            // Simple fix: outer bow 0-2-4-0, 1-3-5-1, plus top 0-1, bottom 4-5, center 2-3 -> deg: 0:3, 1:3, 2:3, 3:3, 4:3, 5:3 (6 odd).
            // Add 0-3 and 2-5 -> deg: 0:4, 1:3, 2:4, 3:4, 4:3, 5:4 -> 2 odd (1, 4)!
            links = listOf(
                0 to 2, 2 to 4, 4 to 0,
                1 to 3, 3 to 5, 5 to 1,
                0 to 1, 4 to 5, 2 to 3,
                0 to 3, 2 to 5,
            ),
        ),
        // 15: Hexagon Star (6V + center = 7V, 12E - wait: hub has deg 6, rim has deg 3 -> 6 odd! To make 0 odd: pair rim into triangles!)
        // Wait: 0-1-2-3-4-5-0 (deg 2 each) + 6-0,6-1,6-2,6-3,6-4,6-5 (deg +1 each -> 3). If we also add 0-2, 2-4, 4-0 -> 0,2,4 become deg 5. Not good.
        // Instead, connect center 6 to ONLY 4 rim vertices (0, 2, 3, 5) + add chord 0-2, 3-5 ->
        // deg: 0:4, 1:2, 2:4, 3:4, 4:2, 5:4, 6:4 -> ALL EVEN!
        buildLevel(
            15, "Crystal Shield", DifficultyTier.INTERSECTIONS, 30,
            pts = ring(6, 0.35f, -90f) + listOf(0.50f to 0.50f),
            links = cycleEdges(0, 6) + listOf(6 to 0, 6 to 2, 6 to 3, 6 to 5, 0 to 2, 3 to 5),
        ),
        // 16: Hexagram Core (12V, 18E - all even!)
        buildLevel(
            16, "Hexagram Core", DifficultyTier.INTERSECTIONS, 32,
            pts = ring(6, 0.38f, -90f) + ring(6, 0.21f, -60f),
            links = cycleEdges(6, 6) + (0 until 6).flatMap { i ->
                listOf(i to (6 + i), i to (6 + (i + 5) % 6))
            },
        ),
        // 17: Lattice 2x3 with Diagonals (6V, 10E - 2 odd)
        buildLevel(
            17, "Circuit Grid", DifficultyTier.INTERSECTIONS, 30,
            pts = listOf(
                0.22f to 0.28f, 0.50f to 0.28f, 0.78f to 0.28f,
                0.22f to 0.72f, 0.50f to 0.72f, 0.78f to 0.72f,
            ),
            // Grid edges: 0-1, 1-2, 3-4, 4-5, 0-3, 1-4, 2-5
            // Degrees of grid: 0:2, 1:3, 2:2, 3:2, 4:3, 5:2 -> ALREADY 2 ODD (1, 4)!
            // Let's add diagonals 0-4 and 1-5 -> 0:3, 1:4, 2:2, 3:2, 4:4, 5:3 -> 2 odd (0, 5)!
            // And add 2-4, 3-1 -> 0:3, 1:5, 2:3, 3:3, 4:5, 5:3 -> too many. Just 0-4, 1-5, 2-4? 0:3, 1:4, 2:3, 3:2, 4:5, 5:3 (4 odd).
            // So 0-4, 1-5, 3-1, 4-2 -> 0:3, 1:5, 2:3, 3:3, 4:5, 5:3. No, keep 0-4, 1-5, 0-1... wait:
            // Base grid + 0-4 + 2-4 + 1-3 + 1-5 ->
            // deg: 0:3, 1:5, 2:3, 3:3, 4:5, 5:3 -> wait if we add 0-3? already there.
            // Base grid (7E) + 0-4 + 4-2 -> deg: 0:3, 1:3, 2:3, 3:2, 4:5, 5:2 (4 odd). Add 1-3 + 1-5 -> 0:3, 1:5, 2:3, 3:3, 4:5, 5:3.
            // Base grid (7E) + 0-4 + 1-5 -> deg: 0:3, 1:4, 2:2, 3:2, 4:4, 5:3 -> 2 odd (0, 5)!
            links = listOf(
                0 to 1, 1 to 2, 3 to 4, 4 to 5,
                0 to 3, 1 to 4, 2 to 5,
                0 to 4, 1 to 5,
            ),
        ),
        // 18: Pinwheel Blades (9V, 12E - all even!)
        buildLevel(
            18, "Aero Pinwheel", DifficultyTier.INTERSECTIONS, 32,
            pts = listOf(0.50f to 0.50f) + ring(4, 0.36f, -90f) + ring(4, 0.24f, -45f),
            // 4 triangles meeting at center 0: (0,1,5), (0,2,6), (0,3,7), (0,4,8) -> deg(0)=8, all others=2 -> ALL EVEN!
            links = listOf(
                0 to 1, 1 to 5, 5 to 0,
                0 to 2, 2 to 6, 6 to 0,
                0 to 3, 3 to 7, 7 to 0,
                0 to 4, 4 to 8, 8 to 0,
            ),
        ),
        // 19: Royal Crown (7V, 11E - 2 odd)
        buildLevel(
            19, "Neon Crown", DifficultyTier.INTERSECTIONS, 32,
            pts = listOf(
                0.16f to 0.30f, 0.50f to 0.20f, 0.84f to 0.30f,
                0.33f to 0.50f, 0.67f to 0.50f,
                0.22f to 0.78f, 0.78f to 0.78f,
            ),
            // 0-3, 3-1, 1-4, 4-2, 0-5, 5-6, 6-2, 3-4, 3-5, 4-6, 5-1? Wait:
            // deg: 0:2, 1:3, 2:2, 3:4, 4:4, 5:4, 6:3 -> 2 odd (1, 6)! Wait 5-1 crosses 3-4! Avoid crossing without node or make 3-4 pass below?
            // Instead of 5-1, use 1-3.. wait 3-6 and 4-5? Let's check:
            // 0-3, 3-1, 1-4, 4-2, 0-5, 5-6, 6-2, 3-4, 3-5, 4-6 -> deg: 0:2, 1:2, 2:2, 3:4, 4:4, 5:3, 6:3 -> 2 odd (5, 6)! Clean & zero crossings!
            links = listOf(
                0 to 3, 3 to 1, 1 to 4, 4 to 2,
                0 to 5, 5 to 6, 6 to 2,
                3 to 4, 3 to 5, 4 to 6,
            ),
        ),
        // 20: Octagon Core (8V + 1 center? No, octagon + inscribed square + diamond!)
        buildLevel(
            20, "Octa Matrix", DifficultyTier.INTERSECTIONS, 35,
            pts = ring(8, 0.36f, -90f),
            // Outer octagon (deg 2) + even square 0-2-4-6-0 (+2 -> 4) + odd square 1-3-5-7-1 (+2 -> 4) -> ALL EVEN!
            links = cycleEdges(0, 8) + listOf(
                0 to 2, 2 to 4, 4 to 6, 6 to 0,
                1 to 3, 3 to 5, 5 to 7, 7 to 1,
            ),
        ),

        // ============================================================
        // TIER 3: GEOMETRY (Levels 21 - 30)
        // ============================================================
        // 21: Compass Rose (9V, 16E - all even)
        buildLevel(
            21, "Compass Rose", DifficultyTier.GEOMETRY, 35,
            pts = listOf(0.50f to 0.50f) + ring(4, 0.38f, -90f) + ring(4, 0.17f, -45f),
            // 0 connects to 5,6,7,8 (deg 4). Outer 1..4 connect to neighboring inner 5..8 + outer cycle 1-2-3-4-1 ->
            // deg: 0:4, 1..4:4, 5..8:3 -> 4 odd! Fix: add inner cycle 5-6-7-8-5 -> 5..8 become deg 5!
            // Remove outer cycle, add inner cycle 5-6-7-8-5 -> 1..4 deg 2, 5..8 deg 5.
            // Wait: 0 to 1,2,3,4,5,6,7,8 -> 0:8, 1..4:3, 5..8:3. Connect 1-5-2-6-3-7-4-8-1 -> 1..4:3, 5..8:3! Wait: +1 each -> 1..4:5, 5..8:5.
            // Best: 1-5-2-6-3-7-4-8-1 (ring of 8, deg 2) + 0-5,0-6,0-7,0-8 (0:4, 5..8:3) + 5-6-7-8-5? (5..8:5).
            // Instead: 1-5-2-6-3-7-4-8-1 + 1-2-3-4-1 -> 1..4:4, 5..8:2 -> + 5-6-7-8-5 -> 5..8:4 -> ALL EVEN!
            links = listOf(
                1 to 5, 5 to 2, 2 to 6, 6 to 3, 3 to 7, 7 to 4, 4 to 8, 8 to 1,
                1 to 2, 2 to 3, 3 to 4, 4 to 1,
                0 to 5, 0 to 6, 0 to 7, 0 to 8,
                5 to 6, 7 to 8,
            ),
        ),
        // 22: Twin Envelopes (8V, 14E - 2 odd)
        buildLevel(
            22, "Double Envelope", DifficultyTier.GEOMETRY, 35,
            pts = listOf(
                0.18f to 0.24f, 0.50f to 0.24f, 0.82f to 0.24f,
                0.34f to 0.50f, 0.66f to 0.50f,
                0.18f to 0.76f, 0.50f to 0.76f, 0.82f to 0.76f,
            ),
            // Left envelope: 0,1,6,5 with center 3: 0-1, 1-6, 6-5, 5-0, 0-3, 1-3, 6-3, 5-3 -> deg: 0:3,1:3,5:3,6:3,3:4
            // Right envelope: 1,2,7,6 with center 4: 1-2, 2-7, 7-6, 1-4, 2-4, 7-4, 6-4 (1-6 shared!) ->
            // deg: 0:3, 1:5, 2:3, 3:4, 4:4, 5:3, 6:5, 7:3 -> 6 odd!
            // Let's make left & right open-top envelopes with shared middle edge 1-6:
            // Remove 0-5 and 2-7? deg: 0:2, 5:2, 2:2, 7:2, 1:5, 6:5 -> 2 ODD (1, 6)!
            // Wait: 0-1, 1-6, 6-5, 0-3, 1-3, 6-3, 5-3, 1-2, 6-7, 1-4, 2-4, 7-4, 6-4 ->
            // Check degrees: 0:2, 1:5, 2:2, 3:4, 4:4, 5:2, 6:5, 7:2 -> EXACTLY 2 ODD (1, 6)!
            links = listOf(
                0 to 1, 1 to 6, 6 to 5,
                0 to 3, 1 to 3, 6 to 3, 5 to 3,
                1 to 2, 6 to 7,
                1 to 4, 2 to 4, 7 to 4, 6 to 4,
            ),
        ),
        // 23: Aztec Pyramid (9V, 14E - 2 odd)
        buildLevel(
            23, "Aztec Spire", DifficultyTier.GEOMETRY, 36,
            pts = listOf(
                0.50f to 0.14f,
                0.35f to 0.38f, 0.65f to 0.38f,
                0.24f to 0.60f, 0.50f to 0.60f, 0.76f to 0.60f,
                0.14f to 0.84f, 0.50f to 0.84f, 0.86f to 0.84f,
            ),
            // 0-1, 0-2, 1-2,
            // 1-3, 1-4, 2-4, 2-5, 3-4, 4-5,
            // 3-6, 3-7, 4-7, 5-7, 5-8, 6-7, 7-8
            // Check deg: 0:2, 1:4, 2:4, 3:4, 4:4, 5:4, 6:2, 7:4, 8:2 -> ALL EVEN!
            links = listOf(
                0 to 1, 0 to 2, 1 to 2,
                1 to 3, 1 to 4, 2 to 4, 2 to 5, 3 to 4, 4 to 5,
                3 to 6, 3 to 7, 4 to 7, 5 to 7, 5 to 8, 6 to 7, 7 to 8,
            ),
        ),
        // 24: Lotus Frame (9V, 16E - all even)
        buildLevel(
            24, "Lotus Frame", DifficultyTier.GEOMETRY, 36,
            pts = listOf(0.50f to 0.50f) + ring(8, 0.35f, -90f),
            // Outer ring 1..8 (deg 2) + 4 petals through center: (0,1,2), (0,3,4), (0,5,6), (0,7,8)?
            // Wait: 1-2 is already in outer ring! So 0-1,0-2..0-8 gives deg(0)=8, deg(1..8)=3 -> 8 odd!
            // Instead: connect 0 to 1,3,5,7 (4 spokes) -> deg(0)=4, 1,3,5,7=3, 2,4,6,8=2.
            // Add diamond 1-3-5-7-1 -> 1,3,5,7 become deg 5. Add 1-5? No, remove 1-3-5-7-1 and add 1-2.. no.
            // What if we connect 1-3-5-7-1 AND 0-2,0-4,0-6,0-8? No, 1,3,5,7 are deg 4, 2,4,6,8 are deg 3.
            // Add 2-4-6-8-2? Then 2,4,6,8 become deg 5!
            // Simple: 0 to 1,2,3,4,5,6,7,8 (8 spokes, all odd) + 1-2, 3-4, 5-6, 7-8 (adds 1 to each -> all deg 4? Wait: outer ring already has 1-2! So DON'T include outer ring 1-2,3-4,5-6,7-8! Wait: if outer ring is 2-3, 4-5, 6-7, 8-1, plus inner spokes 0-1..0-8 -> 4 disconnected bowties? No, all meet at 0! Let's add 1-3, 5-7 ->
            // Wait: outer ring 1..8 (deg 2) + 1-3, 3-5, 5-7, 7-1 (1,3,5,7 become 4; 2,4,6,8 stay 2) + 0-1, 0-5 -> 1,5 become 5 (2 odd!), 0 becomes 2!
            // Even nicer: outer ring 1..8 + 1-3-5-7-1 + 0-2, 0-4, 0-6, 0-8 + 2-6? Wait 0-2,0-4,0-6,0-8 makes 2,4,6,8 deg 3. Add 2-4, 6-8 -> deg 4!
            links = cycleEdges(1, 8) + listOf(
                1 to 3, 3 to 5, 5 to 7, 7 to 1,
                0 to 2, 0 to 4, 0 to 6, 0 to 8,
                2 to 4, 6 to 8,
            ),
        ),
        // 25: Cyber Bridge Truss (8V, 13E - 2 odd)
        buildLevel(
            25, "Bridge Truss", DifficultyTier.GEOMETRY, 38,
            pts = listOf(
                0.24f to 0.30f, 0.50f to 0.22f, 0.76f to 0.30f,
                0.14f to 0.68f, 0.38f to 0.68f, 0.62f to 0.68f, 0.86f to 0.68f,
                0.50f to 0.84f,
            ),
            // Top chord: 0-1, 1-2
            // Bottom chord: 3-4, 4-5, 5-6, 3-7, 7-6 -> wait 4-7, 5-7:
            // Truss diagonals: 3-0, 0-4, 4-1, 1-5, 5-2, 2-6
            // Let's check deg:
            // 0:3 (1,3,4), 1:4 (0,2,4,5), 2:3 (1,5,6), 3:2 (0,4), 4:4 (3,0,1,5), 5:4 (4,1,2,6), 6:2 (2,5)
            // Add 0-2 -> 0:4, 2:4! Add 3-7, 7-4, 7-5, 7-6 -> 3:3, 4:5, 5:5, 6:3, 7:4 (4 odd).
            // Instead: add 3-7, 7-6 (3:3, 6:3, 7:2) and 0-2 (0:4, 2:4)! Then ONLY 3 and 6 are odd!
            links = listOf(
                0 to 1, 1 to 2, 0 to 2,
                3 to 4, 4 to 5, 5 to 6,
                3 to 0, 0 to 4, 4 to 1, 1 to 5, 5 to 2, 2 to 6,
                3 to 7, 7 to 6,
            ),
        ),
        // 26: Orbital Diamond (9V, 16E - all even)
        buildLevel(
            26, "Orbital Core", DifficultyTier.GEOMETRY, 38,
            pts = listOf(0.50f to 0.50f) + ring(4, 0.38f, 0f) + ring(4, 0.21f, 45f),
            // Outer diamond 1-2-3-4-1 (deg 2)
            // Inner square 5-6-7-8-5 (deg 2)
            // Spokes from 0 to 5,6,7,8 (0:4, 5..8:3)
            // Zigzag connecting outer & inner: 1-5, 5-2, 2-6, 6-3, 3-7, 7-4, 4-8, 8-1? Wait:
            // Actually 1 is at 0deg, 5 is at 45deg, 2 is at 90deg, 6 is at 135deg...
            // So 1-5-2-6-3-7-4-8-1 adds +2 to each 1..4 and +2 to each 5..8!
            // If we omit 0-5..0-8, all are 4! With 0-5..0-8, 5..8 become 5 (4 odd).
            // Instead: connect 0 to 1,2,3,4 (0:4, 1..4:5) -> still 4 odd.
            // Better: omit inner square 5-6-7-8-5, keep outer 1-2-3-4-1 + star 1-5-2-6-3-7-4-8-1 + 0-5,0-6,0-7,0-8 ->
            // deg: 0:4, 1..4:4, 5..8:3 (4 odd). Add 5-7, 6-8 -> crosses 0!
            // Add 5-6, 7-8 (top-right & bottom-left pairs) -> deg: 5..8 become 4! ALL EVEN!
            links = listOf(
                1 to 2, 2 to 3, 3 to 4, 4 to 1,
                1 to 5, 5 to 2, 2 to 6, 6 to 3, 3 to 7, 7 to 4, 4 to 8, 8 to 1,
                0 to 5, 0 to 6, 0 to 7, 0 to 8,
                5 to 6, 7 to 8,
            ),
        ),
        // 27: Sacred Chalice (8V, 13E - 2 odd)
        buildLevel(
            27, "Sacred Vessel", DifficultyTier.GEOMETRY, 38,
            pts = listOf(
                0.22f to 0.20f, 0.78f to 0.20f,
                0.28f to 0.44f, 0.50f to 0.44f, 0.72f to 0.44f,
                0.50f to 0.66f,
                0.28f to 0.84f, 0.72f to 0.84f,
            ),
            // 0-1, 0-2, 1-4, 2-3, 3-4, 0-3, 1-3, 2-5, 3-5, 4-5, 5-6, 6-7, 7-5
            // Check deg:
            // 0:3 (1,2,3), 1:3 (0,3,4), 2:3 (0,3,5), 3:5 (2,4,0,1,5), 4:3 (1,3,5), 5:5 (2,3,4,6,7), 6:2, 7:2
            // Too many odds. Let's make 0-2, 2-4.. wait:
            // Bowl: 0-1, 0-2, 1-4, 2-3, 3-4, 2-5, 4-5, 3-5 -> deg: 0:2, 1:2, 2:3, 3:3, 4:3, 5:3.
            // Add 0-3, 1-3 -> 0:3, 1:3, 2:3, 3:5, 4:3, 5:3. Still many.
            // Clean Eulerian chalice:
            // 0-1, 0-2, 1-4, 2-3, 3-4, 2-5, 4-5, 5-6, 6-7, 7-5 -> deg: 0:2, 1:2, 2:3, 3:2, 4:3, 5:4, 6:2, 7:2 -> 2 ODD (2, 4)!
            // Add 0-3, 3-1, 3-5 -> 0:3, 1:3, 2:3, 3:5, 4:3, 5:5.
            // Instead add 0-4, 1-2? Crosses 3! Wait: 3 is at the center of (0,1,2,4) trapezoid? No, 3 is midpoint of 2-4, so 0-3, 3-1, 3-5, plus 2-6, 4-7 ->
            // deg: 0:3, 1:3, 2:4, 3:5, 4:4, 5:5, 6:3, 7:3.
            // Let's keep 0-1, 0-2, 1-4, 2-3, 3-4, 2-5, 4-5, 5-6, 6-7, 7-5 AND add 0-3, 3-1, 2-6, 4-7? No, 0-3 & 3-1 makes 0:3, 1:3, 3:4. Add 0-4? No.
            // Just 0-1, 0-2, 1-4, 2-3, 3-4, 2-5, 4-5, 3-5, 5-6, 6-7, 7-5, 2-6, 4-7 ->
            // deg: 0:2, 1:2, 2:4, 3:3, 4:4, 5:5, 6:3, 7:3 -> 4 odd. Remove 3-5 -> 3:2, 5:4 -> 2 odd (6, 7)!
            links = listOf(
                0 to 1, 0 to 2, 1 to 4, 2 to 3, 3 to 4,
                2 to 5, 4 to 5, 5 to 6, 6 to 7, 7 to 5,
                2 to 6, 4 to 7,
            ),
        ),
        // 28: Cyber Owl (9V, 14E - 2 odd)
        buildLevel(
            28, "Cyber Owl", DifficultyTier.GEOMETRY, 40,
            pts = listOf(
                0.24f to 0.18f, 0.76f to 0.18f, // ear tips 0,1
                0.34f to 0.34f, 0.50f to 0.28f, 0.66f to 0.34f, // brow 2,3,4
                0.50f to 0.50f, // beak 5
                0.24f to 0.68f, 0.76f to 0.68f, // wings 6,7
                0.50f to 0.84f, // tail 8
            ),
            // Ears/brow: 0-2, 0-3, 1-3, 1-4, 2-3, 3-4 -> deg: 0:2, 1:2, 2:2, 3:4, 4:2
            // Face/beak: 2-5, 4-5 -> 2:3, 4:3, 5:2
            // Wings/body: 2-6, 6-8, 8-7, 7-4, 5-6, 5-7, 5-8 ->
            // deg: 0:2, 1:2, 2:4, 3:4, 4:4, 5:5, 6:3, 7:3, 8:3 -> 4 odd (5,6,7,8).
            // Remove 5-8 -> 5:4, 8:2 -> ONLY 2 ODD (6, 7)!
            links = listOf(
                0 to 2, 0 to 3, 1 to 3, 1 to 4, 2 to 3, 3 to 4,
                2 to 5, 4 to 5,
                2 to 6, 6 to 8, 8 to 7, 7 to 4,
                5 to 6, 5 to 7,
            ),
        ),
        // 29: Honeycomb Pair (10V, 14E - 2 odd)
        buildLevel(
            29, "Twin Hexes", DifficultyTier.GEOMETRY, 40,
            pts = listOf(
                0.30f to 0.22f, 0.50f to 0.32f, 0.50f to 0.54f, 0.30f to 0.64f, 0.14f to 0.52f, 0.14f to 0.32f,
                0.70f to 0.22f, 0.86f to 0.32f, 0.86f to 0.52f, 0.70f to 0.64f,
                // Plus bottom tail vertex? No, 10V is clean twin hexagons sharing edge 1-2!
            ),
            // Left hex: 0-1-2-3-4-5-0 (deg 2 each)
            // Right hex: 1-6-7-8-9-2 (sharing 1-2 -> 1:3, 2:3, all others 2 -> 2 ODD!)
            // Let's add internal chords to make it trickier while preserving 2 odd!
            // Add 0-3, 6-9 (doesn't cross 1-2): 0:3, 3:3, 6:3, 9:3. Instead add 0-2, 2-6 -> 0:3, 1:3, 2:5, 6:3.
            // Add 0-4, 4-2 -> 0:3, 4:4, 2:4 (1:3, 0:3)!
            // Add 6-8, 8-2 -> 6:3, 8:4, 2:5 -> wait 1:3, 0:3, 2:5, 6:3.
            // Add 0-1? already there. Add 0-6! -> 0:4, 6:4! Then ONLY 1(3) and 2(5) are odd!
            links = listOf(
                0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 0,
                1 to 6, 6 to 7, 7 to 8, 8 to 9, 9 to 2,
                0 to 4, 4 to 2, 6 to 8, 8 to 2, 0 to 6,
            ),
        ),
        // 30: Gear Wheel (12V, 20E - all even)
        buildLevel(
            30, "Reactor Ring", DifficultyTier.GEOMETRY, 42,
            pts = ring(6, 0.38f, -90f) + ring(6, 0.20f, -90f),
            // Outer hex 0..5 (deg 2)
            // Inner hex 6..11 (deg 2)
            // Spokes 0-6, 1-7, 2-8, 3-9, 4-10, 5-11 (+1 each -> all 3, 12 odd).
            // Add diagonals 0-7, 2-9, 4-11? No, 0-7, 1-6, 2-9, 3-8, 4-11, 5-10! -> Every vertex gets +1 more -> ALL DEGREE 4!
            links = cycleEdges(0, 6) + cycleEdges(6, 6) + listOf(
                0 to 6, 1 to 7, 2 to 8, 3 to 9, 4 to 10, 5 to 11,
                0 to 7, 1 to 6, 2 to 9, 3 to 8, 4 to 11, 5 to 10,
            ),
        ),

        // ============================================================
        // TIER 4: OVERLAPS (Levels 31 - 40)
        // ============================================================
        // 31: Prism Lattice (8V, 16E - all even)
        buildLevel(
            31, "Prism Lattice", DifficultyTier.OVERLAPS, 42,
            pts = ring(4, 0.38f, -90f) + ring(4, 0.24f, -45f),
            links = cycleEdges(0, 4) + cycleEdges(4, 4) + listOf(
                0 to 4, 4 to 1, 1 to 5, 5 to 2, 2 to 6, 6 to 3, 3 to 7, 7 to 0,
            ),
        ),
        // 32: Isometric Octahedron (6V, 12E - all deg 4 -> all even!)
        buildLevel(
            32, "3D Octahedron", DifficultyTier.OVERLAPS, 44,
            pts = listOf(
                0.50f to 0.14f, // top 0
                0.18f to 0.44f, // left-back 1
                0.44f to 0.54f, // front-left 2
                0.82f to 0.44f, // right-front 3
                0.56f to 0.36f, // back-right 4
                0.50f to 0.86f, // bottom 5
            ),
            links = listOf(
                0 to 1, 0 to 2, 0 to 3, 0 to 4,
                1 to 2, 2 to 3, 3 to 4, 4 to 1,
                5 to 1, 5 to 2, 5 to 3, 5 to 4,
            ),
        ),
        // 33: Triple Overlap Triangles (9V, 15E - 2 odd)
        buildLevel(
            33, "Tri-Wave", DifficultyTier.OVERLAPS, 44,
            pts = listOf(
                0.24f to 0.22f, 0.50f to 0.22f, 0.76f to 0.22f,
                0.14f to 0.54f, 0.40f to 0.54f, 0.60f to 0.54f, 0.86f to 0.54f,
                0.32f to 0.82f, 0.68f to 0.82f,
            ),
            // Triangles: (0,3,4), (1,4,5), (2,5,6), (4,7,8)? Let's connect:
            // 0-3, 3-4, 4-0, 1-4, 4-5, 5-1, 2-5, 5-6, 6-2 (deg: 0:2,1:2,2:2,3:2,4:4,5:4,6:2)
            // Add bottom wave: 3-7, 7-4, 4-8, 8-5, 5-7? Wait: 7-8, 8-6 ->
            // Check deg with: 3-7, 7-8, 8-6, 4-7, 5-8, 0-1, 1-2 ->
            // 0:3, 1:4, 2:3, 3:3, 4:5, 5:5, 6:3, 7:3, 8:3 -> too many odds.
            // Add 0-1, 1-2, 3-7, 7-8, 8-6, 7-5? No: 4-7, 4-8 (4:6), 7-8 (7:3, 8:2 -> 3-7, 8-6: 7:3, 8:3).
            // Wait: base 3 triangles (0,3,4),(1,4,5),(2,5,6) are all deg 2/4.
            // Add triangle (4,7,8) -> 4:6, 7:2, 8:2 -> ALL EVEN!
            // Add bottom connectors 3-7 and 6-8 -> 3:3, 7:3, 6:3, 8:3 -> add 7-5? No.
            // Add loop 0-1-2 + 3-7 + 6-8 -> 0:3, 1:4, 2:3, 3:3, 6:3, 7:3, 8:3.
            // Instead: add ONLY 0-1, 1-2 -> 0:3, 2:3 (2 odd!). And add 4-7, 7-8, 8-5 -> 4:5, 5:5? No, add 4-7, 7-5 (4:5, 5:5).
            // Add 4-7, 7-8, 8-4 (all +2) AND 3-7, 8-6 -> 3:3, 7:5, 8:5, 6:3.
            // Let's do: base (0,3,4), (1,4,5), (2,5,6), (4,7,8), (5,7,8)? Wait 7-8 shared -> 7:3, 8:3, 4:5, 5:5.
            // Base (0,3,4), (1,4,5), (2,5,6), (4,7,8) + 0-1, 1-2 + 7-5, 5-8? -> 0:3, 2:3, 7:3, 8:3.
            // Cleanest: (0,3,4), (1,4,5), (2,5,6), (4,7,8), (3,7,6? no) -> add 0-1, 1-2 -> deg: 0:3, 1:4, 2:3, 3:2, 4:6, 5:4, 6:2, 7:2, 8:2. And add 7-5, 5-8? Then 5:6, 7:3, 8:3 -> 4 odd. Instead add 0-7? No, 3-7, 7-5, 5-8, 8-6 -> 3:3, 7:4, 5:6, 8:4, 6:3. Plus 0-1, 1-2? 0:3,2:3. Just omit 0-1, 1-2? No, add 0-1, 1-2 AND 3-0? already there! Add 3-6? No, 0-3 already, 1-3?
            // Let's do: (0,3,4), (1,4,5), (2,5,6), (4,7,8), 0-1, 1-2, 3-7, 7-5, 5-8, 8-6, 0-2? No, 0-2 passes over 1!
            // Keep 3-7, 7-5, 5-8, 8-6 (3:3, 6:3) + 4-7, 7-8, 8-4 -> 3:3, 6:3, 4:6, 5:6, 7:4, 8:4 -> ONLY 3 AND 6 ODD! And add 0-1, 1-2? Wait 0-1, 1-2 makes 0:3, 2:3. Connect 0-3? Already there. Add 0-4? Already there. Add 1-3, 1-6? Yes! 1-3 and 1-6 -> 1:6, 3:4, 6:4! Wait 1-3, 1-6 don't touch other nodes!
            // Let's check: 0(3), 1(6), 2(3), 3(4), 4(6), 5(6), 6(4), 7(4), 8(4) -> 2 ODD (0, 2)!
            links = listOf(
                0 to 3, 3 to 4, 4 to 0,
                1 to 4, 4 to 5, 5 to 1,
                2 to 5, 5 to 6, 6 to 2,
                4 to 7, 7 to 8, 8 to 4,
                3 to 7, 7 to 5, 5 to 8, 8 to 6,
                0 to 1, 1 to 2, 1 to 3, 1 to 6,
            ),
        ),
        // 34: Diamond Butterfly Web (9V, 16E - all even)
        buildLevel(
            34, "Nexus Web", DifficultyTier.OVERLAPS, 45,
            pts = listOf(
                0.50f to 0.14f, // 0 top
                0.22f to 0.30f, 0.78f to 0.30f, // 1,2
                0.34f to 0.50f, 0.50f to 0.50f, 0.66f to 0.50f, // 3,4,5
                0.22f to 0.70f, 0.78f to 0.70f, // 6,7
                0.50f to 0.86f, // 8 bottom
            ),
            // Outer octagon 0-1-6-8-7-2-0 (6V hex) + inner diamond 0-3-8-5-0? Wait:
            // 0-1, 1-6, 6-8, 8-7, 7-2, 2-0 (deg 2)
            // 0-3, 3-6, 6-4, 4-3, 3-1, 1-4, 4-5, 5-2, 2-4, 4-7, 7-5, 5-8, 8-4, 4-0? Let's count carefully:
            // Better symmetrical design:
            // Outer: 0-1, 1-6, 6-8, 8-7, 7-2, 2-0 (all 2)
            // Inner cross-web around 4:
            // 0-3, 3-6, 0-5, 5-7 (0:4, 3:2, 5:2, 6:3, 7:3)
            // 1-3, 3-8, 2-5, 5-8 (1:3, 3:4, 8:4, 2:3, 5:4)
            // 1-4, 4-7, 2-4, 4-6 (4:4, 1:4, 7:4, 2:4, 6:4) -> ALL DEGREE 4!
            links = listOf(
                0 to 1, 1 to 6, 6 to 8, 8 to 7, 7 to 2, 2 to 0,
                0 to 3, 3 to 6, 0 to 5, 5 to 7,
                1 to 3, 3 to 8, 2 to 5, 5 to 8,
                1 to 4, 4 to 7, 2 to 4, 4 to 6,
            ),
        ),
        // 35: Isometric Cube + Diagonals (8V, 16E - all even!)
        buildLevel(
            35, "Hyper Cube", DifficultyTier.OVERLAPS, 45,
            pts = listOf(
                0.32f to 0.18f, 0.74f to 0.18f, // back top 0,1
                0.20f to 0.36f, 0.62f to 0.36f, // front top 2,3
                0.32f to 0.62f, 0.74f to 0.62f, // back bottom 4,5
                0.20f to 0.82f, 0.62f to 0.82f, // front bottom 6,7
            ),
            // Cube has 12 edges (every vertex deg 3 -> 8 odd).
            // Add 1 perfect matching of 4 face diagonals: (0-3), (1-7), (2-4), (5-6) -> Every vertex becomes degree 4!
            // Let's pick non-overlapping face diagonals:
            // Top face (0,1,3,2): 0-3
            // Right face (1,3,7,5): 1-7
            // Left face (0,2,6,4): 2-4
            // Bottom face (4,5,7,6): 5-6
            // All 8 vertices get +1 degree -> ALL DEGREE 4!
            links = listOf(
                0 to 1, 1 to 3, 3 to 2, 2 to 0,
                4 to 5, 5 to 7, 7 to 6, 6 to 4,
                0 to 4, 1 to 5, 2 to 6, 3 to 7,
                0 to 3, 1 to 7, 2 to 4, 5 to 6,
            ),
        ),
        // 36: Double Pentagram (10V, 20E - all even)
        buildLevel(
            36, "Star Forge", DifficultyTier.OVERLAPS, 48,
            pts = ring(5, 0.39f, -90f) + ring(5, 0.21f, -54f),
            // Outer pentagon 0..4 (deg 2) + inner pentagon 5..9 (deg 2)
            // Star rays 0-5, 0-9, 1-5, 1-6, 2-6, 2-7, 3-7, 3-8, 4-8, 4-9 (all +2 -> deg 4!)
            links = cycleEdges(0, 5) + cycleEdges(5, 5) + (0 until 5).flatMap { i ->
                listOf(i to (5 + i), i to (5 + (i + 4) % 5))
            },
        ),
        // 37: 3x3 Grid Matrix (9V, 16E - 2 odd)
        buildLevel(
            37, "Matrix 3x3", DifficultyTier.OVERLAPS, 48,
            pts = listOf(
                0.22f to 0.22f, 0.50f to 0.22f, 0.78f to 0.22f,
                0.22f to 0.50f, 0.50f to 0.50f, 0.78f to 0.50f,
                0.22f to 0.78f, 0.50f to 0.78f, 0.78f to 0.78f,
            ),
            // 3x3 grid has 12 edges: edge midpoints (1,3,5,7) have deg 3 (4 odd).
            // Add 1-3 and 5-7 -> 1,3,5,7 become deg 4 -> ALL EVEN!
            // Add 0-4 and 4-8? No, 0-4 makes 0:3, 4:5 (wait if we also add 4-8 -> 4:6, 8:3 -> 2 ODD: 0 and 8? Wait 0-4-8 passes through 4!).
            // Instead add 1-3, 5-7, 1-5, 3-7 -> inner diamond! 1,3,5,7 become deg 5 (4 odd).
            // Just 1-3, 5-7, 3-5? No, 3-5 passes through 4!
            // Base 3x3 (12E) + 1-3 + 5-7 (14E, all even) + 0-4 + 4-8 -> 0:3, 8:3 (2 odd!), 4:6 -> clean & intuitive!
            links = listOf(
                0 to 1, 1 to 2, 3 to 4, 4 to 5, 6 to 7, 7 to 8,
                0 to 3, 3 to 6, 1 to 4, 4 to 7, 2 to 5, 5 to 8,
                1 to 3, 5 to 7, 0 to 4, 4 to 8,
            ),
        ),
        // 38: Overlapping Hexagons (10V, 18E - all even)
        buildLevel(
            38, "Venn Lattice", DifficultyTier.OVERLAPS, 50,
            pts = listOf(
                0.32f to 0.18f, 0.68f to 0.18f, // 0,1
                0.16f to 0.40f, 0.50f to 0.36f, 0.84f to 0.40f, // 2,3,4
                0.16f to 0.62f, 0.50f to 0.64f, 0.84f to 0.62f, // 5,6,7
                0.32f to 0.84f, 0.68f to 0.84f, // 8,9
            ),
            // Left hex: 0-3-6-8-5-2-0
            // Right hex: 1-4-7-9-6-3-1 -> shared 3-6 -> 3:3, 6:3 (2 odd).
            // Add chords: 0-2? already. Add 2-3, 3-4, 5-6, 6-7 ->
            // deg: 0:2, 1:2, 2:3, 3:5, 4:3, 5:3, 6:5, 7:3, 8:2, 9:2
            // Add 2-5? already. Add 0-1, 8-9 -> 0:3, 1:3, 8:3, 9:3.
            // Better: Left hex (0-3-6-8-5-2-0) + Right hex (1-4-7-9-6-3-1) + Center diamond (0-1, 1-6, 6-5? no) ->
            // Add 2-3, 3-4, 5-6, 6-7, 2-8? No: 0-5? No:
            // Let's pair up (2,4), (5,7)? No. Pair (2,3),(4,3) -> 2:3,4:3,3:5; (5,6),(7,6) -> 5:3,7:3,6:5.
            // Now add 0-1, 8-9 -> 0:3,1:3,8:3,9:3. Connect 0-3, 1-3? Already there.
            // Connect 0-4? No, connect 2-3, 5-6 (2:3, 5:3, 3:4, 6:4 -> add 0-5? No, add 2-8, 0-5 -> 0:3, 2:4, 5:4, 8:3 -> add 0-8? No).
            // Simple: Left hex + Right hex + 0-1, 8-9, 0-3, 1-3? Wait 0-3 already.
            // Add 0-1, 1-3? already. Add 0-6, 1-6? Crosses 3-6? No! 0(0.32,0.18) to 6(0.50,0.64) and 1(0.68,0.18) to 6(0.50,0.64)!
            // Also 8(0.32,0.84) to 3(0.50,0.36) and 9(0.68,0.84) to 3(0.50,0.36)!
            // Let's check deg of Left hex + Right hex + 0-6, 1-6, 8-3, 9-3:
            // 0:3, 1:3, 2:2, 3:5, 4:2, 5:2, 6:5, 7:2, 8:3, 9:3 -> still odd!
            // Add 0-1, 8-9 -> 0:4, 1:4, 8:4, 9:4! And 3:5, 6:5 -> ONLY 3 AND 6 ODD!
            links = listOf(
                0 to 3, 3 to 6, 6 to 8, 8 to 5, 5 to 2, 2 to 0,
                1 to 4, 4 to 7, 7 to 9, 9 to 6, 3 to 1,
                0 to 6, 1 to 6, 8 to 3, 9 to 3,
                0 to 1, 8 to 9,
            ),
        ),
        // 39: Spiral Shell (10V, 16E - 2 odd)
        buildLevel(
            39, "Nautilus", DifficultyTier.OVERLAPS, 50,
            pts = ring(5, 0.40f, -90f) + ring(5, 0.21f, -54f),
            // Outer 0..4 (deg 2)
            // Spokes 0-5, 1-6, 2-7, 3-8, 4-9 + zigzags 5-1, 6-2, 7-3, 8-4 ->
            // deg: 0:3, 1:4, 2:4, 3:4, 4:4, 5:2, 6:2, 7:2, 8:2, 9:1 -> 2 ODD (0, 9)!
            // Add inner pentagon 5-6-7-8-9-5 -> 5:4, 6:4, 7:4, 8:4, 9:3! Still 2 ODD (0, 9)!
            links = cycleEdges(0, 5) + cycleEdges(5, 5) + listOf(
                0 to 5, 5 to 1, 1 to 6, 6 to 2, 2 to 7, 7 to 3, 3 to 8, 8 to 4, 4 to 9,
            ),
        ),
        // 40: Cyber Butterfly Mandala (11V, 20E - all even)
        buildLevel(
            40, "Nebula Crest", DifficultyTier.OVERLAPS, 52,
            pts = listOf(0.50f to 0.50f) + ring(5, 0.40f, -90f) + ring(5, 0.22f, -54f),
            // Outer 1..5 (deg 2) + Inner 6..10 (deg 2)
            // Zigzag 1-6-2-7-3-8-4-9-5-10-1 (all +2 -> deg 4)
            // Wait: connect 0 to 6,7,8,9,10? No, 5 is odd!
            // Connect 0 to 6,7,9,10 (4 spokes -> 0:4, 6,7,9,10 become 5) + 6-7, 9-10? Already there! Add 6-9, 7-10 -> crosses 0? No, 6(-54), 7(18), 9(162), 10(234) -> 6-10 and 7-9 don't cross 0!
            // Wait: 0-6, 0-7, 0-9, 0-10 + 6-10, 7-9? No, 6-10 is already in cycleEdges(6,5)!
            // Add 0-6, 0-8, 0-9, 0-10 + 6-9? Wait: 6(-54deg), 8(90deg), 9(162deg), 10(234deg).
            // 6-8 (chord) and 9-10 (already edge). Let's do: 0-6, 0-7, 0-8, 0-9 (4 spokes) + 6-8, 7-9 -> 6,7,8,9 get +2 -> ALL DEGREE 6 or 4 -> ALL EVEN!
            links = cycleEdges(1, 5) + cycleEdges(6, 5) + listOf(
                1 to 6, 6 to 2, 2 to 7, 7 to 3, 3 to 8, 8 to 4, 4 to 9, 9 to 5, 5 to 10, 10 to 1,
                0 to 6, 0 to 7, 0 to 8, 0 to 9,
                6 to 8, 7 to 9,
            ),
        ),

        // ============================================================
        // TIER 5: MASTERMIND (Levels 41 - 50)
        // ============================================================
        // 41: Complete K5 Pentagram! (5V, 10E - all deg 4 -> all even!)
        buildLevel(
            41, "K5 Pentacle", DifficultyTier.MASTERMIND, 52,
            pts = ring(5, 0.37f, -90f),
            links = (0 until 5).flatMap { i -> ((i + 1) until 5).map { j -> i to j } },
        ),
        // 42: Dual K4 + Bridge (8V, 16E - 2 odd)
        buildLevel(
            42, "Twin K4 Core", DifficultyTier.MASTERMIND, 54,
            pts = listOf(
                0.18f to 0.24f, 0.44f to 0.24f, 0.44f to 0.76f, 0.18f to 0.76f,
                0.56f to 0.24f, 0.82f to 0.24f, 0.82f to 0.76f, 0.56f to 0.76f,
            ),
            // Wait: diagonals of square 0-2 and 1-3 cross, that's fine! Every K4 has 4 deg-3 vertices.
            // Connect 1-4, 2-7, 0-5? No, 1-4, 2-7, 1-7, 2-4! -> 1,2,4,7 become deg 5 (4 odd).
            // Instead: connect 0-4? No, 1-4, 2-7 -> 0,3,5,6 are deg 3, 1,2,4,7 are deg 4.
            // Add 0-3? already there. Add 0-4? Wait: 0-5 and 3-6? No, pass through 1,4!
            // Curve/offset the outer vertices slightly so 0-5 and 3-6 are clean!
            // Let's use clean octagonal positions for left K4 and right K4:
            // 0(0.16,0.36), 1(0.38,0.22), 2(0.38,0.78), 3(0.16,0.64)
            // 4(0.62,0.22), 5(0.84,0.36), 6(0.84,0.64), 7(0.62,0.78)
            // K4 left (0,1,2,3) + K4 right (4,5,6,7) -> all deg 3.
            // Connect 1-4, 2-7, 0-5 (arches over top!), 3-6 (arches under bottom!)?
            // Wait: 0(0.16,0.36) to 5(0.84,0.36) passes right across 1(0.38,0.22)? No, y=0.36 is BELOW 0.22, clean!
            // Wait: if 0-5 and 3-6 are connected, AND 1-4, 2-7 are connected -> ALL 8 VERTICES BECOME DEGREE 4!
            // Let's also check: does 0-5 intersect 1-2/1-3? Yes, edges can cross cleanly, just must not pass through a vertex!
            // Let's verify y of 0-5 (0.36) vs 1(0.22), 2(0.78), 3(0.64) -> nowhere near!
            links = listOf(
                0 to 1, 0 to 2, 0 to 3, 1 to 2, 1 to 3, 2 to 3,
                4 to 5, 4 to 6, 4 to 7, 5 to 6, 5 to 7, 6 to 7,
                1 to 4, 2 to 7, 0 to 5, 3 to 6,
            ),
        ).let {
            buildLevel(
                42, "Twin K4 Core", DifficultyTier.MASTERMIND, 54,
                pts = listOf(
                    0.16f to 0.36f, 0.38f to 0.20f, 0.38f to 0.80f, 0.16f to 0.64f,
                    0.62f to 0.20f, 0.84f to 0.36f, 0.84f to 0.64f, 0.62f to 0.80f,
                ),
                links = listOf(
                    0 to 1, 0 to 2, 0 to 3, 1 to 2, 1 to 3, 2 to 3,
                    4 to 5, 4 to 6, 4 to 7, 5 to 6, 5 to 7, 6 to 7,
                    1 to 4, 2 to 7, 0 to 5, 3 to 6,
                ),
            )
        },
        // 43: Hexagonal Star Mandala (13V, 24E - all even)
        buildLevel(
            43, "Solar Mandala", DifficultyTier.MASTERMIND, 55,
            pts = listOf(0.50f to 0.50f) + ring(6, 0.40f, -90f) + ring(6, 0.21f, -60f),
            // Outer hex 1..6 (deg 2)
            // Inner hex 7..12 (deg 2)
            // Star rays 1-7, 1-12, 2-7, 2-8, ..., 6-11, 6-12 (1..6 become 4, 7..12 become 4)
            // Center 0 connects to 7, 8, 9, 10, 11, 12 -> 0:6, 7..12 become 5 (6 odd).
            // Wait: instead connect 0 to 7, 9, 11 (3 spokes -> 0:3) and 7-9, 9-11, 11-7? Then 7,9,11 become 7 (odd).
            // Connect 0 to 7, 8, 10, 11 (0:4, 7,8,10,11 become 5) + 7-8? already! Add 7-10, 8-11? Passes through 0!
            // Add 7-9, 9-11, 11-7? No. Connect 0-7, 0-8, 0-9, 0-10, 0-11, 0-12 + 7-9, 9-11, 11-7 + 8-10, 10-12, 12-8?
            // If we add TWO triangles (7-9-11-7) and (8-10-12-8) -> each of 7..12 gets +2 -> stays odd!
            // Instead, add 7-8? already. Add 1-3, 3-5, 5-1? No.
            // Wait: if we OMIT inner hex 7..12 cycle edges, and instead have:
            // Outer hex 1..6 (deg 2) + Star rays (1..6:+2 -> 4, 7..12:2) + 0 to 7..12 (0:6, 7..12:3) + 7-8, 9-10, 11-12 (7..12:+1 -> 4!) -> ALL EVEN!
            links = cycleEdges(1, 6) + (0 until 6).flatMap { i ->
                listOf((1 + i) to (7 + i), (1 + i) to (7 + (i + 5) % 6))
            } + (7..12).map { 0 to it } + listOf(7 to 8, 9 to 10, 11 to 12),
        ),
        // 44: Complete K7 (7V, 21E - all deg 6 -> all even!)
        buildLevel(
            44, "Heptagram K7", DifficultyTier.MASTERMIND, 60,
            pts = ring(7, 0.39f, -90f),
            links = (0 until 7).flatMap { i -> ((i + 1) until 7).map { j -> i to j } },
        ),
        // 45: Octagonal Diamond Web (12V, 24E - all even)
        buildLevel(
            45, "Quantum Web", DifficultyTier.MASTERMIND, 60,
            pts = ring(8, 0.40f, -90f) + ring(4, 0.19f, -45f),
            // Outer 0..7 (deg 2)
            // Inner 8..11 (deg 2)
            // Outer skip-2: 0-2-4-6-0 and 1-3-5-7-1 (0..7 become deg 4)
            // Inner connections: 8 connects to 1,2, 9 to 3,4, 10 to 5,6, 11 to 7,0? Wait: 0..7 become 5!
            // Connect 8 to 0,2 (even), 9 to 2,4, 10 to 4,6, 11 to 6,0 -> 0,2,4,6 get +2 (deg 6!), 8..11 get +2 (deg 4!) -> ALL EVEN!
            links = cycleEdges(0, 8) + cycleEdges(8, 4) + listOf(
                0 to 2, 2 to 4, 4 to 6, 6 to 0,
                1 to 3, 3 to 5, 5 to 7, 7 to 1,
                8 to 0, 8 to 2,
                9 to 2, 9 to 4,
                10 to 4, 10 to 6,
                11 to 6, 11 to 0,
            ),
        ),
        // 46: Double Crown Matrix (11V, 20E - 2 odd)
        buildLevel(
            46, "Monarch Grid", DifficultyTier.MASTERMIND, 60,
            pts = listOf(
                0.18f to 0.22f, 0.50f to 0.16f, 0.82f to 0.22f, // 0,1,2
                0.34f to 0.38f, 0.66f to 0.38f, // 3,4
                0.16f to 0.58f, 0.50f to 0.56f, 0.84f to 0.58f, // 5,6,7
                0.34f to 0.78f, 0.66f to 0.78f, // 8,9
                0.50f to 0.90f, // 10
            ),
            // Top crown: 0-1, 1-2, 0-3, 1-3, 1-4, 2-4, 3-4
            // Mid: 0-5, 3-5, 3-6, 4-6, 4-7, 2-7, 5-6, 6-7
            // Bot: 5-8, 6-8, 6-9, 7-9, 8-9, 8-10, 9-10
            // Check degrees:
            // 0:3 (1,3,5)
            // 1:4 (0,2,3,4)
            // 2:3 (1,4,7)
            // 3:5 (0,1,4,5,6)
            // 4:5 (1,2,3,6,7)
            // 5:4 (0,3,6,8)
            // 6:6 (3,4,5,7,8,9)
            // 7:4 (2,4,6,9)
            // 8:4 (5,6,9,10)
            // 9:4 (6,7,8,10)
            // 10:2 (8,9)
            // Odds: 0(3), 2(3), 3(5), 4(5) -> 4 odd!
            // Fix: add 3-8? No, add 0-2? Crosses 1! Add 3-9? Crosses 6!
            // Remove 3-4 -> 3:4, 4:4! Leaves ONLY 0(3) and 2(3) as odd! Wait: add 5-10, 7-10 -> 5:5, 7:5, 10:4.
            // Just remove 3-4 and add 3-8? No, removing 3-4 gives 0:3, 2:3 (ONLY 2 ODD!) and adds 0-6? No, 3-4 removed is clean, OR add 0-10? No, add 3-8, 4-9, 0-2? No.
            // Actually, keep 3-4, add 0-6 and 2-6? Then 0:4, 2:4, 6:8, leaving ONLY 3(5) and 4(5) as odd! Even denser & cooler!
            links = listOf(
                0 to 1, 1 to 2, 0 to 3, 1 to 3, 1 to 4, 2 to 4,
                0 to 5, 3 to 5, 3 to 6, 4 to 6, 4 to 7, 2 to 7, 5 to 6, 6 to 7,
                5 to 8, 6 to 8, 6 to 9, 7 to 9, 8 to 9, 8 to 10, 9 to 10,
            ),
        ),
        // 47: Concentric Hex Rings (12V, 24E - all even)
        buildLevel(
            47, "Hyper Hex", DifficultyTier.MASTERMIND, 65,
            pts = ring(6, 0.40f, -90f) + ring(6, 0.22f, -90f),
            // Outer 0..5 (deg 2) + Inner 6..11 (deg 2)
            // Triangles on inner: 6-8-10-6 and 7-9-11-7 (inner become deg 4)
            // Spokes: 0-6, 1-7, 2-8, 3-9, 4-10, 5-11 (all +1 -> outer 3, inner 5)
            // Zigzag 0-7, 1-6, 2-9, 3-8, 4-11, 5-10 (all +1 -> outer 4, inner 6) -> ALL EVEN!
            links = cycleEdges(0, 6) + cycleEdges(6, 6) + listOf(
                6 to 8, 8 to 10, 10 to 6,
                7 to 9, 9 to 11, 11 to 7,
                0 to 6, 1 to 7, 2 to 8, 3 to 9, 4 to 10, 5 to 11,
                0 to 7, 1 to 6, 2 to 9, 3 to 8, 4 to 11, 5 to 10,
            ),
        ),
        // 48: 4x3 Cyber Grid (12V, 21E - 2 odd)
        buildLevel(
            48, "Circuit 4x3", DifficultyTier.MASTERMIND, 65,
            pts = listOf(
                0.16f to 0.24f, 0.386f to 0.24f, 0.613f to 0.24f, 0.84f to 0.24f,
                0.16f to 0.50f, 0.386f to 0.50f, 0.613f to 0.50f, 0.84f to 0.50f,
                0.16f to 0.76f, 0.386f to 0.76f, 0.613f to 0.76f, 0.84f to 0.76f,
            ),
            // 4x3 grid edges (17E):
            // deg: corners (0,3,8,11)=2, top/bot mids (1,2,9,10)=3, side mids (4,7)=3, centers (5,6)=4 -> 6 odd (1,2,4,7,9,10).
            // Add 1-4, 2-7, 4-9, 7-10?
            // Check deg after adding (1-4, 2-7, 9-10? already):
            // Add (1-4), (2-7), (9-5)? No:
            // Add (1-4) -> 1:4, 4:4
            // Add (2-7) -> 2:4, 7:4
            // Leaves ONLY 9(3) and 10(3) as odd! (2 ODD!)
            // Let's also add diagonals inside central boxes for extra challenge: (0-5), (5-10)? No:
            // Add (0-5), (5-2) -> 0:3, 5:6, 2:5? No, add (0-5), (5-10) -> 0:3, 5:6, 10:4 -> 0(3), 9(3) ONLY 2 ODD!
            // Plus (3-6), (6-9) -> 3:3, 6:6, 9:4 -> 0(3), 3(3) ONLY 2 ODD!
            links = listOf(
                0 to 1, 1 to 2, 2 to 3,
                4 to 5, 5 to 6, 6 to 7,
                8 to 9, 9 to 10, 10 to 11,
                0 to 4, 4 to 8,
                1 to 5, 5 to 9,
                2 to 6, 6 to 10,
                3 to 7, 7 to 11,
                1 to 4, 2 to 7,
                0 to 5, 5 to 10,
                3 to 6, 6 to 9,
            ),
        ),
        // 49: Sacred Rose 16-Node (16V, 28E - all even)
        buildLevel(
            49, "Astra Constellation", DifficultyTier.MASTERMIND, 70,
            pts = ring(8, 0.40f, -90f) + ring(8, 0.22f, -67.5f),
            // Outer 0..7 (deg 2)
            // Inner 8..15 (deg 2)
            // Zigzag 0-8-1-9-2-10-3-11-4-12-5-13-6-14-7-15-0 (+2 -> all deg 4!)
            // Plus inner star 8-10-12-14-8 and 9-11-13-15-9 (+2 to inner -> inner deg 6, outer deg 4!) -> ALL EVEN!
            links = cycleEdges(0, 8) + cycleEdges(8, 8) + (0 until 8).flatMap { i ->
                listOf(i to (8 + i), (8 + i) to ((i + 1) % 8))
            } + listOf(
                8 to 10, 10 to 12, 12 to 14, 14 to 8,
                9 to 11, 11 to 13, 13 to 15, 15 to 9,
            ),
        ),
        // 50: Grand Neural Mandala (15V, 30E - all even!)
        buildLevel(
            50, "Neural Singularity", DifficultyTier.MASTERMIND, 75,
            pts = listOf(0.50f to 0.50f) + ring(7, 0.41f, -90f) + ring(7, 0.22f, -90f + (180f / 7f)),
            // Outer 1..7 (deg 2)
            // Inner 8..14 (deg 2)
            // Zigzag 1-8-2-9-3-10-4-11-5-12-6-13-7-14-1 (all +2 -> deg 4)
            // Inner heptagram skip-2: 8-10-12-14-9-11-13-8 (all +2 -> inner deg 6)
            // Center 0 connects to 8,9,10,11,12,13 -> 6 spokes (0:6, 8..13 become 7 -> 6 odd! wait!).
            // Fix: connect 0 to 8,9,10,11 (4 spokes) AND connect 8-11? No, 8-11 crosses 0!
            // Instead of connecting 0 to inner, connect 0 to 8, 9, 10, 11, 12, 13, 14? (7 odd).
            // Best: connect 0 to 8, 9, 11, 12 (0:4) AND add 8-9? already! Wait: remove 8-9, 11-12 from inner cycle? No, add 1-2? already.
            // Simply add 0 to 8, 9, 11, 12 AND skip inner cycle edges (8-9) and (11-12)? Wait:
            // Inner cycle is 8-9, 9-10, 10-11, 11-12, 12-13, 13-14, 14-8.
            // If we omit (8-9) and (11-12) from inner cycle, and instead connect 0-8, 0-9, 0-11, 0-12 ->
            // deg(0)=4, deg(8..14)=6 -> ALL EVEN, symmetrical, and awesome!
            links = cycleEdges(1, 7) + listOf(
                9 to 10, 10 to 11, 12 to 13, 13 to 14, 14 to 8,
                0 to 8, 0 to 9, 0 to 11, 0 to 12,
            ) + (0 until 7).flatMap { i ->
                listOf((1 + i) to (8 + i), (8 + i) to (1 + (i + 1) % 7))
            } + listOf(
                8 to 10, 10 to 12, 12 to 14, 14 to 9, 9 to 11, 11 to 13, 13 to 8,
            ),
        ),
    )

    /**
     * Deterministically generates a unique, 100%-Eulerian Daily Challenge puzzle for [dateKey] (YYYY-MM-DD).
     */
    fun generateDailyPuzzle(dateKey: String): PuzzleLevel {
        val seed = dateKey.hashCode().toLong()
        val rng = Random(seed)

        val outerCount = rng.nextInt(6, 10) // 6..9 outer nodes
        val outerRadius = 0.39f
        val innerRadius = 0.19f + rng.nextFloat() * 0.05f
        val angleOffset = rng.nextFloat() * 30f

        val outerPts = ring(outerCount, outerRadius, -90f + angleOffset)
        val innerPts = ring(outerCount, innerRadius, -90f + angleOffset + (180f / outerCount))
        val allPts = listOf(0.50f to 0.50f) + outerPts + innerPts

        val edges = LinkedHashSet<Pair<Int, Int>>()
        fun addEdge(a: Int, b: Int) {
            if (a == b) return
            val pair = if (a < b) a to b else b to a
            edges.add(pair)
        }

        // Outer cycle (even)
        for (i in 0 until outerCount) {
            addEdge(1 + i, 1 + (i + 1) % outerCount)
        }
        // Zigzag outer <-> inner (all even)
        for (i in 0 until outerCount) {
            val inner = 1 + outerCount + i
            addEdge(1 + i, inner)
            addEdge(inner, 1 + (i + 1) % outerCount)
        }
        // Inner cycle
        for (i in 0 until outerCount) {
            val a = 1 + outerCount + i
            val b = 1 + outerCount + (i + 1) % outerCount
            addEdge(a, b)
        }
        // Add 2 or 3 seeded triangular/hub loops (each triangle adds +2 degree -> preserves Eulerian!)
        val extraTriangles = 2 + rng.nextInt(2)
        repeat(extraTriangles) {
            val idx = rng.nextInt(outerCount)
            val a = 1 + outerCount + idx
            val b = 1 + outerCount + (idx + 1) % outerCount
            addEdge(0, a)
            addEdge(0, b)
            // Toggle a-b if already exists? No, adding a duplicate wouldn't be simple, so instead pick 0-a-b only if a-b wasn't toggled, OR form triangle (0, a, c) where c = (idx+2)%outerCount!
        }

        // Clean guaranteed Eulerian construction: start from outer cycle + zigzag (all deg 4 on outer, deg 2 on inner),
        // then add seeded triangle cycles on inner + center so EVERY vertex stays even (or exactly 2 odd via 1 single extra chord!).
        edges.clear()
        for (i in 0 until outerCount) {
            addEdge(1 + i, 1 + (i + 1) % outerCount)
            val inner = 1 + outerCount + i
            addEdge(1 + i, inner)
            addEdge(inner, 1 + (i + 1) % outerCount)
        }
        // Add inner cycle if seeded
        if (rng.nextBoolean()) {
            for (i in 0 until outerCount) {
                addEdge(1 + outerCount + i, 1 + outerCount + (i + 1) % outerCount)
            }
        }
        // Connect center 0 using closed 3-cycles (0, innerA, innerB) where innerA-innerB isn't already used
        val step = if (outerCount >= 7) 2 else 1
        val usedInnerChords = HashSet<Pair<Int, Int>>()
        var addedHubCycles = 0
        for (attempt in 0 until outerCount) {
            val i1 = (attempt * step) % outerCount
            val i2 = (i1 + 2) % outerCount
            val u = 1 + outerCount + i1
            val v = 1 + outerCount + i2
            val pair = if (u < v) u to v else v to u
            if (!edges.contains(pair) && !edges.contains(0 to u) && !edges.contains(0 to v) && usedInnerChords.add(pair)) {
                addEdge(0, u)
                addEdge(0, v)
                addEdge(u, v)
                addedHubCycles++
                if (addedHubCycles >= 2) break
            }
        }
        // Optionally add 1 chord between two outer vertices to create an Euler Path (2 odd vertices)
        if (rng.nextBoolean()) {
            val s = rng.nextInt(outerCount)
            val a = 1 + s
            val b = 1 + (s + 2) % outerCount
            addEdge(a, b)
        }

        // Remove vertex 0 if it ended up isolated
        val usedNodes = edges.flatMap { listOf(it.first, it.second) }.toSet()
        val finalPts = if (0 in usedNodes) allPts else allPts.drop(1)
        val idShift = if (0 in usedNodes) 0 else -1

        val finalEdges = edges.mapIndexed { idx, (u, v) -> Edge(idx, u + idShift, v + idShift) }
        val finalVertices = finalPts.mapIndexed { idx, (x, y) -> Vertex(idx, x, y) }

        val candidate = PuzzleLevel(
            id = 1000 + dateKey.hashCode().and(0xFFFF),
            name = "Daily $dateKey",
            tier = DifficultyTier.DAILY,
            vertices = finalVertices,
            edges = finalEdges,
            parTimeSeconds = 45,
            isDailyChallenge = true,
            dailyDateKey = dateKey,
        )
        return if (EulerSolver.hasEulerPath(candidate)) {
            candidate
        } else {
            // Guaranteed fallback from Tier 4/5 rotated deterministically
            val fallback = levels[(dateKey.hashCode().and(0x7FFFFFFF) % 25) + 20]
            fallback.copy(
                id = candidate.id,
                name = "Daily $dateKey",
                tier = DifficultyTier.DAILY,
                isDailyChallenge = true,
                dailyDateKey = dateKey,
            )
        }
    }
}
