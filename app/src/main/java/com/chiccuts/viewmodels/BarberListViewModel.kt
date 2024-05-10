package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiccuts.models.Barber
import com.google.firebase.firestore.FirebaseFirestore

class BarberListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _barbers = MutableLiveData<List<Barber>>()
    val barbers: LiveData<List<Barber>> = _barbers

    init {
        loadBarbers()
    }

    private fun loadBarbers() {
        firestore.collection("barbers")
            .get()
            .addOnSuccessListener { documents ->
                val barbersList = documents.toObjects(Barber::class.java)
                _barbers.value = barbersList
            }
            .addOnFailureListener { e ->
                // Log error or handle the error according to the app's need
                e.printStackTrace()
            }
    }
}
