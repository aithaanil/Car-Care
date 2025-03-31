package uk.ac.tees.mad.carcare.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.ui.navigation.Dest
import uk.ac.tees.mad.carcare.ui.navigation.DrawerScreen
import uk.ac.tees.mad.carcare.ui.navigation.SubGraph
import uk.ac.tees.mad.carcare.ui.screens.CarCareAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarCareScreen(
    title: String,
    openAndPopUp: (Any, Any) -> Unit = { _, _ -> },
    navigate: (Any) -> Unit = {},
    popUp: () -> Unit = {},
    showBackArrow: Boolean = false,
    viewModel: CarCareAppViewModel = koinViewModel<CarCareAppViewModel>(),
    content: @Composable (Modifier) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val drawerScreens = mapOf(
        DrawerScreen("Dashboard", Icons.Default.Dashboard) to Dest.HomeScreen,
        DrawerScreen("Book Service", Icons.Default.BookOnline) to Dest.BookingScreen,
        DrawerScreen("Appointment History", Icons.Default.History) to Dest.AppointmentHistoryScreen,
        DrawerScreen(
            "Profile & Settings",
            Icons.Default.ManageAccounts
        ) to Dest.ProfileAndSettingsScreen,
    )
    val selectedItem = remember { mutableStateOf(drawerScreens.keys.first()) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            CarCareTopAppBar(
                title = title, showBackArrow = showBackArrow, openDrawer = {
                scope.launch {
                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                }
            }, popUp = popUp, logOut = {
                viewModel.logOut()
                openAndPopUp(SubGraph.AuthGraph, SubGraph.HomeGraph)
            }, scrollBehavior = scrollBehavior
            )
        }, contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        CarCareModalNavigationDrawer(
            drawerState = drawerState,
            selectedItem = selectedItem,
            drawerScreens = drawerScreens,
            innerPadding = innerPadding,
            navigate = navigate
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}