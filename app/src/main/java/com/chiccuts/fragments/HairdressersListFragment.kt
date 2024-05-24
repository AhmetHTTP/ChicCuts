package com.chiccuts.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.activities.BookAppointmentActivity
import com.chiccuts.adapters.HairdresserAdapter
import com.chiccuts.databinding.FragmentHairdressersListBinding
import com.chiccuts.models.Hairdresser
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class HairdressersListFragment : Fragment() {
    private var _binding: FragmentHairdressersListBinding? = null
    private val binding get() = _binding!!
    private lateinit var hairdresserAdapter: HairdresserAdapter
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHairdressersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCitySpinner()
    }

    private fun setupRecyclerView() {
        hairdresserAdapter = HairdresserAdapter(requireContext()) { hairdresser ->
            openBookAppointment(hairdresser)
        }
        binding.rvHairdressersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hairdresserAdapter
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
                loadHairdressers(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadHairdressers(city: String) {
        binding.progressBar.visibility = View.VISIBLE
        firestoreInstance.collection("hairdressers")
            .whereEqualTo("city", city)
            .get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                hairdresserAdapter.submitList(hairdressers)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(context, "Failed to load hairdressers: ${exception.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun openBookAppointment(hairdresser: Hairdresser) {
        val intent = Intent(context, BookAppointmentActivity::class.java).apply {
            putExtra("HAIRDRESSER_ID", hairdresser.hairdresserId)
            putExtra("SALON_NAME", hairdresser.salonName)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
