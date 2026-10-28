package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProductDetailBinding
import com.example.myapplication.models.Product
import com.example.myapplication.utils.FirebaseUtils

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private var currentProduct: Product? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProductDetails()

        binding.btnAddToCart.setOnClickListener { addToCart() }
        binding.btnChat.setOnClickListener { 
            currentProduct?.let { product ->
                val bundle = Bundle().apply { putString("receiverId", product.vendedorId) }
                findNavController().navigate(R.id.action_productDetailFragment_to_chatFragment, bundle)
            }
        }
        binding.btnRate.setOnClickListener {
            submitRating()
        }
    }

    private fun submitRating() {
        val product = currentProduct ?: return
        val rating = binding.ratingBar.rating
        FirebaseUtils.db.collection("productos").document(product.id)
            .update("calificacion", rating)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Calificación enviada: $rating estrellas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProductDetails() {
        FirebaseUtils.db.collection("productos").document(args.productId)
            .get()
            .addOnSuccessListener { document ->
                currentProduct = document.toObject(Product::class.java)?.apply { id = document.id }
                currentProduct?.let { displayProduct(it) }
            }
    }

    private fun displayProduct(product: Product) {
        binding.apply {
            tvName.text = product.nombre
            tvPrice.text = "$${product.precio}"
            tvDescription.text = product.descripcion
            tvCategory.text = product.categoria
            tvState.text = product.estado
            tvQuantity.text = "Disponibles: ${product.cantidad}"
            ratingBar.rating = product.calificacion
            tvSeller.text = "Vendedor ID: ${product.vendedorId}"
            
            Glide.with(this@ProductDetailFragment)
                .load(product.imagen)
                .into(ivProduct)
        }
    }

    private fun addToCart() {
        val product = currentProduct ?: return
        val userId = FirebaseUtils.currentUserId ?: return
        FirebaseUtils.db.collection("usuarios").document(userId).collection("cart").document(product.id)
            .set(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
