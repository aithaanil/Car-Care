package uk.ac.tees.mad.carcare.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    data object SplashScreenGraph : SubGraph()

    @Serializable
    data object AuthGraph : SubGraph()

    @Serializable
    data object HomeGraph : SubGraph()
}

sealed class Dest {
    @Serializable
    data object SplashScreen : Dest()

    @Serializable
    data object LogInScreen : Dest()

    @Serializable
    data object SignUPScreen : Dest()

    @Serializable
    data object HomeScreen : Dest()

    @Serializable
    data object BookingScreen : Dest()

    @Serializable
    data class AppointmentConfirmationScreen(val appointmentId: String) : Dest()

    @Serializable
    data object AppointmentHistoryScreen : Dest()

    @Serializable
    data object ProfileAndSettingsScreen : Dest()

    @Serializable
    data class LoadingErrorScreen(val errorMessage: String) : Dest()
}

data class DrawerScreen(val name: String, val icon: ImageVector)