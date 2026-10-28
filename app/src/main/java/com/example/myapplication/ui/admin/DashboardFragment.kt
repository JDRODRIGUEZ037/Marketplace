package com.example.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.example.myapplication.utils.FirebaseUtils

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.cvManageProducts.setOnClickListener {
            findNavController().navigate(R.id.manageProductsFragment)
        }
        
        binding.cvChats.setOnClickListener {
            findNavController().navigate(R.id.chatListFragment)
        }

        binding.tvTotalProducts.setOnClickListener {
            seedDummyData()
        }

        loadStats()
    }

    private fun seedDummyData() {
        val userId = FirebaseUtils.currentUserId ?: return
        val dummyProducts = com.example.myapplication.utils.DummyData.getDummyProducts(userId)
        
        val batch = FirebaseUtils.db.batch()
        dummyProducts.forEach { product ->
            val docRef = FirebaseUtils.db.collection("productos").document()
            batch.set(docRef, product)
        }
        
        batch.commit().addOnSuccessListener {
            android.widget.Toast.makeText(requireContext(), "Datos dummy agregados. Toca de nuevo para actualizar stats.", android.widget.Toast.LENGTH_SHORT).show()
            loadStats()
        }
    }

    private fun loadStats() {
        val userId = FirebaseUtils.currentUserId ?: return
        FirebaseUtils.db.collection("productos")
            .whereEqualTo("vendedorId", userId)
            .get()
            .addOnSuccessListener { result ->
                binding.tvTotalProducts.text = "Total Productos: ${result.size()}"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
