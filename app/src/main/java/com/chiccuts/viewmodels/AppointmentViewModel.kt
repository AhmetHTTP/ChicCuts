package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {

    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> = _appointments

    private val _appointmentStatus = MutableLiveData<String>()
    val appointmentStatus: LiveData<String> = _appointmentStatus

    fun fetchAppointments(userId: String, isBusinessOwner: Boolean) {
        if (userId.isEmpty()) {
            _appointmentStatus.postValue("User is not authenticated")
            return
        }

        viewModelScope.launch {
            FirestoreUtil.getAppointments(userId, isBusinessOwner) { appointments, error ->
                if (error != null) {
                    _appointmentStatus.postValue("Error fetching appointment data: ${error.message}")
                } else {
                    _appointments.postValue(appointments)
                }
            }
        }
    }

    fun addAppointment(appointment: Appointment) {
        if (appointment.userId.isEmpty()) {
            _appointmentStatus.postValue("User is not authenticated")
            return
        }

        viewModelScope.launch {
            FirestoreUtil.addAppointment(appointment) { success, message ->
                _appointmentStatus.postValue(message)
                if (success) {
                    fetchAppointments(appointment.userId, false) // Update appointment list
                }
            }
        }
    }

    fun clearData() {
        _appointments.postValue(emptyList())
        _appointmentStatus.postValue("")
    }
}
