package com.clickbus.challenge.indoor

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IndoorRouteEngineTest {
    @Test
    fun `chooses the shortest connected path`() {
        val map = IndoorMapData(
            points = listOf(
                point("A", 0f, 0f),
                point("B", 1f, 0f),
                point("C", 5f, 5f),
                point("D", 2f, 0f),
            ),
            connections = listOf(
                IndoorMapConnection("A", "B"),
                IndoorMapConnection("B", "D"),
                IndoorMapConnection("A", "C"),
                IndoorMapConnection("C", "D"),
            ),
        )

        val route = IndoorRouteEngine.shortestPath(map, "A", "D")

        assertEquals(listOf("A", "B", "D"), route.points.map { it.id })
        assertEquals(2f, route.distance, 0.001f)
    }

    @Test
    fun `returns unavailable route for disconnected points`() {
        val map = IndoorMapData(
            points = listOf(point("A", 0f, 0f), point("B", 1f, 1f)),
            connections = emptyList(),
        )

        val route = IndoorRouteEngine.shortestPath(map, "A", "B")

        assertFalse(route.isAvailable)
        assertTrue(route.points.isEmpty())
    }

    @Test
    fun `returns one point when already at destination`() {
        val map = IndoorMapData(points = listOf(point("A", 0f, 0f)), connections = emptyList())

        val route = IndoorRouteEngine.shortestPath(map, "A", "A")

        assertEquals(listOf("A"), route.points.map { it.id })
        assertEquals(0f, route.distance, 0.001f)
    }

    @Test
    fun `generates right and left turn instructions from route geometry`() {
        val route = IndoorRoute(
            points = listOf(
                point("A", 0f, 0f),
                point("B", 1f, 0f),
                point("C", 1f, 1f),
                point("D", 2f, 1f),
            ),
            distance = 3f,
        )

        val instructions = IndoorDirectionEngine.directions(route, "Destino").map { it.instruction }

        assertEquals(
            listOf("Siga em frente pela rota destacada", "Vire à direita", "Vire à esquerda", "Você chegou a Destino"),
            instructions,
        )
    }

    @Test
    fun `beacon simulator detects every route point in order`() = runBlocking {
        val path = listOf(point("A", 0f, 0f), point("B", 1f, 0f), point("C", 2f, 0f))
        val simulator = BeaconSimulator(path)

        val detections = simulator.detections(intervalMillis = 0L).toList()

        assertEquals(listOf("A", "B", "C"), detections.map { it.pointId })
        assertEquals(listOf(0, 1, 2), detections.map { it.sequence })
    }

    private fun point(id: String, x: Float, y: Float) = IndoorMapPoint(
        id = id,
        label = id,
        floor = "terreo",
        type = "corredor",
        xPercent = x,
        yPercent = y,
    )
}
