package com.chiccuts.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chiccuts.R
import com.chiccuts.adapters.AppointmentSlotAdapter
import com.chiccuts.databinding.FragmentBookAppointmentBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import com.chiccuts.viewmodels.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.QuerySnapshot

class BookAppointmentFragment : Fragment() {
    private var _binding: FragmentBookAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var selectedDate: Date? = null
    private lateinit var appointmentSlotAdapter: AppointmentSlotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        appointmentViewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupAppointmentSlotsRecyclerView()
        loadBarbersSpinner()
    }

    private fun setupListeners() {
        binding.etAppointmentDate.setOnClickListener { openDatePicker() }
        binding.btnBookAppointment.setOnClickListener {
            if (validateInputs()) {
                bookAppointment()
            }
        }
    }

    private fun setupAppointmentSlotsRecyclerView() {
        appointmentSlotAdapter = AppointmentSlotAdapter { slot ->
            selectedDate = slot
            binding.etAppointmentDate.setText(SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault()).format(slot))
        }
        binding.rvAppointmentSlots.adapter = appointmentSlotAdapter
    }

    private fun loadBarbersSpinner() {
        val barbers = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, barbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBarbers.adapter = adapter

        FirestoreUtil.db.collection("barbers").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val barberName = document.getString("salonName") ?: "Unknown Barber"
                    barbers.add("Barber: $barberName")
                }

                FirestoreUtil.db.collection("hairdressers").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val hairdresserName = document.getString("salonName") ?: "Unknown Hairdresser"
                            barbers.add("Hairdresser: $hairdresserName")
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Error loading hairdressers: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading barbers: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            openTimePicker(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun openTimePicker(date: Calendar) {
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, if (minute < 30) 30 else 0) // En yakın yarım saate yuvarla
            checkExistingAppointments(date.time)
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
    }

    private fun checkExistingAppointments(selectedDateTime: Date) {
        val selectedBarberName = binding.spinnerBarbers.selectedItem.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = dateFormat.format(selectedDateTime)
        val startTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDateTime)
        val endTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedDateTime.apply {
            time += 30 * 60 * 1000 // 30 dakika ekle
        })

        val collection = if (selectedBarberName.startsWith("Barber")) "barbers" else "hairdressers"
        val businessId = if (collection == "barbers") {
            FirestoreUtil.db.collection(collection)
                .whereEqualTo("salonName", selectedBarberName.substringAfter(": ").trim())
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "Barber not found!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    documents.documents[0].id
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error loading barber: ${exception.message}", Toast.LENGTH_SHORT).show()
                    null
                }.result?.documents?.get(0)?.id
        } else {
            FirestoreUtil.db.collection(collection)
                .whereEqualTo("salonName", selectedBarberName.substringAfter(": ").trim())
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "Hairdresser not found!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    documents.documents[0].id
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error loading hairdresser: ${exception.message}", Toast.LENGTH_SHORT).show()
                    null
                }.result?.documents?.get(0)?.id
        }

        // Firestore'dan seçilen tarihteki randevuları al
        FirestoreUtil.db.collection("appointments")
            .whereEqualTo(if (collection == "barbers") "barberId" else "hairdresserId", businessId) // Barber/Hairdresser ID ile sorgulayın
            .whereEqualTo("appointmentTime", selectedDate) // Tarih ile sorgulayın
            .whereGreaterThanOrEqualTo("appointmentTime", startTime) // Başlangıç saatinden büyük veya eşit
            .whereLessThanOrEqualTo("appointmentTime", endTime) // Bitiş saatinden küçük veya eşit
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                generateAppointmentSlots(selectedDateTime, querySnapshot)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error checking appointments: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateAppointmentSlots(date: Date, existingAppointments: QuerySnapshot) {
        val slots = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = date
        }

        for (i in 0..7) {
            val slotTime = calendar.time
            val slotTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(slotTime)

            if (existingAppointments.documents.none {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.getTimestamp("appointmentTime")?.toDate()) == slotTimeString
                }) {
                slots.add(slotTime)
            }

            calendar.add(Calendar.MINUTE, 30)
        }

        appointmentSlotAdapter.submitList(slots)
    }

    private fun bookAppointment() {
        val date = selectedDate ?: return
        val userId = auth.currentUser?.uid ?: return
        val selectedBarberName = binding.spinnerBarbers.selectedItem.toString()

        val collection = if (selectedBarberName.startsWith("Barber")) "barbers" else "hairdressers"

        // Seçilen berberin ID'sini Firestore'dan al
        FirestoreUtil.db.collection(collection)
            .whereEqualTo("salonName", selectedBarberName.substringAfter(": ").trim())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "Barber not found!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Doğru businessId'yi belirle
                val barberId = if (collection == "barbers") {
                    documents.documents[0].id
                } else {
                    null
                }
                val hairdresserId = if (collection == "hairdressers") {
                    documents.documents[0].id
                } else {
                    null
                }

                val serviceType = "Haircut"
                val location = "Default Location"

                val appointment = Appointment(
                    userId = userId,
                    barberId = barberId,  // Doğru ID'yi ata
                    hairdresserId = hairdresserId, // Doğru ID'yi ata
                    serviceType = serviceType,
                    appointmentTime = date,
                    location = location
                )

                appointmentViewModel.addAppointment(appointment)
                appointmentViewModel.appointmentStatus.observe(viewLifecycleOwner) { status ->
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error booking appointment: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInputs(): Boolean {
        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Please select a date and time", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.spinnerBarbers.selectedItem == null) {
            Toast.makeText(requireContext(), "Please select a barber", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}