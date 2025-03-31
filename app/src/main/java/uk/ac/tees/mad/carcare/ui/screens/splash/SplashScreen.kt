package uk.ac.tees.mad.carcare.ui.screens.splash

import android.Manifest
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.ui.navigation.Dest

private const val SPLASH_TIMEOUT = 5000L

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    openAndPopUp: (Any, Any) -> Unit,
    navigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel<SplashViewModel>()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        Splash()
    }

    LaunchedEffect(Unit) {
        if (multiplePermissionsState.allPermissionsGranted) {
            // All permissions granted
            delay(SPLASH_TIMEOUT)
            viewModel.onAppStart(openAndPopUp)
        } else {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(multiplePermissionsState) {
        if (multiplePermissionsState.allPermissionsGranted) {
            // All permissions granted
            delay(SPLASH_TIMEOUT)
            viewModel.onAppStart(openAndPopUp)
        } else {
            navigate(Dest.LoadingErrorScreen("Permissions not granted. Please grant them."))
        }
    }

//    if (multiplePermissionsState.allPermissionsGranted) {
//        // Continue with the app flow
//    } else {
//        // Handle the case where permissions are not granted
//        LaunchedEffect(Unit) {
//            // Delay for a shorter time if permissions are not granted
//            delay(SPLASH_TIMEOUT)
//            // Navigate to a screen explaining the permissions and allowing the user to grant them
//            // Or, you could just close the app
//            navigate(Dest.LoadingErrorScreen("Permissions not granted. Please grant them."))
//            //viewModel.openAuthGraph(openAndPopUp)
//        }
//    }
}

@Composable
fun Splash() {
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val progressbarAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Logo animation
        logoScale.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        // App name animation
        delay(500) // Stagger the animation
        textAlpha.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        // ProgressBar animation
        delay(500) // Stagger the animation
        progressbarAlpha.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(150.dp)
                .scale(logoScale.value),
            painter = painterResource(id = R.drawable.carcareapp_logo),
            contentDescription = "App Logo",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Text(
            text = "CarCare",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 20.dp)
                .alpha(textAlpha.value)
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 20.dp)
                .alpha(progressbarAlpha.value),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}