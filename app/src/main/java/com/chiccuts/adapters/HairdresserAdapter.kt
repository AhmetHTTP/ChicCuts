package com.chiccuts.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chiccuts.R
import com.chiccuts.activities.BookAppointmentActivity
import com.chiccuts.databinding.ItemHairdresserBinding
import com.chiccuts.models.Hairdresser

class HairdresserAdapter(private val context: Context, private val onClick: (Hairdresser) -> Unit) : ListAdapter<Hairdresser, HairdresserAdapter.HairdresserViewHolder>(HairdresserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HairdresserViewHolder {
        val binding = ItemHairdresserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HairdresserViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: HairdresserViewHolder, position: Int) {
        val hairdresser = getItem(position)
        holder.bind(hairdresser)
    }

    inner class HairdresserViewHolder(private val binding: ItemHairdresserBinding, private val onClick: (Hairdresser) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hairdresser: Hairdresser) {
            binding.tvHairdresserName.text = hairdresser.salonName
            binding.tvHairdresserServices.text = hairdresser.serviceTypes.joinToString(", ")
            // Rating ve ratingsCount değerlerini doğru şekilde göster
            binding.tvHairdresserRating.text = if (hairdresser.ratingsCount != null && hairdresser.ratingsCount!! > 0) {
                String.format("%.1f (%d ratings)", hairdresser.rating, hairdresser.ratingsCount)
            } else {
                "0 (0 ratings)"
            }

            if (!hairdresser.profilePictureUrl.isNullOrEmpty()) {
                Glide.with(binding.ivHairdresserProfile.context)
                    .load(hairdresser.profilePictureUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .circleCrop()
                    .into(binding.ivHairdresserProfile)
            }

            binding.root.setOnClickListener {
                onClick(hairdresser)
            }

            binding.btnShowOnMap.setOnClickListener {
                val location = hairdresser.location
                val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        }
    }

    private fun openBookAppointment(hairdresser: Hairdresser) {
        val intent = Intent(context, BookAppointmentActivity::class.java).apply {
            putExtra("HAIRDRESSER_ID", hairdresser.hairdresserId)
            putExtra("SALON_NAME", hairdresser.salonName)
        }
        context.startActivity(intent)
    }
}

class HairdresserDiffCallback : DiffUtil.ItemCallback<Hairdresser>() {
    override fun areItemsTheSame(oldItem: Hairdresser, newItem: Hairdresser): Boolean {
        return oldItem.hairdresserId == newItem.hairdresserId
    }

    override fun areContentsTheSame(oldItem: Hairdresser, newItem: Hairdresser): Boolean {
        return oldItem == newItem
    }
}
