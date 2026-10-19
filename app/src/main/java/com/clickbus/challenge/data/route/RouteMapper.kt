package com.clickbus.challenge.data.route

import com.clickbus.challenge.data.remote.CoordinateDto
import com.clickbus.challenge.data.remote.PointDto
import com.clickbus.challenge.data.remote.RouteResultDto
import com.clickbus.challenge.data.remote.RouteStepDto
import com.clickbus.challenge.domain.route.MapPlace
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.domain.route.Route
import com.clickbus.challenge.domain.route.RouteStep

internal fun RouteResultDto.toDomain(): Route = Route(
    origin = origin.toDomain(),
    destination = destination.toDomain(),
    distanceMeters = totalDistanceMeters,
    estimatedSeconds = estimatedSeconds,
    steps = steps.map { it.toDomain() },
    path = polyline.map { it.toDomain() },
)

private fun RouteStepDto.toDomain() = RouteStep(
    order = order,
    place = point.toDomain(),
    distanceFromPreviousMeters = distanceFromPrevious,
    instruction = instruction,
)

internal fun PointDto.toDomain() = MapPlace(id = id, label = label, position = MapPosition(x, y))

private fun CoordinateDto.toDomain() = MapPosition(x, y)
