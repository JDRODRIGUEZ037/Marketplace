package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.CartAdapter
import com.example.myapplication.databinding.FragmentCartBinding
import com.example.myapplication.models.Product
import com.example.myapplication.utils.FirebaseUtils

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCartItems()

        binding.btnCheckout.setOnClickListener {
            Toast.makeText(requireContext(), "Función de pago no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(emptyList()) { product ->
            removeFromCart(product)
        }
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadCartItems() {
        val userId = FirebaseUtils.currentUserId ?: return
        FirebaseUtils.db.collection("usuarios").document(userId).collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val products = result.toObjects(Product::class.java)
                cartAdapter.updateList(products)
                updateTotal(products)
            }
    }

    private fun removeFromCart(product: Product) {
        val userId = FirebaseUtils.currentUserId ?: return
        FirebaseUtils.db.collection("usuarios").document(userId).collection("cart").document(product.id)
            .delete()
            .addOnSuccessListener {
                loadCartItems()
            }
    }

    private fun updateTotal(products: List<Product>) {
        val total = products.sumOf { it.precio }
        binding.tvTotalAmount.text = "$${String.format("%.2f", total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
