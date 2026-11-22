package com.example

import com.example.data.LevelCatalog
import com.example.engine.EulerSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.hypot

class LevelCatalogTest {

    @Test
    fun all100LevelsExistAndAreUnique() {
        val levels = LevelCatalog.levels
        assertEquals("Must have exactly 100 levels", 100, levels.size)
        assertEquals((1..100).toList(), levels.map { it.id })
        assertEquals(100, levels.map { it.name }.toSet().size)
    }

    @Test
    fun all100LevelsHaveValidEulerianSolutionsAndCleanGeometry() {
        val errors = mutableListOf<String>()
        for (level in LevelCatalog.levels) {
            if (level.vertices.isEmpty() || level.edges.isEmpty()) {
                errors.add("Level ${level.id} empty")
                continue
            }
            val seenPairs = HashSet<Pair<Int, Int>>()
            for (e in level.edges) {
                if (e.u == e.v) errors.add("Level ${level.id} self-loop on ${e.u}")
                val pair = if (e.u < e.v) e.u to e.v else e.v to e.u
                if (!seenPairs.add(pair)) errors.add("Level ${level.id} duplicate edge $pair")
            }
            if (!EulerSolver.isConnected(level)) {
                errors.add("Level ${level.id} (${level.name}) not connected")
            }
            val oddCount = EulerSolver.oddDegreeVertices(level).size
            if (oddCount != 0 && oddCount != 2) {
                errors.add("Level ${level.id} (${level.name}) has $oddCount odd-degree vertices: ${EulerSolver.oddDegreeVertices(level)}")
            }
            if (!EulerSolver.hasEulerPath(level)) {
                errors.add("Level ${level.id} (${level.name}) lacks Euler path")
            } else {
                val path = EulerSolver.findEulerPath(level)
                if (path.size != level.edges.size + 1) {
                    errors.add("Level ${level.id} (${level.name}) path length ${path.size} != ${level.edges.size + 1}")
                }
            }
            for (i in level.vertices.indices) {
                for (j in i + 1 until level.vertices.size) {
                    val a = level.vertices[i]
                    val b = level.vertices[j]
                    val dist = hypot(a.x - b.x, a.y - b.y)
                    if (dist < 0.08f) {
                        errors.add("Level ${level.id} vertices ${a.id} and ${b.id} too close ($dist)")
                    }
                }
            }
            for (e in level.edges) {
                val a = level.vertices[e.u]
                val b = level.vertices[e.v]
                val len = hypot(b.x - a.x, b.y - a.y)
                for (v in level.vertices) {
                    if (v.id == e.u || v.id == e.v) continue
                    val dot = ((v.x - a.x) * (b.x - a.x) + (v.y - a.y) * (b.y - a.y)) / (len * len)
                    if (dot in 0.05f..0.95f) {
                        val projX = a.x + dot * (b.x - a.x)
                        val projY = a.y + dot * (b.y - a.y)
                        val perpDist = hypot(v.x - projX, v.y - projY)
                        if (abs(perpDist) < 0.025f) {
                            errors.add("Level ${level.id}: vertex ${v.id} on edge (${e.u}-${e.v}) dist=$perpDist")
                        }
                    }
                }
            }
        }
        assertTrue("Level validation errors:\n${errors.joinToString("\n")}", errors.isEmpty())
    }

    @Test
    fun dailyChallengesAreSolvableAcross30Days() {
        for (day in 1..30) {
            val dateKey = "2026-11-${day.toString().padStart(2, '0')}"
            val daily = LevelCatalog.generateDailyPuzzle(dateKey)
            assertTrue("Daily $dateKey must be Eulerian", EulerSolver.hasEulerPath(daily))
            val path = EulerSolver.findEulerPath(daily)
            assertEquals("Daily $dateKey solution incomplete", daily.edges.size + 1, path.size)
        }
    }
}
