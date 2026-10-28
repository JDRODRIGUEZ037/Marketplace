package com.example.myapplication.utils

import com.example.myapplication.models.Product

object DummyData {
    fun getDummyProducts(sellerId: String = "seller_default"): List<Product> {
        return listOf(
            Product(
                id = "dummy_1",
                nombre = "Pantalla Smart TV OLED 55\" 4K",
                descripcion = "Pantalla OLED 55 pulgadas Ultra HD 4K, HDR10+, Smart TV con Google TV, sonido Dolby Atmos y control por voz.",
                precio = 799.99,
                categoria = "Televisores",
                estado = "nuevo",
                cantidad = 8,
                imagen = "https://images.unsplash.com/photo-1593784991095-a205069470b6?w=500",
                vendedorId = sellerId,
                calificacion = 4.8f
            ),
            Product(
                id = "dummy_2",
                nombre = "Pantalla Gamer Curva 27\" 165Hz",
                descripcion = "Pantalla curva de 27 pulgadas QHD 1440p, tasa de refresco de 165Hz, 1ms de respuesta, FreeSync Premium para gaming.",
                precio = 280.00,
                categoria = "Monitores",
                estado = "nuevo",
                cantidad = 15,
                imagen = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500",
                vendedorId = sellerId,
                calificacion = 4.7f
            ),
            Product(
                id = "dummy_3",
                nombre = "Pantalla LED Smart TV 43\" Full HD",
                descripcion = "Pantalla LED de 43 pulgadas, Smart TV con streaming de Netflix, YouTube y Prime Video preinstalados.",
                precio = 320.00,
                categoria = "Televisores",
                estado = "nuevo",
                cantidad = 10,
                imagen = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500",
                vendedorId = sellerId,
                calificacion = 4.3f
            ),
            Product(
                id = "dummy_4",
                nombre = "Smartphone Pro Max 256GB",
                descripcion = "Pantalla AMOLED 120Hz, cámara triple de 108MP, procesador Octa-Core y batería de carga rápida 5000mAh.",
                precio = 899.99,
                categoria = "Electrónica",
                estado = "nuevo",
                cantidad = 12,
                imagen = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500",
                vendedorId = sellerId,
                calificacion = 4.6f
            ),
            Product(
                id = "dummy_5",
                nombre = "Laptop Ultra Slim i7 16GB RAM",
                descripcion = "Laptop ligera con procesador Intel i7 de 12a generación, 16GB RAM, SSD 512GB y pantalla IPS Full HD 15.6\".",
                precio = 1200.00,
                categoria = "Electrónica",
                estado = "nuevo",
                cantidad = 6,
                imagen = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500",
                vendedorId = sellerId,
                calificacion = 4.9f
            ),
            Product(
                id = "dummy_6",
                nombre = "Auriculares Inalámbricos Noise Cancelling",
                descripcion = "Cancelación activa de ruido (ANC), conectividad Bluetooth 5.2, 30 horas de autonomía de batería.",
                precio = 199.99,
                categoria = "Electrónica",
                estado = "nuevo",
                cantidad = 25,
                imagen = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                vendedorId = sellerId,
                calificacion = 4.5f
            ),
            Product(
                id = "dummy_7",
                nombre = "Silla Ergonómica Ejecutiva",
                descripcion = "Silla de oficina ergonómica, soporte lumbar ajustable, reclinable con reposacabezas y malla transpirable.",
                precio = 150.50,
                categoria = "Hogar",
                estado = "nuevo",
                cantidad = 20,
                imagen = "https://images.unsplash.com/photo-1580481072645-022f9a6d8310?w=500",
                vendedorId = sellerId,
                calificacion = 4.2f
            ),
            Product(
                id = "dummy_8",
                nombre = "Cafetera Express Automática",
                descripcion = "Prepara expresso, capuchinos y lattes con espumador de leche integrado y presión de 15 bares.",
                precio = 85.00,
                categoria = "Hogar",
                estado = "nuevo",
                cantidad = 15,
                imagen = "https://images.unsplash.com/photo-1517668808822-9e428824603b?w=500",
                vendedorId = sellerId,
                calificacion = 4.1f
            )
        )
    }
}
