package com.clickbus.challenge.domain.route

/** Posição na planta: 0..100 nos dois eixos, Y cresce para baixo. */
data class MapPosition(val x: Double, val y: Double)

data class MapPlace(
    val id: String,
    val label: String,
    val position: MapPosition,
)

/** Um passo do trajeto: chegar em [place] seguindo [instruction]. */
data class RouteStep(
    val order: Int,
    val place: MapPlace,
    val distanceFromPreviousMeters: Double,
    val instruction: String,
)

data class Route(
    val origin: MapPosition,
    val destination: MapPlace,
    val distanceMeters: Double,
    val estimatedSeconds: Int,
    val steps: List<RouteStep>,
    /** Pontos para desenhar a linha no mapa, na ordem: origem, entrada na malha e cada passo. */
    val path: List<MapPosition>,
)
