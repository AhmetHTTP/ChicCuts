package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chiccuts.databinding.ItemAppointmentBinding
import com.chiccuts.databinding.ItemAppointmentBusinessBinding
import com.chiccuts.models.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentAdapter(
    private val isBusiness: Boolean,
    private val noAppointmentsCallback: (Boolean) -> Unit
) : ListAdapter<Appointment, RecyclerView.ViewHolder>(AppointmentDiffCallback()) {

    companion object {
        private const val TYPE_NORMAL = 1
        private const val TYPE_BUSINESS = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isBusiness) TYPE_BUSINESS else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_BUSINESS) {
            val binding = ItemAppointmentBusinessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BusinessViewHolder(binding)
        } else {
            val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NormalViewHolder(binding, this)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appointment = getItem(position)
        if (holder is NormalViewHolder) {
            holder.bind(appointment)
        } else if (holder is BusinessViewHolder) {
            holder.bind(appointment)
        }
    }

    class NormalViewHolder(
        private val binding: ItemAppointmentBinding,
        private val adapter: AppointmentAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.tvServiceType.text = appointment.serviceType
            binding.tvAppointmentTime.text = appointment.appointmentTime.toString()
            binding.tvLocation.text = appointment.location
            binding.tvSalonName.text = appointment.salonName
            binding.tvRating.text = "Rating: ${appointment.rating}"

            if (!appointment.profilePictureUrl.isNullOrEmpty()) {
                Glide.with(binding.ivProfileImage.context)
                    .load(appointment.profilePictureUrl)
                    .into(binding.ivProfileImage)
            }

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentId.isNotBlank()) {
                    cancelAppointment(appointment.appointmentId)
                } else {
                    println("Invalid appointment ID")
                }
            }
        }

        private fun cancelAppointment(appointmentId: String) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener {
                    println("Appointment successfully deleted")
                    adapter.removeAppointment(appointmentId)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    class BusinessViewHolder(private val binding: ItemAppointmentBusinessBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.tvAppointmentTime.text = appointment.appointmentTime.toString()
            binding.tvUsername.text = appointment.userUsername
            binding.tvUserFullName.text = "${appointment.userFirstName} ${appointment.userLastName}"

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentId.isNotBlank()) {
                    cancelAppointment(appointment.appointmentId)
                } else {
                    println("Invalid appointment ID")
                }
            }
        }

        private fun cancelAppointment(appointmentId: String) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener {
                    println("Appointment successfully deleted")
                    (binding.root.context as? AppointmentAdapter)?.removeAppointment(appointmentId)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    fun removeAppointment(appointmentId: String) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.appointmentId == appointmentId }
        if (index != -1) {
            currentList.removeAt(index)
            submitList(currentList)
            noAppointmentsCallback(currentList.isEmpty())
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
