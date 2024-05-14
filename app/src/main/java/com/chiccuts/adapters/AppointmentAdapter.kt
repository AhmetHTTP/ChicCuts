package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chiccuts.databinding.ItemAppointmentBinding
import com.chiccuts.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentAdapter(private var appointments: MutableList<Appointment>) : ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    fun updateAppointments(newAppointments: MutableList<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }

    fun removeAppointment(appointmentId: String) {
        val index = appointments.indexOfFirst { it.appointmentId == appointmentId }
        if (index != -1) {
            appointments.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class AppointmentViewHolder(private val binding: ItemAppointmentBinding, private val adapter: AppointmentAdapter) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.tvServiceType.text = appointment.serviceType
            binding.tvAppointmentTime.text = appointment.appointmentTime.toString()
            binding.tvLocation.text = appointment.location

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentId.isNotBlank()) {
                    cancelAppointment(appointment.appointmentId)
                } else {
                    // Hata mesajı veya başka bir işlem yapılabilir
                    println("Invalid appointment ID")
                }
            }
        }

        private fun cancelAppointment(appointmentId: String) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener {
                    // Appointment successfully deleted
                    println("Appointment successfully deleted")
                    adapter.removeAppointment(appointmentId)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}

class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
    override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem.appointmentId == newItem.appointmentId
    }

    override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem == newItem
    }
}
