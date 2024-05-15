package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.utils.FirestoreUtil

class LocationViewModel : ViewModel() {

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _hairdressers = MutableLiveData<List<Hairdresser>>()
    val hairdressers: LiveData<List<Hairdresser>> = _hairdressers

    fun fetchBarbersByLocation(location: String) {
        FirestoreUtil.getBarbersByLocation(location) { barbers ->
            _barbers.postValue(barbers)
        }
    }

    fun fetchHairdressersByLocation(location: String) {
        FirestoreUtil.getHairdressersByLocation(location) { hairdressers ->
            _hairdressers.postValue(hairdressers)
        }
    }
}
