package com.clickbus.challenge.model

/** Sample upcoming trips shown in the app's "Suas próximas viagens" carousel. */
val upcomingTrips = listOf(
    TripSummary(
        day = "22",
        month = "AGO",
        originCity = "São Paulo",
        originState = "SP",
        destinationCity = "Curitiba",
        destinationState = "PR",
        weekday = "Sexta-feira",
        time = "10:30",
        company = "Expresso do Sul",
        platform = "12",
        seat = "21",
    ),
    TripSummary(
        day = "05",
        month = "SET",
        originCity = "Curitiba",
        originState = "PR",
        destinationCity = "Florianópolis",
        destinationState = "SC",
        weekday = "Sexta-feira",
        time = "22:15",
        company = "Catarinense",
        platform = "07",
        seat = "14",
    ),
)
