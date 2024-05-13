package com.chiccuts.viewmodels

import androidx.lifecycle.ViewModel
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil

class AppointmentViewModel : ViewModel() {

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
            onComplete(success, message)
        }
    }
}
