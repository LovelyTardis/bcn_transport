package com.example.wearosbarcelona

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.wearosbarcelona.data.model.TransportType
import com.example.wearosbarcelona.ui.screens.MainScreen
import com.example.wearosbarcelona.ui.screens.TransportArrivalsScreen
import com.example.wearosbarcelona.ui.theme.WearOSBarcelonaTheme
import com.example.wearosbarcelona.ui.viewmodel.TransportViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: TransportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearOSBarcelonaTheme {
                val navController = rememberSwipeDismissableNavController()
                
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            onNavigateToMetro = { navController.navigate("metro") },
                            onNavigateToFgc = { navController.navigate("fgc") },
                            isMockMode = viewModel.isMockMode,
                            onToggleMockMode = { viewModel.toggleMockMode() }
                        )
                    }
                    
                    composable("metro") {
                        TransportArrivalsScreen(
                            transportType = TransportType.METRO,
                            viewModel = viewModel
                        )
                    }
                    
                    composable("fgc") {
                        TransportArrivalsScreen(
                            transportType = TransportType.TRAIN_FGC,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
