package com.chiccuts.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.R
import com.chiccuts.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRegistration()
    }

    private fun setupRegistration() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val gender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                R.id.rbOther -> "Other"
                else -> ""
            }

            if (validateInput(username, email, password, gender)) {
                registerUser(username, email, password, gender)
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            // Navigate back to LoginActivity
            finish()
        }
    }

    private fun validateInput(username: String, email: String, password: String, gender: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerUser(username: String, email: String, password: String, gender: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(
                    userId = auth.currentUser!!.uid,
                    username = username,
                    email = email,
                    gender = gender,
                    userType = "customer", // Default user type
                    profilePictureUrl = null,
                    isActive = true
                )
                firestore.collection("users").document(user.userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        // Optionally navigate to the main activity or wherever appropriate
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
