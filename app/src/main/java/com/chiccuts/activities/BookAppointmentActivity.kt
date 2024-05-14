package com.chiccuts.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.databinding.ActivityBookAppointmentBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookAppointmentBinding
    private lateinit var auth: FirebaseAuth
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        checkUserType()
        setupListeners()
        prefillAppointmentDetails()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSelectDate.setOnClickListener { openDatePicker() }
        binding.btnBookAppointment.setOnClickListener {
            if (validateInputs()) {
                bookAppointment()
            }
        }
    }

    private fun prefillAppointmentDetails() {
        val barberId = intent.getStringExtra("BARBER_ID")
        val hairdresserId = intent.getStringExtra("HAIRDRESSER_ID")
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            openTimePicker(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun openTimePicker(date: Calendar) {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, minute)
            displaySelectedDateTime(date.time)
            selectedDate = date.time
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
    }

    private fun displaySelectedDateTime(date: Date) {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        binding.tvSelectedDateTime.text = dateFormat.format(date)
    }

    private fun bookAppointment() {
        val date = selectedDate ?: return
        val userId = auth.currentUser?.uid ?: return
        val barberId = intent.getStringExtra("BARBER_ID")
        val hairdresserId = intent.getStringExtra("HAIRDRESSER_ID")
        val serviceType = "Haircut"
        val location = "Default Location"

        val appointment = Appointment(
            userId = userId,
            barberId = barberId,
            hairdresserId = hairdresserId,
            serviceType = serviceType,
            appointmentTime = date,
            location = location
        )

        FirestoreUtil.addAppointment(appointment) { success, message ->
            if (success) {
                showToast("Appointment booked successfully")
                finish()
            } else {
                showToast("Error booking appointment: $message")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun validateInputs(): Boolean {
        // Validate date and time
        if (selectedDate == null) {
            showToast("Please select a date and time for the appointment")
            return false
        }
        // Additional validation for service type and location can be added here
        return true
    }

    private fun checkUserType() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        showToast("Only normal users can book appointments")
                        finish()
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to check user type")
                    finish()
                }
        } else {
            showToast("User not authenticated")
            finish()
        }
    }
}
