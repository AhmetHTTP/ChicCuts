package com.chiccuts.models

import java.util.Date

data class Barber(
    val barberId: String = "",
    val name: String = "",
    val email: String = "",
    val shopName: String = "",
    val location: String = "",
    val serviceTypes: List<String> = listOf(),
    val rating: Double = 0.0,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val registrationDate: Date = Date()
)
