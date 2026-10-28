package com.example.myapplication.adapters

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemAiMessageAiBinding
import com.example.myapplication.databinding.ItemAiMessageUserBinding
import com.example.myapplication.models.AiChatMessage
import com.example.myapplication.models.Product
import java.text.SimpleDateFormat
import java.util.*

class AiChatAdapter(
    private var messages: MutableList<AiChatMessage>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_AI = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == AiChatMessage.SenderType.USER) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_USER) {
            UserViewHolder(ItemAiMessageUserBinding.inflate(inflater, parent, false))
        } else {
            AiViewHolder(ItemAiMessageAiBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))

        if (holder is UserViewHolder) {
            holder.binding.tvMessage.text = msg.text
            holder.binding.tvTime.text = timeString
        } else if (holder is AiViewHolder) {
            holder.binding.tvMessage.text = msg.text
            holder.binding.tvTime.text = timeString

            val product = msg.recommendedProduct
            if (product != null) {
                holder.binding.cardProductRecommendation.visibility = View.VISIBLE
                holder.binding.tvProductName.text = product.nombre
                holder.binding.tvProductPrice.text = "$${product.precio}"
                holder.binding.tvProductRating.text = "⭐️ ${product.calificacion} / 5.0"
                holder.binding.tvProductDescription.text = product.descripcion

                Glide.with(holder.itemView.context)
                    .load(product.imagen)
                    .placeholder(R.drawable.ic_menu_report_image)
                    .into(holder.binding.ivProduct)

                val clickListener = View.OnClickListener {
                    onProductClick(product)
                }

                holder.binding.cardProductRecommendation.setOnClickListener(clickListener)
                holder.binding.btnViewDetail.setOnClickListener(clickListener)
            } else {
                holder.binding.cardProductRecommendation.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: AiChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(newList: List<AiChatMessage>) {
        messages = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: ItemAiMessageUserBinding) : RecyclerView.ViewHolder(binding.root)
    inner class AiViewHolder(val binding: ItemAiMessageAiBinding) : RecyclerView.ViewHolder(binding.root)
}
