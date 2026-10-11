package com.example.trueke.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val condition: String = "",
    val referenceValue: Int = 0,
    val distanceKm: Double = 0.0,
    val owner: String = "",
    val ownerUid: String = ""
) {
    fun isOwnedBy(userUid: String?): Boolean {
        return !userUid.isNullOrBlank() &&
                ownerUid.isNotBlank() && ownerUid == userUid
    }
}
