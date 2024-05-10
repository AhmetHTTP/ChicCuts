package com.chiccuts.models

import java.util.Date

/**
 * Represents a barber in the ChicCuts application.
 * @property barberId The unique identifier for the barber.
 * @property name The barber's full name.
 * @property email The barber's email address.
 * @property shopName The name of the barber shop where the barber works.
 * @property location The geographical location of the barber shop.
 * @property serviceTypes A list of services the barber offers.
 * @property rating The average rating of the barber, typically from user reviews.
 * @property profilePictureUrl URL to the barber's profile picture.
 * @property isActive Boolean value indicating if the barber is currently active.
 * @property registrationDate The date when the barber registered on the platform.
 */
data class Barber(
    val barberId: String,
    val name: String,
    val email: String,
    val shopName: String,
    val location: String,
    val serviceTypes: List<String>,
    val rating: Double,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val registrationDate: Date = Date()
)
