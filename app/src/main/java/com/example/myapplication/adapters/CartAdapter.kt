package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemCartBinding
import com.example.myapplication.models.Product

class CartAdapter(
    private var products: List<Product>,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvName.text = product.nombre
            tvPrice.text = "$${product.precio}"
            tvQuantity.text = "Cantidad: 1" // Simplified for now
            
            Glide.with(root.context)
                .load(product.imagen)
                .into(ivProduct)

            btnRemove.setOnClickListener { onRemoveClick(product) }
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
