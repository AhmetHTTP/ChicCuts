package com.chiccuts.utils

import com.chiccuts.models.User
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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

    fun getUser(userId: String, onComplete: (User?, String) -> Unit) {
        firestoreInstance.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
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

    fun updateAppointment(appointmentId: String, newDetails: Map<String, Any>, onComplete: (Boolean, String) -> Unit) {
        firestoreInstance.collection("appointments")
            .document(appointmentId)
            .update(newDetails)
            .addOnSuccessListener {
                onComplete(true, "Appointment updated successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error updating appointment")
            }
    }

    fun cancelAppointment(appointmentId: String, onComplete: (Boolean, String) -> Unit) {
        updateAppointment(appointmentId, mapOf("status" to "cancelled"), onComplete)
    }

    fun getBarbersByLocation(location: String, onComplete: (List<Barber>, String) -> Unit) {
        firestoreInstance.collection("barbers")
            .whereEqualTo("location", location)
            .get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                onComplete(barbers, "Barbers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.message ?: "Error fetching barbers by location")
            }
    }

    fun getHairdressersByLocation(location: String, onComplete: (List<Hairdresser>, String) -> Unit) {
        firestoreInstance.collection("hairdressers")
            .whereEqualTo("location", location)
            .get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                onComplete(hairdressers, "Hairdressers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.message ?: "Error fetching hairdressers by location")
            }
    }

    fun getBarbersSortedByRating(onComplete: (List<Barber>, String) -> Unit) {
        firestoreInstance.collection("barbers")
            .orderBy("rating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                onComplete(barbers, "Barbers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.message ?: "Error fetching barbers sorted by rating")
            }
    }

    fun getHairdressersSortedByRating(onComplete: (List<Hairdresser>, String) -> Unit) {
        firestoreInstance.collection("hairdressers")
            .orderBy("rating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                onComplete(hairdressers, "Hairdressers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.message ?: "Error fetching hairdressers sorted by rating")
            }
    }
}
