package com.example.trueke

import com.example.trueke.model.Product
import com.example.trueke.utils.filterProducts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private val products = listOf(

        Product(
            id = "1",
            name = "Bicicleta Oxford",
            description = "Bicicleta aro 29",
            category = "Deportes",
            condition = "Buen estado",
            referenceValue = 120000,
            distanceKm = 1.2,
            owner = "Carlos"
        ),

        Product(
            id = "2",
            name = "PlayStation 4",
            description = "Consola PlayStation 4",
            category = "Videojuegos",
            condition = "Buen estado",
            referenceValue = 150000,
            distanceKm = 2.1,
            owner = "Daniela"
        ),

        Product(
            id = "3",
            name = "Guitarra acústica",
            description = "Guitarra en excelente estado",
            category = "Instrumentos",
            condition = "Excelente",
            referenceValue = 110000,
            distanceKm = 3.4,
            owner = "Matías"
        ),

        Product(
            id = "4",
            name = "Notebook Lenovo",
            description = "Notebook Lenovo 14 pulgadas",
            category = "Tecnología",
            condition = "Usado",
            referenceValue = 180000,
            distanceKm = 8.0,
            owner = "Andrea"
        )
    )

    /**
     * Comprueba que el filtro por categoría
     * retorne únicamente los productos esperados.
     */
    @Test
    fun filterProducts_byCategory_returnsCorrectProduct() {

        val result = filterProducts(products) { product ->
            product.category == "Deportes"
        }

        assertEquals(1, result.size)
        assertEquals("Bicicleta Oxford", result.first().name)
        assertEquals("Deportes", result.first().category)
    }

    /**
     * Comprueba que el filtro por distancia
     * descarte productos que superan el máximo.
     */
    @Test
    fun filterProducts_byDistance_returnsProductsWithinFiveKm() {

        val result = filterProducts(products) { product ->
            product.distanceKm <= 5.0
        }

        assertEquals(3, result.size)

        assertTrue(
            result.all { product ->
                product.distanceKm <= 5.0
            }
        )
    }

    /**
     * Comprueba la lógica utilizada en HomeScreen
     * para determinar productos en buen estado.
     */
    @Test
    fun filterProducts_byGoodCondition_returnsExpectedProducts() {

        val result = filterProducts(products) { product ->

            product.condition.contains(
                "Buen",
                ignoreCase = true
            ) ||
                    product.condition.contains(
                        "Excelente",
                        ignoreCase = true
                    )
        }

        assertEquals(3, result.size)

        assertTrue(
            result.none { product ->
                product.condition == "Usado"
            }
        )
    }

    /**
     * Comprueba filtros combinados:
     * categoría y distancia máxima.
     */
    @Test
    fun filterProducts_combinedFilters_returnsCorrectResult() {

        val result = filterProducts(products) { product ->

            product.category == "Videojuegos" &&
                    product.distanceKm <= 5.0
        }

        assertEquals(1, result.size)
        assertEquals("PlayStation 4", result.first().name)
    }

    /**
     * Comprueba que una búsqueda sin coincidencias
     * retorne una lista vacía.
     */
    @Test
    fun filterProducts_withoutMatches_returnsEmptyList() {

        val result = filterProducts(products) { product ->
            product.category == "Categoría inexistente"
        }

        assertTrue(result.isEmpty())
    }
}