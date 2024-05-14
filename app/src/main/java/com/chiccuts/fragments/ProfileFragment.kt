package com.chiccuts.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.chiccuts.R
import com.chiccuts.activities.LoginActivity
import com.chiccuts.databinding.FragmentProfileBinding
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.models.User
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

        binding.btnEdit.setOnClickListener {
            showEditDialog()
        }

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
                    val firstName = documentSnapshot.getString("firstName")
                    val lastName = documentSnapshot.getString("lastName")
                    val profilePictureUrl = documentSnapshot.getString("profilePictureUrl")
                    if (username != null && email != null) {
                        binding.tvUsername.text = getString(R.string.username_placeholder, username)
                        binding.tvName.text = getString(R.string.name_placeholder, "$firstName $lastName")
                        binding.tvEmail.text = getString(R.string.email_placeholder, email)
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profilePictureUrl).into(binding.ivProfileImage)
                        }
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
                val username = documentSnapshot.getString("username")
                val email = documentSnapshot.getString("email")
                val salonName = documentSnapshot.getString("salonName")
                val profilePictureUrl = documentSnapshot.getString("profilePictureUrl")
                if (username != null && email != null) {
                    binding.tvUsername.text = getString(R.string.username_placeholder, username)
                    binding.tvName.text = getString(R.string.salon_name_placeholder, salonName)
                    binding.tvEmail.text = getString(R.string.email_placeholder, email)
                    if (!profilePictureUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profilePictureUrl).into(binding.ivProfileImage)
                    }
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
                val username = documentSnapshot.getString("username")
                val email = documentSnapshot.getString("email")
                val salonName = documentSnapshot.getString("salonName")
                val profilePictureUrl = documentSnapshot.getString("profilePictureUrl")
                if (username != null && email != null) {
                    // _binding null olabilir, kontrol etmeniz gerekiyor
                    _binding?.let {
                        it.tvUsername.text = getString(R.string.username_placeholder, username)
                        it.tvName.text = getString(R.string.salon_name_placeholder, salonName)
                        it.tvEmail.text = getString(R.string.email_placeholder, email)
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profilePictureUrl).into(it.ivProfileImage)
                        }
                    }
                } else {
                    // _binding null olabilir, kontrol etmeniz gerekiyor
                    _binding?.let {
                        it.tvUsername.text = "Profile not available"
                        it.tvEmail.text = "Email not available"
                    }
                }
            }
            .addOnFailureListener {
                // _binding null olabilir, kontrol etmeniz gerekiyor
                _binding?.let {
                    it.tvUsername.text = "Profile not available"
                    it.tvEmail.text = "Email not available"
                }
                Toast.makeText(context, "Failed to load any profile: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showEditDialog() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        showUserEditDialog(user)
                    }
                } else {
                    firestore.collection("barbers").document(userId).get()
                        .addOnSuccessListener { barberDocument ->
                            if (barberDocument.exists()) {
                                val barber = barberDocument.toObject(Barber::class.java)
                                if (barber != null) {
                                    showBarberEditDialog(barber)
                                }
                            } else {
                                firestore.collection("hairdressers").document(userId).get()
                                    .addOnSuccessListener { hairdresserDocument ->
                                        if (hairdresserDocument.exists()) {
                                            val hairdresser = hairdresserDocument.toObject(Hairdresser::class.java)
                                            if (hairdresser != null) {
                                                showHairdresserEditDialog(hairdresser)
                                            }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    private fun showUserEditDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val etFirstName = dialogView.findViewById<EditText>(R.id.etFirstName)
        val etLastName = dialogView.findViewById<EditText>(R.id.etLastName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        etFirstName.setText(user.firstName)
        etLastName.setText(user.lastName)
        etEmail.setText(user.email)

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.edit_profile))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val updatedUser = user.copy(
                    firstName = etFirstName.text.toString(),
                    lastName = etLastName.text.toString(),
                    email = etEmail.text.toString()
                )
                saveUserProfile(updatedUser)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showBarberEditDialog(barber: Barber) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_business, null)
        val etSalonName = dialogView.findViewById<EditText>(R.id.etSalonName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        etSalonName.setText(barber.salonName)
        etEmail.setText(barber.email)

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.edit_profile))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val updatedBarber = barber.copy(
                    salonName = etSalonName.text.toString(),
                    email = etEmail.text.toString()
                )
                saveBarberProfile(updatedBarber)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showHairdresserEditDialog(hairdresser: Hairdresser) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_business, null)
        val etSalonName = dialogView.findViewById<EditText>(R.id.etSalonName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        etSalonName.setText(hairdresser.salonName)
        etEmail.setText(hairdresser.email)

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.edit_profile))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val updatedHairdresser = hairdresser.copy(
                    salonName = etSalonName.text.toString(),
                    email = etEmail.text.toString()
                )
                saveHairdresserProfile(updatedHairdresser)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun saveUserProfile(user: User) {
        firestore.collection("users").document(user.userId).set(user)
            .addOnSuccessListener {
                displayUserProfile(user)
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveBarberProfile(barber: Barber) {
        firestore.collection("barbers").document(barber.barberId).set(barber)
            .addOnSuccessListener {
                displayBarberProfile(barber)
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveHairdresserProfile(hairdresser: Hairdresser) {
        firestore.collection("hairdressers").document(hairdresser.hairdresserId).set(hairdresser)
            .addOnSuccessListener {
                displayHairdresserProfile(hairdresser)
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayUserProfile(user: User) {
        binding.tvUsername.text = getString(R.string.username_placeholder, user.username)
        binding.tvName.text = getString(R.string.name_placeholder, "${user.firstName} ${user.lastName}")
        binding.tvEmail.text = getString(R.string.email_placeholder, user.email)
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this).load(user.profilePictureUrl).into(binding.ivProfileImage)
        }
    }

    private fun displayBarberProfile(barber: Barber) {
        binding.tvUsername.text = getString(R.string.username_placeholder, barber.username)
        binding.tvName.text = getString(R.string.salon_name_placeholder, barber.salonName)
        binding.tvEmail.text = getString(R.string.email_placeholder, barber.email)
        if (!barber.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this).load(barber.profilePictureUrl).into(binding.ivProfileImage)
        }
    }

    private fun displayHairdresserProfile(hairdresser: Hairdresser) {
        binding.tvUsername.text = getString(R.string.username_placeholder, hairdresser.username)
        binding.tvName.text = getString(R.string.salon_name_placeholder, hairdresser.salonName)
        binding.tvEmail.text = getString(R.string.email_placeholder, hairdresser.email)
        if (!hairdresser.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this).load(hairdresser.profilePictureUrl).into(binding.ivProfileImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
