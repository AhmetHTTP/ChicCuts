package com.chiccuts.models

import java.util.Date

data class Hairdresser(
    val hairdresserId: String = "",
    val businessName: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val salonName: String = "",
    val location: String = "",
    val serviceTypes: List<String> = listOf(),
    val rating: Double = 0.0,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true, // isActive artık Boolean tipinde
    val registrationDate: Date = Date(),
    val userId: String = ""
)