package uk.ac.tees.mad.carcare.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import uk.ac.tees.mad.carcare.ui.navigation.Dest
import uk.ac.tees.mad.carcare.ui.navigation.DrawerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarCareModalNavigationDrawer(
    drawerState: DrawerState,
    selectedItem: MutableState<DrawerScreen>,
    drawerScreens: Map<DrawerScreen, Dest>,
    navigate: (Any) -> Unit,
    innerPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState, modifier = Modifier.padding(innerPadding), drawerContent = {
            ModalDrawerSheet {
                Column {
                    drawerScreens.keys.forEach { screen ->
                        NavigationDrawerItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.name) },
                            selected = screen == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = screen
                                drawerScreens[screen]?.let { navigate(it) }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }, content = content
    )
}