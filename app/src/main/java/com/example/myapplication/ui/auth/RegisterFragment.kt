package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.models.User
import com.example.myapplication.ui.admin.AdminActivity
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.utils.FirebaseUtils

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val tipoUsuario = if (binding.rbAdmin.isChecked) "Administrador" else "Comprador"

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                binding.btnRegister.isEnabled = false
                FirebaseUtils.auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: ""
                        val user = User(uid, name, email, tipoUsuario)
                        saveUserToFirestore(user)
                    }
                    .addOnFailureListener { exception ->
                        binding.btnRegister.isEnabled = true
                        val errorMsg = exception.localizedMessage ?: "Error al registrar usuario"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun saveUserToFirestore(user: User) {
        FirebaseUtils.db.collection("usuarios").document(user.uid).set(user)
            .addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                
                // Directly navigate to appropriate activity upon registration
                val targetIntent = if (user.tipoUsuario.equals("Administrador", ignoreCase = true)) {
                    Intent(requireActivity(), AdminActivity::class.java)
                } else {
                    Intent(requireActivity(), MainActivity::class.java)
                }
                targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(targetIntent)
                requireActivity().finish()
            }
            .addOnFailureListener { exception ->
                if (!isAdded) return@addOnFailureListener
                binding.btnRegister.isEnabled = true
                Toast.makeText(context, "Error al guardar datos de usuario: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                // Still allow navigation to login or main
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
