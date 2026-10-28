package com.example.myapplication.models

import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val tipoUsuario: String = "Comprador", // "Comprador" or "Administrador"
    val fechaRegistro: Long = System.currentTimeMillis()
)
