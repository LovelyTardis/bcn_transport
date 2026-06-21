package com.example.wearosbarcelona.data.repository

import android.util.Log
import com.example.wearosbarcelona.data.model.TransportType
import java.util.Calendar

object ScheduleHelper {

    fun checkServiceStatus(type: TransportType, stationId: String): Pair<Boolean, String?> {
        // Usamos la zona horaria por defecto del dispositivo para que coincida exactamente
        // con la hora visible en la pantalla del reloj (útil para pruebas y emuladores).
        val calendar = Calendar.getInstance()
        
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val result = when (type) {
            TransportType.METRO -> {
                val nextTrain = "L1 a las 05:00 h"
                val isClosed = when (dayOfWeek) {
                    Calendar.SATURDAY -> {
                        hour in 2..4
                    }
                    Calendar.SUNDAY -> {
                        false
                    }
                    else -> {
                        hour in 0..4
                    }
                }
                Pair(isClosed, if (isClosed) nextTrain else null)
            }
            TransportType.TRAIN_FGC -> {
                val nextTrain = when (stationId) {
                    "pl_catalunya" -> "S1 a las 05:05 h (Vía 1)"
                    "sant_cugat" -> "S1 a las 05:02 h (Vía 2 a BCN)"
                    else -> "FGC a las 05:05 h"
                }
                
                val isClosed = when (dayOfWeek) {
                    Calendar.SATURDAY, Calendar.SUNDAY -> {
                        hour in 2..4
                    }
                    else -> {
                        hour in 0..4
                    }
                }
                Pair(isClosed, if (isClosed) nextTrain else null)
            }
        }
        
        return result
    }
}
