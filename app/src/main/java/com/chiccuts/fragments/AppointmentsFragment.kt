package com.chiccuts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.adapters.AppointmentAdapter
import com.chiccuts.databinding.FragmentAppointmentsBinding
import com.chiccuts.utils.FirestoreUtil
import com.chiccuts.viewmodels.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth

class AppointmentsFragment : Fragment() {
    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var appointmentAdapter: AppointmentAdapter
    private var isBusinessUser = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        appointmentViewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserType { isBusiness ->
            isBusinessUser = isBusiness
            if (_binding != null) { // Binding null kontrolü
                setupRecyclerView()
                loadAppointments()
            }
        }
    }

    private fun checkUserType(callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)

        // Check if user is a barber or hairdresser
        FirestoreUtil.getUser(userId) { user ->
            if (user != null) {
                callback(false) // User is a normal user
            } else {
                FirestoreUtil.getBarber(userId) { barber ->
                    if (barber != null) {
                        callback(true) // User is a barber
                    } else {
                        FirestoreUtil.getHairdresser(userId) { hairdresser ->
                            callback(hairdresser != null) // User is a hairdresser
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        if (_binding == null) return // Binding null kontrolü
        appointmentAdapter = AppointmentAdapter(isBusinessUser) { isEmpty ->
            binding.tvNoAppointments.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun loadAppointments() {
        val userId = auth.currentUser?.uid
        if (userId == null || _binding == null) { // Binding null kontrolü
            binding?.progressBar?.visibility = View.GONE
            Toast.makeText(context, "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE
        appointmentViewModel.fetchAppointments(userId, isBusinessUser)

        appointmentViewModel.appointments.observe(viewLifecycleOwner, Observer { appointments ->
            if (_binding != null) { // Binding null kontrolü
                appointmentAdapter.submitList(appointments)
                binding.tvNoAppointments.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
                binding.progressBar.visibility = View.GONE
            }
        })

        appointmentViewModel.appointmentStatus.observe(viewLifecycleOwner, Observer { status ->
            if (_binding != null) { // Binding null kontrolü
                if (status.contains("Error")) {
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        FirestoreUtil.removeAllListeners()
    }
}
