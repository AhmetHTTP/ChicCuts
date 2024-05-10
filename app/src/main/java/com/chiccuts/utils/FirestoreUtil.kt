package com.chiccuts.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.models.User
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.models.Appointment

/**
 * Utility class to handle Firestore operations.
 */
object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Add a new user to the Firestore database.
     */
    fun addUser(user: User, onComplete: (Boolean) -> Unit) {
        firestoreInstance.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Fetch a user from Firestore by their userId.
     */
    fun getUser(userId: String, onComplete: (User?) -> Unit) {
        firestoreInstance.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                onComplete(documentSnapshot.toObject(User::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Add a new barber to the Firestore database.
     */
    fun addBarber(barber: Barber, onComplete: (Boolean) -> Unit) {
        firestoreInstance.collection("barbers")
            .document(barber.barberId)
            .set(barber)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Fetch a barber from Firestore by their barberId.
     */
    fun getBarber(barberId: String, onComplete: (Barber?) -> Unit) {
        firestoreInstance.collection("barbers")
            .document(barberId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                onComplete(documentSnapshot.toObject(Barber::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Add a new hairdresser to the Firestore database.
     */
    fun addHairdresser(hairdresser: Hairdresser, onComplete: (Boolean) -> Unit) {
        firestoreInstance.collection("hairdressers")
            .document(hairdresser.hairdresserId)
            .set(hairdresser)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Schedule a new appointment.
     */
    fun addAppointment(appointment: Appointment, onComplete: (Boolean) -> Unit) {
        firestoreInstance.collection("appointments")
            .document(appointment.appointmentId)
            .set(appointment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    /**
     * Fetch appointments for a specific user.
     */
    fun getAppointmentsForUser(userId: String, onComplete: (List<Appointment>) -> Unit) {
        firestoreInstance.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.toObjects(Appointment::class.java)
                onComplete(appointments)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
