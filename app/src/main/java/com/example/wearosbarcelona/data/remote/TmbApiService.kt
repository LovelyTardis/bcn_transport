package com.example.wearosbarcelona.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmbApiService {

    @GET("v1/itransit/metro/estacions")
    suspend fun getMetroPredictions(
        @Query("estacions") stationId: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): TmbItransitResponse

    @GET("v1/transit/linies/metro")
    suspend fun getMetroLines(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): TmbLinesResponse

    @GET("v1/transit/linies/metro/{lineId}/estacions")
    suspend fun getMetroLineStations(
        @Path("lineId") lineId: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): TmbStationsResponse
}

data class TmbItransitResponse(
    val timestamp: Long,
    val linies: List<TmbItransitLine>?
)

data class TmbItransitLine(
    val codi_linia: Int?,
    val nom_linia: String?,
    val nom_familia: String?,
    val codi_familia: Int?,
    val color_linia: String?,
    val estacions: List<TmbItransitStation>?
)

data class TmbItransitStation(
    val codi_via: Int?,
    val id_sentit: Int?,
    val codi_estacio: Int?,
    val linies_trajectes: List<TmbItransitLiniesTrajectes>?
)

data class TmbItransitLiniesTrajectes(
    val codi_linia: Int?,
    val nom_linia: String?,
    val color_linia: String?,
    val codi_trajecte: String?,
    val desti_trajecte: String?,
    val propers_trens: List<TmbItransitPropersTrens>?
)

data class TmbItransitPropersTrens(
    val codi_servei: String?,
    val temps_arribada: Long?
)

data class TmbMetroResponse(
    val status: String?,
    val data: TmbMetroData?
)

data class TmbMetroData(
    val previsions: List<TmbPrevision>?
)

data class TmbPrevision(
    val linia: String?,
    val origen: String?,
    val desti: String?,
    val minut: String?, // TMB api returns prediction time in minutes as string or number
    val segons: Int?
)

data class TmbLinesResponse(
    val features: List<TmbLineFeature>?
)

data class TmbLineFeature(
    val properties: TmbLineProperties?
)

data class TmbLineProperties(
    val CODI_LINIA: Int?,
    val NOM_LINIA: String?
)

data class TmbStationsResponse(
    val features: List<TmbStationFeature>?
)

data class TmbStationFeature(
    val properties: TmbStationProperties?
)

data class TmbStationProperties(
    val CODI_ESTACIO: Int?,
    val NOM_ESTACIO: String?
)
