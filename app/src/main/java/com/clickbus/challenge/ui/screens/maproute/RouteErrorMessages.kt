package com.clickbus.challenge.ui.screens.maproute

import androidx.annotation.StringRes
import com.clickbus.challenge.R
import com.clickbus.challenge.domain.route.RouteError

/** Traduz o erro de domínio em texto para o usuário. O `when` exaustivo obriga a tratar todo erro novo. */
@StringRes
fun RouteError.messageRes(): Int = when (this) {
    RouteError.NoConnection -> R.string.route_error_no_connection
    RouteError.Timeout -> R.string.route_error_timeout
    is RouteError.DestinationNotFound -> R.string.route_error_destination_not_found
    is RouteError.Unreachable -> R.string.route_error_unreachable
    is RouteError.InvalidRequest -> R.string.route_error_invalid_request
    RouteError.Server -> R.string.route_error_server
    RouteError.InvalidResponse -> R.string.route_error_invalid_response
    is RouteError.Unknown -> R.string.route_error_unknown
}
