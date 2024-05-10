package com.chiccuts.utils

import com.chiccuts.models.User
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

/**
 * Utility class to handle Firestore operations efficiently.
 */
object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Add a new user to the Firestore database.
     */
    fun addUser(user: User, onComplete: (Boolean, String) -> Unit) {
        firestoreInstance.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                onComplete(true, "User added successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error adding user")
            }
    }

    /**
     * Fetch a user from Firestore by their userId.
     */
    fun getUser(userId: String, onComplete: (User?, String) -> Unit) {
        firestoreInstance.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    onComplete(user, "User fetched successfully")
                } else {
                    onComplete(null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message ?: "Error fetching user")
            }
    }

    /**
     * Add a new barber to the Firestore database.
     */
    fun addBarber(barber: Barber, onComplete: (Boolean, String) -> Unit) {
        firestoreInstance.collection("barbers")
            .document(barber.barberId)
            .set(barber)
            .addOnSuccessListener {
                onComplete(true, "Barber added successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error adding barber")
            }
    }

    /**
     * Fetch a barber from Firestore by their barberId.
     */
    fun getBarber(barberId: String, onComplete: (Barber?, String) -> Unit) {
        firestoreInstance.collection("barbers")
            .document(barberId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val barber = documentSnapshot.toObject<Barber>()
                if (barber != null) {
                    onComplete(barber, "Barber fetched successfully")
                } else {
                    onComplete(null, "Barber not found")
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.message ?: "Error fetching barber")
            }
    }

    /**
     * Add a new hairdresser to the Firestore database.
     */
    fun addHairdresser(hairdresser: Hairdresser, onComplete: (Boolean, String) -> Unit) {
        firestoreInstance.collection("hairdressers")
            .document(hairdresser.hairdresserId)
            .set(hairdresser)
            .addOnSuccessListener {
                onComplete(true, "Hairdresser added successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error adding hairdresser")
            }
    }

    /**
     * Schedule a new appointment.
     */
    fun addAppointment(appointment: Appointment, onComplete: (Boolean, String) -> Unit) {
        firestoreInstance.collection("appointments")
            .document(appointment.appointmentId)
            .set(appointment)
            .addOnSuccessListener {
                onComplete(true, "Appointment scheduled successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error scheduling appointment")
            }
    }

    /**
     * Fetch appointments for a specific user.
     */
    fun getAppointmentsForUser(userId: String, onComplete: (List<Appointment>, String) -> Unit) {
        firestoreInstance.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.toObjects(Appointment::class.java)
                onComplete(appointments, "Appointments fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.message ?: "Error fetching appointments")
            }
    }
}
