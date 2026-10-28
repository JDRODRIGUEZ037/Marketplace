package com.example.myapplication.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.AiChatAdapter
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.ProductRepository
import com.example.myapplication.databinding.FragmentAiChatBinding
import com.example.myapplication.models.AiChatMessage
import com.example.myapplication.models.Product
import com.example.myapplication.utils.AiRecommendationEngine
import com.example.myapplication.utils.DummyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AiChatFragment : Fragment() {

    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AiChatAdapter
    private val engine = AiRecommendationEngine()
    private var catalogProducts = listOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadCatalog()
        setupListeners()

        // Welcome message
        adapter.addMessage(engine.getWelcomeMessage())
    }

    private fun setupRecyclerView() {
        adapter = AiChatAdapter(mutableListOf()) { product ->
            // Navigate to ProductDetailFragment when clicking on recommended product card
            val bundle = bundleOf("productId" to product.id)
            findNavController().navigate(R.id.action_aiChatFragment_to_productDetailFragment, bundle)
        }

        binding.rvAiMessages.apply {
            adapter = this@AiChatFragment.adapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun loadCatalog() {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = ProductRepository(db.productDao())

        CoroutineScope(Dispatchers.IO).launch {
            val products = repository.getLocalProducts()
            catalogProducts = if (products.isNotEmpty()) products else DummyData.getDummyProducts()
        }
    }

    private fun setupListeners() {
        binding.btnSendAi.setOnClickListener {
            sendMessage(binding.etAiMessage.text.toString())
        }

        binding.chipPantallas.setOnClickListener {
            sendMessage("📺 Recomiéndame las mejores pantallas")
        }

        binding.chipLaptops.setOnClickListener {
            sendMessage("💻 Busco recomendaciones de laptops para trabajo")
        }

        binding.chipCelulares.setOnClickListener {
            sendMessage("📱 ¿Qué smartphone me recomiendas?")
        }

        binding.chipHogar.setOnClickListener {
            sendMessage("🏠 Recomiéndame productos para el hogar")
        }
    }

    private fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        // Clear input
        binding.etAiMessage.text?.clear()

        // 1. Add User Message
        val userMsg = AiChatMessage(
            sender = AiChatMessage.SenderType.USER,
            text = trimmed
        )
        adapter.addMessage(userMsg)
        binding.rvAiMessages.smoothScrollToPosition(adapter.itemCount - 1)

        // 2. Process with AI engine & generate AI Response
        val aiResponse = engine.processMessage(trimmed, catalogProducts)
        adapter.addMessage(aiResponse)
        binding.rvAiMessages.smoothScrollToPosition(adapter.itemCount - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
