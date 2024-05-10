package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chiccuts.databinding.ItemBarberBinding
import com.chiccuts.models.Barber

class BarberAdapter : ListAdapter<Barber, BarberAdapter.BarberViewHolder>(BarberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
        val binding = ItemBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        val barber = getItem(position)
        holder.bind(barber)
    }

    class BarberViewHolder(private val binding: ItemBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(barber: Barber) {
            binding.tvBarberName.text = barber.name
            binding.tvBarberServices.text = barber.serviceTypes.joinToString(", ")
            // Set more fields as needed, e.g., ratings, image with Glide or Picasso
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