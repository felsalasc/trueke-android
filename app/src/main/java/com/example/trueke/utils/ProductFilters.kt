package com.example.trueke.utils

import com.example.trueke.model.Product

/**
 * Función de orden superior para filtrar productos.
 * Es inline: el compilador inserta la función y su lambda en el lugar de llamada.
 * Conserva el comportamiento del filtrado y evita crear un objeto para la lambda
 * cuando esta se pasa directamente en la llamada.
 *
 * Recibe:
 * - Una lista de productos.
 * - Una función lambda que recibe un Product
 *   y devuelve true o false.
 *
 * Retorna:
 * - Una lista de productos que cumplen la condición.
 */
inline fun filterProducts(
    products: List<Product>,
    predicate: (Product) -> Boolean
): List<Product> {

    return products.filter(predicate)
}
