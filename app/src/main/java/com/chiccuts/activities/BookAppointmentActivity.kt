package com.chiccuts.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chiccuts.databinding.ActivityBookAppointmentBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSelectDate.setOnClickListener {
            openDatePicker()
        }
        binding.btnBookAppointment.setOnClickListener {
            bookAppointment()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                openTimePicker(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openTimePicker(date: Calendar) {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                date.set(Calendar.MINUTE, minute)
                displaySelectedDateTime(date.time)
            },
            date.get(Calendar.HOUR_OF_DAY),
            date.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun displaySelectedDateTime(date: Date) {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        binding.tvSelectedDateTime.text = dateFormat.format(date)
    }

    private fun bookAppointment() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        val date = dateFormat.parse(binding.tvSelectedDateTime.text.toString()) ?: return
        val appointment = Appointment(
            userId = "user_id_here", // This should be dynamic based on logged in user
            barberId = "barber_id_here", // Should be selected from a list
            serviceType = "service_type_here", // Should be selected or input by user
            appointmentTime = date,
            location = "location_here" // Should be selected or input by user
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
