package com.example.trueke.data

import com.example.trueke.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private const val COLLECTION_PRODUCTS = "products"

    /**
     * Escucha en tiempo real los productos almacenados en Firestore.
     */
    fun listenProducts(
        onProductsChanged: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {

        return firestore
            .collection(COLLECTION_PRODUCTS)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {

                    onError(
                        exception.localizedMessage
                            ?: "Error al obtener los productos"
                    )

                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onProductsChanged(emptyList())
                    return@addSnapshotListener
                }

                val products = snapshot.documents.mapNotNull { document ->

                    try {

                        Product(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            category = document.getString("category") ?: "",
                            condition = document.getString("condition") ?: "",
                            referenceValue =
                                document.getLong("referenceValue")
                                    ?.toInt() ?: 0,
                            distanceKm =
                                document.getDouble("distanceKm") ?: 0.0,
                            owner = document.getString("owner") ?: ""
                        )

                    } catch (e: Exception) {

                        null
                    }
                }

                onProductsChanged(products)
            }
    }

    /**
     * CREATE
     */
    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val document =
            firestore.collection(COLLECTION_PRODUCTS).document()

        val productWithId =
            product.copy(id = document.id)

        document
            .set(productWithId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "No fue posible registrar el producto"
                )
            }
    }

    /**
     * UPDATE
     */
    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (product.id.isBlank()) {

            onError("Producto inválido")
            return
        }

        firestore
            .collection(COLLECTION_PRODUCTS)
            .document(product.id)
            .set(product)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "No fue posible actualizar el producto"
                )
            }
    }

    /**
     * DELETE
     */
    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (productId.isBlank()) {

            onError("Producto inválido")
            return
        }

        firestore
            .collection(COLLECTION_PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.localizedMessage
                        ?: "No fue posible eliminar el producto"
                )
            }
    }
}