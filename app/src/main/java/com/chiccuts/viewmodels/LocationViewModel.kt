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

    fun loadBarbersByLocation(location: String) {
        FirestoreUtil.getBarbersByLocation(location) { barbersList, message ->
            _barbers.value = barbersList
            // İhtiyaca göre 'message' ile ilgili işlem yapılabilir. Örneğin, bir log kaydı veya kullanıcıya bilgi verme.
        }
    }

    fun loadHairdressersByLocation(location: String) {
        FirestoreUtil.getHairdressersByLocation(location) { hairdressersList, message ->
            _hairdressers.value = hairdressersList
            // İhtiyaca göre 'message' ile ilgili işlem yapılabilir. Örneğin, bir log kaydı veya kullanıcıya bilgi verme.
        }
    }
}
