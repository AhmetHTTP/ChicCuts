package com.chiccuts.models

import java.util.Date

data class Appointment(
    var appointmentId: String = "",
    val userId: String = "",
    val barberId: String? = null,
    val hairdresserId: String? = null,
    val serviceType: String = "",
    val appointmentTime: Date = Date(),
    val location: String = "",
    var salonName: String = "",  // var olarak değiştirildi
    var rating: Double = 0.0,    // var olarak değiştirildi
    var profilePictureUrl: String? = null,  // var olarak değiştirildi
    var userUsername: String = "",  // Yeni alan
    var userFirstName: String = "",  // Yeni alan
    var userLastName: String = ""  // Yeni alan
)
