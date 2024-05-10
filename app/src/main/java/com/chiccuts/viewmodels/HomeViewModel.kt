package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    private val _hairdressers = MutableLiveData<List<Hairdresser>>()
    val hairdressers: LiveData<List<Hairdresser>> = _hairdressers

    init {
        loadBarbers()
        loadHairdressers()
    }

    private fun loadBarbers() {
        firestore.collection("barbers")
            .get()
            .addOnSuccessListener { documents ->
                val barbersList = documents.toObjects(Barber::class.java)
                _barbers.value = barbersList
            }
            .addOnFailureListener {
                _barbers.value = listOf()
            }
    }

    private fun loadHairdressers() {
        firestore.collection("hairdressers")
            .get()
            .addOnSuccessListener { documents ->
                val hairdressersList = documents.toObjects(Hairdresser::class.java)
                _hairdressers.value = hairdressersList
            }
            .addOnFailureListener {
                _hairdressers.value = listOf()
            }
    }
}
