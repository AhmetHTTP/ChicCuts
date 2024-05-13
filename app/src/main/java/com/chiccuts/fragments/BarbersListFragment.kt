package com.chiccuts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.adapters.BarberAdapter
import com.chiccuts.databinding.FragmentBarbersListBinding
import com.chiccuts.models.Barber
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.activities.BookAppointmentActivity

class BarbersListFragment : Fragment() {
    private var _binding: FragmentBarbersListBinding? = null
    private val binding get() = _binding!!
    private lateinit var barberAdapter: BarberAdapter
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarbersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadBarbers()
    }

    private fun setupRecyclerView() {
        barberAdapter = BarberAdapter { barber ->
            openBookAppointment(barber)
        }
        binding.rvBarbersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = barberAdapter
        }
    }

    private fun loadBarbers() {
        firestoreInstance.collection("barbers").get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                barberAdapter.submitList(barbers)
            }
            .addOnFailureListener { exception ->
                // Handle the error appropriately
                exception.printStackTrace()
            }
    }

    private fun openBookAppointment(barber: Barber) {
        val intent = Intent(context, BookAppointmentActivity
        ::class.java).apply {
            putExtra("BARBER_ID", barber.barberId)
            putExtra("BARBER_NAME", barber.name)
        }
        startActivity(intent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
