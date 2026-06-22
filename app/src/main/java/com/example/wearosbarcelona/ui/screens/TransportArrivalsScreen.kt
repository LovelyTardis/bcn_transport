package com.example.wearosbarcelona.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Velocity
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.example.wearosbarcelona.ui.modifier.rotaryScroll
import com.example.wearosbarcelona.data.model.TransportType
import com.example.wearosbarcelona.ui.components.ArrivalRow
import com.example.wearosbarcelona.ui.viewmodel.TransportViewModel
import com.example.wearosbarcelona.ui.viewmodel.UiState
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun TransportArrivalsScreen(
    transportType: TransportType,
    viewModel: TransportViewModel
) {
    // Resolver propiedades dinámicas según el tipo de transporte
    val isMetro = transportType == TransportType.METRO
    
    val state by (if (isMetro) viewModel.metroArrivalsState else viewModel.fgcArrivalsState).collectAsState()
    val selectedStation = if (isMetro) viewModel.selectedMetroStation else viewModel.selectedFgcStation
    val stations = if (isMetro) viewModel.getMetroStations() else viewModel.getFgcStations()

    var showStationPicker by remember { mutableStateOf(false) }
    var initialCheckDone by remember { mutableStateOf(false) }
    var locationLookupDone by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            val cancellationTokenSource = CancellationTokenSource()
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.selectClosestStation(location.latitude, location.longitude, isMetro)
                    } else {
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (lastLoc != null) {
                                viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro)
                            } else {
                                Toast.makeText(context, "No se pudo obtener la localización", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro)
                        } else {
                            Toast.makeText(context, "Error de localización", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: SecurityException) {
                // permission revoked
            }
        } else {
            Toast.makeText(context, "Permiso de localización denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocationAndSelect() {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            val cancellationTokenSource = CancellationTokenSource()
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.selectClosestStation(location.latitude, location.longitude, isMetro)
                    } else {
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (lastLoc != null) {
                                viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro)
                            } else {
                                Toast.makeText(context, "No se pudo obtener la localización", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro)
                        } else {
                            Toast.makeText(context, "Error de localización", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: SecurityException) {
                // permission revoked
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (locationLookupDone) {
            initialCheckDone = true
            return@LaunchedEffect
        }
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            locationLookupDone = true
            try {
                // Try lastLocation first (instant)
                fusedLocationClient.lastLocation.addOnCompleteListener { lastTask ->
                    if (lastTask.isSuccessful && lastTask.result != null) {
                        val lastLoc = lastTask.result
                        viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro)
                        initialCheckDone = true
                    }
                    
                    // Request fresh location in parallel to be accurate
                    val cancellationTokenSource = CancellationTokenSource()
                    val timeoutJob = launch {
                        kotlinx.coroutines.delay(4000L) // 4 seconds timeout for watch GPS cold start
                        cancellationTokenSource.cancel()
                        if (!initialCheckDone) {
                            initialCheckDone = true
                        }
                    }
                    
                    try {
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationTokenSource.token
                        ).addOnCompleteListener { task ->
                            timeoutJob.cancel()
                            if (task.isSuccessful && task.result != null) {
                                val location = task.result
                                viewModel.selectClosestStation(location.latitude, location.longitude, isMetro)
                            }
                            initialCheckDone = true
                        }
                    } catch (e: SecurityException) {
                        timeoutJob.cancel()
                        initialCheckDone = true
                    }
                }
            } catch (e: SecurityException) {
                initialCheckDone = true
            }
        } else {
            initialCheckDone = true
        }
    }


    var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            currentTimeMs = System.currentTimeMillis()
        }
    }





    // Cargar llegadas periódicamente cada 20 segundos a partir de la primera vez que un tren entre en rango de < 16s (entra), o de inmediato al cambiar de estación
    LaunchedEffect(selectedStation, initialCheckDone) {
        if (!initialCheckDone) return@LaunchedEffect
        
        if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals()
        
        var firstEntryTimeMs: Long? = null
        var nextAllowedRefreshMs = 0L
        
        while (true) {
            kotlinx.coroutines.delay(1000L) // Chequeamos cada segundo
            
            val currentMs = System.currentTimeMillis()
            val arrivalsState = if (isMetro) viewModel.metroArrivalsState.value else viewModel.fgcArrivalsState.value
            
            if (arrivalsState is UiState.Success) {
                val arrivals = arrivalsState.data.arrivals
                val hasTrainEntering = arrivals.any { arrival ->
                    val targetMs = arrival.expectedArrivalEpochMs
                    targetMs != null && (targetMs - currentMs) < 16000L
                }
                
                if (hasTrainEntering) {
                    if (firstEntryTimeMs == null) {
                        firstEntryTimeMs = currentMs
                        nextAllowedRefreshMs = currentMs + 20000L
                    } else if (currentMs >= nextAllowedRefreshMs) {
                        if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals()
                        nextAllowedRefreshMs = currentMs + 20000L
                    }
                } else {
                    firstEntryTimeMs = null
                    nextAllowedRefreshMs = 0L
                }
            } else if (arrivalsState is UiState.Error) {
                // Si está en error y estábamos esperando refresco por tren entrando, reintentamos
                if (firstEntryTimeMs != null) {
                    if (currentMs >= nextAllowedRefreshMs) {
                        if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals()
                        nextAllowedRefreshMs = currentMs + 20000L
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        
        // Selector de estación: chip y botón GPS en fila
        val currentStationName = stations.firstOrNull { it.first == selectedStation }?.second ?: selectedStation
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactChip(
                onClick = {
                    showStationPicker = true
                },
                label = {
                    Text(
                        text = "$currentStationName ▾",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = androidx.wear.compose.material.ChipDefaults.secondaryChipColors(),
                modifier = Modifier.weight(1f, fill = false)
            )
            
            Spacer(modifier = Modifier.size(6.dp))
            
            Button(
                onClick = {
                    requestLocationAndSelect()
                },
                modifier = Modifier.size(32.dp),
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Text(
                    text = "📍",
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val screenState = state) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
                is UiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = screenState.errorMessage,
                            color = Color.Red,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CompactChip(
                            onClick = { if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals() },
                            label = { Text("Reintentar") }
                        )
                    }
                }
                is UiState.Success -> {
                    val stationData = screenState.data
                    
                    if (stationData.isClosed) {
                        // Estado sin servicio (Fin de servicio)
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🌙",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    text = "Fin de servicio",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Siguiente tren:",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stationData.nextTrainInfo ?: "05:00 h",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Mostrar listado de llegadas
                        val arrivals = stationData.arrivals
                        if (arrivals.isEmpty()) {
                            Text(
                                text = if (isMetro) "No hay metros programados" else "No hay trenes programados",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val limit = minOf(arrivals.size, 2)
                                for (i in 0 until limit) {
                                    ArrivalRow(arrival = arrivals[i], currentTimeMs = currentTimeMs)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de selección de estación
    val dialogListState = rememberScalingLazyListState()

    Dialog(
        showDialog = showStationPicker,
        onDismissRequest = { showStationPicker = false },
        scrollState = dialogListState
    ) {
        val dialogFocusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            val stationIndex = stations.indexOfFirst { it.first == selectedStation }
            if (stationIndex >= 0) {
                dialogListState.scrollToItem(stationIndex + 1, 0)
            }
            kotlinx.coroutines.delay(100L) // Esperar a que la ventana se adjunte y active
            dialogFocusRequester.requestFocus()
        }

        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .rotaryScroll(dialogListState, dialogFocusRequester),
            state = dialogListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        text = if (isMetro) "Metro TMB" else "Línea S1",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(stations.size) { index ->
                val station = stations[index]
                val stationId = station.first
                val stationName = station.second
                val isSelected = stationId == selectedStation
                Chip(
                    onClick = {
                        if (isMetro) {
                            viewModel.selectMetroStation(stationId)
                        } else {
                            viewModel.selectFgcStation(stationId)
                        }
                        showStationPicker = false
                    },
                    label = {
                        Text(
                            text = stationName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = if (isSelected) {
                        ChipDefaults.primaryChipColors()
                    } else {
                        ChipDefaults.secondaryChipColors()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
