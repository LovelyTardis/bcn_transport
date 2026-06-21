package com.example.wearosbarcelona.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ArrivalRow(arrival: TrainArrival) {
    Card(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Insignia de la línea (círculo de color) con optimización de caché
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

            // Destino y Vía
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = arrival.destination,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                arrival.platform?.let {
                    Text(
                        text = it,
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
            }

            // Tiempo restante
            Text(
                text = if (arrival.minutesLeft <= 0) "Ahora" else "${arrival.minutesLeft}m",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (arrival.minutesLeft <= 2) Color(0xFF4CAF50) else Color.White
            )
        }
    }
}
