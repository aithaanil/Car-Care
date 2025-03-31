package uk.ac.tees.mad.carcare.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarCareTopAppBar(
    title: String,
    showBackArrow: Boolean = false,
    openDrawer: () -> Unit,
    logOut: () -> Unit,
    popUp: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
        Text(
            title, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }, navigationIcon = {
        if (showBackArrow) {
            IconButton(onClick = popUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Button"
                )
            }
        } else {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu, contentDescription = "Open Drawer"
                )
            }
        }
    }, actions = {
        IconButton(onClick = logOut) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout Button"
            )
        }
    }, scrollBehavior = scrollBehavior
    )
}