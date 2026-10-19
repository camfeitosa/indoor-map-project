package com.clickbus.challenge.data.remote

import kotlinx.serialization.Serializable

/** Espelha CoordinateDto da API (0..100, o eixo Y cresce para baixo). */
@Serializable
data class CoordinateDto(
    val x: Double,
    val y: Double,
)

/** Espelha PointDto da API. `type` chega como texto: "entrada", "corredor" ou "servico". */
@Serializable
data class PointDto(
    val id: String,
    val label: String,
    val type: String,
    val floor: String,
    val x: Double,
    val y: Double,
)

@Serializable
data class EntryPointDto(
    val position: CoordinateDto,
    val distanceFromOrigin: Double,
    val segmentFromId: String,
    val segmentToId: String,
)

@Serializable
data class RouteStepDto(
    val order: Int,
    val point: PointDto,
    val distanceFromPrevious: Double,
    val cumulativeDistance: Double,
    val instruction: String,
)

/** Espelha RouteResult da API. `polyline` sempre tem `steps.size + 2` pontos. */
@Serializable
data class RouteResultDto(
    val origin: CoordinateDto,
    val entryPoint: EntryPointDto,
    val destination: PointDto,
    val totalDistance: Double,
    val totalDistanceMeters: Double,
    val estimatedSeconds: Int,
    val steps: List<RouteStepDto>,
    val polyline: List<CoordinateDto>,
)
