package com.chiccuts.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chiccuts.R
import com.chiccuts.databinding.ItemAppointmentBinding
import com.chiccuts.databinding.ItemAppointmentBusinessBinding
import com.chiccuts.models.Appointment
import com.chiccuts.utils.FirestoreUtil

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
            BusinessViewHolder(binding, this)
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

            val profileUrl = appointment.businessProfilePictureUrl
            if (!profileUrl.isNullOrEmpty()) {
                Glide.with(binding.ivProfileImage.context)
                    .load(profileUrl)
                    .into(binding.ivProfileImage)
            } else {
                binding.ivProfileImage.setImageResource(R.drawable.ic_default_avatar)
            }

            binding.btnShowLocation.setOnClickListener {
                val location = appointment.location
                if (location != "Default Location" && location.contains(",")) {
                    val locationParts = location.split(",")
                    val latitude = locationParts[0].trim()
                    val longitude = locationParts[1].trim()
                    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    if (intent.resolveActivity(binding.root.context.packageManager) != null) {
                        binding.root.context.startActivity(intent)
                    } else {
                        // Handle the case where the Maps app is not installed
                    }
                } else {
                    // Handle the case where the location is "Default Location" or incorrect
                }
            }

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentId.isNotBlank()) {
                    FirestoreUtil.cancelAppointment(appointment.appointmentId) { success, message ->
                        if (success) {
                            adapter.removeAppointment(appointment.appointmentId)
                        } else {
                            println("Error cancelling appointment: $message")
                        }
                    }
                } else {
                    println("Invalid appointment ID")
                }
            }
        }
    }

    class BusinessViewHolder(
        private val binding: ItemAppointmentBusinessBinding,
        private val adapter: AppointmentAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.tvAppointmentTime.text = appointment.appointmentTime.toString()
            binding.tvServiceType.text = appointment.serviceType
            binding.tvLocation.text = appointment.location
            binding.tvUserName.text = "${appointment.userFirstName} ${appointment.userLastName}"

            val profileUrl = appointment.userProfilePictureUrl
            if (!profileUrl.isNullOrEmpty()) {
                Glide.with(binding.ivProfileImage.context)
                    .load(profileUrl)
                    .into(binding.ivProfileImage)
            } else {
                binding.ivProfileImage.setImageResource(R.drawable.ic_default_avatar)
            }

            binding.btnShowLocation.setOnClickListener {
                val location = appointment.location
                if (location != "Default Location" && location.contains(",")) {
                    val locationParts = location.split(",")
                    val latitude = locationParts[0].trim()
                    val longitude = locationParts[1].trim()
                    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    if (intent.resolveActivity(binding.root.context.packageManager) != null) {
                        binding.root.context.startActivity(intent)
                    } else {
                        // Handle the case where the Maps app is not installed
                    }
                } else {
                    // Handle the case where the location is "Default Location" or incorrect
                }
            }

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentId.isNotBlank()) {
                    FirestoreUtil.cancelAppointment(appointment.appointmentId) { success, message ->
                        if (success) {
                            adapter.removeAppointment(appointment.appointmentId)
                        } else {
                            println("Error cancelling appointment: $message")
                        }
                    }
                } else {
                    println("Invalid appointment ID")
                }
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
