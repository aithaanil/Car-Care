package uk.ac.tees.mad.carcare.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment
import uk.ac.tees.mad.carcare.model.room.CarCareAppointmentDao

/**
 * Repository for managing [CarCareAppointment] data.
 *
 * This repository abstracts the data layer from the rest of the application, providing a clean API
 * for interacting with the underlying data sources, which in this case is a Room database via
 * [CarCareAppointmentDao].
 */
class CarCareAppointmentRepository(private val carCareAppointmentDao: CarCareAppointmentDao) {

    /**
     * Inserts a new appointment into the data source.
     *
     * @param appointment The [CarCareAppointment] to insert.
     */
    suspend fun insertAppointment(appointment: CarCareAppointment) {
        carCareAppointmentDao.insertAppointment(appointment)
    }

    /**
     * Updates or inserts an appointment in the data source.
     *
     * @param appointment The [CarCareAppointment] to upsert.
     */
    suspend fun upsertAppointment(appointment: CarCareAppointment) {
        carCareAppointmentDao.upsertAppointment(appointment)
    }

    /**
     * Deletes an appointment from the data source.
     *
     * @param appointment The [CarCareAppointment] to delete.
     */
    suspend fun deleteAppointment(appointment: CarCareAppointment) {
        carCareAppointmentDao.deleteAppointment(appointment)
    }

    /**
     * Retrieves all appointments for a given user that are not marked as deleted.
     *
     * @param userId The ID of the user.
     * @return A [Flow] emitting a list of [CarCareAppointment]s.
     */
    fun getAllAppointmentDataForUser(userId: String): Flow<List<CarCareAppointment>> {
        return carCareAppointmentDao.getAllAppointmentDataForUser(userId)
    }

    /**
     * Retrieves an appointment by its local database ID.
     *
     * @param id The local database ID of the appointment.
     * @return The corresponding [CarCareAppointment] or null if not found.
     */
    suspend fun getAppointmentById(id: Int): CarCareAppointment? {
        return carCareAppointmentDao.getAppointmentById(id)
    }

    /**
     * Retrieves an appointment by its Firestore ID.
     *
     * @param firestoreId The Firestore ID of the appointment.
     * @return The corresponding [CarCareAppointment] or null if not found.
     */
    suspend fun getAppointmentByFirestoreId(firestoreId: String): CarCareAppointment? {
        return carCareAppointmentDao.getAppointmentByFirestoreId(firestoreId)
    }

    /**
     * Retrieves all appointments that need synchronization with the remote database.
     *
     * This includes appointments that have been updated or deleted locally.
     *
     * @return A [Flow] emitting a list of [CarCareAppointment]s.
     */
    fun getAppointmentForSync(): Flow<List<CarCareAppointment>> {
        return carCareAppointmentDao.getAppointmentForSync()
    }

    /**
     * Deletes all appointments for a given user.
     *
     * @param userId The ID of the user whose appointments should be deleted.
     * @return The number of rows affected.
     */
    suspend fun deleteAllAppointmentDataForUser(userId: String): Int {
        return carCareAppointmentDao.deleteAllAppointmentDataForUser(userId)
    }

    /**
     * Deletes an appointment by its local database ID.
     *
     * @param id The local database ID of the appointment to delete.
     * @return The number of rows affected.
     */
    suspend fun deleteAppointmentDataById(id: Int): Int {
        return carCareAppointmentDao.deleteAppointmentDataById(id)
    }

    /**
     * Deletes an appointment by its Firestore ID.
     *
     * @param firestoreId The Firestore ID of the appointment to delete.
     * @return The number of rows affected.
     */
    suspend fun deleteAppointmentDataByFirestoreId(firestoreId: String): Int {
        return carCareAppointmentDao.deleteAppointmentDataByFirestoreId(firestoreId)
    }

    /**
     * Updates an appointment using its local database ID.
     *
     * @param id The local database ID of the appointment to update.
     * @param service The new service.
     * @param serviceCenter The new service center.
     * @param appointmentDate The new appointment date.
     * @param appointmentTime The new appointment time.
     * @param appointmentServiceDescription The new appointment service description.
     * @param carImage The new car image.
     * @param appointmentBookedOn The new appointment booking date.
     * @param isDeleted Whether the appointment is marked as deleted.
     * @param needsUpdate Whether the appointment needs to be updated in the remote database.
     * @return The number of rows affected.
     */
    suspend fun updateAppointmentById(
        id: Int,
        service: String,
        serviceCenter: String,
        appointmentDate: String,
        appointmentTime: String,
        appointmentServiceDescription: String,
        carImage: String,
        appointmentBookedOn: String,
        isDeleted: Boolean,
        needsUpdate: Boolean
    ): Int {
        return carCareAppointmentDao.updateAppointmentById(
            id,
            service,
            serviceCenter,
            appointmentDate,
            appointmentTime,
            appointmentServiceDescription,
            carImage,
            appointmentBookedOn,
            isDeleted,
            needsUpdate
        )
    }


    suspend fun updateAppointmentFromFirestore(
        appointment: CarCareAppointment
    ) {
        carCareAppointmentDao.updateAppointmentFromFirestore(
            appointment.firestoreId,
            appointment.service,
            appointment.serviceCenter,
            appointment.appointmentDate,
            appointment.appointmentTime,
            appointment.appointmentServiceDescription,
            appointment.carImage,
            appointment.appointmentBookedOn,
            appointment.isDeleted,
            appointment.needsUpdate
        )
    }
}