package com.chiccuts.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chiccuts.adapters.HairdresserAdapter
import com.chiccuts.databinding.FragmentHairdressersListBinding
import com.chiccuts.models.Hairdresser
import com.google.firebase.firestore.FirebaseFirestore

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
        loadHairdressers()
    }

    private fun setupRecyclerView() {
        hairdresserAdapter = HairdresserAdapter()
        binding.rvHairdressersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hairdresserAdapter
        }
    }

    private fun loadHairdressers() {
        firestoreInstance.collection("hairdressers").get()
            .addOnSuccessListener { documents ->
                val hairdressers = documents.toObjects(Hairdresser::class.java)
                hairdresserAdapter.submitList(hairdressers)
            }
            .addOnFailureListener { exception ->
                // Handle the error appropriately
                exception.printStackTrace()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
