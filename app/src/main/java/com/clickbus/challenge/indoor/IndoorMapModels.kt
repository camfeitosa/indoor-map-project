package com.clickbus.challenge.indoor

data class IndoorMapPoint(
    val id: String,
    val label: String,
    val floor: String,
    val type: String,
    val xPercent: Float,
    val yPercent: Float,
)

data class IndoorMapConnection(
    val fromId: String,
    val toId: String,
)

data class IndoorMapData(
    val points: List<IndoorMapPoint>,
    val connections: List<IndoorMapConnection>,
) {
    val pointsById: Map<String, IndoorMapPoint> = points.associateBy { it.id }
}

data class IndoorRoute(
    val points: List<IndoorMapPoint>,
    val distance: Float,
) {
    val isAvailable: Boolean get() = points.isNotEmpty()
}
