package uk.ac.tees.mad.carcare.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import uk.ac.tees.mad.carcare.ui.screens.booking.BookingScreen
import uk.ac.tees.mad.carcare.ui.screens.confirmpage.AppointmentConfirmationScreen
import uk.ac.tees.mad.carcare.ui.screens.home.HomeScreen
import uk.ac.tees.mad.carcare.ui.screens.login.LogInScreen
import uk.ac.tees.mad.carcare.ui.screens.signup.SignUpScreen
import uk.ac.tees.mad.carcare.ui.screens.splash.SplashScreen

fun NavGraphBuilder.CarCareGraph(appState: CarCareAppState) {
    navigation<SubGraph.SplashScreenGraph>(startDestination = Dest.SplashScreen) {
        composable<Dest.SplashScreen> {
            SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
        }
    }
    navigation<SubGraph.AuthGraph>(startDestination = Dest.LogInScreen) {
        composable<Dest.LogInScreen> {
            LogInScreen(
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
                navigate = { route -> appState.navigate(route) })
        }
        composable<Dest.SignUPScreen> {
            SignUpScreen(
                popUp = { appState.popUp() })
        }
    }
    navigation<SubGraph.HomeGraph>(startDestination = Dest.HomeScreen) {
        composable<Dest.HomeScreen> {
            HomeScreen(
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
                navigate = { route -> appState.navigate(route) }
            )
        }
        composable<Dest.BookingScreen> {
            BookingScreen(
                navigate = { route -> appState.navigate(route) },
                openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
                popUp = { appState.popUp() }
            )
        }
        composable<Dest.AppointmentConfirmationScreen> {
            val args= it.toRoute<Dest.AppointmentConfirmationScreen>()
            AppointmentConfirmationScreen(
                popUp = { appState.popUp() },
                appointmentId = args.appointmentId
            )
        }
        composable<Dest.AppointmentHistoryScreen> {
            //AppointmentHistoryScreen()
        }
        composable<Dest.ProfileAndSettingsScreen> {
            //ProfileAndSettingsScreen()
        }
    }

}