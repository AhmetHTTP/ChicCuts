package com.chiccuts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.adapters.AppointmentAdapter
import com.chiccuts.databinding.FragmentAppointmentsBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var appointmentAdapter: AppointmentAdapter
    private var isBusinessUser = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserType { isBusiness ->
            isBusinessUser = isBusiness
            setupRecyclerView()
            loadAppointments()
        }
    }

    private fun checkUserType(callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { _ ->
                callback(false)
            }
            .addOnFailureListener {
                firestore.collection("barbers").document(userId).get()
                    .addOnSuccessListener { barberDocSnapshot ->
                        if (barberDocSnapshot.exists()) {
                            callback(true)
                        } else {
                            firestore.collection("hairdressers").document(userId).get()
                                .addOnSuccessListener { hairdresserDocSnapshot ->
                                    callback(hairdresserDocSnapshot.exists())
                                }
                                .addOnFailureListener {
                                    callback(false)
                                }
                        }
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(isBusinessUser) { isEmpty ->
            binding.tvNoAppointments.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun loadAppointments() {
        val userId = auth.currentUser?.uid ?: return

        binding.progressBar.visibility = View.VISIBLE

        if (isBusinessUser) {
            loadBusinessAppointments(userId)
        } else {
            loadUserAppointments(userId)
        }
    }

    private fun loadUserAppointments(userId: String) {
        firestore.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val appointments = documents.map { document ->
                    val appointment = document.toObject(Appointment::class.java).apply {
                        appointmentId = document.id
                    }
                    val businessId = appointment.barberId ?: appointment.hairdresserId
                    businessId?.let {
                        firestore.collection(if (appointment.barberId != null) "barbers" else "hairdressers").document(it)
                            .get()
                            .addOnSuccessListener { businessDoc ->
                                appointment.salonName = businessDoc.getString("salonName") ?: ""
                                appointment.rating = businessDoc.getDouble("rating") ?: 0.0
                                appointment.profilePictureUrl = businessDoc.getString("profilePictureUrl")
                                appointmentAdapter.notifyDataSetChanged()
                            }
                    }
                    appointment
                }.toMutableList()
                appointmentAdapter.submitList(appointments)
                binding.tvNoAppointments.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                binding.tvNoAppointments.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun loadBusinessAppointments(userId: String) {
        FirestoreUtil.getAppointmentsForBusiness(userId) { appointments, message ->
            appointmentAdapter.submitList(appointments)
            binding.tvNoAppointments.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
