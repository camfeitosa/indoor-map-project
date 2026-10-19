package com.clickbus.challenge.data.remote

import kotlinx.serialization.Serializable

/** Corpo de erro padrão (RFC 7807) devolvido pela API em 4xx. `detail` vem em português. */
@Serializable
data class ProblemDetailsDto(
    val title: String? = null,
    val detail: String? = null,
    val status: Int? = null,
)
