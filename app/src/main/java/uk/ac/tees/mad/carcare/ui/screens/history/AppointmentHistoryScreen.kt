package uk.ac.tees.mad.carcare.ui.screens.history

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment
import uk.ac.tees.mad.carcare.ui.util.CarCareScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentHistoryScreen(
    modifier: Modifier = Modifier,
    popUp: () -> Unit,
    viewModel: AppointmentHistoryScreenViewModel = koinViewModel<AppointmentHistoryScreenViewModel>()
) {
    val appointmentHistory by viewModel.appointmentHistory.collectAsStateWithLifecycle()
    CarCareScreen(
        title = stringResource(id = R.string.appointment_history),
        popUp = popUp,
        showBackArrow = true
    ) { innerModifier ->
        LazyColumn(
            modifier = innerModifier,
            //verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = appointmentHistory, key = { it.id }) { appointment ->
                AppointmentHistoryItem(appointment = appointment, viewModel = viewModel)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (appointmentHistory.isEmpty()) {
                    Text(
                        text = "No appointments found",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AppointmentHistoryItem(
    appointment: CarCareAppointment, viewModel: AppointmentHistoryScreenViewModel
) {
    val cal = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val date = Date(appointment.appointmentDate.toLong())
    cal.set(Calendar.DATE, date.date)
    val formattedDate = formatter.format(date)
    // Appointment history item UI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display appointment details here
            Text(
                text = "Appointment ID: ${appointment.firestoreId}",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Service: ${appointment.service}",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Service Center: ${appointment.serviceCenter}",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Appointment Date: ${formattedDate}",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Appointment Time: ${appointment.appointmentTime}",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                fun decodeBase64ToBitmap(base64String: String): Bitmap? {
                    return try {
                        val decodedByteArray = Base64.decode(base64String, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(
                            decodedByteArray,
                            0,
                            decodedByteArray.size
                        )
                    } catch (e: Exception) {
                        // Handle decoding errors (e.g., invalid Base64 string)
                        null
                    }
                }
                val bitmap = if(appointment.carImage.isNotEmpty()){
                    decodeBase64ToBitmap(appointment.carImage)
                }else{
                    null
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Car Image",
                    )
                } else {
                    // Photo placeholder
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "No Image",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }
            }
            if (appointment.appointmentServiceDescription.isNotEmpty() || appointment.appointmentServiceDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Appointment Service Description: \n${appointment.appointmentServiceDescription}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.deleteAppointmentData(appointment)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Appointment",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Delete Appointment")
            }
        }
    }
}
