package com.chiccuts.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _usersStatus = MutableLiveData<String>()
    val usersStatus: LiveData<String> = _usersStatus

    fun activateUsers() {
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .whereEqualTo("isActive", false)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            firestore.collection("users").document(document.id)
                                .update("isActive", true)
                                .addOnSuccessListener {
                                    _usersStatus.postValue("Users activated successfully")
                                }
                                .addOnFailureListener { e ->
                                    _usersStatus.postValue("Error activating users: ${e.message}")
                                }
                        }
                    }
            } catch (e: Exception) {
                _usersStatus.postValue("Error activating users: ${e.message}")
            }
        }
    }

    fun deactivateUsers() {
        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .whereEqualTo("isActive", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            firestore.collection("users").document(document.id)
                                .update("isActive", false)
                                .addOnSuccessListener {
                                    _usersStatus.postValue("Users deactivated successfully")
                                }
                                .addOnFailureListener { e ->
                                    _usersStatus.postValue("Error deactivating users: ${e.message}")
                                }
                        }
                    }
            } catch (e: Exception) {
                _usersStatus.postValue("Error deactivating users: ${e.message}")
            }
        }
    }
}
