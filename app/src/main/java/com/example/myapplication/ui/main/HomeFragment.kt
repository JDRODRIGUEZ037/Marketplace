package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ProductAdapter
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.ProductRepository
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.Product
import com.example.myapplication.utils.FirebaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private lateinit var repository: ProductRepository
    private var allProducts = listOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val db = AppDatabase.getDatabase(requireContext())
        repository = ProductRepository(db.productDao())
        
        setupRecyclerView()
        loadProducts()
        setupAiChatListeners()

        binding.etSearch.addTextChangedListener { text ->
            filterProducts(text.toString())
        }
    }

    private fun setupAiChatListeners() {
        val openAiChat = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_aiChatFragment)
        }
        binding.cardAiBanner.setOnClickListener(openAiChat)
        binding.fabAiChat.setOnClickListener(openAiChat)
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
            val bundle = Bundle().apply { putString("productId", product.id) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
        }
        binding.rvProducts.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun loadProducts() {
        // Sync Firebase with SQL (Room)
        CoroutineScope(Dispatchers.IO).launch {
            repository.refreshProducts()
            val localProducts = repository.getLocalProducts()
            allProducts = localProducts
            launch(Dispatchers.Main) {
                adapter.updateList(allProducts)
            }
        }
        
        // Also listen for real-time changes in Firebase
        FirebaseUtils.db.collection("productos").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            CoroutineScope(Dispatchers.IO).launch {
                repository.refreshProducts()
                val localProducts = repository.getLocalProducts()
                allProducts = localProducts
                launch(Dispatchers.Main) {
                    adapter.updateList(allProducts)
                }
            }
        }
    }

    private fun filterProducts(query: String) {
        val filtered = allProducts.filter { 
            it.nombre.contains(query, ignoreCase = true) || 
            it.categoria.contains(query, ignoreCase = true) 
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
