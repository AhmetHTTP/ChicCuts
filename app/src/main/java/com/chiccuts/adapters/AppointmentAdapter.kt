package com.chiccuts.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chiccuts.R
import com.chiccuts.databinding.ItemAppointmentBinding
import com.chiccuts.databinding.ItemAppointmentBusinessBinding
import com.chiccuts.models.Appointment
import com.chiccuts.models.Barber
import com.chiccuts.models.Hairdresser
import com.chiccuts.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date

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
            holder.bind(appointment, position)
        } else if (holder is BusinessViewHolder) {
            holder.bind(appointment)
        }
    }

    class NormalViewHolder(
        private val binding: ItemAppointmentBinding,
        private val adapter: AppointmentAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        private var snapshotListener: ListenerRegistration? = null
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val authStateListener: FirebaseAuth.AuthStateListener

        init {
            authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null) {
                    // Kullanıcı çıkış yaptı
                    unbind()
                }
            }
            auth.addAuthStateListener(authStateListener)
        }

        fun bind(appointment: Appointment, position: Int) {
            binding.tvServiceType.text = appointment.serviceType
            binding.tvAppointmentTime.text = appointment.appointmentTime.toString()
            binding.tvLocation.text = appointment.location
            binding.tvSalonName.text = appointment.salonName
            binding.tvRating.text = "Rating: ${appointment.rating}"

            val profileUrl = appointment.businessProfilePictureUrl
            if (!profileUrl.isNullOrEmpty()) {
                Glide.with(binding.ivProfileImage.context)
                    .load(profileUrl)
                    .circleCrop()
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
                        Toast.makeText(binding.root.context, "Maps app is not installed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Invalid location information.", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentTime.after(Date())) {
                    if (appointment.appointmentId.isNotBlank()) {
                        FirestoreUtil.cancelAppointment(appointment.appointmentId) { success, message ->
                            if (success) {
                                adapter.removeAppointment(appointment.appointmentId)
                                Toast.makeText(binding.root.context, "Appointment cancelled.", Toast.LENGTH_SHORT).show()
                            } else {
                                println("Error cancelling appointment: $message")
                                Toast.makeText(binding.root.context, "Error cancelling appointment.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        println("Invalid appointment ID")
                        Toast.makeText(binding.root.context, "Invalid appointment ID.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Cannot cancel past appointments.", Toast.LENGTH_SHORT).show()
                }
            }

            snapshotListener = FirestoreUtil.db.collection("appointments").document(appointment.appointmentId)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        println("Error fetching appointment data: ${error.message}")
                        if (auth.currentUser != null) {
                            Toast.makeText(binding.root.context, "Error fetching appointment data.", Toast.LENGTH_SHORT).show()
                        }
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        appointment.isRated = documentSnapshot.getBoolean("isRated") ?: false
                        if (appointment.appointmentTime.before(Date()) && !appointment.isRated) {
                            binding.btnRateAppointment.visibility = View.VISIBLE
                        } else {
                            binding.btnRateAppointment.visibility = View.GONE
                        }
                    }
                }

            binding.btnRateAppointment.setOnClickListener {
                showRatingDialog(appointment) { rating ->
                    updateAppointmentRating(appointment, rating, position)
                }
            }
        }

        private fun showRatingDialog(appointment: Appointment, onRatingSubmitted: (Float) -> Unit) {
            val dialogView = LayoutInflater.from(binding.root.context).inflate(R.layout.dialog_rate_appointment, null)
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

            AlertDialog.Builder(binding.root.context)
                .setTitle("Rate your appointment")
                .setView(dialogView)
                .setPositiveButton("Submit") { _, _ ->
                    onRatingSubmitted(ratingBar.rating)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun updateAppointmentRating(appointment: Appointment, rating: Float, position: Int) {
            val appointmentId = appointment.appointmentId
            if (appointmentId.isNotBlank()) {
                FirestoreUtil.updateAppointment(appointmentId, mapOf("rating" to rating.toDouble(), "isRated" to true)) { success, message ->
                    if (success) {
                        appointment.isRated = true
                        adapter.updateAppointment(appointmentId, rating.toDouble(), true)
                        updateBusinessRating(appointment, position)
                        binding.btnRateAppointment.visibility = View.GONE
                        Toast.makeText(binding.root.context, "Appointment rated successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        println("Error updating appointment rating: $message")
                        Toast.makeText(binding.root.context, "Error rating appointment.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                println("Invalid appointment ID")
                Toast.makeText(binding.root.context, "Invalid appointment ID.", Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateBusinessRating(appointment: Appointment, position: Int) {
            val businessId = appointment.barberId ?: appointment.hairdresserId ?: return
            val collection = if (appointment.barberId != null) "barbers" else "hairdressers"

            FirestoreUtil.db.collection(collection).document(businessId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        when (val business = if (collection == "barbers") documentSnapshot.toObject(Barber::class.java) else documentSnapshot.toObject(Hairdresser::class.java)) {
                            is Barber -> updateBarberRating(business, appointment, collection, businessId, position)
                            is Hairdresser -> updateHairdresserRating(business, appointment, collection, businessId, position)
                            else -> println("Unknown business type!")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error updating business rating: ${e.message}")
                    Toast.makeText(binding.root.context, "Error updating rating: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        private fun updateBarberRating(barber: Barber, appointment: Appointment, collection: String, businessId: String, position: Int) {
            val oldRatingsCount = barber.ratingsCount ?: 0
            val newRatingsCount = oldRatingsCount + 1
            val totalRating = (barber.rating * oldRatingsCount) + appointment.rating
            val newRating = totalRating / newRatingsCount

            val updateData = mapOf(
                "rating" to newRating,
                "ratingsCount" to newRatingsCount
            )

            FirestoreUtil.db.collection(collection).document(businessId).update(updateData)
                .addOnSuccessListener {
                    println("Barber rating updated successfully")
                    adapter.notifyItemChanged(position)
                }
                .addOnFailureListener { e ->
                    println("Error updating barber rating: ${e.message}")
                    Toast.makeText(binding.root.context, "Error updating rating: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        private fun updateHairdresserRating(hairdresser: Hairdresser, appointment: Appointment, collection: String, businessId: String, position: Int) {
            val oldRatingsCount = hairdresser.ratingsCount ?: 0
            val newRatingsCount = oldRatingsCount + 1
            val totalRating = (hairdresser.rating * oldRatingsCount) + appointment.rating
            val newRating = totalRating / newRatingsCount

            val updateData = mapOf(
                "rating" to newRating,
                "ratingsCount" to newRatingsCount
            )

            FirestoreUtil.db.collection(collection).document(businessId).update(updateData)
                .addOnSuccessListener {
                    println("Hairdresser rating updated successfully")
                    adapter.notifyItemChanged(position)
                }
                .addOnFailureListener { e ->
                    println("Error updating hairdresser rating: ${e.message}")
                    Toast.makeText(binding.root.context, "Error updating rating: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun unbind() {
            snapshotListener?.remove()
            snapshotListener = null
            auth.removeAuthStateListener(authStateListener)
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
                    .circleCrop()
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
                        Toast.makeText(binding.root.context, "Maps app is not installed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Invalid location information.", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnCancelAppointment.setOnClickListener {
                if (appointment.appointmentTime.after(Date())) {
                    if (appointment.appointmentId.isNotBlank()) {
                        FirestoreUtil.cancelAppointment(appointment.appointmentId) { success, message ->
                            if (success) {
                                adapter.removeAppointment(appointment.appointmentId)
                                Toast.makeText(binding.root.context, "Appointment cancelled.", Toast.LENGTH_SHORT).show()
                            } else {
                                println("Error cancelling appointment: $message")
                                Toast.makeText(binding.root.context, "Error cancelling appointment.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        println("Invalid appointment ID")
                        Toast.makeText(binding.root.context, "Invalid appointment ID.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Cannot cancel past appointments.", Toast.LENGTH_SHORT).show()
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

    fun updateAppointment(appointmentId: String, rating: Double, isRated: Boolean) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.appointmentId == appointmentId }
        if (index != -1) {
            currentList[index].rating = rating
            currentList[index].isRated = isRated
            submitList(currentList)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is NormalViewHolder) {
            holder.unbind()
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
