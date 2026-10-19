package com.clickbus.challenge.data.route

import com.clickbus.challenge.data.remote.RouterApiFactory

/** Montagem manual das dependências (um único cliente HTTP para o app todo). Troque por Hilt se o app crescer. */
object RouteModule {
    val repository: RouteRepository by lazy { RemoteRouteRepository(RouterApiFactory.create()) }
}
