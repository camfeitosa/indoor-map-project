package com.clickbus.challenge.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato HTTP da ClickBusRouterAPI. O Retrofit gera a implementação em tempo de execução.
 * `suspend` = chamada assíncrona integrada às coroutines; em erro HTTP lança HttpException.
 */
interface RouterApi {

    /** Rota até um ponto exato. Ex.: GET api/routes?x=51.81&y=75.81&to=TIETE-TER-SERVICO-031 */
    @GET("api/routes")
    suspend fun routeToPoint(
        @Query("x") x: Double,
        @Query("y") y: Double,
        @Query("to") destinationId: String,
    ): RouteResultDto

    /** Rota até o candidato mais próximo pelo caminho. Ex.: GET api/routes?x=..&y=..&q=bilheteria */
    @GET("api/routes")
    suspend fun routeToNearest(
        @Query("x") x: Double,
        @Query("y") y: Double,
        @Query("q") destinationQuery: String,
    ): RouteResultDto

    /** Um ponto pelo id. Ex.: GET api/points/TIETE-TER-ENTRADA-002 (404 se não existir). */
    @GET("api/points/{id}")
    suspend fun point(@Path("id") id: String): PointDto

    @GET("api/points")
    suspend fun points(
        @Query("type") type: String? = null,
        @Query("search") search: String? = null,
    ): List<PointDto>
}
