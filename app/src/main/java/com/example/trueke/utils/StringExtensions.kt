package com.example.trueke.utils

import android.util.Patterns

/**
 * Función de extensión Kotlin para validar
 * el formato de un correo electrónico.
 */
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() &&
            Patterns.EMAIL_ADDRESS
                .matcher(this.trim())
                .matches()
}