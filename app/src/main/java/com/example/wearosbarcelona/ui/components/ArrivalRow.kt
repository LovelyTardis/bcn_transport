package com.example.wearosbarcelona.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Text
import com.example.wearosbarcelona.data.model.TrainArrival

@Composable
fun ArrivalRow(arrival: TrainArrival, currentTimeMs: Long = System.currentTimeMillis()) {
    Card(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: Insignia de la línea (círculo de color)
            val lineBgColor = remember(arrival.line.colorHex) {
                try {
                    Color(android.graphics.Color.parseColor(arrival.line.colorHex))
                } catch (e: Exception) {
                    Color.DarkGray
                }
            }
            
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(lineBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = arrival.line.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            val bottomText = remember(arrival.destination) {
                val rawDest = arrival.destination
                rawDest
                    .replace("í", "i")
                    .replace("Í", "I")
                    .replace("á", "a")
                    .replace("Á", "A")
                    .replace("é", "e")
                    .replace("É", "E")
                    .replace("ó", "o")
                    .replace("Ó", "O")
                    .replace("ú", "u")
                    .replace("Ú", "U")
                    .replace("à", "a")
                    .replace("À", "A")
                    .replace("è", "e")
                    .replace("È", "E")
                    .replace("ò", "o")
                    .replace("Ò", "O")
            }
            
            Text(
                text = bottomText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            // Derecha: Cuenta atrás (temporizador destacado directo)
            val hasTimestamp = arrival.expectedArrivalEpochMs != null
            val timeText = if (hasTimestamp) {
                val targetMs = arrival.expectedArrivalEpochMs!!
                val diffMs = targetMs - currentTimeMs
                val diffSeconds = diffMs / 1000
                if (diffSeconds < 16) {
                    "Entra"
                } else {
                    val mins = diffSeconds / 60
                    val secs = diffSeconds % 60
                    val minsStr = mins.toString().padStart(2, '0')
                    val secsStr = secs.toString().padStart(2, '0')
                    "$minsStr:$secsStr"
                }
            } else {
                arrival.timeLeftFormatted ?: (if (arrival.minutesLeft <= 0) "Ahora" else "${arrival.minutesLeft}m")
            }
            
            val isGreen = if (hasTimestamp) {
                timeText == "Entra" || timeText == "00:00"
            } else {
                arrival.minutesLeft <= 2
            }
            
            val timerColor = if (isGreen) Color(0xFF4CAF50) else Color.White
            
            Text(
                text = timeText,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = timerColor
            )
        }
    }
}
