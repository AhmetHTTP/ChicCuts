package com.chiccuts.models

import java.util.Date

data class Appointment(
    var appointmentId: String = "",
    val userId: String = "",
    val barberId: String? = null,
    val hairdresserId: String? = null,
    val serviceType: String = "",
    val appointmentTime: Date = Date(),
    val location: String = ""
)
