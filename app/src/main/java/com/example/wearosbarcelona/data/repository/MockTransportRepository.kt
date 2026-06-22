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

    private val staticMetroStationNames = mapOf(
        "111" to "Hospital de Bellvitge",
        "112" to "Bellvitge",
        "113" to "Av. Carrilet",
        "114" to "Rambla Just Oliveras",
        "115" to "Can Serra",
        "116" to "Florida",
        "117" to "Torrassa",
        "118" to "Santa Eulàlia",
        "119" to "Mercat Nou",
        "120" to "Plaça de Sants",
        "121" to "Hostafrancs",
        "122" to "Espanya",
        "123" to "Rocafort",
        "124" to "Urgell",
        "125" to "Universitat",
        "126" to "Catalunya",
        "127" to "Urquinaona",
        "128" to "Arc de Triomf",
        "129" to "Marina",
        "130" to "Glòries",
        "131" to "Clot",
        "132" to "Navas",
        "133" to "La Sagrera",
        "134" to "Fabra i Puig",
        "135" to "Sant Andreu",
        "136" to "Torras i Bages",
        "137" to "Trinitat Vella",
        "138" to "Baró de Viver",
        "139" to "Santa Coloma",
        "140" to "Fondo"
    )

    override suspend fun getMetroArrivals(stationId: String): StationArrivals {
        delay(600)
        
        val (isClosed, nextTrainInfo) = ScheduleHelper.checkServiceStatus(TransportType.METRO, stationId)
        val stationName = staticMetroStationNames[stationId] ?: "Estación Metro L1"

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
        val lineL1 = metroLines["L1"]!!

        // Generate simulated future timestamps (countdown style)
        val currentMs = System.currentTimeMillis()
        val nextTrainSecs1 = Random.nextInt(45, 180)
        val nextTrainSecs2 = Random.nextInt(90, 300)
        
        if (stationId == "111") {
            // Only towards Fondo
            val formattedTime = "${nextTrainSecs1 / 60}m ${nextTrainSecs1 % 60}s"
            arrivals.add(
                TrainArrival(
                    line = lineL1,
                    destination = "Fondo",
                    minutesLeft = nextTrainSecs1 / 60,
                    secondsLeft = nextTrainSecs1 % 60,
                    platform = "Vía 1",
                    timeLeftFormatted = formattedTime,
                    expectedArrivalEpochMs = currentMs + (nextTrainSecs1 * 1000L)
                )
            )
        } else if (stationId == "140") {
            // Only towards Hospital de Bellvitge
            val formattedTime = "${nextTrainSecs2 / 60}m ${nextTrainSecs2 % 60}s"
            arrivals.add(
                TrainArrival(
                    line = lineL1,
                    destination = "Hospital de Bellvitge",
                    minutesLeft = nextTrainSecs2 / 60,
                    secondsLeft = nextTrainSecs2 % 60,
                    platform = "Vía 2",
                    timeLeftFormatted = formattedTime,
                    expectedArrivalEpochMs = currentMs + (nextTrainSecs2 * 1000L)
                )
            )
        } else {
            // Both directions: Fondo (Vía 1) and Hospital de Bellvitge (Vía 2)
            val formattedTime1 = "${nextTrainSecs1 / 60}m ${nextTrainSecs1 % 60}s"
            arrivals.add(
                TrainArrival(
                    line = lineL1,
                    destination = "Fondo",
                    minutesLeft = nextTrainSecs1 / 60,
                    secondsLeft = nextTrainSecs1 % 60,
                    platform = "Vía 1",
                    timeLeftFormatted = formattedTime1,
                    expectedArrivalEpochMs = currentMs + (nextTrainSecs1 * 1000L)
                )
            )
            
            val formattedTime2 = "${nextTrainSecs2 / 60}m ${nextTrainSecs2 % 60}s"
            arrivals.add(
                TrainArrival(
                    line = lineL1,
                    destination = "Hospital de Bellvitge",
                    minutesLeft = nextTrainSecs2 / 60,
                    secondsLeft = nextTrainSecs2 % 60,
                    platform = "Vía 2",
                    timeLeftFormatted = formattedTime2,
                    expectedArrivalEpochMs = currentMs + (nextTrainSecs2 * 1000L)
                )
            )
        }

        return StationArrivals(
            stationId = stationId,
            stationName = stationName,
            transportType = TransportType.METRO,
            arrivals = arrivals
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
            TransportType.METRO -> STATIC_METRO_STATIONS
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
