package com.chiccuts.models

import java.util.Date

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val gender: String = "",
    val userType: String = "",
    val profilePictureUrl: String? = null,
    val registrationDate: Date = Date(),
    val isActive: Boolean = true
)
