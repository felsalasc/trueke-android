package com.example.trueke.model

import java.util.Date

/** Solicitud y respuesta de una operación de comunicación completada. */
data class CommunicationRecord(
    val id: String,
    val mode: String,
    val request: String,
    val response: String,
    val createdAt: Date?
) {
    companion object {
        const val WRITE = "write"
        const val SPEAK = "speak"
        const val MAX_TEXT_LENGTH = 4000
    }
}
