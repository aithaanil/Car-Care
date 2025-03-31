package uk.ac.tees.mad.carcare.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment

@Dao
interface CarCareAppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: CarCareAppointment)

    @Upsert
    suspend fun upsertAppointment(appointment: CarCareAppointment)

    @Delete
    suspend fun deleteAppointment(appointment: CarCareAppointment)

    @Query("SELECT * FROM carcare_appointment_data WHERE userId = :userId AND isDeleted = 0 ORDER BY appointmentBookedOn DESC")
    fun getAllAppointmentDataForUser(userId: String): Flow<List<CarCareAppointment>>

    @Query("SELECT * FROM carcare_appointment_data WHERE id = :id")
    suspend fun getAppointmentById(id: Int): CarCareAppointment?

    @Query("SELECT * FROM carcare_appointment_data WHERE firestoreId = :firestoreId")
    suspend fun getAppointmentByFirestoreId(firestoreId: String): CarCareAppointment?

    @Query("SELECT * FROM carcare_appointment_data WHERE (needsUpdate = 1 OR isDeleted= 1)")
    fun getAppointmentForSync(): Flow<List<CarCareAppointment>>

    @Query("DELETE FROM carcare_appointment_data WHERE userId = :userId")
    suspend fun deleteAllAppointmentDataForUser(userId: String): Int

    @Query("DELETE FROM carcare_appointment_data WHERE id = :id")
    suspend fun deleteAppointmentDataById(id: Int): Int

    @Query("DELETE FROM carcare_appointment_data WHERE firestoreId = :firestoreId")
    suspend fun deleteAppointmentDataByFirestoreId(firestoreId: String): Int

    @Query(
        """
        UPDATE carcare_appointment_data
        SET service = :service,
        serviceCenter = :serviceCenter,
        appointmentDate = :appointmentDate,
        appointmentTime = :appointmentTime,
        appointmentServiceDescription = :appointmentServiceDescription,
        carImage = :carImage,
        appointmentBookedOn = :appointmentBookedOn,
        isDeleted = :isDeleted,
        needsUpdate = :needsUpdate
        WHERE id = :id
    """
    )
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
    ): Int

    @Query(
        """
        UPDATE carcare_appointment_data
        SET service = :service,
        serviceCenter = :serviceCenter,
        appointmentDate = :appointmentDate,
        appointmentTime = :appointmentTime,
        appointmentServiceDescription = :appointmentServiceDescription,
        carImage = :carImage,
        appointmentBookedOn = :appointmentBookedOn,
        isDeleted = :isDeleted,
        needsUpdate = :needsUpdate
        WHERE firestoreId = :firestoreId
    """
    )
    suspend fun updateAppointmentFromFirestore(
        firestoreId: String,
        service: String,
        serviceCenter: String,
        appointmentDate: String,
        appointmentTime: String,
        appointmentServiceDescription: String,
        carImage: String,
        appointmentBookedOn: String,
        isDeleted: Boolean,
        needsUpdate: Boolean
    ): Int
}