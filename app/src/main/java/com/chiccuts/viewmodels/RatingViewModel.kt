package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.utils.FirestoreUtil

class RatingViewModel : ViewModel() {
    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _hairdressers = MutableLiveData<List<Hairdresser>>()
    val hairdressers: LiveData<List<Hairdresser>> = _hairdressers

    fun loadBarbersSortedByRating() {
        FirestoreUtil.getBarbersSortedByRating { barbersList, message ->
            _barbers.value = barbersList
            // Optionally handle the message, e.g., logging or user notifications
        }
    }

    fun loadHairdressersSortedByRating() {
        FirestoreUtil.getHairdressersSortedByRating { hairdressersList, message ->
            _hairdressers.value = hairdressersList
            // Optionally handle the message, e.g., logging or user notifications
        }
    }
}
