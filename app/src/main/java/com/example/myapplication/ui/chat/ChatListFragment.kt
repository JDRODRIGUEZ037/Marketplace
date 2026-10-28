package com.example.myapplication.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ChatListAdapter
import com.example.myapplication.databinding.FragmentChatListBinding
import com.example.myapplication.models.User
import com.example.myapplication.utils.FirebaseUtils

class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadChats()
    }

    private fun setupRecyclerView() {
        adapter = ChatListAdapter(emptyList()) { user ->
            val bundle = Bundle().apply { putString("receiverId", user.uid) }
            findNavController().navigate(R.id.chatFragment, bundle)
        }
        binding.rvChatList.apply {
            adapter = this@ChatListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadChats() {
        // Simplified: just loading all users for now as potential chat partners
        // In a real app, you'd filter by existing conversations
        FirebaseUtils.db.collection("usuarios").get().addOnSuccessListener { result ->
            val users = result.toObjects(User::class.java).filter { it.uid != FirebaseUtils.currentUserId }
            adapter.updateList(users)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
