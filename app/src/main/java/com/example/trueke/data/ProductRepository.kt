package com.example.trueke.data

import com.example.trueke.model.Product

object ProductRepository {

    val products = listOf(

        Product(
            id = 1,
            name = "Bicicleta Oxford",
            description = "Bicicleta aro 29 en muy buen estado. Ideal para ciudad y paseos.",
            category = "Deportes",
            condition = "Buen estado",
            referenceValue = 120000,
            distanceKm = 1.2,
            owner = "Carlos"
        ),

        Product(
            id = 2,
            name = "PlayStation 4",
            description = "Consola PlayStation 4 de 1 TB con dos controles.",
            category = "Videojuegos",
            condition = "Buen estado",
            referenceValue = 150000,
            distanceKm = 2.1,
            owner = "Daniela"
        ),

        Product(
            id = 3,
            name = "Guitarra acústica",
            description = "Guitarra acústica en excelente estado, poco uso.",
            category = "Instrumentos",
            condition = "Excelente",
            referenceValue = 110000,
            distanceKm = 3.4,
            owner = "Matías"
        ),

        Product(
            id = 4,
            name = "Notebook Lenovo",
            description = "Notebook Lenovo de 14 pulgadas, 8 GB RAM y SSD de 256 GB.",
            category = "Tecnología",
            condition = "Usado",
            referenceValue = 180000,
            distanceKm = 4.3,
            owner = "Andrea"
        ),

        Product(
            id = 5,
            name = "Cámara Canon",
            description = "Cámara digital Canon con lente incluido y bolso de transporte.",
            category = "Fotografía",
            condition = "Muy buen estado",
            referenceValue = 160000,
            distanceKm = 5.1,
            owner = "Pedro"
        )
    )
}