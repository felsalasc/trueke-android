package com.example.trueke.data

import com.example.trueke.model.Product
import com.google.firebase.firestore.ListenerRegistration

/**
 * Contrato de las operaciones del catálogo.
 * HomeScreen utiliza esta interfaz; ProductRepository realiza las operaciones con Firebase.
 * Los resultados se entregan mediante callbacks porque las operaciones son asíncronas.
 */
interface ProductDataSource {
    /** Devuelve la suscripción para que la pantalla pueda eliminarla al salir. */
    fun listenProducts(
        onProductsChanged: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration

    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}
