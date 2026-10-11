package com.example.trueke.utils

fun parseReferenceValue(input: String): Int? =
    input.trim().toIntOrNull()?.takeIf { it >= 0 }

fun parseDistanceKm(input: String): Double? =
    input.trim().replace(',', '.').toDoubleOrNull()
        ?.takeIf { it.isFinite() && it >= 0.0 }
