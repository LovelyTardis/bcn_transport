package com.example.wearosbarcelona.ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
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
import com.example.wearosbarcelona.data.model.TransportType
import com.example.wearosbarcelona.ui.components.ArrivalRow
import com.example.wearosbarcelona.ui.viewmodel.TransportViewModel
import com.example.wearosbarcelona.ui.viewmodel.UiState
import kotlinx.coroutines.launch

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
    
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var dragAccumulator by remember { mutableStateOf(0f) }
    var isPullActive by remember { mutableStateOf(false) }
    var isRefreshTriggered by remember { mutableStateOf(false) }
    var showStationPicker by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isAtTop = !listState.canScrollBackward
                
                if (available.y > 0f && !isRefreshTriggered) {
                    if (isPullActive || isAtTop) {
                        isPullActive = true
                        dragAccumulator += available.y
                        if (dragAccumulator > 80f) {
                            dragAccumulator = 0f
                            isPullActive = false
                            isRefreshTriggered = true
                            if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals()
                        }
                        // Consumimos el scroll vertical para evitar el overscroll del ScalingLazyColumn
                        // y garantizar que sigamos recibiendo los eventos en onPreScroll.
                        return Offset(x = 0f, y = available.y)
                    }
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                dragAccumulator = 0f
                isPullActive = false
                isRefreshTriggered = false
                return Velocity.Zero
            }
        }
    }

    // Cargar llegadas cuando cambia la estación o al inicializar
    LaunchedEffect(selectedStation) {
        if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals()
    }

    // Solicitar el foco para la corona rotatoria
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .padding(horizontal = 8.dp)
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        autoCentering = null
    ) {
        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
        // Selector de estación: chip que abre el diálogo de selección
        item {
            val currentStationName = stations.firstOrNull { it.first == selectedStation }?.second ?: selectedStation
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
                colors = androidx.wear.compose.material.ChipDefaults.secondaryChipColors()
            )
        }

        when (val screenState = state) {
            is UiState.Loading -> {
                item {
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
            }
            is UiState.Error -> {
                item {
                    Text(
                        text = screenState.errorMessage,
                        color = Color.Red,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
                item {
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
                    item {
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
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    text = "Fin de servicio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Siguiente tren:",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stationData.nextTrainInfo ?: "05:00 h",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Mostrar listado de llegadas
                    val arrivals = stationData.arrivals
                    if (arrivals.isEmpty()) {
                        item {
                            Text(
                                text = if (isMetro) "No hay metros programados" else "No hay trenes programados",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    } else {
                        items(arrivals.size) { index ->
                            ArrivalRow(arrival = arrivals[index])
                        }
                    }
                }

                // Botón común de refresco al final
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    Button(
                        onClick = { if (isMetro) viewModel.refreshMetroArrivals() else viewModel.refreshFgcArrivals() },
                        modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("↻", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Diálogo de selección de estación
    val dialogListState = rememberScalingLazyListState()
    val dialogFocusRequester = remember { FocusRequester() }
    val dialogCoroutineScope = rememberCoroutineScope()

    Dialog(
        showDialog = showStationPicker,
        onDismissRequest = { showStationPicker = false },
        scrollState = dialogListState
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent {
                    dialogCoroutineScope.launch {
                        dialogListState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(dialogFocusRequester)
                .focusable(),
            state = dialogListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        text = if (isMetro) "Línea L3" else "Línea S1",
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

        LaunchedEffect(showStationPicker) {
            if (showStationPicker) {
                dialogFocusRequester.requestFocus()
            }
        }
    }
}
