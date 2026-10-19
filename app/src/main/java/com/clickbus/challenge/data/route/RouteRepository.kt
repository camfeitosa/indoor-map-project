package com.clickbus.challenge.data.route

import com.clickbus.challenge.data.remote.RouterApi
import com.clickbus.challenge.domain.route.MapPlace
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.domain.route.Outcome
import com.clickbus.challenge.domain.route.Route
import kotlin.coroutines.cancellation.CancellationException

/** Fonte de rotas do app. A UI depende desta interface, não do Retrofit. */
interface RouteRepository {
    /** Rota até um ponto exato, pelo id (ex.: "TIETE-TER-SERVICO-031"). */
    suspend fun routeToPlace(from: MapPosition, destinationId: String): Outcome<Route>

    /** Rota até o candidato mais próximo pelo caminho (ex.: "bilheteria", "sanitarios"). */
    suspend fun routeToNearest(from: MapPosition, query: String): Outcome<Route>

    /** Um ponto do mapa pelo id; usado para descobrir as coordenadas de uma origem conhecida. */
    suspend fun placeById(id: String): Outcome<MapPlace>
}

class RemoteRouteRepository(private val api: RouterApi) : RouteRepository {

    override suspend fun routeToPlace(from: MapPosition, destinationId: String): Outcome<Route> =
        call { api.routeToPoint(from.x, from.y, destinationId).toDomain() }

    override suspend fun routeToNearest(from: MapPosition, query: String): Outcome<Route> =
        call { api.routeToNearest(from.x, from.y, query).toDomain() }

    override suspend fun placeById(id: String): Outcome<MapPlace> =
        call { api.point(id).toDomain() }

    private suspend fun <T> call(block: suspend () -> T): Outcome<T> = try {
        Outcome.Success(block())
    } catch (e: CancellationException) {
        throw e // cancelamento de coroutine NÃO é erro de negócio: precisa propagar
    } catch (e: Exception) {
        Outcome.Failure(e.toRouteError())
    }
}
