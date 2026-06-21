package com.example.wearosbarcelona.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmbApiService {

    @GET("v1/transit/linies/metro/{lineId}/estacions/{stationId}/previsions")
    suspend fun getMetroPredictions(
        @Path("lineId") lineId: String,
        @Path("stationId") stationId: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): TmbMetroResponse

    @GET("v1/transit/linies/metro")
    suspend fun getMetroLines(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): retrofit2.Response<okhttp3.ResponseBody>
}

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
