package com.example.wearosbarcelona.data.repository

import com.example.wearosbarcelona.data.model.LineInfo
import com.example.wearosbarcelona.data.model.StationArrivals
import com.example.wearosbarcelona.data.model.TrainArrival
import com.example.wearosbarcelona.data.model.TransportType
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockTransportRepository : TransportRepository {

    private val metroLines = mapOf(
        "L1" to LineInfo("L1", "L1", "#E51C24") // Rojo L1
    )

    private val fgcLines = mapOf(
        "S1" to LineInfo("S1", "S1", "#FF9800"), // Naranja
        "S2" to LineInfo("S2", "S2", "#E91E63"), // Rosa
        "L6" to LineInfo("L6", "L6", "#3F51B5"), // Azul Oscuro
        "L7" to LineInfo("L7", "L7", "#795548")  // Marrón
    )

    override suspend fun getMetroArrivals(stationId: String): StationArrivals {
        delay(600)
        
        val (isClosed, nextTrainInfo) = ScheduleHelper.checkServiceStatus(TransportType.METRO, stationId)
        val stationName = when (stationId) {
            "espanya" -> "Espanya"
            "universitat" -> "Universitat"
            else -> "Estación Metro L1"
        }

        if (isClosed) {
            return StationArrivals(
                stationId = stationId,
                stationName = stationName,
                transportType = TransportType.METRO,
                arrivals = emptyList(),
                isClosed = true,
                nextTrainInfo = nextTrainInfo
            )
        }

        val arrivals = mutableListOf<TrainArrival>()
        when (stationId) {
            "espanya" -> {
                arrivals.add(TrainArrival(metroLines["L1"]!!, "Fondo", Random.nextInt(1, 4), "Vía 1"))
                arrivals.add(TrainArrival(metroLines["L1"]!!, "Hospital de Bellvitge", Random.nextInt(3, 6), "Vía 2"))
            }
            "universitat" -> {
                arrivals.add(TrainArrival(metroLines["L1"]!!, "Fondo", Random.nextInt(2, 4), "Vía 1"))
                arrivals.add(TrainArrival(metroLines["L1"]!!, "Hospital de Bellvitge", Random.nextInt(1, 3), "Vía 2"))
            }
            else -> {
                arrivals.add(TrainArrival(metroLines["L1"]!!, "Fondo", 3, "Vía 1"))
            }
        }

        return StationArrivals(
            stationId = stationId,
            stationName = stationName,
            transportType = TransportType.METRO,
            arrivals = arrivals.sortedBy { it.minutesLeft }.take(4)
        )
    }

    override suspend fun getFgcArrivals(stationId: String): StationArrivals {
        delay(600)

        val (isClosed, nextTrainInfo) = ScheduleHelper.checkServiceStatus(TransportType.TRAIN_FGC, stationId)
        val stationName = when (stationId) {
            "pl_catalunya" -> "Barcelona - Pl. Catalunya"
            "pr" -> "Provença"
            "gr" -> "Gràcia"
            "mu" -> "Muntaner"
            "sr" -> "Sarrià"
            "lp" -> "Les Planes"
            "lf" -> "La Floresta"
            "vd" -> "Valldoreix"
            "sant_cugat" -> "Sant Cugat Centre"
            "ms" -> "Mira-sol"
            "hg" -> "Hospital General"
            "rb" -> "Rubí"
            "fn" -> "Les Fonts"
            "tr" -> "Terrassa Rambla"
            "vu" -> "Vallparadís Universitat"
            "tn" -> "Terrassa Estació del Nord"
            "na" -> "Terrassa Nacions Unides"
            else -> "Estación FGC"
        }

        if (isClosed) {
            return StationArrivals(
                stationId = stationId,
                stationName = stationName,
                transportType = TransportType.TRAIN_FGC,
                arrivals = emptyList(),
                isClosed = true,
                nextTrainInfo = nextTrainInfo
            )
        }

        val arrivals = mutableListOf<TrainArrival>()
        val lineS1 = fgcLines["S1"]!!
        when (stationId) {
            "pl_catalunya" -> {
                arrivals.add(TrainArrival(lineS1, "Terrassa Nacions U.", Random.nextInt(2, 6), "Vía 1"))
                arrivals.add(TrainArrival(lineS1, "Terrassa Nacions U.", Random.nextInt(7, 12), "Vía 1"))
            }
            "na" -> {
                arrivals.add(TrainArrival(lineS1, "Barcelona - Pl. Catalunya", Random.nextInt(2, 6), "Vía 2"))
                arrivals.add(TrainArrival(lineS1, "Barcelona - Pl. Catalunya", Random.nextInt(8, 14), "Vía 2"))
            }
            else -> {
                arrivals.add(TrainArrival(lineS1, "Terrassa Nacions U.", Random.nextInt(1, 6), "Vía 1"))
                arrivals.add(TrainArrival(lineS1, "Barcelona - Pl. Catalunya", Random.nextInt(2, 7), "Vía 2"))
                arrivals.add(TrainArrival(lineS1, "Terrassa Nacions U.", Random.nextInt(8, 14), "Vía 1"))
                arrivals.add(TrainArrival(lineS1, "Barcelona - Pl. Catalunya", Random.nextInt(9, 15), "Vía 2"))
            }
        }

        return StationArrivals(
            stationId = stationId,
            stationName = stationName,
            transportType = TransportType.TRAIN_FGC,
            arrivals = arrivals.sortedBy { it.minutesLeft }.take(4)
        )
    }

    override suspend fun getAvailableStations(type: TransportType): List<Pair<String, String>> {
        return when (type) {
            TransportType.METRO -> listOf(
                "espanya" to "Espanya L1",
                "universitat" to "Universitat L1"
            )
            TransportType.TRAIN_FGC -> listOf(
                "pl_catalunya" to "Barcelona - Pl. Catalunya",
                "pr" to "Provença",
                "gr" to "Gràcia",
                "mu" to "Muntaner",
                "sr" to "Sarrià",
                "lp" to "Les Planes",
                "lf" to "La Floresta",
                "vd" to "Valldoreix",
                "sant_cugat" to "Sant Cugat Centre",
                "ms" to "Mira-sol",
                "hg" to "Hospital General",
                "rb" to "Rubí",
                "fn" to "Les Fonts",
                "tr" to "Terrassa Rambla",
                "vu" to "Vallparadís Universitat",
                "tn" to "Terrassa Estació del Nord",
                "na" to "Terrassa Nacions Unides"
            )
        }
    }
}
