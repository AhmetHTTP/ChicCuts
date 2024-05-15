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

    fun fetchAppointments(userId: String, isBusinessUser: Boolean) {
        viewModelScope.launch {
            FirestoreUtil.getAppointments(userId, isBusinessUser) { appointments ->
                _appointments.postValue(appointments)
            }
        }
    }

    fun addAppointment(appointment: Appointment) {
        viewModelScope.launch {
            FirestoreUtil.addAppointment(appointment) { success, message ->
                _appointmentStatus.postValue(message)
                if (success) {
                    fetchAppointments(appointment.userId, false)
                }
            }
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            FirestoreUtil.cancelAppointment(appointmentId) { success, message ->
                if (success) {
                    _appointments.value = _appointments.value?.filter { it.appointmentId != appointmentId }
                }
                _appointmentStatus.postValue(message)
            }
        }
    }
}
