package com.example.engine

enum class DifficultyTier(val index: Int) {
    FOUNDATIONS(0),
    INTERSECTIONS(1),
    GEOMETRY(2),
    OVERLAPS(3),
    MASTERMIND(4),
    DAILY(5),
}

data class Vertex(
    val id: Int,
    val x: Float, // Normalized 0f..1f
    val y: Float, // Normalized 0f..1f
)

data class Edge(
    val id: Int,
    val u: Int,
    val v: Int,
) {
    fun connects(a: Int, b: Int): Boolean = (u == a && v == b) || (u == b && v == a)
    fun other(node: Int): Int = if (u == node) v else u
}

data class PuzzleLevel(
    val id: Int,
    val name: String,
    val tier: DifficultyTier,
    val vertices: List<Vertex>,
    val edges: List<Edge>,
    val parTimeSeconds: Int = 25,
    val isDailyChallenge: Boolean = false,
    val dailyDateKey: String? = null,
)

enum class FailureReason {
    LIFTED_FINGER,
    DEAD_END,
    RETRACED_EDGE,
}

enum class PuzzleStatus {
    READY,
    DRAWING,
    PAUSED,
    COMPLETE,
    FAILED,
}
