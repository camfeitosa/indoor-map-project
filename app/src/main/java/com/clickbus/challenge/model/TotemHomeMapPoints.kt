package com.clickbus.challenge.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Escalator
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Wc

/**
 * Points of interest plotted on the Totem home illustration (`R.drawable.totem_map_illustration`).
 * Coordinates were extracted from the source asset (463x341px, cropped tight to the artwork) and
 * normalized to fractions.
 */
val totemHomeMapPoints = listOf(
    MapPoint(
        id = "current_location",
        name = "Você está aqui",
        icon = Icons.Filled.LocationOn,
        xFraction = 0.5032f,
        yFraction = 0.2639f,
        isCurrentLocation = true,
    ),
    MapPoint(id = "banheiros", name = "Banheiros", icon = Icons.Filled.Wc, xFraction = 0.2307f, yFraction = 0.1572f),
    MapPoint(id = "praca_alimentacao", name = "Praça de Alimentação", icon = Icons.Filled.Restaurant, xFraction = 0.6625f, yFraction = 0.1443f),
    MapPoint(id = "guarda_volumes", name = "Guarda-volumes", icon = Icons.Filled.Inventory2, xFraction = 0.8455f, yFraction = 0.1935f),
    MapPoint(id = "escada_rolante", name = "Escada Rolante", icon = Icons.Filled.Escalator, xFraction = 0.3151f, yFraction = 0.4457f),
    MapPoint(id = "taxi_aplicativos", name = "Táxi / Aplicativos", icon = Icons.Filled.LocalTaxi, xFraction = 0.8523f, yFraction = 0.4695f),
    MapPoint(id = "informacoes", name = "Informações", icon = Icons.Filled.Info, xFraction = 0.7534f, yFraction = 0.6529f),
)

/** This illustration is a hub-and-spoke: every point of interest routes straight to "Você está aqui". */
val totemHomeMapEdges = totemHomeMapPoints
    .filterNot { it.isCurrentLocation }
    .map { MapEdge(fromId = "current_location", toId = it.id) }
