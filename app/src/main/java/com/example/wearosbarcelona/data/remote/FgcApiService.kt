package com.example.wearosbarcelona.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FgcApiService {

    @GET("api/explore/v2.1/catalog/datasets/posicionament-dels-trens/records")
    suspend fun getTrainPositions(
        @Query("limit") limit: Int = 40,
        @Query("select") select: String? = null,
        @Query("where") where: String? = null
    ): FgcRecordsResponse

    @GET("api/explore/v2.1/catalog/datasets/codigo-estaciones/records")
    suspend fun getStationCodes(
        @Query("limit") limit: Int = 100
    ): FgcStationsResponse
}

data class FgcRecordsResponse(
    val total_count: Int?,
    val results: List<FgcTrainRecord>?
)

data class FgcTrainRecord(
    val id: String?,
    val lin: String?,       // Línea (ej. "S1", "S2")
    val desti: String?,     // Destino (ej. "Barcelona - Pl. Catalunya")
    val properes_parades: String?, // Próximas paradas (JSON string)
    val estacionat_a: String?      // Estación actual si está parado
)

data class FgcStationsResponse(
    val results: List<FgcStationRecord>?
)

data class FgcStationRecord(
    val inicials: String?,
    val nom_estacio: String?,
    val nom_linia: String?
)
