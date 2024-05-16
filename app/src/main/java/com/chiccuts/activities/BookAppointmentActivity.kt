package com.chiccuts.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chiccuts.databinding.ActivityBookAppointmentBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import com.chiccuts.viewmodels.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookAppointmentBinding
    private lateinit var auth: FirebaseAuth
    private var selectedDate: Date? = null
    private lateinit var appointmentViewModel: AppointmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        appointmentViewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)

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
        val salonName = intent.getStringExtra("SALON_NAME")
        binding.tvSalonName.text = salonName
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
        val salonName = intent.getStringExtra("SALON_NAME") ?: ""
        val serviceType = "Haircut"
        val location = "Default Location"

        val appointment = Appointment(
            userId = userId,
            barberId = barberId,
            hairdresserId = hairdresserId,
            serviceType = serviceType,
            appointmentTime = date,
            location = location,
            salonName = salonName
        )

        appointmentViewModel.addAppointment(appointment)
        appointmentViewModel.appointmentStatus.observe(this) { status ->
            if (status == "Appointment scheduled successfully") {
                showToast(status)
                finish()
            } else {
                showToast("Error scheduling appointment: $status")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun validateInputs(): Boolean {
        if (selectedDate == null) {
            showToast("Please select a date and time for the appointment")
            return false
        }
        return true
    }

    private fun checkUserType() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            FirestoreUtil.getUser(userId) { user ->
                if (user == null) {
                    showToast("Only normal users can book appointments")
                    finish()
                }
            }
        } else {
            showToast("User not authenticated")
            finish()
        }
    }
}
