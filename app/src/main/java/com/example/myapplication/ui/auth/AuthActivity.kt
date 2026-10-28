package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAuthBinding
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.ui.admin.AdminActivity
import com.example.myapplication.utils.FirebaseUtils
import com.example.myapplication.models.User

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Always show Login/Register first as per user request
        // Even if currentUser is not null, we don't skip the auth screen
    }

    private fun checkUserType() {
        val uid = FirebaseUtils.currentUserId ?: return
        FirebaseUtils.db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        val intent = if (user.tipoUsuario == "Administrador") {
                            Intent(this, AdminActivity::class.java)
                        } else {
                            Intent(this, MainActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener {
                android.widget.Toast.makeText(this, "Error al verificar sesión: ${it.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
    }
}
