package com.chiccuts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.databinding.FragmentAppointmentsBinding
import com.chiccuts.models.Appointment
import com.chiccuts.adapters.AppointmentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var appointmentAdapter: AppointmentAdapter

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
        setupRecyclerView()
        loadAppointments()
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(mutableListOf())
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun loadAppointments() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("appointments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val appointments = documents.map { document ->
                        document.toObject(Appointment::class.java).apply {
                            appointmentId = document.id
                        }
                    }.toMutableList()
                    appointmentAdapter.updateAppointments(appointments)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
