package com.example.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ProductAdapter
import com.example.myapplication.databinding.FragmentManageProductsBinding
import com.example.myapplication.models.Product
import com.example.myapplication.utils.DummyData
import com.example.myapplication.utils.FirebaseUtils

class ManageProductsFragment : Fragment() {
    private var _binding: FragmentManageProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadMyProducts()

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_manageProductsFragment_to_addEditProductFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
            val bundle = Bundle().apply { putString("productId", product.id) }
            findNavController().navigate(R.id.addEditProductFragment, bundle)
        }
        binding.rvManageProducts.apply {
            adapter = this@ManageProductsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadMyProducts() {
        val userId = FirebaseUtils.currentUserId ?: "admin_default"
        
        FirebaseUtils.db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                val allProducts = result.toObjects(Product::class.java).mapIndexed { index, product ->
                    product.apply { id = result.documents[index].id }
                }
                
                val myProducts = allProducts.filter { it.vendedorId == userId }

                if (myProducts.isEmpty()) {
                    // Seed dummy products for this seller
                    val dummyList = DummyData.getDummyProducts(userId)
                    val createdProducts = mutableListOf<Product>()
                    var count = 0
                    for (p in dummyList) {
                        val docRef = FirebaseUtils.db.collection("productos").document()
                        p.id = docRef.id
                        docRef.set(p).addOnSuccessListener {
                            createdProducts.add(p)
                            count++
                            if (count == dummyList.size) {
                                adapter.updateList(createdProducts)
                            }
                        }
                    }
                    if (dummyList.isEmpty()) {
                        adapter.updateList(DummyData.getDummyProducts(userId))
                    }
                } else {
                    adapter.updateList(myProducts)
                }
            }
            .addOnFailureListener {
                // Fallback offline
                adapter.updateList(DummyData.getDummyProducts(userId))
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
