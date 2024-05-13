package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chiccuts.databinding.ItemHairdresserBinding
import com.chiccuts.models.Hairdresser

class HairdresserAdapter : ListAdapter<Hairdresser, HairdresserAdapter.HairdresserViewHolder>(HairdresserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HairdresserViewHolder {
        val binding = ItemHairdresserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HairdresserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HairdresserViewHolder, position: Int) {
        val hairdresser = getItem(position)
        holder.bind(hairdresser)
    }

    class HairdresserViewHolder(private val binding: ItemHairdresserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hairdresser: Hairdresser) {
            binding.tvHairdresserName.text = hairdresser.name
            binding.tvHairdresserServices.text = hairdresser.serviceTypes.joinToString(", ")
            // Additional details can be added here
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
