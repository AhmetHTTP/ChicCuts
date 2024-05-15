package com.chiccuts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.databinding.ActivityRegisterBusinessBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class RegisterBusinessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBusinessBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val businessName = binding.etBusinessName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val selectedBusinessTypeId = binding.rgBusinessType.checkedRadioButtonId

            if (businessName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedBusinessTypeId == -1) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val businessType = findViewById<RadioButton>(selectedBusinessTypeId).text.toString()
            val businessData = hashMapOf(
                "username" to username,  // Ensure username is added here
                "businessName" to businessName,
                "email" to email,
                "isActive" to false,
                "location" to "Default Location",
                "salonName" to businessName,  // "name" yerine "salonName" kullanıyoruz
                "profilePictureUrl" to null,
                "rating" to 5,
                "registrationDate" to Date(),
                "serviceTypes" to listOf("Styling", "Coloring")
            )

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        businessData["userId"] = userId
                        val collection = if (businessType == "Barber") "barbers" else "hairdressers"
                        businessData["${businessType.lowercase()}Id"] = userId

                        firestore.collection(collection).document(userId).set(businessData).addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save business data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
