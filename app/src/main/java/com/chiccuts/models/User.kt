package com.chiccuts.models

import java.util.Date

data class User(
    val userId: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val gender: String = "",
    val registrationDate: Date = Date(),
    val isActive: Boolean = true,
)
