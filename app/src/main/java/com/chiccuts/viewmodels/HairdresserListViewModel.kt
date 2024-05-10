package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiccuts.models.Hairdresser
import com.google.firebase.firestore.FirebaseFirestore

class HairdresserListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _hairdressers = MutableLiveData<List<Hairdresser>>()
    val hairdressers: LiveData<List<Hairdresser>> = _hairdressers

    init {
        loadHairdressers()
    }

    private fun loadHairdressers() {
        firestore.collection("hairdressers")
            .get()
            .addOnSuccessListener { documents ->
                val hairdressersList = documents.toObjects(Hairdresser::class.java)
                _hairdressers.value = hairdressersList
            }
            .addOnFailureListener { e ->
                // Log error or handle the error according to the app's need
                e.printStackTrace()
            }
    }
}
