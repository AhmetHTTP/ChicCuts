package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth

class AppointmentViewModel : ViewModel() {
    private val appointments = MutableLiveData<List<Appointment>>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun bookAppointment(appointment: Appointment, onComplete: (Boolean, String) -> Unit) {
        FirestoreUtil.addAppointment(appointment) { success, message ->
            onComplete(success, message)
        }
    }

    fun updateAppointment(appointmentId: String, newDetails: Map<String, Any>, onComplete: (Boolean, String) -> Unit) {
        FirestoreUtil.updateAppointment(appointmentId, newDetails) { success, message ->
            onComplete(success, message)
        }
    }

    fun cancelAppointment(appointmentId: String, onComplete: (Boolean, String) -> Unit) {
        FirestoreUtil.cancelAppointment(appointmentId) { success, message ->
            if (success) {
                fetchAppointmentsForCurrentUser()
            }
            onComplete(success, message)
        }
    }

    fun fetchAppointments(userId: String, isBusinessOwner: Boolean, onComplete: (List<Appointment>) -> Unit) {
        FirestoreUtil.getAppointments(userId, isBusinessOwner) { appointments ->
            this.appointments.postValue(appointments)
            onComplete(appointments)
        }
    }

    fun fetchAppointmentsForCurrentUser() {
        val currentUser = getCurrentUser()
        fetchAppointments(currentUser.first, currentUser.second) { }
    }

    fun getAppointments(): LiveData<List<Appointment>> {
        return appointments
    }

    private fun getCurrentUser(): Pair<String, Boolean> {
        val userId = auth.currentUser?.uid ?: ""
        // You should replace this with actual logic to determine if the user is a business owner
        val isBusinessOwner = false // Change this to the actual check
        return Pair(userId, isBusinessOwner)
    }
}
