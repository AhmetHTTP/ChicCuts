package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chiccuts.R
import com.chiccuts.databinding.ItemHairdresserBinding
import com.chiccuts.models.Hairdresser

class HairdresserAdapter(private val onClick: (Hairdresser) -> Unit) : ListAdapter<Hairdresser, HairdresserAdapter.HairdresserViewHolder>(HairdresserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HairdresserViewHolder {
        val binding = ItemHairdresserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HairdresserViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: HairdresserViewHolder, position: Int) {
        val hairdresser = getItem(position)
        holder.bind(hairdresser)
    }

    class HairdresserViewHolder(private val binding: ItemHairdresserBinding, private val onClick: (Hairdresser) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hairdresser: Hairdresser) {
            binding.tvHairdresserName.text = hairdresser.salonName  // "name" yerine "salonName" kullanıyoruz
            binding.tvHairdresserServices.text = hairdresser.serviceTypes.joinToString(", ")
            binding.tvHairdresserRating.text = "Rating: ${hairdresser.rating}"

            // Load profile picture using Glide
            if (!hairdresser.profilePictureUrl.isNullOrEmpty()) {
                Glide.with(binding.ivHairdresserProfile.context)
                    .load(hairdresser.profilePictureUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivHairdresserProfile)
            }

            binding.root.setOnClickListener {
                onClick(hairdresser) // Trigger click listener when item is clicked
            }
        }
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
