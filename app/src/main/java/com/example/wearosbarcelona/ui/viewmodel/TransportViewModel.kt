package com.example.wearosbarcelona.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearosbarcelona.BuildConfig
import com.example.wearosbarcelona.data.model.StationArrivals
import com.example.wearosbarcelona.data.model.TransportType
import com.example.wearosbarcelona.data.repository.MockTransportRepository
import com.example.wearosbarcelona.data.repository.RealTransportRepository
import com.example.wearosbarcelona.data.repository.TransportRepository
import com.example.wearosbarcelona.data.remote.TmbApiService
import com.example.wearosbarcelona.data.remote.FgcApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val errorMessage: String) : UiState<Nothing>
}

class TransportViewModel : ViewModel() {

    // Credentials loaded from BuildConfig (.env file)
    private val tmbAppId = BuildConfig.TMB_APP_ID
    private val tmbAppKey = BuildConfig.TMB_APP_KEY

    private val mockRepository = MockTransportRepository()
    private var realRepository: RealTransportRepository? = null

    // Por defecto, si no hay credenciales de TMB, usamos el MockRepository para que funcione al instante
    private var activeRepository: TransportRepository = mockRepository

    var isMockMode by mutableStateOf(true)
        private set

    init {
        // Inicializar servicios reales solo si hay credenciales
        if (tmbAppId.isNotEmpty() && tmbAppKey.isNotEmpty()) {
            try {
                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build()

                val retrofitTmb = Retrofit.Builder()
                    .baseUrl("https://api.tmb.cat/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val retrofitFgc = Retrofit.Builder()
                    .baseUrl("https://dadesobertes.fgc.cat/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val tmbService = retrofitTmb.create(TmbApiService::class.java)
                val fgcService = retrofitFgc.create(FgcApiService::class.java)

                realRepository = RealTransportRepository(tmbService, fgcService, tmbAppId, tmbAppKey)
                // Opcional: Descomentar la siguiente línea para activar el modo Real por defecto si hay claves
                activeRepository = realRepository!!
                isMockMode = false
            } catch (e: Exception) {
                // Si falla la inicialización de red, mantén el repositorio mock
                activeRepository = mockRepository
                isMockMode = true
            }
        }
        loadStationsList()
    }

    private val _metroArrivalsState = MutableStateFlow<UiState<StationArrivals>>(UiState.Loading)
    val metroArrivalsState: StateFlow<UiState<StationArrivals>> = _metroArrivalsState.asStateFlow()

    private val _fgcArrivalsState = MutableStateFlow<UiState<StationArrivals>>(UiState.Loading)
    val fgcArrivalsState: StateFlow<UiState<StationArrivals>> = _fgcArrivalsState.asStateFlow()

    var selectedMetroStation by mutableStateOf("espanya")
        private set

    var selectedFgcStation by mutableStateOf("pl_catalunya")
        private set

    var metroStationsList by mutableStateOf<List<Pair<String, String>>>(
        listOf(
            "espanya" to "Espanya L1",
            "universitat" to "Universitat L1"
        )
    )
        private set

    var fgcStationsList by mutableStateOf<List<Pair<String, String>>>(
        listOf(
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
    )
        private set

    fun getMetroStations(): List<Pair<String, String>> {
        return metroStationsList
    }

    fun getFgcStations(): List<Pair<String, String>> {
        return fgcStationsList
    }

    private fun loadStationsList() {
        viewModelScope.launch {
            try {
                metroStationsList = activeRepository.getAvailableStations(TransportType.METRO)
            } catch (e: Exception) {
                // Mantener predeterminados
            }
        }
        viewModelScope.launch {
            try {
                fgcStationsList = activeRepository.getAvailableStations(TransportType.TRAIN_FGC)
            } catch (e: Exception) {
                // Mantener predeterminados
            }
        }
    }

    fun selectMetroStation(stationId: String) {
        selectedMetroStation = stationId
        refreshMetroArrivals()
    }

    fun selectFgcStation(stationId: String) {
        selectedFgcStation = stationId
        refreshFgcArrivals()
    }

    fun refreshMetroArrivals() {
        viewModelScope.launch {
            _metroArrivalsState.value = UiState.Loading
            try {
                val arrivals = activeRepository.getMetroArrivals(selectedMetroStation)
                _metroArrivalsState.value = UiState.Success(arrivals)
            } catch (e: Exception) {
                _metroArrivalsState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun refreshFgcArrivals() {
        viewModelScope.launch {
            _fgcArrivalsState.value = UiState.Loading
            try {
                val arrivals = activeRepository.getFgcArrivals(selectedFgcStation)
                _fgcArrivalsState.value = UiState.Success(arrivals)
            } catch (e: Exception) {
                _fgcArrivalsState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun toggleMockMode() {
        val targetMock = !isMockMode
        if (targetMock) {
            activeRepository = mockRepository
            isMockMode = true
            loadStationsList()
            selectedMetroStation = "espanya"
            selectedFgcStation = "pl_catalunya"
            refreshMetroArrivals()
            refreshFgcArrivals()
        } else {
            val real = realRepository
            if (real != null) {
                activeRepository = real
                isMockMode = false
                loadStationsList()
                selectedMetroStation = "espanya"
                selectedFgcStation = "pl_catalunya"
                refreshMetroArrivals()
                refreshFgcArrivals()
            }
        }
    }
}
