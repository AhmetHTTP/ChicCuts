package com.chiccuts.models

import java.util.Date

data class Appointment(
    var appointmentId: String = "",
    val userId: String = "",
    val barberId: String? = null,
    val hairdresserId: String? = null,
    val serviceType: String = "",
    val appointmentTime: Date = Date(),
    var location: String = "",
    var salonName: String = "",
    var rating: Double = 0.0,
    var userProfilePictureUrl: String? = null,
    var businessProfilePictureUrl: String? = null,
    var userUsername: String = "",
    var userFirstName: String = "",
    var userLastName: String = "",
    var isRated: Boolean = false
)