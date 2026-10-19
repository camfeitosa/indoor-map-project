package com.clickbus.challenge.data.route

import com.clickbus.challenge.data.remote.ProblemDetailsDto
import com.clickbus.challenge.domain.route.RouteError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

private const val HTTP_BAD_REQUEST = 400
private const val HTTP_NOT_FOUND = 404
private const val HTTP_UNPROCESSABLE = 422
private const val HTTP_SERVER_ERROR = 500

private val errorJson = Json { ignoreUnknownKeys = true }

/**
 * A ordem dos `is` importa: SocketTimeoutException É UM IOException, então precisa vir antes.
 * CancellationException nunca chega aqui (é relançada pelo repositório).
 */
internal fun Throwable.toRouteError(): RouteError = when (this) {
    is HttpException -> toRouteError()
    is SocketTimeoutException -> RouteError.Timeout
    is IOException -> RouteError.NoConnection
    is SerializationException, is IllegalArgumentException -> RouteError.InvalidResponse
    else -> RouteError.Unknown(this)
}

private fun HttpException.toRouteError(): RouteError {
    val message = readProblemDetail()
    return when {
        code() == HTTP_NOT_FOUND -> RouteError.DestinationNotFound(message)
        code() == HTTP_UNPROCESSABLE -> RouteError.Unreachable(message)
        code() == HTTP_BAD_REQUEST -> RouteError.InvalidRequest(message)
        code() >= HTTP_SERVER_ERROR -> RouteError.Server
        else -> RouteError.Unknown(this)
    }
}

/** Lê o `detail` do corpo de erro; se o corpo não for um ProblemDetails válido, devolve null. */
private fun HttpException.readProblemDetail(): String? = try {
    response()?.errorBody()?.string()
        ?.let { errorJson.decodeFromString<ProblemDetailsDto>(it) }
        ?.detail
} catch (_: SerializationException) {
    null
} catch (_: IOException) {
    null
}
