package com.clickbus.challenge.ui.screens.maproute

import com.clickbus.challenge.domain.route.Route
import com.clickbus.challenge.domain.route.RouteError

/** Tudo que a tela precisa para se desenhar. Imutável: a UI só observa, nunca altera. */
sealed interface MapRouteUiState {
    data object Loading : MapRouteUiState

    data class Error(val error: RouteError) : MapRouteUiState

    data class Content(
        val route: Route,
        /** Rótulo do ponto onde o usuário está (origem, ou o último beacon detectado). */
        val currentPlaceLabel: String,
        val isSimulating: Boolean = false,
        val hasArrived: Boolean = false,
        /** Falha ao recalcular durante o percurso; a rota anterior continua na tela. */
        val recalculationError: RouteError? = null,
    ) : MapRouteUiState
}
