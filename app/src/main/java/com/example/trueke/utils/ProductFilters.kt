package com.example.trueke.utils

import com.example.trueke.model.Product

/**
 * Función de orden superior para filtrar productos.
 *
 * Recibe:
 * - Una lista de productos.
 * - Una función lambda que recibe un Product
 *   y devuelve true o false.
 *
 * Retorna:
 * - Una lista de productos que cumplen la condición.
 */
fun filterProducts(
    products: List<Product>,
    predicate: (Product) -> Boolean
): List<Product> {

    return products.filter(predicate)
}