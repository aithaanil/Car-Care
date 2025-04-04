package uk.ac.tees.mad.carcare.ui.screens.booking

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.carcare.model.utils.ImageFileProvider
import uk.ac.tees.mad.carcare.ui.navigation.Dest
import uk.ac.tees.mad.carcare.ui.util.CarCareScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    navigate: (Any) -> Unit,
    openAndPopUp: (Any, Any) -> Unit,
    popUp: () -> Unit,
    viewmodel: BookingScreenViewModel = koinViewModel<BookingScreenViewModel>()
) {
    val serviceOptions: List<String> = viewmodel.serviceOptions
    val centerOptions: List<String> = viewmodel.centerOptions
    val serviceMenuExpanded by viewmodel.serviceMenuExpanded.collectAsStateWithLifecycle()
    val centerMenuExpanded by viewmodel.centerMenuExpanded.collectAsStateWithLifecycle()
    val serviceTextFieldState = rememberTextFieldState(serviceOptions[0])
    val centerTextFieldState = rememberTextFieldState(centerOptions[0])

    val cal = Calendar.getInstance()
    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis
    val formattedDate = if (selectedDateMillis != null) {
        viewmodel.selectDate(selectedDateMillis)
        val date = Date(selectedDateMillis)
        cal.set(Calendar.DATE, date.date)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        formatter.format(date)
    } else {
        "No Date Selected"
    }

    val timePickerState = rememberTimePickerState()
    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
    cal.set(Calendar.MINUTE, timePickerState.minute)
    cal.isLenient = false
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = formatter.format(cal.time)
    viewmodel.selectTime(formattedTime)

    val problemDescription by viewmodel.problemDescription.collectAsStateWithLifecycle()

    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
            uri?.let { viewmodel.updateImage(it, context.contentResolver) }
        })
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
            imageUri?.let { viewmodel.updateImage(it, context.contentResolver) }
        })

    val showBookingDialog by viewmodel.showBookingDialog.collectAsStateWithLifecycle()
    val bookingState by viewmodel.bookingState.collectAsStateWithLifecycle()
    val onSuccessAppointmentId by viewmodel.onSuccessAppointmentId.collectAsStateWithLifecycle()

    CarCareScreen(
        title = stringResource(id = R.string.book_service),
        popUp = popUp,
        navigate = navigate,
        showBackArrow = true
    ) { innerModifier ->

        if (showBookingDialog) {
            when (val state = bookingState) {
                is FirestoreResult.Error -> {
                    AlertDialog(icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }, title = {
                        Text(
                            text = "Error",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.exception.message.toString(),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }, confirmButton = {
                        TextButton(onClick = {
                            viewmodel.toggleShowBookingDialog()
                        }) {
                            Text(text = "Retry?", fontWeight = FontWeight.Bold)
                        }
                    }, onDismissRequest = {
                        viewmodel.toggleShowBookingDialog()
                    })
                }

                FirestoreResult.Loading -> {
                    AlertDialog(onDismissRequest = {
                        viewmodel.toggleShowBookingDialog()
                    }, icon = {
                        Icon(
                            Icons.Default.BookOnline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }, title = {
                        Text(
                            text = "Booking...",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }, text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }, confirmButton = { })
                }

                is FirestoreResult.Success -> {
                    showNotification(
                        context,
                        "Appointment Booked on $formattedDate at $formattedTime"
                    )
                    navigate(Dest.AppointmentConfirmationScreen(onSuccessAppointmentId))
                    viewmodel.toggleShowBookingDialog()
                }
            }
        }

        LazyColumn(
            modifier = innerModifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "Select service",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = serviceMenuExpanded,
                    onExpandedChange = { viewmodel.serviceMenuToggle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = serviceTextFieldState,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        label = { Text("Service") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceMenuExpanded) })
                    ExposedDropdownMenu(
                        expanded = serviceMenuExpanded,
                        onDismissRequest = { viewmodel.serviceMenuToggle() },
                    ) {
                        serviceOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    serviceTextFieldState.setTextAndPlaceCursorAtEnd(option)
                                    viewmodel.selectService(option)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Select center",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = centerMenuExpanded,
                    onExpandedChange = { viewmodel.centerMenuToggle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = centerTextFieldState,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        label = { Text("Center") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = centerMenuExpanded) })
                    ExposedDropdownMenu(
                        expanded = centerMenuExpanded,
                        onDismissRequest = { viewmodel.centerMenuToggle() },
                    ) {
                        centerOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    centerTextFieldState.setTextAndPlaceCursorAtEnd(option)
                                    viewmodel.selectCenter(option)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Select date for service",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))
                    Text(
                        "Selected date for Service: $formattedDate",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            item {
                Text(
                    text = "Select time for service",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        if (displayMode == TimePickerDisplayMode.Picker) {
                            IconButton(
                                onClick = {
                                    displayMode = TimePickerDisplayMode.Input
                                }, modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Keyboard,
                                    contentDescription = "Switch to input mode",
                                )
                            }
                            TimePicker(
                                state = timePickerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    displayMode = TimePickerDisplayMode.Picker
                                }, modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Switch to picker mode",
                                )
                            }
                            TimeInput(
                                state = timePickerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                    }
                    Text(
                        "Selected time for Service: ${formattedTime ?: "No Time Selected"}",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            item {
                Text(
                    text = "Add problem description",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                TextField(
                    value = problemDescription,
                    onValueChange = {
                        viewmodel.updateProblemDescription(it)
                    },
                    label = { Text("Problem Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                Text(
                    text = "Add photo",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasImage && imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = "Selected image",
                            )
                        } else {
                            // Photo placeholder
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(40.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            hasImage = false
                            imageUri = null
                            val uri = ImageFileProvider.getImageUri(context)
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Open Camera"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Camera")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { imagePicker.launch("image/*") }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Get Image from Gallery"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Get from Gallery")
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        enabled = formattedDate != "No Date Selected",
                        onClick = {
                            viewmodel.bookService()
                            viewmodel.toggleShowBookingDialog()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text(
                            text = "Book Service",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

fun showNotification(context: Context, message: String) {
    val builder = NotificationCompat.Builder(context, "appointment_channel")
        .setSmallIcon(R.drawable.carcareapp_logo).setContentTitle("Appintment Booked")
        .setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        try {
            val notificationId = Random.nextInt()
            notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e("Notification", "Notification permission not granted: ${e.message}")
        }
    }
}