package com.example.wearosbarcelona.data.repository

import com.example.wearosbarcelona.data.model.StationArrivals
import com.example.wearosbarcelona.data.model.TransportType

val STATIC_METRO_STATIONS = listOf(
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

val METRO_STATION_COORDINATES = mapOf(
    "111" to Pair(41.344677, 2.107242), // Bellvitge / Hospital de Bellvitge
    "112" to Pair(41.350974, 2.110918), // Bellvitge
    "113" to Pair(41.358522, 2.102645), // Av. Carrilet
    "114" to Pair(41.364090, 2.099749), // Rambla Just Oliveras
    "115" to Pair(41.367693, 2.102755), // Can Serra
    "116" to Pair(41.368316, 2.110027), // Florida
    "117" to Pair(41.368261, 2.116089), // Torrassa
    "118" to Pair(41.368816, 2.128617), // Santa Eulàlia
    "119" to Pair(41.373003, 2.133536), // Mercat Nou
    "120" to Pair(41.375532, 2.135576), // Plaça de Sants
    "121" to Pair(41.375254, 2.143291), // Hostafrancs
    "122" to Pair(41.375459, 2.149387), // Espanya
    "123" to Pair(41.379232, 2.154562), // Rocafort
    "124" to Pair(41.382487, 2.158891), // Urgell
    "125" to Pair(41.385588, 2.164059), // Universitat
    "126" to Pair(41.387713, 2.169717), // Catalunya
    "127" to Pair(41.388847, 2.173668), // Urquinaona
    "128" to Pair(41.392337, 2.181165), // Arc de Triomf
    "129" to Pair(41.394725, 2.185801), // Marina
    "130" to Pair(41.402277, 2.187537), // Glòries
    "131" to Pair(41.410512, 2.186978), // Clot
    "132" to Pair(41.416187, 2.187057), // Navas
    "133" to Pair(41.420696, 2.186811), // La Sagrera
    "134" to Pair(41.429633, 2.183661), // Fabra i Puig
    "135" to Pair(41.436610, 2.191188), // Sant Andreu
    "136" to Pair(41.443225, 2.190671), // Torras i Bages
    "137" to Pair(41.448956, 2.193837), // Trinitat Vella
    "138" to Pair(41.449936, 2.199563), // Baró de Viver
    "139" to Pair(41.451067, 2.207969), // Santa Coloma
    "140" to Pair(41.451687, 2.218538)  // Fondo
)

val FGC_STATION_COORDINATES = mapOf(
    "pl_catalunya" to Pair(41.3869, 2.1700),
    "pr" to Pair(41.3934, 2.1581),
    "gr" to Pair(41.4014, 2.1524),
    "mu" to Pair(41.4005, 2.1408),
    "sr" to Pair(41.3995, 2.1227),
    "lp" to Pair(41.4278, 2.0944),
    "lf" to Pair(41.4422, 2.0744),
    "vd" to Pair(41.4639, 2.0672),
    "sant_cugat" to Pair(41.4697, 2.0833),
    "ms" to Pair(41.4722, 2.0578),
    "hg" to Pair(41.4764, 2.0417),
    "rb" to Pair(41.4903, 2.0322),
    "fn" to Pair(41.5164, 2.0392),
    "tr" to Pair(41.5606, 2.0108),
    "vu" to Pair(41.5647, 2.0225),
    "tn" to Pair(41.5714, 2.0169),
    "na" to Pair(41.5878, 2.0083)
)

interface TransportRepository {
    suspend fun getMetroArrivals(stationId: String): StationArrivals
    suspend fun getFgcArrivals(stationId: String): StationArrivals
    suspend fun getAvailableStations(type: TransportType): List<Pair<String, String>> // Pair of (StationId, StationName)
}
