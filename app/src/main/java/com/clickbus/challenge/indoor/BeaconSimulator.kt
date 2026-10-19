package com.clickbus.challenge.indoor

import com.clickbus.challenge.domain.route.MapPlace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class BeaconDetection(
    val beaconId: String,
    val place: MapPlace,
    val sequence: Int,
)

/**
 * Simulates proximity beacons by detecting each node in a calculated route in sequence.
 * The UI consumes the same kind of point update that a future BLE implementation will provide.
 */
class BeaconSimulator(
    private val path: List<MapPlace>,
) {
    private var nextIndex = 0

    fun detections(intervalMillis: Long = DEFAULT_INTERVAL_MILLIS): Flow<BeaconDetection> = flow {
        while (nextIndex < path.size) {
            val place = path[nextIndex]
            emit(BeaconDetection(beaconId = "SIM-${place.id}", place = place, sequence = nextIndex))
            nextIndex++
            if (nextIndex < path.size) delay(intervalMillis)
        }
    }

    fun reset() {
        nextIndex = 0
    }

    companion object {
        const val DEFAULT_INTERVAL_MILLIS = 1_500L
    }
}
