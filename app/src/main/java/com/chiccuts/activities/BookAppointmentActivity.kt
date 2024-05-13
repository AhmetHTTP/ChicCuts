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
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookAppointmentBinding
    private lateinit var auth: FirebaseAuth

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
        // Example prefilling logic, which you might need to adjust based on your actual app flow
        val barberId = intent.getStringExtra("BARBER_ID")
        val hairdresserId = intent.getStringExtra("HAIRDRESSER_ID")
        // You can fetch and display barber or hairdresser details if needed
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            openTimePicker(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000 // Set minimum date to current date
            show()
        }
    }

    private fun openTimePicker(date: Calendar) {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
            date.set(Calendar.MINUTE, minute)
            displaySelectedDateTime(date.time)
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true).show()
    }

    private fun displaySelectedDateTime(date: Date) {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        binding.tvSelectedDateTime.text = dateFormat.format(date)
    }

    private fun bookAppointment() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        val date = dateFormat.parse(binding.tvSelectedDateTime.text.toString()) ?: return
        val userId = auth.currentUser?.uid ?: return
        val barberId = intent.getStringExtra("BARBER_ID")
        val hairdresserId = intent.getStringExtra("HAIRDRESSER_ID")
        val serviceType = "Haircut"  // Ideally this should be selected from the UI
        val location = "Default Location"  // Ideally this should also be selected from the UI

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
                finish()  // Optionally, navigate to another screen or back to the main menu
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
        if (binding.tvSelectedDateTime.text.isEmpty()) {
            showToast("Please select a date and time for the appointment")
            return false
        }
        // Additional validation for service type and location can be added here
        return true
    }
}
