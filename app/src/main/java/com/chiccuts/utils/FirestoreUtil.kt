package com.chiccuts.utils

import com.chiccuts.models.User
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirestoreUtil {
    private val db = FirebaseFirestore.getInstance()

    fun addUser(user: User, onComplete: (Boolean, String) -> Unit) {
        db.collection("users").document(user.userId).set(user)
            .addOnSuccessListener { onComplete(true, "User added successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error adding user")
            }
    }

    fun getUser(userId: String, onComplete: (User?, String) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject(User::class.java), "User fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(null, exception.localizedMessage ?: "Error fetching user")
            }
    }

    fun addBarber(barber: Barber, onComplete: (Boolean, String) -> Unit) {
        db.collection("barbers").document(barber.barberId).set(barber)
            .addOnSuccessListener { onComplete(true, "Barber added successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error adding barber")
            }
    }

    fun addHairdresser(hairdresser: Hairdresser, onComplete: (Boolean, String) -> Unit) {
        db.collection("hairdressers").document(hairdresser.hairdresserId).set(hairdresser)
            .addOnSuccessListener { onComplete(true, "Hairdresser added successfully") }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error adding hairdresser")
            }
    }

    fun addAppointment(appointment: Appointment, onComplete: (Boolean, String) -> Unit) {
        val appointmentRef = db.collection("appointments").document()
        appointment.appointmentId = appointmentRef.id
        appointmentRef.set(appointment)
            .addOnSuccessListener {
                onComplete(true, "Appointment scheduled successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage ?: "Error scheduling appointment")
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

    fun getAppointmentsForBusiness(userId: String, onComplete: (List<Appointment>, String) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("barberId", userId)
            .get()
            .addOnSuccessListener { barberDocuments ->
                val barberAppointments = barberDocuments.toObjects(Appointment::class.java)

                db.collection("appointments")
                    .whereEqualTo("hairdresserId", userId)
                    .get()
                    .addOnSuccessListener { hairdresserDocuments ->
                        val hairdresserAppointments = hairdresserDocuments.toObjects(Appointment::class.java)
                        onComplete(barberAppointments + hairdresserAppointments, "Appointments fetched successfully")
                    }
                    .addOnFailureListener { exception ->
                        onComplete(emptyList(), exception.localizedMessage ?: "Error fetching appointments")
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.localizedMessage ?: "Error fetching appointments")
            }
    }

    fun getBarbersByLocation(location: String, onComplete: (List<Barber>, String) -> Unit) {
        db.collection("barbers").whereEqualTo("location", location).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.toObjects(Barber::class.java), "Barbers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.localizedMessage ?: "Error fetching barbers by location")
            }
    }

    fun getHairdressersByLocation(location: String, onComplete: (List<Hairdresser>, String) -> Unit) {
        db.collection("hairdressers").whereEqualTo("location", location).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.toObjects(Hairdresser::class.java), "Hairdressers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.localizedMessage ?: "Error fetching hairdressers by location")
            }
    }

    fun getBarbersSortedByRating(onComplete: (List<Barber>, String) -> Unit) {
        db.collection("barbers").orderBy("rating", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.toObjects(Barber::class.java), "Barbers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.localizedMessage ?: "Error fetching barbers sorted by rating")
            }
    }

    fun getHairdressersSortedByRating(onComplete: (List<Hairdresser>, String) -> Unit) {
        db.collection("hairdressers").orderBy("rating", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.toObjects(Hairdresser::class.java), "Hairdressers fetched successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(emptyList(), exception.localizedMessage ?: "Error fetching hairdressers sorted by rating")
            }
    }
}
