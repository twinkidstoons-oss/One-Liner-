package com.example.engine

/**
 * Graph traversal & Eulerian path/circuit solver and validator.
 */
object EulerSolver {

    fun degrees(level: PuzzleLevel): Map<Int, Int> {
        val deg = level.vertices.associate { it.id to 0 }.toMutableMap()
        for (edge in level.edges) {
            deg[edge.u] = (deg[edge.u] ?: 0) + 1
            deg[edge.v] = (deg[edge.v] ?: 0) + 1
        }
        return deg
    }

    fun oddDegreeVertices(level: PuzzleLevel): List<Int> {
        return degrees(level).filter { (_, d) -> d % 2 != 0 }.keys.sorted()
    }

    fun isConnected(level: PuzzleLevel): Boolean {
        if (level.vertices.isEmpty() || level.edges.isEmpty()) return false
        val adj = HashMap<Int, MutableList<Int>>()
        for (v in level.vertices) adj[v.id] = mutableListOf()
        for (e in level.edges) {
            adj[e.u]?.add(e.v)
            adj[e.v]?.add(e.u)
        }
        val activeVertices = level.vertices.filter { (adj[it.id]?.size ?: 0) > 0 }.map { it.id }
        if (activeVertices.isEmpty()) return false
        val visited = HashSet<Int>()
        val queue = ArrayDeque<Int>()
        queue.add(activeVertices.first())
        visited.add(activeVertices.first())
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            for (next in adj[curr].orEmpty()) {
                if (visited.add(next)) {
                    queue.add(next)
                }
            }
        }
        return visited.size == level.vertices.size
    }

    fun hasEulerPath(level: PuzzleLevel): Boolean {
        if (!isConnected(level)) return false
        val oddCount = oddDegreeVertices(level).size
        return oddCount == 0 || oddCount == 2
    }

    /**
     * Uses Hierholzer's algorithm to produce a guaranteed valid single-line vertex sequence
     * traversing every edge in [level] exactly once. Returns empty list if invalid.
     */
    fun findEulerPath(level: PuzzleLevel): List<Int> {
        if (!hasEulerPath(level)) return emptyList()

        val adj = HashMap<Int, ArrayDeque<Pair<Int, Int>>>()
        for (v in level.vertices) adj[v.id] = ArrayDeque()
        for (edge in level.edges) {
            adj[edge.u]?.add(edge.v to edge.id)
            adj[edge.v]?.add(edge.u to edge.id)
        }
        // Sort neighbor lists deterministically so hints are clean and reproducible
        for ((_, list) in adj) {
            val sorted = list.sortedBy { it.first }
            list.clear()
            list.addAll(sorted)
        }

        val odd = oddDegreeVertices(level)
        val startVertex = if (odd.isNotEmpty()) odd.first() else level.vertices.first().id

        val usedEdges = BooleanArray(level.edges.maxOfOrNull { it.id }?.plus(1) ?: 0)
        val stack = ArrayDeque<Int>()
        val circuit = ArrayList<Int>(level.edges.size + 1)

        stack.addLast(startVertex)
        while (stack.isNotEmpty()) {
            val v = stack.last()
            val neighbors = adj[v]
            var nextVertex: Int? = null
            if (neighbors != null) {
                while (neighbors.isNotEmpty()) {
                    val (to, edgeId) = neighbors.removeFirst()
                    if (!usedEdges[edgeId]) {
                        usedEdges[edgeId] = true
                        nextVertex = to
                        break
                    }
                }
            }
            if (nextVertex != null) {
                stack.addLast(nextVertex)
            } else {
                circuit.add(stack.removeLast())
            }
        }
        circuit.reverse()
        return if (circuit.size == level.edges.size + 1) circuit else emptyList()
    }

    fun findUndrawnEdge(
        level: PuzzleLevel,
        u: Int,
        v: Int,
        drawnEdgeIds: Set<Int>,
    ): Edge? = level.edges.firstOrNull { !drawnEdgeIds.contains(it.id) && it.connects(u, v) }

    fun findDrawnEdgeBetween(
        level: PuzzleLevel,
        u: Int,
        v: Int,
        drawnEdgeIds: Set<Int>,
    ): Edge? = level.edges.firstOrNull { drawnEdgeIds.contains(it.id) && it.connects(u, v) }

    fun hasAnyValidMoveFrom(
        level: PuzzleLevel,
        vertexId: Int,
        drawnEdgeIds: Set<Int>,
    ): Boolean = level.edges.any { !drawnEdgeIds.contains(it.id) && (it.u == vertexId || it.v == vertexId) }

    fun validNextVertices(
        level: PuzzleLevel,
        vertexId: Int,
        drawnEdgeIds: Set<Int>,
    ): Set<Int> = level.edges
        .filter { !drawnEdgeIds.contains(it.id) && (it.u == vertexId || it.v == vertexId) }
        .map { it.other(vertexId) }
        .toSet()
}
