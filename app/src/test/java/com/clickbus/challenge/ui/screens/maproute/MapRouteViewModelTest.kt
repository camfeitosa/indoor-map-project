package com.clickbus.challenge.ui.screens.maproute

import com.clickbus.challenge.data.route.RouteRepository
import com.clickbus.challenge.domain.route.MapPlace
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.domain.route.Outcome
import com.clickbus.challenge.domain.route.Route
import com.clickbus.challenge.domain.route.RouteError
import com.clickbus.challenge.domain.route.RouteStep
import com.clickbus.challenge.indoor.DemoMapLocations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapRouteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val destinationId = DemoMapLocations.destinationId("Bilheteria")
    private val originPlace = place(DemoMapLocations.DEFAULT_ORIGIN_ID, "Acesso metro", 50.0, 70.0)
    private val corridor = place("C1", "Corredor 1", 50.0, 40.0)
    private val destination = place(destinationId, "bilheteria", 40.0, 25.0)

    private fun place(id: String, label: String, x: Double, y: Double) = MapPlace(id, label, MapPosition(x, y))

    private fun routeFrom(vararg places: MapPlace) = Route(
        origin = places.first().position,
        destination = destination,
        distanceMeters = 100.0,
        estimatedSeconds = 80,
        steps = places.mapIndexed { i, p -> RouteStep(i + 1, p, 0.0, "Siga até ${p.label}") },
        path = places.map { it.position },
    )

    private class FakeRepository : RouteRepository {
        var placeResult: Outcome<MapPlace> = Outcome.Failure(RouteError.NoConnection)
        val routeResults = ArrayDeque<Outcome<Route>>()
        val routeRequests = mutableListOf<Pair<MapPosition, String>>()

        override suspend fun routeToPlace(from: MapPosition, destinationId: String): Outcome<Route> {
            routeRequests += from to destinationId
            return routeResults.removeFirst()
        }

        override suspend fun routeToNearest(from: MapPosition, query: String): Outcome<Route> =
            error("não usado por esta tela")

        override suspend fun placeById(id: String): Outcome<MapPlace> = placeResult
    }

    private fun viewModel(repository: RouteRepository) =
        MapRouteViewModel(repository, originLabel = null, destinationLabel = "Bilheteria", beaconIntervalMillis = 1_500L)

    @Test
    fun `comeca em Loading e vira Content com a rota da API`() = runTest(dispatcher) {
        val repo = FakeRepository().apply {
            placeResult = Outcome.Success(originPlace)
            routeResults += Outcome.Success(routeFrom(originPlace, corridor, destination))
        }

        val vm = viewModel(repo)
        assertEquals(MapRouteUiState.Loading, vm.uiState.value)
        advanceUntilIdle()

        val state = vm.uiState.value as MapRouteUiState.Content
        assertEquals("Acesso metro", state.currentPlaceLabel)
        assertEquals(3, state.route.steps.size)
        assertFalse(state.isSimulating)
        // pediu a rota a partir das coordenadas da origem e até o destino mapeado
        assertEquals(originPlace.position to destinationId, repo.routeRequests.single())
    }

    @Test
    fun `falha vira Error e retry carrega de novo`() = runTest(dispatcher) {
        val repo = FakeRepository().apply { placeResult = Outcome.Failure(RouteError.NoConnection) }

        val vm = viewModel(repo)
        advanceUntilIdle()
        assertEquals(MapRouteUiState.Error(RouteError.NoConnection), vm.uiState.value)

        repo.placeResult = Outcome.Success(originPlace)
        repo.routeResults += Outcome.Success(routeFrom(originPlace, destination))
        vm.load()
        assertEquals(MapRouteUiState.Loading, vm.uiState.value)
        advanceUntilIdle()

        assertTrue(vm.uiState.value is MapRouteUiState.Content)
    }

    @Test
    fun `erro ao buscar a rota tambem vira Error`() = runTest(dispatcher) {
        val repo = FakeRepository().apply {
            placeResult = Outcome.Success(originPlace)
            routeResults += Outcome.Failure(RouteError.Unreachable("sem caminho"))
        }

        val vm = viewModel(repo)
        advanceUntilIdle()

        assertEquals(MapRouteUiState.Error(RouteError.Unreachable("sem caminho")), vm.uiState.value)
    }

    @Test
    fun `simulacao recalcula a cada beacon e termina ao chegar no destino`() = runTest(dispatcher) {
        val repo = FakeRepository().apply {
            placeResult = Outcome.Success(originPlace)
            routeResults += Outcome.Success(routeFrom(originPlace, corridor, destination)) // rota inicial
            routeResults += Outcome.Success(routeFrom(originPlace, corridor, destination)) // recálculo no beacon 1
            routeResults += Outcome.Success(routeFrom(corridor, destination)) // recálculo no beacon 2
        }
        val vm = viewModel(repo)
        advanceUntilIdle()

        vm.startSimulation()
        advanceUntilIdle()

        val state = vm.uiState.value as MapRouteUiState.Content
        assertTrue(state.hasArrived)
        assertFalse(state.isSimulating)
        assertEquals("bilheteria", state.currentPlaceLabel)
        // 1 pedido inicial + recálculo nos 2 primeiros beacons; no destino NÃO chama a API
        assertEquals(3, repo.routeRequests.size)
        assertEquals(corridor.position, repo.routeRequests.last().first)
    }

    @Test
    fun `falha ao recalcular pausa a simulacao e mantem a rota na tela`() = runTest(dispatcher) {
        val repo = FakeRepository().apply {
            placeResult = Outcome.Success(originPlace)
            routeResults += Outcome.Success(routeFrom(originPlace, corridor, destination))
            routeResults += Outcome.Failure(RouteError.Timeout)
        }
        val vm = viewModel(repo)
        advanceUntilIdle()

        vm.startSimulation()
        advanceUntilIdle()

        val state = vm.uiState.value as MapRouteUiState.Content
        assertFalse(state.isSimulating)
        assertFalse(state.hasArrived)
        assertEquals(RouteError.Timeout, state.recalculationError)
        assertEquals(3, state.route.steps.size)
    }

    @Test
    fun `pausar interrompe os beacons`() = runTest(dispatcher) {
        val repo = FakeRepository().apply {
            placeResult = Outcome.Success(originPlace)
            repeat(10) { routeResults += Outcome.Success(routeFrom(originPlace, corridor, destination)) }
        }
        val vm = viewModel(repo)
        advanceUntilIdle()

        vm.startSimulation()
        dispatcher.scheduler.runCurrent() // processa só o primeiro beacon
        vm.pauseSimulation()
        advanceUntilIdle()

        val state = vm.uiState.value as MapRouteUiState.Content
        assertFalse(state.isSimulating)
        assertFalse(state.hasArrived)
        assertNull(state.recalculationError)
        assertNotNull(state.currentPlaceLabel)
    }
}
