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

    fun fetchBarbersSortedByRating() {
        FirestoreUtil.getBarbersSortedByRating { barbers ->
            _barbers.postValue(barbers)
        }
    }

    fun fetchHairdressersSortedByRating() {
        FirestoreUtil.getHairdressersSortedByRating { hairdressers ->
            _hairdressers.postValue(hairdressers)
        }
    }
}
