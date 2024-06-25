package com.chiccuts.models

import java.util.Date

data class Barber(
    val barberId: String = "",
    val username: String = "",
    val email: String = "",
    val salonName: String = "",
    val location: String = "",
    val city: String = "",
    val serviceTypes: List<String> = listOf(),
    val rating: Double = 0.0,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val registrationDate: Date = Date(),
    val serviceDescription: String = "",
    var ratingsCount: Int? = 0,
    val address: String = "", // Eklenen alan
    val businessName: String = "", // Eklenen alan
    val userId: String = "" // Eklenen alan
)
