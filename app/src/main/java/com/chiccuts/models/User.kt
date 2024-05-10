package com.chiccuts.models

import java.util.Date

/**
 * Represents a user in the ChicCuts application.
 * @property userId The unique identifier for the user.
 * @property username The user's chosen username.
 * @property email The user's email address.
 * @property gender The user's gender, can be "male", "female", or "other".
 * @property userType The type of user, e.g., "customer", "barber", "admin".
 * @property profilePictureUrl URL to the user's profile picture.
 * @property registrationDate The date when the user registered.
 * @property isActive Boolean value indicating if the user's account is active.
 */
data class User(
    val userId: String,
    val username: String,
    val email: String,
    val gender: String,
    val userType: String,
    val profilePictureUrl: String? = null,
    val registrationDate: Date = Date(),
    val isActive: Boolean = true
)
