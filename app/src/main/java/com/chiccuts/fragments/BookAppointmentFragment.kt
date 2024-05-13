package com.chiccuts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chiccuts.databinding.FragmentBookAppointmentBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentFragment : Fragment() {
    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBookAppointment.setOnClickListener {
            bookAppointment()
        }
    }

    private fun bookAppointment() {
        // SimpleDateFormat kullanarak tarih ve zamanı Date objesine dönüştürme
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        val dateString = "12/31/2024 15:00" // Kullanıcıdan alınacak tarih ve saat
        val appointmentTime = dateFormat.parse(dateString)  // String'den Date'e dönüşüm

        // Create a new appointment object
        val appointment = Appointment(
            userId = "user_id_here",
            barberId = "barber_id_here",
            serviceType = "service_type_here",
            appointmentTime = appointmentTime!!,  // Non-null assertion; handle null properly in production
            location = "location_here"
        )

        FirestoreUtil.addAppointment(appointment) { success, message ->
            if (success) {
                showToast("Appointment booked successfully")
            } else {
                showToast("Error booking appointment: $message")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
