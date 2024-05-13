package com.chiccuts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chiccuts.activities.LoginActivity
import com.chiccuts.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Kullanıcı giriş kontrolü
        if (auth.currentUser == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return binding.root
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        loadUserProfile()
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val username = documentSnapshot.getString("username")
                    val email = documentSnapshot.getString("email")
                    if (username != null && email != null) {
                        binding.tvUsername.text = username
                        binding.tvEmail.text = email
                    } else {
                        loadBarberProfile(userId)
                    }
                }
                .addOnFailureListener {
                    loadBarberProfile(userId)
                }
        }
    }

    private fun loadBarberProfile(userId: String) {
        firestore.collection("barbers").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name")
                val email = documentSnapshot.getString("email")
                if (name != null && email != null) {
                    binding.tvUsername.text = name
                    binding.tvEmail.text = email
                } else {
                    loadHairdresserProfile(userId)
                }
            }
            .addOnFailureListener {
                loadHairdresserProfile(userId)
            }
    }

    private fun loadHairdresserProfile(userId: String) {
        firestore.collection("hairdressers").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name")
                val email = documentSnapshot.getString("email")
                if (name != null && email != null) {
                    binding.tvUsername.text = name
                    binding.tvEmail.text = email
                } else {
                    // If no profiles found
                    binding.tvUsername.text = "Profile not available"
                    binding.tvEmail.text = "Email not available"
                }
            }
            .addOnFailureListener {
                binding.tvUsername.text = "Profile not available"
                binding.tvEmail.text = "Email not available"
                Toast.makeText(context, "Failed to load any profile: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
