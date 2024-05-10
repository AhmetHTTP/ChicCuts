package com.chiccuts.models

import java.util.Date

/**
 * Represents an appointment in the ChicCuts application.
 * @property appointmentId The unique identifier for the appointment.
 * @property userId The unique identifier of the user who made the appointment.
 * @property barberId The unique identifier of the barber (if applicable).
 * @property hairdresserId The unique identifier of the hairdresser (if applicable).
 * @property serviceType The type of service requested for the appointment.
 * @property appointmentTime The scheduled time and date of the appointment.
 * @property location The location where the appointment will take place.
 * @property status The current status of the appointment (e.g., "Scheduled", "Completed", "Cancelled").
 * @property creationDate The date and time when the appointment was created.
 */
data class Appointment(
    val appointmentId: String = "",
    val userId: String = "",
    val barberId: String? = null,
    val hairdresserId: String? = null,
    val serviceType: String = "",
    val appointmentTime: Date = Date(),
    val location: String = "",
    val status: String = "Scheduled",  // Assuming "Scheduled" as the default status.
    val creationDate: Date = Date()
)
