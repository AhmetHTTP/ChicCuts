package com.chiccuts.utils

import com.chiccuts.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreUtil {

    private val db = FirebaseFirestore.getInstance()

    fun addUser(user: User, onComplete: (Boolean, String) -> Unit) {
        db.collection("users").document(user.userId).set(user)
            .addOnSuccessListener { onComplete(true, "User added successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error adding user")
            }
    }

    fun getUser(userId: String, onComplete: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(User::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getBarber(barberId: String, onComplete: (Barber?) -> Unit) {
        db.collection("barbers").document(barberId).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(Barber::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getHairdresser(hairdresserId: String, onComplete: (Hairdresser?) -> Unit) {
        db.collection("hairdressers").document(hairdresserId).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(Hairdresser::class.java))
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    suspend fun addAppointment(appointment: Appointment, onComplete: (Boolean, String) -> Unit) {
        val appointmentRef = db.collection("appointments").document()
        appointment.appointmentId = appointmentRef.id

        try {
            val user = db.collection("users").document(appointment.userId).get().await().toObject(User::class.java)
            if (user != null) {
                appointment.userUsername = user.username
                appointment.userFirstName = user.firstName
                appointment.userLastName = user.lastName

                if (appointment.barberId != null) {
                    val barber = db.collection("barbers").document(appointment.barberId!!).get().await().toObject(Barber::class.java)
                    if (barber != null) {
                        appointment.salonName = barber.salonName
                        appointment.rating = barber.rating
                        appointment.profilePictureUrl = barber.profilePictureUrl
                    }
                } else if (appointment.hairdresserId != null) {
                    val hairdresser = db.collection("hairdressers").document(appointment.hairdresserId!!).get().await().toObject(Hairdresser::class.java)
                    if (hairdresser != null) {
                        appointment.salonName = hairdresser.salonName
                        appointment.rating = hairdresser.rating
                        appointment.profilePictureUrl = hairdresser.profilePictureUrl
                    }
                }

                appointmentRef.set(appointment).await()
                onComplete(true, "Appointment scheduled successfully")
            } else {
                onComplete(false, "Error fetching user details")
            }
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage ?: "Error scheduling appointment")
        }
    }

    fun updateAppointment(appointmentId: String, newDetails: Map<String, Any>, onComplete: (Boolean, String) -> Unit) {
        db.collection("appointments").document(appointmentId).update(newDetails)
            .addOnSuccessListener { onComplete(true, "Appointment updated successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error updating appointment")
            }
    }

    fun cancelAppointment(appointmentId: String, onComplete: (Boolean, String) -> Unit) {
        db.collection("appointments").document(appointmentId).delete()
            .addOnSuccessListener { onComplete(true, "Appointment cancelled successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error cancelling appointment")
            }
    }

    fun getAppointments(userId: String, isBusinessOwner: Boolean, onComplete: (List<Appointment>) -> Unit) {
        val collectionRef = db.collection("appointments")
        val appointments = mutableListOf<Appointment>()

        val task = if (isBusinessOwner) {
            collectionRef.whereEqualTo("barberId", userId).get().addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointments.add(appointment)
                }
            }.continueWithTask {
                collectionRef.whereEqualTo("hairdresserId", userId).get()
            }.addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointments.add(appointment)
                }
                onComplete(appointments)
            }
        } else {
            collectionRef.whereEqualTo("userId", userId).get().addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val appointment = document.toObject(Appointment::class.java)
                    appointments.add(appointment)
                }
                onComplete(appointments)
            }
        }

        task.addOnFailureListener { e ->
            onComplete(emptyList())
        }
    }

    fun getBarbersByLocation(location: String, onComplete: (List<Barber>) -> Unit) {
        db.collection("barbers").whereEqualTo("location", location).get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                onComplete(barbers)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun getHairdressersByLocation(location: String, onComplete: (List<Hairdresser>) -> Unit) {
        db.collection("hairdressers").whereEqualTo("location", location).get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                onComplete(hairdressers)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun getBarbersSortedByRating(onComplete: (List<Barber>) -> Unit) {
        db.collection("barbers").orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                onComplete(barbers)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun getHairdressersSortedByRating(onComplete: (List<Hairdresser>) -> Unit) {
        db.collection("hairdressers").orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                onComplete(hairdressers)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    // Yeni fonksiyon: Profil fotoğrafı URL'sini Firestore'a kaydet
    fun updateProfilePictureUrl(userId: String, url: String, onComplete: (Boolean, String) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.update("profilePictureUrl", url)
            .addOnSuccessListener {
                onComplete(true, "Profile picture URL updated successfully for user")
            }
            .addOnFailureListener {
                val barberRef = db.collection("barbers").document(userId)
                barberRef.update("profilePictureUrl", url)
                    .addOnSuccessListener {
                        onComplete(true, "Profile picture URL updated successfully for barber")
                    }
                    .addOnFailureListener {
                        val hairdresserRef = db.collection("hairdressers").document(userId)
                        hairdresserRef.update("profilePictureUrl", url)
                            .addOnSuccessListener {
                                onComplete(true, "Profile picture URL updated successfully for hairdresser")
                            }
                            .addOnFailureListener { e ->
                                onComplete(false, e.localizedMessage ?: "Error updating profile picture URL")
                            }
                    }
            }
    }
}
