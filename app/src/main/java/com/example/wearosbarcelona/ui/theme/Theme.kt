package com.example.wearosbarcelona.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val TmbRed = Color(0xFFE51C24)
val FgcGreen = Color(0xFF00A550)
val DarkBackground = Color(0xFF000000)
val CardBackground = Color(0xFF1E1E1E)
val LightText = Color(0xFFFFFFFF)
val MutedText = Color(0xFFB0B0B0)

val WearColors = Colors(
    primary = TmbRed,
    primaryVariant = Color(0xFFB3001B),
    secondary = FgcGreen,
    secondaryVariant = Color(0xFF007A33),
    background = DarkBackground,
    surface = CardBackground,
    onPrimary = LightText,
    onSecondary = LightText,
    onBackground = LightText,
    onSurface = LightText
)

@Composable
fun WearOSBarcelonaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColors,
        content = content
    )
}
