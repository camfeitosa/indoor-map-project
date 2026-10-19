package com.clickbus.challenge.ui.screens.maproute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clickbus.challenge.data.route.RouteRepository
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.domain.route.Outcome
import com.clickbus.challenge.domain.route.Route
import com.clickbus.challenge.indoor.BeaconSimulator
import com.clickbus.challenge.indoor.DemoMapLocations
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapRouteViewModel(
    private val repository: RouteRepository,
    originLabel: String?,
    destinationLabel: String,
    private val beaconIntervalMillis: Long = BeaconSimulator.DEFAULT_INTERVAL_MILLIS,
) : ViewModel() {

    private val originId = DemoMapLocations.originId(originLabel)
    private val destinationId = DemoMapLocations.destinationId(destinationLabel)

    private val _uiState = MutableStateFlow<MapRouteUiState>(MapRouteUiState.Loading)
    val uiState: StateFlow<MapRouteUiState> = _uiState.asStateFlow()

    private var initialRoute: Route? = null
    private var simulationJob: Job? = null

    init {
        load()
    }

    /** Carrega a rota inicial. Também é o "Tentar novamente" da tela de erro. */
    fun load() {
        simulationJob?.cancel()
        _uiState.value = MapRouteUiState.Loading
        viewModelScope.launch {
            val route = fetchInitialRoute()
            if (route == null) return@launch // fetchInitialRoute já publicou o erro
            initialRoute = route
            _uiState.value = MapRouteUiState.Content(route, currentPlaceLabel = route.steps.first().place.label)
        }
    }

    private suspend fun fetchInitialRoute(): Route? {
        val origin = when (val outcome = repository.placeById(originId)) {
            is Outcome.Success -> outcome.value
            is Outcome.Failure -> return fail(outcome)
        }
        return when (val outcome = repository.routeToPlace(origin.position, destinationId)) {
            is Outcome.Success -> outcome.value
            is Outcome.Failure -> fail(outcome)
        }
    }

    private fun fail(outcome: Outcome.Failure): Route? {
        _uiState.value = MapRouteUiState.Error(outcome.error)
        return null
    }

    fun startSimulation() {
        val route = initialRoute ?: return
        val current = _uiState.value as? MapRouteUiState.Content ?: return
        if (current.isSimulating) return

        simulationJob?.cancel()
        _uiState.value = if (current.hasArrived) {
            // Reiniciar: volta para a rota original, calculada a partir da origem.
            MapRouteUiState.Content(route, currentPlaceLabel = route.steps.first().place.label, isSimulating = true)
        } else {
            current.copy(isSimulating = true, recalculationError = null)
        }
        // Os beacons simulados são os pontos da rota original, na ordem.
        val simulator = BeaconSimulator(route.steps.map { it.place })
        simulationJob = viewModelScope.launch {
            simulator.detections(beaconIntervalMillis).collect { detection ->
                onBeaconDetected(detection.place.id, detection.place.label, detection.place.position)
            }
        }
    }

    fun pauseSimulation() {
        simulationJob?.cancel()
        updateContent { it.copy(isSimulating = false) }
    }

    private suspend fun onBeaconDetected(
        placeId: String,
        placeLabel: String,
        position: MapPosition,
    ) {
        if (placeId == destinationId) {
            updateContent { it.copy(currentPlaceLabel = placeLabel, isSimulating = false, hasArrived = true) }
            return
        }
        updateContent { it.copy(currentPlaceLabel = placeLabel) }
        // Recalcula só o trecho que falta, a partir do beacon atual.
        when (val outcome = repository.routeToPlace(position, destinationId)) {
            is Outcome.Success -> updateContent { it.copy(route = outcome.value, recalculationError = null) }
            is Outcome.Failure -> {
                simulationJob?.cancel()
                updateContent { it.copy(isSimulating = false, recalculationError = outcome.error) }
            }
        }
    }

    private fun updateContent(transform: (MapRouteUiState.Content) -> MapRouteUiState.Content) {
        _uiState.update { state -> if (state is MapRouteUiState.Content) transform(state) else state }
    }
}
