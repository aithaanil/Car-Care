package uk.ac.tees.mad.carcare.model.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import uk.ac.tees.mad.carcare.model.dataclass.firebase.FirestoreResult
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareAppointmentRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareFirestoreRepository

class AppointmentSynchronizer(
    private val carCareFirestoreRepository: CarCareFirestoreRepository,
    private val carCareAppointmentRepository: CarCareAppointmentRepository,
    private val authRepository: AuthRepository,
) {
    fun startSync() {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                syncFromFirestoreToLocal(userId)
                carCareAppointmentRepository.getAppointmentForSync().collectLatest { appointments ->
                    appointments.forEach { appointment ->
                        if (appointment.needsUpdate) {
                            if (!appointment.isDeleted) {
                                carCareFirestoreRepository.updateAppointment(userId, appointment)
                                    .collectLatest { firestoreResult ->
                                        if (firestoreResult is FirestoreResult.Success) {
                                            Log.d(
                                                "AppointmentSync",
                                                "Successfully updated appointment in Firestore"
                                            )
                                            // Clear needsUpdate
                                            carCareAppointmentRepository.updateAppointmentFromFirestore(
                                                appointment.copy(
                                                    needsUpdate = false
                                                )
                                            )
                                        }
                                    }
                            }
                        } else if (appointment.isDeleted) {
                            carCareFirestoreRepository.deleteAppointment(userId, appointment)
                                .collectLatest { firestoreResult ->
                                    if (firestoreResult is FirestoreResult.Success) {
                                        Log.d(
                                            "AppointmentSync",
                                            "Successfully deleted appointment from Firestore"
                                        )
                                        carCareAppointmentRepository.deleteAppointment(appointment)
                                    }
                                }
                        }
                    }
                }

            }
        }
    }

    private suspend fun syncFromFirestoreToLocal(userId: String) {
        val localAppointments =
            carCareAppointmentRepository.getAllAppointmentDataForUser(userId).firstOrNull()
                ?: emptyList()
        carCareFirestoreRepository.getAppointmentForUser(userId).collectLatest { firestoreResult ->
            when (firestoreResult) {
                is FirestoreResult.Error -> {
                    Log.e(
                        "AppointmentSync",
                        "Error fetching Appointments from Firestore",
                        firestoreResult.exception
                    )
                }

                is FirestoreResult.Success -> {
                    val remoteAppointments = firestoreResult.data
                    remoteAppointments.forEach { remoteAppointment ->
                        val localAppointment =
                            localAppointments.find { it.firestoreId == remoteAppointment.firestoreId }
                        if (localAppointment == null) {
                            carCareAppointmentRepository.insertAppointment(remoteAppointment)
                        } else if (remoteAppointment != localAppointment) {
                            carCareAppointmentRepository.updateAppointmentFromFirestore(
                                remoteAppointment
                            )
                        }
                    }
                }

                else -> {}
            }

        }
    }
}