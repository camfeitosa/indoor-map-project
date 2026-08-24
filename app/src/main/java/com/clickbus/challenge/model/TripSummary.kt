package com.clickbus.challenge.model

/** A booked trip shown on the app home's "Suas próximas viagens" card. */
data class TripSummary(
    val day: String,
    val month: String,
    val originCity: String,
    val originState: String,
    val destinationCity: String,
    val destinationState: String,
    val weekday: String,
    val time: String,
    val company: String,
    val platform: String,
    val seat: String,
)
