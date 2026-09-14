package com.clickbus.challenge.indoor

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

data class IndoorDirection(
    val pointId: String,
    val instruction: String,
)

object IndoorDirectionEngine {
    private const val TURN_THRESHOLD_DEGREES = 30f

    fun directions(route: IndoorRoute, destinationLabel: String): List<IndoorDirection> {
        if (!route.isAvailable) return emptyList()
        if (route.points.size == 1) {
            return listOf(IndoorDirection(route.points.first().id, "Você chegou a $destinationLabel"))
        }

        val directions = mutableListOf(
            IndoorDirection(route.points.first().id, "Siga em frente pela rota destacada"),
        )

        for (index in 1 until route.points.lastIndex) {
            val previous = route.points[index - 1]
            val current = route.points[index]
            val next = route.points[index + 1]
            val incomingX = current.xPercent - previous.xPercent
            val incomingY = current.yPercent - previous.yPercent
            val outgoingX = next.xPercent - current.xPercent
            val outgoingY = next.yPercent - current.yPercent
            val cross = incomingX * outgoingY - incomingY * outgoingX
            val dot = incomingX * outgoingX + incomingY * outgoingY
            val angle = (atan2(cross, dot) * 180f / PI.toFloat())

            if (abs(angle) >= TURN_THRESHOLD_DEGREES) {
                // The map Y axis points down, so a positive signed angle is a clockwise/right turn.
                val side = if (angle > 0f) "direita" else "esquerda"
                directions += IndoorDirection(current.id, "Vire à $side")
            }
        }

        directions += IndoorDirection(route.points.last().id, "Você chegou a $destinationLabel")
        return directions
    }
}
