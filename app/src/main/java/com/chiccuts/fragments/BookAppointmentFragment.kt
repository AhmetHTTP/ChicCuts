package com.chiccuts.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chiccuts.databinding.FragmentBookAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookAppointmentFragment : Fragment() {
    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            // Kullanıcı giriş yapmamışsa, giriş aktivitesine yönlendirilir (Aktivite bağlamında işlemek gerekirse)
            // Bu işlem fragment bağlamında yönetilemiyorsa Activity üzerinden kontrol edilmelidir.
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        loadBarbersOrHairdressers()
        binding.btnBookAppointment.setOnClickListener {
            bookAppointment()
        }
    }

    private fun setupDatePicker() {
        binding.etAppointmentDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.etAppointmentDate.setText(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.time))
                }
                TimePickerDialog(requireContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun loadBarbersOrHairdressers() {
        // Fetch barbers or hairdressers from Firestore and populate a dropdown
        val items = arrayOf("Barber 1", "Barber 2", "Hairdresser 1")  // Example names
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinnerBarbers.adapter = adapter
    }

    private fun bookAppointment() {
        val selectedBarberOrHairdresser = binding.spinnerBarbers.selectedItem.toString()
        val dateAndTime = binding.etAppointmentDate.text.toString()

        if (dateAndTime.isNotEmpty()) {
            // Save the appointment to Firestore
            val appointment = hashMapOf(
                "barber_or_hairdresser" to selectedBarberOrHairdresser,
                "date_and_time" to dateAndTime,
                "user_id" to auth.currentUser?.uid  // Assuming the user is logged in
            )
            firestore.collection("appointments").add(appointment)
                .addOnSuccessListener {
                    Toast.makeText(context, "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to book appointment: ${it.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(context, "Please select a date and time", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
