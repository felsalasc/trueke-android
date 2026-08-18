package com.example.trueke.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val condition: String,
    val referenceValue: Int,
    val distanceKm: Double,
    val owner: String
)