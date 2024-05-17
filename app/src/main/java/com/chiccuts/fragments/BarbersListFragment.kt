package com.chiccuts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.adapters.BarberAdapter
import com.chiccuts.databinding.FragmentBarbersListBinding
import com.chiccuts.models.Barber
import com.google.firebase.firestore.FirebaseFirestore
import com.chiccuts.activities.BookAppointmentActivity
import android.widget.Toast

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
        setupCitySpinner()
    }

    private fun setupRecyclerView() {
        barberAdapter = BarberAdapter(requireContext()) { barber ->
            openBookAppointment(barber)
        }
        binding.rvBarbersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = barberAdapter
        }
    }

    private fun setupCitySpinner() {
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCity = parent?.getItemAtPosition(position).toString()
                loadBarbers(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadBarbers(city: String) {
        binding.progressBar.visibility = View.VISIBLE
        firestoreInstance.collection("barbers")
            .whereEqualTo("city", city)
            .get()
            .addOnSuccessListener { documents ->
                val barbers = documents.toObjects(Barber::class.java)
                barberAdapter.submitList(barbers)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(context, "Failed to load barbers: ${exception.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun openBookAppointment(barber: Barber) {
        val intent = Intent(context, BookAppointmentActivity::class.java).apply {
            putExtra("BARBER_ID", barber.barberId)
            putExtra("SALON_NAME", barber.salonName)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
