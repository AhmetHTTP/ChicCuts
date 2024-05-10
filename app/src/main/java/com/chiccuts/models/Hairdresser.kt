package com.chiccuts.models

import java.util.Date

/**
 * Represents a hairdresser in the ChicCuts application.
 * @property hairdresserId The unique identifier for the hairdresser.
 * @property name The hairdresser's full name.
 * @property email The hairdresser's email address.
 * @property salonName The name of the salon where the hairdresser works.
 * @property location The geographical location of the salon.
 * @property serviceTypes A list of services the hairdresser offers.
 * @property rating The average rating of the hairdresser, typically from user reviews.
 * @property profilePictureUrl URL to the hairdresser's profile picture.
 * @property isActive Boolean value indicating if the hairdresser is currently active.
 * @property registrationDate The date when the hairdresser registered on the platform.
 */
data class Hairdresser(
    val hairdresserId: String,
    val name: String,
    val email: String,
    val salonName: String,
    val location: String,
    val serviceTypes: List<String>,
    val rating: Double,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val registrationDate: Date = Date()
)
