package com.clickbus.challenge.indoor

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class BeaconDetection(
    val beaconId: String,
    val pointId: String,
    val pointLabel: String,
    val sequence: Int,
)

/**
 * Simulates proximity beacons by detecting each node in a calculated route in sequence.
 * The UI consumes the same kind of point update that a future BLE implementation will provide.
 */
class BeaconSimulator(
    private val path: List<IndoorMapPoint>,
) {
    private var nextIndex = 0

    fun detections(intervalMillis: Long = 1_500L): Flow<BeaconDetection> = flow {
        while (nextIndex < path.size) {
            val point = path[nextIndex]
            emit(
                BeaconDetection(
                    beaconId = "SIM-${point.id}",
                    pointId = point.id,
                    pointLabel = point.label,
                    sequence = nextIndex,
                ),
            )
            nextIndex++
            if (nextIndex < path.size) delay(intervalMillis)
        }
    }

    fun reset() {
        nextIndex = 0
    }
}
