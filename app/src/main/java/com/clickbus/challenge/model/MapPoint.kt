package com.clickbus.challenge.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A point of interest positioned on a station map illustration.
 *
 * [xFraction]/[yFraction] are normalized (0f..1f) coordinates relative to the illustration's
 * width/height, so the same point lines up correctly regardless of how large the image is
 * rendered on screen.
 */
data class MapPoint(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val xFraction: Float,
    val yFraction: Float,
    val isCurrentLocation: Boolean = false,
)

/** A walkable connection between two [MapPoint]s, used to route from one point to another. */
data class MapEdge(
    val fromId: String,
    val toId: String,
)
