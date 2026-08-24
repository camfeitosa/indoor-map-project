package com.clickbus.challenge.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** A point of interest inside the terminal (service, platform, entrance...). */
data class StationPlace(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
)

/** A boarding platform, highlighted separately from generic services. */
data class Platform(
    val number: String,
    val label: String = "Plataforma $number",
    val isCurrentTrip: Boolean = false,
)

/** A single step of a turn-by-turn walking route. */
data class RouteStep(
    val index: Int,
    val instruction: String,
)
