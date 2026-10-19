package com.clickbus.challenge.data.route

import com.clickbus.challenge.data.remote.RouterApiFactory
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.domain.route.Outcome
import com.clickbus.challenge.domain.route.RouteError
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteRouteRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: RouteRepository
    private val origin = MapPosition(51.81, 75.81)

    @Before
    fun setUp() {
        server = MockWebServer().also { it.start() }
        repository = RemoteRouteRepository(RouterApiFactory.create(server.url("/").toString()))
    }

    @After
    fun tearDown() {
        runCatching { server.shutdown() }
    }

    private fun problem(status: Int, title: String, detail: String) = MockResponse()
        .setResponseCode(status)
        .setHeader("Content-Type", "application/problem+json")
        .setBody("""{"title":"$title","status":$status,"detail":"$detail"}""")

    private fun failureOf(outcome: Outcome<*>): RouteError = (outcome as Outcome.Failure).error

    @Test
    fun `sucesso mapeia o contrato da API para o dominio`() = runTest {
        server.enqueue(MockResponse().setBody(SUCCESS_JSON))

        val outcome = repository.routeToNearest(origin, "bilheteria")

        val route = (outcome as Outcome.Success).value
        assertEquals("bilheteria 6", route.destination.label)
        assertEquals(117.9, route.distanceMeters, 0.0)
        assertEquals(91, route.estimatedSeconds)
        assertEquals(2, route.steps.size)
        assertEquals("Você está em Acesso metro", route.steps.first().instruction)
        // contrato: polyline tem steps + 2 pontos
        assertEquals(route.steps.size + 2, route.path.size)
    }

    @Test
    fun `envia x, y e q na query string`() = runTest {
        server.enqueue(MockResponse().setBody(SUCCESS_JSON))

        repository.routeToNearest(origin, "bilheteria")

        val path = server.takeRequest().path.orEmpty()
        assertTrue(path, path.startsWith("/api/routes?"))
        assertTrue(path, "x=51.81" in path && "y=75.81" in path && "q=bilheteria" in path)
    }

    @Test
    fun `routeToPlace usa o parametro to`() = runTest {
        server.enqueue(MockResponse().setBody(SUCCESS_JSON))

        repository.routeToPlace(origin, "TIETE-TER-SERVICO-031")

        assertTrue(server.takeRequest().path.orEmpty().contains("to=TIETE-TER-SERVICO-031"))
    }

    @Test
    fun `404 vira DestinationNotFound com a mensagem do servidor`() = runTest {
        server.enqueue(problem(404, "Destino não encontrado", "Nenhum ponto encontrado"))

        val error = failureOf(repository.routeToNearest(origin, "xyz"))

        assertEquals(RouteError.DestinationNotFound("Nenhum ponto encontrado"), error)
    }

    @Test
    fun `422 vira Unreachable`() = runTest {
        server.enqueue(problem(422, "Origem fora do mapa", "Origem longe dos corredores"))

        val error = failureOf(repository.routeToNearest(origin, "bilheteria"))

        assertEquals(RouteError.Unreachable("Origem longe dos corredores"), error)
    }

    @Test
    fun `400 vira InvalidRequest`() = runTest {
        server.enqueue(problem(400, "Requisição inválida", "Informe um destino"))

        assertEquals(RouteError.InvalidRequest("Informe um destino"), failureOf(repository.routeToNearest(origin, "")))
    }

    @Test
    fun `500 vira Server mesmo com corpo que nao e ProblemDetails`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("<html>boom</html>"))

        assertEquals(RouteError.Server, failureOf(repository.routeToNearest(origin, "bilheteria")))
    }

    @Test
    fun `erro 4xx com corpo ilegivel nao quebra e fica sem mensagem`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404).setBody("nao e json"))

        assertEquals(RouteError.DestinationNotFound(null), failureOf(repository.routeToNearest(origin, "x")))
    }

    @Test
    fun `200 com JSON fora do contrato vira InvalidResponse`() = runTest {
        server.enqueue(MockResponse().setBody("""{"foo":"bar"}"""))

        assertEquals(RouteError.InvalidResponse, failureOf(repository.routeToNearest(origin, "bilheteria")))
    }

    @Test
    fun `servidor desligado vira NoConnection`() = runTest {
        server.shutdown()

        assertEquals(RouteError.NoConnection, failureOf(repository.routeToNearest(origin, "bilheteria")))
    }

    @Test
    fun `servidor que nao responde dentro do prazo vira Timeout`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        val slow = RemoteRouteRepository(
            RouterApiFactory.create(server.url("/").toString(), timeoutSeconds = 1),
        )

        val error = failureOf(slow.routeToNearest(origin, "bilheteria"))

        assertEquals(RouteError.Timeout, error)
    }

    private companion object {
        // Recorte da resposta real de GET /api/routes?x=51.81&y=75.81&q=bilheteria (2 steps em vez de 8).
        const val SUCCESS_JSON = """
{"origin":{"x":51.81,"y":75.81},
 "entryPoint":{"position":{"x":51.81,"y":75.81},"distanceFromOrigin":0,
   "segmentFromId":"TIETE-TER-ENTRADA-002","segmentToId":"TIETE-TER-CORREDOR-047"},
 "destination":{"id":"TIETE-TER-SERVICO-008","label":"bilheteria 6","type":"servico","floor":"terreo","x":41.65,"y":29.48},
 "totalDistance":58.97,"totalDistanceMeters":117.9,"estimatedSeconds":91,
 "steps":[
  {"order":1,"point":{"id":"TIETE-TER-ENTRADA-002","label":"Acesso metro","type":"entrada","floor":"terreo","x":51.81,"y":75.81},
   "distanceFromPrevious":0,"cumulativeDistance":0,"instruction":"Você está em Acesso metro"},
  {"order":2,"point":{"id":"TIETE-TER-SERVICO-008","label":"bilheteria 6","type":"servico","floor":"terreo","x":41.65,"y":29.48},
   "distanceFromPrevious":58.97,"cumulativeDistance":58.97,"instruction":"Você chegou ao destino."}],
 "polyline":[{"x":51.81,"y":75.81},{"x":51.81,"y":75.81},{"x":51.81,"y":75.81},{"x":41.65,"y":29.48}]}
"""
    }
}
