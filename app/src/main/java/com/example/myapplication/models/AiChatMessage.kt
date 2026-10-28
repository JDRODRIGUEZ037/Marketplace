package com.example.myapplication.models

data class AiChatMessage(
    val sender: SenderType,
    val text: String,
    val recommendedProduct: Product? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class SenderType {
        USER,
        AI
    }
}
