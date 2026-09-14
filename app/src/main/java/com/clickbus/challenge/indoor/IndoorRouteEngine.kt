package com.clickbus.challenge.indoor

import java.util.PriorityQueue
import kotlin.math.hypot

object IndoorRouteEngine {
    private data class QueueEntry(val pointId: String, val distance: Float)

    fun shortestPath(map: IndoorMapData, startId: String, destinationId: String): IndoorRoute {
        if (startId !in map.pointsById || destinationId !in map.pointsById) return IndoorRoute(emptyList(), 0f)
        if (startId == destinationId) return IndoorRoute(listOf(map.pointsById.getValue(startId)), 0f)

        val adjacency = mutableMapOf<String, MutableSet<String>>()
        map.connections.forEach { connection ->
            if (connection.fromId in map.pointsById && connection.toId in map.pointsById) {
                adjacency.getOrPut(connection.fromId) { mutableSetOf() }.add(connection.toId)
                adjacency.getOrPut(connection.toId) { mutableSetOf() }.add(connection.fromId)
            }
        }

        val distances = map.points.associate { it.id to Float.POSITIVE_INFINITY }.toMutableMap()
        val previous = mutableMapOf<String, String>()
        val queue = PriorityQueue<QueueEntry>(compareBy { it.distance })
        distances[startId] = 0f
        queue.add(QueueEntry(startId, 0f))

        while (queue.isNotEmpty()) {
            val current = queue.remove()
            if (current.distance > distances.getValue(current.pointId)) continue
            if (current.pointId == destinationId) break

            adjacency[current.pointId].orEmpty().forEach { neighborId ->
                val candidate = current.distance + distance(
                    map.pointsById.getValue(current.pointId),
                    map.pointsById.getValue(neighborId),
                )
                if (candidate < distances.getValue(neighborId)) {
                    distances[neighborId] = candidate
                    previous[neighborId] = current.pointId
                    queue.add(QueueEntry(neighborId, candidate))
                }
            }
        }

        val totalDistance = distances.getValue(destinationId)
        if (!totalDistance.isFinite()) return IndoorRoute(emptyList(), 0f)

        val ids = mutableListOf(destinationId)
        var cursor = destinationId
        while (cursor != startId) {
            cursor = previous[cursor] ?: return IndoorRoute(emptyList(), 0f)
            ids.add(cursor)
        }
        ids.reverse()
        return IndoorRoute(ids.map(map.pointsById::getValue), totalDistance)
    }

    private fun distance(from: IndoorMapPoint, to: IndoorMapPoint): Float =
        hypot(from.xPercent - to.xPercent, from.yPercent - to.yPercent)
}
