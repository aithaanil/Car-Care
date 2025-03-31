package uk.ac.tees.mad.carcare.model.dataclass.firebase


data class CarCareAppointment(
    val id: Int = 0,
    var firestoreId: String = "",
    val userId: String = "",
    val service: String = "",
    val serviceCenter: String = "",
    val appointmentDate: String = "",
    val appointmentTime: String = "",
    val appointmentServiceDescription: String = "",
    val carImage: String = "",
    val appointmentBookedOn: String = System.currentTimeMillis().toString(),
    val isDeleted: Boolean = false,
    val needsUpdate: Boolean = false
)