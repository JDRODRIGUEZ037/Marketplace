package com.example.myapplication.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.MessageAdapter
import com.example.myapplication.databinding.FragmentChatBinding
import com.example.myapplication.models.Message
import com.example.myapplication.utils.FirebaseUtils
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        listenForMessages()

        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(emptyList())
        binding.rvMessages.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun listenForMessages() {
        val currentId = FirebaseUtils.currentUserId ?: return
        val receiverId = args.receiverId

        FirebaseUtils.db.collection("chats")
            .whereIn("senderId", listOf(currentId, receiverId))
            .whereIn("receiverId", listOf(currentId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                // Filtering client-side because Firestore doesn't support logical OR for different fields easily in this way
                val filteredMessages = messages.filter {
                    (it.senderId == currentId && it.receiverId == receiverId) ||
                    (it.senderId == receiverId && it.receiverId == currentId)
                }
                adapter.updateList(filteredMessages)
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)
            }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString()
        if (text.isEmpty()) return

        val currentId = FirebaseUtils.currentUserId ?: return
        val message = Message(currentId, args.receiverId, text)
        
        FirebaseUtils.db.collection("chats").add(message)
            .addOnSuccessListener {
                binding.etMessage.text?.clear()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
