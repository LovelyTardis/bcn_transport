package com.example.wearosbarcelona.ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.wearosbarcelona.R
import com.example.wearosbarcelona.ui.theme.FgcGreen
import com.example.wearosbarcelona.ui.theme.TmbRed
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onNavigateToMetro: () -> Unit,
    onNavigateToFgc: () -> Unit,
    isMockMode: Boolean,
    onToggleMockMode: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ListHeader {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = androidx.wear.compose.material.MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }

        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToMetro,
                label = { Text(stringResource(id = R.string.metro_tmb)) },
                secondaryLabel = { Text("Líneas de Metro", color = Color.White.copy(alpha = 0.7f)) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = TmbRed,
                    contentColor = Color.White
                )
            )
        }

        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToFgc,
                label = { Text(stringResource(id = R.string.train_fgc)) },
                secondaryLabel = { Text("Ferrocarrils", color = Color.White.copy(alpha = 0.7f)) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = FgcGreen,
                    contentColor = Color.White
                )
            )
        }

        item {
            Chip(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                onClick = onToggleMockMode,
                label = { 
                    Text(
                        if (isMockMode) "Modo: Demo (Mock)" else "Modo: Real (REST)",
                        textAlign = TextAlign.Center
                    ) 
                },
                secondaryLabel = { 
                    Text(
                        if (isMockMode) "Toca para cambiar a real" else "Toca para cambiar a demo",
                        color = Color.Gray
                    ) 
                },
                colors = ChipDefaults.chipColors(
                    backgroundColor = if (isMockMode) Color(0xFF263238) else Color(0xFF1B5E20)
                )
            )
        }
    }
}
