package uk.ac.tees.mad.carcare.ui.util

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
/**
 * Composable function to display an error screen with an error message and a retry button.
 *
 * @param errorMessage The error message to display.
 * @param onRetry Callback function to be executed when the retry button is clicked.
 * @param modifier Modifier for the layout.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoadingErrorScreen(
    errorMessage: String, onRetry: () -> Unit, modifier: Modifier = Modifier
) {
    var multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        multiplePermissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        )
    }
    LaunchedEffect(multiplePermissionsState) {
        if (multiplePermissionsState.allPermissionsGranted) {
            onRetry
        } else {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
    if (multiplePermissionsState.allPermissionsGranted) {
        onRetry
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error Icon
        // `Icons.Outlined.Warning` is used to display a warning icon.
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "Error Icon",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error Title
        Text(
            text = "Oops! Something Went Wrong",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error Message
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Permission Allowed?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Retry Button
        Button(onClick = onRetry) {
            Text("Reload App")
        }
    }
}