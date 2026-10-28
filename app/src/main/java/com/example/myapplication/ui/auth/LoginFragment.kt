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
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.models.User
import com.example.myapplication.ui.admin.AdminActivity
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.utils.FirebaseUtils

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                setLoading(true)
                FirebaseUtils.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        checkUserTypeAndNavigate()
                    }
                    .addOnFailureListener { exception ->
                        setLoading(false)
                        val errorMsg = exception.localizedMessage ?: "Error al iniciar sesión"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun checkUserTypeAndNavigate() {
        val uid = FirebaseUtils.currentUserId
        val userEmail = FirebaseUtils.auth.currentUser?.email ?: ""

        if (uid.isNullOrEmpty()) {
            setLoading(false)
            Toast.makeText(context, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseUtils.db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (!isAdded || _binding == null) return@addOnSuccessListener

                val user = if (document.exists()) document.toObject(User::class.java) else null
                val userType = user?.tipoUsuario ?: "Comprador"

                // Create document in Firestore if it doesn't exist yet
                if (!document.exists()) {
                    val newUser = User(
                        uid = uid,
                        nombre = userEmail.substringBefore("@").ifEmpty { "Usuario" },
                        correo = userEmail,
                        tipoUsuario = userType
                    )
                    FirebaseUtils.db.collection("usuarios").document(uid).set(newUser)
                }

                val targetIntent = if (userType.equals("Administrador", ignoreCase = true)) {
                    Intent(requireActivity(), AdminActivity::class.java)
                } else {
                    Intent(requireActivity(), MainActivity::class.java)
                }

                targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(targetIntent)
                requireActivity().finish()
            }
            .addOnFailureListener { exception ->
                if (!isAdded || _binding == null) return@addOnFailureListener

                // Fallback to MainActivity if Firestore query fails (e.g., rules/offline)
                Toast.makeText(context, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                val targetIntent = Intent(requireActivity(), MainActivity::class.java)
                targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(targetIntent)
                requireActivity().finish()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        if (_binding == null) return
        binding.btnLogin.isEnabled = !isLoading
        binding.tvRegister.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
