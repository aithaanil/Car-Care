package uk.ac.tees.mad.carcare.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.UserDetails
import uk.ac.tees.mad.carcare.ui.navigation.Dest
import uk.ac.tees.mad.carcare.ui.util.CarCareScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (Any, Any) -> Unit,
    navigate: (Any) -> Unit,
    viewmodel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>()
) {
    val userDetails by viewmodel.userDetails.collectAsStateWithLifecycle()
    CarCareScreen(
        title = stringResource(R.string.app_name), openAndPopUp = openAndPopUp, navigate = navigate
    ) { innerModifier ->
        LazyColumn(
            modifier = innerModifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                UserDetailsCard(userDetails, viewmodel)
            }
            item {
                QuickActionsCard(viewmodel, navigate)
            }
        }
    }
}

@Composable
fun UserDetailsCard(userDetails: AuthResult<UserDetails>, viewmodel: HomeScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (userDetails) {
                is AuthResult.Success -> {
                    Text("Welcome, ${if (userDetails.data.displayName.isNullOrEmpty()) "User" else userDetails.data.displayName}")
                    Text("Email: ${userDetails.data.email}")
                }

                is AuthResult.Error -> {
                    Text("Error: ${userDetails.exception.message}")
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                            //retry fetching user details
                            viewmodel.fetchUserDetails()
                        }) {
                        Text("Retry?")
                    }
                }

                is AuthResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsCard(
    viewmodel: HomeScreenViewModel, navigate: (Any) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quick Actions", style = MaterialTheme.typography.headlineMedium
            )
            // Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CarCareButton(
                    modifier = Modifier.weight(1f), label = "Book Service", onClick = {
                        navigate(Dest.BookingScreen)
                    })

                CarCareButton(
                    modifier = Modifier.weight(1f), label = "View History", onClick = {
                        navigate(Dest.AppointmentHistoryScreen)
                    })

                CarCareButton(
                    modifier = Modifier.weight(1f), label = "Manage Profile", onClick = {
                        navigate(Dest.ProfileAndSettingsScreen)
                    })
            }
        }
    }
}

@Composable
fun CarCareButton(
    modifier: Modifier = Modifier, label: String, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(text = label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}