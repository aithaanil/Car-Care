package uk.ac.tees.mad.carcare.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment
import uk.ac.tees.mad.carcare.model.dataclass.firebase.FirestoreResult

class CarCareFirestoreRepository(
    private val firestore: FirebaseFirestore
) {
    private fun getCarCareAppointmentCollection(userId: String) =
        firestore.collection("users").document(userId).collection("appointments")

    fun addAppointment(
        userId: String,
        appointment: CarCareAppointment
    ): Flow<FirestoreResult<String>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val appointmentMap = appointment.toMapForFirestore()

            val documentReference =
                getCarCareAppointmentCollection(userId).add(appointmentMap).await()

            val firestoreId = documentReference.id
            emit(FirestoreResult.Success(firestoreId))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun getAppointmentForUser(userId: String): Flow<FirestoreResult<List<CarCareAppointment>>> =
        flow {
            emit(FirestoreResult.Loading)
            try {
                val querySnapshot = getCarCareAppointmentCollection(userId).orderBy(
                    "appointmentDate",
                    Query.Direction.DESCENDING
                ).get().await()
                val appointmentEntries = querySnapshot.documents.mapNotNull { document ->
                    val appointment = document.toObject(CarCareAppointment::class.java)
                    appointment?.copy(firestoreId = document.id)
                }
                emit(FirestoreResult.Success(appointmentEntries))
            } catch (e: Exception) {
                emit(FirestoreResult.Error(e))
            }
        }

    fun getAppointmentDetails(
        userId: String,
        firestoreId: String
    ): Flow<FirestoreResult<CarCareAppointment>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val documentSnapshot =
                getCarCareAppointmentCollection(userId).document(firestoreId).get().await()
            if (documentSnapshot.exists()) {
                val appointment = documentSnapshot.toObject(CarCareAppointment::class.java)
                appointment?.let {
                    emit(FirestoreResult.Success(it.copy(firestoreId = documentSnapshot.id)))
                } ?: emit(FirestoreResult.Error(Exception("Failed to parse appointment data")))
            } else {
                emit(FirestoreResult.Error(Exception("Appointment not found")))
            }
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun updateAppointment(
        userId: String,
        appointment: CarCareAppointment
    ): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Find the correct Firestore document ID
            val firestoreId = appointment.firestoreId
            if (firestoreId.isNotBlank()) {
                getCarCareAppointmentCollection(userId).document(firestoreId)
                    .update(appointment.toMapForFirestore()).await()
                emit(FirestoreResult.Success(true))
            } else {
                emit(FirestoreResult.Error(Exception("Can not find Appointment in the database")))
            }
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun deleteAppointment(
        userId: String,
        appointment: CarCareAppointment
    ): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            // Find the correct Firestore document ID
            val firestoreId = appointment.firestoreId
            getCarCareAppointmentCollection(userId).document(firestoreId).delete().await()
            emit(FirestoreResult.Success(true))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }
}

fun CarCareAppointment.toMapForFirestore(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "service" to service,
        "serviceCenter" to serviceCenter,
        "appointmentDate" to appointmentDate,
        "appointmentTime" to appointmentTime,
        "appointmentServiceDescription" to appointmentServiceDescription,
        "carImage" to carImage,
        "appointmentBookedOn" to appointmentBookedOn
    )
}