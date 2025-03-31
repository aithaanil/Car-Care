package uk.ac.tees.mad.carcare.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.carcare.model.dataclass.firebase.CarCareAppointment

@Database(entities = [CarCareAppointment::class], version = 1, exportSchema = false)
abstract class CarCareDatabase : RoomDatabase() {
    abstract fun carCareAppointmentDao(): CarCareAppointmentDao
}