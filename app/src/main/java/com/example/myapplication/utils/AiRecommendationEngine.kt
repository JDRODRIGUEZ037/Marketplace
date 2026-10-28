package com.example.myapplication.utils

import com.example.myapplication.models.AiChatMessage
import com.example.myapplication.models.Product

class AiRecommendationEngine {

    enum class DialogueState {
        IDLE,
        ASKING_BUDGET,
        ASKING_USE_CASE
    }

    private var currentState = DialogueState.IDLE
    private var selectedTopic: String = "pantallas"
    private var budgetPreference: String = ""
    private var useCasePreference: String = ""

    fun processMessage(userMessage: String, catalog: List<Product>): AiChatMessage {
        val lowerText = userMessage.lowercase().trim()

        // If in IDLE and user asks for recommendations or mentions product category
        if (currentState == DialogueState.IDLE) {
            val isRecommendationQuery = lowerText.contains("recomiend") ||
                    lowerText.contains("pantalla") ||
                    lowerText.contains("tv") ||
                    lowerText.contains("televisor") ||
                    lowerText.contains("monitor") ||
                    lowerText.contains("laptop") ||
                    lowerText.contains("celular") ||
                    lowerText.contains("mejores") ||
                    lowerText.contains("buscar") ||
                    lowerText.contains("comprar") ||
                    lowerText.contains("suger")

            if (isRecommendationQuery) {
                // Determine topic
                selectedTopic = when {
                    lowerText.contains("laptop") || lowerText.contains("computador") -> "laptops"
                    lowerText.contains("celular") || lowerText.contains("smartphone") || lowerText.contains("telefono") -> "celulares"
                    lowerText.contains("silla") -> "sillas"
                    lowerText.contains("cafe") || lowerText.contains("cafetera") -> "cafeteras"
                    else -> "pantallas"
                }

                currentState = DialogueState.ASKING_BUDGET
                val responseText = "¡Excelente! Con gusto te ayudo a elegir la mejor opción en **$selectedTopic** para ti. 🎯\n\n" +
                        "1️⃣ **Primera pregunta:** ¿Cuál es tu presupuesto aproximado?\n\n" +
                        "• **A)** Menos de $350 USD (Económico)\n" +
                        "• **B)** Entre $350 y $800 USD (Gama Media/Alta)\n" +
                        "• **C)** Más de $800 USD (Gama Premium)"

                return AiChatMessage(
                    sender = AiChatMessage.SenderType.AI,
                    text = responseText
                )
            } else {
                // General conversation
                val greeting = if (lowerText.contains("hola") || lowerText.contains("buenas")) {
                    "¡Hola! 👋 Soy tu Asistente Virtual de Marketplace. Puedo ayudarte a encontrar los mejores productos y hacerte recomendaciones personalizadas.\n\n¿Buscas alguna **pantalla**, **laptop**, **celular** o alguna sugerencia en especial?"
                } else {
                    "Soy tu Asistente Virtual. Escribe algo como *'Recomiéndame las mejores pantallas'* para ayudarte a elegir el producto perfecto en 2 sencillos pasos."
                }
                return AiChatMessage(
                    sender = AiChatMessage.SenderType.AI,
                    text = greeting
                )
            }
        }

        // State 1: User is responding to Question 1 (Budget)
        if (currentState == DialogueState.ASKING_BUDGET) {
            budgetPreference = lowerText
            currentState = DialogueState.ASKING_USE_CASE

            val question2 = "¡Entendido! Presupuesto registrado. 👍\n\n" +
                    "2️⃣ **Segunda pregunta:** ¿Qué uso principal le darás o qué características prefieres?\n\n" +
                    "• **A)** Para la sala / ver películas y series en alta definición (Smart TV)\n" +
                    "• **B)** Para gaming / videojuegos (Alta tasa de refresco 165Hz, baja latencia)\n" +
                    "• **C)** Para trabajo, oficina o tareas diarias"

            return AiChatMessage(
                sender = AiChatMessage.SenderType.AI,
                text = question2
            )
        }

        // State 2: User is responding to Question 2 (Use case) -> Recommend Product
        if (currentState == DialogueState.ASKING_USE_CASE) {
            useCasePreference = lowerText
            currentState = DialogueState.IDLE // Reset to IDLE after recommendation

            val recommendedProduct = findBestProduct(selectedTopic, budgetPreference, useCasePreference, catalog)

            val textResponse = if (recommendedProduct != null) {
                "¡Perfecto! Analicé tus respuestas (Presupuesto: *$budgetPreference*, Uso: *$useCasePreference*).\n\n" +
                        "Basado en tus preferencias, te recomiendo este producto:\n\n" +
                        "📺 **${recommendedProduct.nombre}**\n" +
                        "${recommendedProduct.descripcion}\n\n" +
                        "💰 **Precio:** $${recommendedProduct.precio}\n" +
                        "⭐️ **Calificación:** ${recommendedProduct.calificacion} / 5.0\n\n" +
                        "👇 **Haz clic en la tarjeta de abajo para ir directo al detalle del producto:**"
            } else {
                "No encontré un producto exacto que coincida, pero te sugiero revisar nuestro catálogo principal."
            }

            return AiChatMessage(
                sender = AiChatMessage.SenderType.AI,
                text = textResponse,
                recommendedProduct = recommendedProduct
            )
        }

        return AiChatMessage(
            sender = AiChatMessage.SenderType.AI,
            text = "¡Hola! ¿En qué más te puedo colaborar hoy?"
        )
    }

    private fun findBestProduct(
        topic: String,
        budget: String,
        useCase: String,
        catalog: List<Product>
    ): Product? {
        val products = if (catalog.isNotEmpty()) catalog else DummyData.getDummyProducts()

        if (topic == "pantallas") {
            val isGaming = useCase.contains("gaming") || useCase.contains("juego") || useCase.contains("b") || useCase.contains("monitor")
            val isLowBudget = budget.contains("menos") || budget.contains("350") || budget.contains("a")

            if (isGaming) {
                return products.firstOrNull { it.nombre.contains("Gamer", ignoreCase = true) || it.nombre.contains("Curva", ignoreCase = true) }
                    ?: products.firstOrNull { it.categoria.contains("Monitor", ignoreCase = true) }
                    ?: products[1]
            } else if (isLowBudget) {
                return products.firstOrNull { it.nombre.contains("43", ignoreCase = true) || it.precio < 400.0 }
                    ?: products[2]
            } else {
                return products.firstOrNull { it.nombre.contains("OLED", ignoreCase = true) || it.nombre.contains("55", ignoreCase = true) }
                    ?: products[0]
            }
        } else if (topic == "laptops") {
            return products.firstOrNull { it.nombre.contains("Laptop", ignoreCase = true) }
                ?: products.firstOrNull { it.categoria.contains("Electrónica", ignoreCase = true) }
        } else if (topic == "celulares") {
            return products.firstOrNull { it.nombre.contains("Smartphone", ignoreCase = true) }
                ?: products.firstOrNull { it.categoria.contains("Electrónica", ignoreCase = true) }
        }

        return products.maxByOrNull { it.calificacion } ?: products.firstOrNull()
    }

    fun getWelcomeMessage(): AiChatMessage {
        return AiChatMessage(
            sender = AiChatMessage.SenderType.AI,
            text = "¡Hola! 👋 Soy tu **Asistente Virtual IA de Marketplace**.\n\n" +
                    "Estoy aquí para ayudarte a elegir los mejores productos. Si buscas recomendaciones, dime por ejemplo:\n" +
                    "• *\"Recomiéndame las mejores pantallas\"*\n" +
                    "• *\"Busco una laptop para trabajo\"*\n" +
                    "• *\"¿Qué smartphone me recomiendas?\"*\n\n" +
                    "Te haré 2 sencillas preguntas para recomendarte la opción perfecta."
        )
    }
}
