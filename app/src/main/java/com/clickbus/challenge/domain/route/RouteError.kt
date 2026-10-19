package com.clickbus.challenge.domain.route

/** Resultado de uma operação que pode falhar sem lançar exceção para a UI. */
sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>
    data class Failure(val error: RouteError) : Outcome<Nothing>
}

/**
 * Tudo que pode dar errado ao pedir uma rota. `serverMessage` é o `detail` em português
 * enviado pela API (ProblemDetails), útil para mostrar ao usuário quando existir.
 */
sealed interface RouteError {
    /** Sem internet, API desligada ou endereço inalcançável. */
    data object NoConnection : RouteError

    /** A API não respondeu dentro do tempo limite. */
    data object Timeout : RouteError

    /** 404: destino inexistente. */
    data class DestinationNotFound(val serverMessage: String?) : RouteError

    /** 422: origem fora do mapa ou destino sem caminho até ele. */
    data class Unreachable(val serverMessage: String?) : RouteError

    /** 400: pedido malformado (bug do app, não do usuário). */
    data class InvalidRequest(val serverMessage: String?) : RouteError

    /** 5xx: falha interna da API. */
    data object Server : RouteError

    /** A resposta chegou, mas não tem o formato esperado do contrato. */
    data object InvalidResponse : RouteError

    data class Unknown(val cause: Throwable) : RouteError
}
