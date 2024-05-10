package com.chiccuts.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chiccuts.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.activities.LoginActivity

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadUserProfile()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit profile clicked", Toast.LENGTH_SHORT).show()
            // Navigate to EditProfileActivity or similar
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
                    binding.tvUsername.text = username ?: "Username not available"
                    binding.tvEmail.text = email ?: "Email not available"
                    Log.d("ProfileFragment", "Username: $username, Email: $email")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Error loading user data", e)
                }
        } else {
            Log.e("ProfileFragment", "No user logged in")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
