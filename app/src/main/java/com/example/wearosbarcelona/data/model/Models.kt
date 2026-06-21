package com.example.wearosbarcelona.data.model

enum class TransportType {
    METRO,
    TRAIN_FGC
}

data class LineInfo(
    val id: String,
    val name: String,
    val colorHex: String
)

data class TrainArrival(
    val line: LineInfo,
    val destination: String,
    val minutesLeft: Int,
    val platform: String? = null,
    val secondsLeft: Int = 0,
    val timeLeftFormatted: String? = null,
    val expectedArrivalEpochMs: Long? = null
)

data class StationArrivals(
    val stationId: String,
    val stationName: String,
    val transportType: TransportType,
    val arrivals: List<TrainArrival>,
    val timestamp: Long = System.currentTimeMillis(),
    val isClosed: Boolean = false,
    val nextTrainInfo: String? = null
)
