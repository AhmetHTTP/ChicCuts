package com.chiccuts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chiccuts.R
import java.text.SimpleDateFormat
import java.util.*

class AppointmentSlotAdapter(private val onSlotClick: (Date) -> Unit) : RecyclerView.Adapter<AppointmentSlotAdapter.SlotViewHolder>() {

    private val slots = mutableListOf<Date>()

    fun submitList(newSlots: List<Date>) {
        slots.clear()
        slots.addAll(newSlots)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment_slot, parent, false)
        return SlotViewHolder(view, onSlotClick)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.bind(slot)
    }

    override fun getItemCount(): Int = slots.size

    class SlotViewHolder(itemView: View, private val onSlotClick: (Date) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvAppointmentSlot = itemView.findViewById<TextView>(R.id.tvAppointmentSlot)

        fun bind(slot: Date) {
            tvAppointmentSlot.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(slot)
            itemView.setOnClickListener { onSlotClick(slot) }
        }
    }
}
