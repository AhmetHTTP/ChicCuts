package com.chiccuts.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.databinding.ActivityAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnActivateUsers.setOnClickListener {
            activateUsers()
        }

        binding.btnDeactivateUsers.setOnClickListener {
            deactivateUsers()
        }
    }

    private fun activateUsers() {
        firestore.collection("users")
            .whereEqualTo("isActive", false)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("users").document(document.id)
                        .update("isActive", true)
                        .addOnSuccessListener {
                            binding.statusTextView.text = "User activated successfully"
                        }
                        .addOnFailureListener { e ->
                            binding.statusTextView.text = "Error activating user: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                binding.statusTextView.text = "Error retrieving users: ${e.message}"
            }
    }

    private fun deactivateUsers() {
        firestore.collection("users")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("users").document(document.id)
                        .update("isActive", false)
                        .addOnSuccessListener {
                            binding.statusTextView.text = "User deactivated successfully"
                        }
                        .addOnFailureListener { e ->
                            binding.statusTextView.text = "Error deactivating user: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { e ->
                binding.statusTextView.text = "Error retrieving users: ${e.message}"
            }
    }
}
