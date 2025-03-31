package uk.ac.tees.mad.carcare.ui.screens.booking

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.carcare.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment
import uk.ac.tees.mad.carcare.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.UserData
import uk.ac.tees.mad.carcare.model.dataclass.firebase.UserDetails
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareAppointmentRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareFirestoreRepository
import java.io.ByteArrayOutputStream
import java.io.InputStream

class BookingScreenViewModel(
    private val authRepository: AuthRepository,
    private val carCareFirestoreRepository: CarCareFirestoreRepository,
    private val careAppointmentRepository: CarCareAppointmentRepository
) : ViewModel() {
    val serviceOptions: List<String> =
        listOf("Oil Change", "Tire Rotation", "Brake Inspection", "Full Service")
    val centerOptions: List<String> = listOf("Center A", "Center B", "Center C", "Center D")

    private val _selectedService = MutableStateFlow(serviceOptions[0])
    val selectedService: StateFlow<String> = _selectedService.asStateFlow()

    private val _selectedCenter = MutableStateFlow(centerOptions[0])
    val selectedCenter: StateFlow<String> = _selectedCenter.asStateFlow()

    private val _serviceMenuExpanded = MutableStateFlow(false)
    val serviceMenuExpanded: StateFlow<Boolean> = _serviceMenuExpanded.asStateFlow()

    private val _centerMenuExpanded = MutableStateFlow(false)
    val centerMenuExpanded: StateFlow<Boolean> = _centerMenuExpanded.asStateFlow()

    private val _selectedDate = MutableStateFlow<Long?>(System.currentTimeMillis())
    val selectedDate: StateFlow<Long?> = _selectedDate.asStateFlow()

    private val _selextedTime = MutableStateFlow<String?>("")
    val selectedTime: StateFlow<String?> = _selextedTime.asStateFlow()

    private val _problemDescription = MutableStateFlow<String>("")
    val problemDescription: StateFlow<String> = _problemDescription.asStateFlow()

    private val _image = MutableStateFlow<String>("")
    val image: StateFlow<String> = _image.asStateFlow()

    private val _appointment = MutableStateFlow<CarCareAppointment?>(null)
    val appointment: StateFlow<CarCareAppointment?> = _appointment.asStateFlow()

    private val _userDetails = MutableStateFlow<AuthResult<UserDetails>>(AuthResult.Loading)
    val userDetails: StateFlow<AuthResult<UserDetails>> = _userDetails.asStateFlow()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    private val _showBookingDialog = MutableStateFlow(false)
    val showBookingDialog: StateFlow<Boolean> = _showBookingDialog.asStateFlow()

    private val _bookingState = MutableStateFlow<FirestoreResult<Any>>(FirestoreResult.Loading)
    val bookingState: StateFlow<FirestoreResult<Any>> = _bookingState.asStateFlow()

    private val _onSuccessAppointmentId = MutableStateFlow<String>("")
    val onSuccessAppointmentId: StateFlow<String> = _onSuccessAppointmentId.asStateFlow()

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            authRepository.getCurrentUserDetails().collect { result ->
                _userDetails.value = result
                if (result is AuthResult.Success) {
                    _userData.update {
                        it.copy(
                            userDetails = result.data, userId = authRepository.getCurrentUserId()
                        )
                    }
                }
            }
        }
    }

    fun bookService() {
        updateCarCareAppointment()
        viewModelScope.launch {
            carCareFirestoreRepository.addAppointment(userData.value.userId!!, _appointment.value!!)
                .collectLatest { firestoreResult ->
                    when (firestoreResult) {
                        is FirestoreResult.Error -> {
                            _bookingState.value = FirestoreResult.Error(firestoreResult.exception)
                            Log.e(
                                "CarCareAppointmentFirestore",
                                "Error adding Appointment to Firestore"
                            )
                        }

                        is FirestoreResult.Loading -> {
                            _bookingState.value = FirestoreResult.Loading
                        }

                        is FirestoreResult.Success -> {
                            updateCarCareAppointment(firestoreResult.data.toString())
                            careAppointmentRepository.insertAppointment(_appointment.value!!)
                            _onSuccessAppointmentId.value = firestoreResult.data
                            _bookingState.value = FirestoreResult.Success(firestoreResult.data)
                            Log.d(
                                "CarCareAppointmentFirestore",
                                "Successfully added Appointment, firestoreId: ${firestoreResult.data}"
                            )
                        }
                    }
                }
        }
    }

    fun updateCarCareAppointment(firsStoreId: String = "") {
        _appointment.update {
            CarCareAppointment(
                userId = _userData.value.userId!!,
                firestoreId = firsStoreId,
                service = _selectedService.value,
                serviceCenter = _selectedCenter.value,
                appointmentDate = _selectedDate.value.toString(),
                appointmentTime = _selextedTime.value.toString(),
                appointmentServiceDescription = _problemDescription.value,
                carImage = _image.value,
                appointmentBookedOn = System.currentTimeMillis().toString(),
            )
        }
    }

    fun serviceMenuToggle() {
        _serviceMenuExpanded.value = !_serviceMenuExpanded.value
    }

    fun centerMenuToggle() {
        _centerMenuExpanded.value = !_centerMenuExpanded.value
    }

    fun selectService(service: String) {
        _selectedService.value = service
        serviceMenuToggle()
    }

    fun selectCenter(center: String) {
        _selectedCenter.value = center
        centerMenuToggle()
    }

    fun selectDate(date: Long) {
        _selectedDate.value = date
    }

    fun selectTime(time: String) {
        _selextedTime.value = time
    }

    fun updateProblemDescription(description: String) {
        _problemDescription.value = description
    }

    fun updateImage(imageUri: Uri, contentResolver: ContentResolver) {
        val base64Image = encodeImageToBase64(imageUri, contentResolver)
        _image.value = base64Image ?: ""
    }

    fun toggleShowBookingDialog() {
        _showBookingDialog.value = !_showBookingDialog.value
    }

    fun encodeImageToBase64(uri: Uri, contentResolver: ContentResolver): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                // 1. Get dimensions (without loading the whole bitmap)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)

                // Reset the input stream to start again from the beginning
                inputStream.close()
                val newInputStream: InputStream? = contentResolver.openInputStream(uri)

                // 2. Calculate inSampleSize
                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = calculateInSampleSize(options, 1024, 1024) // Example: Target 1024x1024
                }

                // 3. Decode with downsampling
                var bitmap: Bitmap? = BitmapFactory.decodeStream(newInputStream, null, options)

                // 4. Read EXIF orientation
                val exifInterface = ExifInterface(contentResolver.openInputStream(uri)!!)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                // 5. Rotate bitmap if necessary
                bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap!!, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap!!, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap!!, 270f)
                    else -> bitmap
                }

                // 6. Compress
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)

                // 7. Encode to Base64
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            Log.e("Image Encoding", "Error encoding image: ${e.message}")
            null
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

}