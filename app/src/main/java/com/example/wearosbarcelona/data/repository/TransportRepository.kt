package com.example.wearosbarcelona.data.repository

import com.example.wearosbarcelona.data.model.StationArrivals
import com.example.wearosbarcelona.data.model.TransportType

interface TransportRepository {
    suspend fun getMetroArrivals(stationId: String): StationArrivals
    suspend fun getFgcArrivals(stationId: String): StationArrivals
    suspend fun getAvailableStations(type: TransportType): List<Pair<String, String>> // Pair of (StationId, StationName)
}
