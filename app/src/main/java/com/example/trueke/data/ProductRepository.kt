package com.example.trueke.data

import com.example.trueke.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration

object ProductRepository : ProductDataSource {

    private val firestore = FirebaseFirestore.getInstance()

    private const val COLLECTION_PRODUCTS = "products"

    /**
     * Escucha en tiempo real los productos almacenados en Firestore.
     */
    override fun listenProducts(
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
                            owner = document.getString("owner") ?: "",
                            ownerUid = document.getString("ownerUid") ?: ""
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
    override fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onError("Inicia sesión para publicar un producto")
            return
        }

        val document =
            firestore.collection(COLLECTION_PRODUCTS).document()

        val productWithId =
            product.copy(
                id = document.id,
                ownerUid = currentUser.uid,
                owner = currentUser.email ?: "Usuario TRUEKE"
            )

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
    override fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (product.id.isBlank()) {

            onError("Producto inválido")
            return
        }

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid == null) {
            onError("Inicia sesión para editar un producto")
            return
        }

        val document = firestore
            .collection(COLLECTION_PRODUCTS)
            .document(product.id)

        firestore.runTransaction { transaction ->
            val storedProduct = transaction.get(document)
            requireOwnership(storedProduct, userUid)

            // La edición conserva el ID y los datos de propiedad almacenados.
            transaction.update(
                document,
                mapOf(
                    "name" to product.name,
                    "description" to product.description,
                    "category" to product.category,
                    "condition" to product.condition,
                    "referenceValue" to product.referenceValue,
                    "distanceKm" to product.distanceKm
                )
            )
            true
        }
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
    override fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (productId.isBlank()) {

            onError("Producto inválido")
            return
        }

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid == null) {
            onError("Inicia sesión para eliminar un producto")
            return
        }

        val document = firestore
            .collection(COLLECTION_PRODUCTS)
            .document(productId)

        firestore.runTransaction { transaction ->
            val storedProduct = transaction.get(document)
            requireOwnership(storedProduct, userUid)
            transaction.delete(document)
            true
        }
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

    private fun requireOwnership(document: DocumentSnapshot, userUid: String) {
        if (!document.exists()) {
            throw FirebaseFirestoreException(
                "El producto ya no existe",
                FirebaseFirestoreException.Code.NOT_FOUND
            )
        }

        val storedProduct = Product(
            ownerUid = (document.get("ownerUid") as? String).orEmpty()
        )
        if (!storedProduct.isOwnedBy(userUid)) {
            throw FirebaseFirestoreException(
                "Solo el propietario puede modificar este producto",
                FirebaseFirestoreException.Code.PERMISSION_DENIED
            )
        }
    }
}
