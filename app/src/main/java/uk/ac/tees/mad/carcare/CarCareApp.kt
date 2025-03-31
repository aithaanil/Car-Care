package uk.ac.tees.mad.carcare

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.carcare.ui.navigation.CarCareAppState
import uk.ac.tees.mad.carcare.ui.navigation.CarCareGraph
import uk.ac.tees.mad.carcare.ui.navigation.SubGraph

@Composable
fun CarCareApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        val appState = rememberAppState()
        NavHost(
            navController = appState.navController, startDestination = SubGraph.SplashScreenGraph
        ) {
            CarCareGraph(appState)
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        CarCareAppState(navController)
    }