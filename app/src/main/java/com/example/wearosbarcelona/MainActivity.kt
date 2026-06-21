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
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MainActivity : ComponentActivity() {
    
    private val viewModel: TransportViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            fetchLocationAndPreSelect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        
        if (hasFine || hasCoarse) {
            fetchLocationAndPreSelect()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
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

    private fun fetchLocationAndPreSelect() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                if (lastLoc != null) {
                    viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro = true)
                    viewModel.selectClosestStation(lastLoc.latitude, lastLoc.longitude, isMetro = false)
                }
            }

            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.selectClosestStation(location.latitude, location.longitude, isMetro = true)
                    viewModel.selectClosestStation(location.latitude, location.longitude, isMetro = false)
                }
            }
        } catch (e: SecurityException) {
            // ignore
        }
    }
}
