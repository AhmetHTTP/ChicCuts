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
import com.chiccuts.databinding.ItemBarberBinding
import com.chiccuts.models.Barber

class BarberAdapter(private val context: Context, private val onClick: (Barber) -> Unit) : ListAdapter<Barber, BarberAdapter.BarberViewHolder>(BarberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
        val binding = ItemBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarberViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        val barber = getItem(position)
        holder.bind(barber)
    }

    inner class BarberViewHolder(private val binding: ItemBarberBinding, private val onClick: (Barber) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(barber: Barber) {
            binding.tvBarberName.text = barber.salonName
            binding.tvBarberServices.text = barber.serviceTypes.joinToString(", ")
            binding.tvBarberRating.text = "Rating: ${barber.rating}"

            // Load profile picture using Glide
            if (!barber.profilePictureUrl.isNullOrEmpty()) {
                Glide.with(binding.ivBarberProfile.context)
                    .load(barber.profilePictureUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivBarberProfile)
            }

            binding.root.setOnClickListener {
                onClick(barber) // Trigger click listener when item is clicked
            }

            binding.btnShowOnMap.setOnClickListener {
                val location = barber.location
                val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        }
    }
}

class BarberDiffCallback : DiffUtil.ItemCallback<Barber>() {
    override fun areItemsTheSame(oldItem: Barber, newItem: Barber): Boolean {
        return oldItem.barberId == newItem.barberId
    }

    override fun areContentsTheSame(oldItem: Barber, newItem: Barber): Boolean {
        return oldItem == newItem
    }
}
