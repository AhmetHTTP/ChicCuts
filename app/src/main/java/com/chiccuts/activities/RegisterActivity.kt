package com.chiccuts.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setupRegistration()
    }

    private fun setupRegistration() {
        binding.btnRegisterBarber.setOnClickListener {
            val name = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInput(name, email, password)) {
                registerBarber(name, email, password)
            }
        }

        binding.btnRegisterHairdresser.setOnClickListener {
            val name = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInput(name, email, password)) {
                registerHairdresser(name, email, password)
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerBarber(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val barber = Barber(
                    barberId = auth.currentUser!!.uid,
                    name = name,
                    email = email,
                    location = "Default Location", // Modify as needed
                    serviceTypes = listOf("Haircut", "Shave"),
                    rating = 5.0
                )
                firestore.collection("barbers").document(barber.barberId).set(barber)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Barber registration successful", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save barber data: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerHairdresser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val hairdresser = Hairdresser(
                    hairdresserId = auth.currentUser!!.uid,
                    name = name,
                    email = email,
                    location = "Default Location", // Modify as needed
                    serviceTypes = listOf("Styling", "Coloring"),
                    rating = 5.0
                )
                firestore.collection("hairdressers").document(hairdresser.hairdresserId).set(hairdresser)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Hairdresser registration successful", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save hairdresser data: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
