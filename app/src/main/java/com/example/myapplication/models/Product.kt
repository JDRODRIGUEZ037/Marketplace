package com.example.myapplication.models

data class Product(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "",
    val estado: String = "nuevo", // "nuevo" or "usado"
    val cantidad: Int = 0,
    val imagen: String = "",
    val vendedorId: String = "",
    val calificacion: Float = 0f
)
