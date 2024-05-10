package com.chiccuts.models

import java.util.Date

data class Hairdresser(
    val hairdresserId: String = "",
    val name: String = "",
    val email: String = "",
    val salonName: String = "",
    val location: String = "",
    val serviceTypes: List<String> = listOf(),
    val rating: Double = 0.0,  // Varsayılan başlangıç puanı olarak 0.0 atanabilir.
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val registrationDate: Date = Date()  // Kayıt tarihi için varsayılan değer olarak mevcut tarih.
)
