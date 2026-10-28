package com.example.myapplication.database

import com.example.myapplication.models.Product
import com.example.myapplication.utils.DummyData
import com.example.myapplication.utils.FirebaseUtils
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepository(private val productDao: ProductDao) {

    suspend fun refreshProducts() {
        try {
            val snapshot = FirebaseUtils.db.collection("productos").get().await()
            var products = snapshot.toObjects(Product::class.java)

            if (products.isEmpty()) {
                // Seed Firestore with dummy products if collection is empty
                val currentSellerId = FirebaseUtils.currentUserId ?: "vendedor_default"
                val dummyList = DummyData.getDummyProducts(currentSellerId)
                for (prod in dummyList) {
                    val docRef = FirebaseUtils.db.collection("productos").document()
                    prod.id = docRef.id
                    docRef.set(prod).await()
                }
                val newSnapshot = FirebaseUtils.db.collection("productos").get().await()
                products = newSnapshot.documents.mapIndexed { index, doc ->
                    val p = newSnapshot.toObjects(Product::class.java)[index]
                    p.apply { id = doc.id }
                }
            } else {
                products = snapshot.documents.mapIndexed { index, doc ->
                    products[index].apply { if (id.isEmpty()) id = doc.id }
                }
            }

            val entities = products.map { 
                ProductEntity(
                    it.id, it.nombre, it.descripcion, it.precio, 
                    it.categoria, it.estado, it.cantidad, it.imagen, 
                    it.vendedorId, it.calificacion
                )
            }
            productDao.deleteAllProducts()
            productDao.insertProducts(entities)
        } catch (e: Exception) {
            // Offline fallback: insert dummy data into local Room DB if empty
            val local = productDao.getAllProducts()
            if (local.isEmpty()) {
                val dummyEntities = DummyData.getDummyProducts("vendedor_default").map {
                    ProductEntity(
                        it.id.ifEmpty { UUID.randomUUID().toString() },
                        it.nombre, it.descripcion, it.precio, it.categoria,
                        it.estado, it.cantidad, it.imagen, it.vendedorId, it.calificacion
                    )
                }
                productDao.insertProducts(dummyEntities)
            }
        }
    }

    suspend fun getLocalProducts(): List<Product> {
        val entities = productDao.getAllProducts()
        if (entities.isEmpty()) {
            return DummyData.getDummyProducts("vendedor_default")
        }
        return entities.map {
            Product(
                it.id, it.nombre, it.descripcion, it.precio,
                it.categoria, it.estado, it.cantidad, it.imagen,
                it.vendedorId, it.calificacion
            )
        }
    }
}
