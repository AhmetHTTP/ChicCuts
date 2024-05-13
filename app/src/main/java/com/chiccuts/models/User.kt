package com.chiccuts.models

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val gender: String = "",
    val userType: String = "",
    val profilePictureUrl: String? = null,
    val registrationDate: Timestamp = Timestamp.now(),  // Firebase Timestamp kullanımı
    val isActive: Boolean = true
)
