package com.example.trueke.data

import com.example.trueke.model.CommunicationRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/** Historial privado bajo el UID de la cuenta que inició la operación. */
object CommunicationRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun ownsHistory(userUid: String?): Boolean =
        !userUid.isNullOrBlank() && FirebaseAuth.getInstance().currentUser?.uid == userUid

    fun saveRecord(
        userUid: String?,
        mode: String,
        request: String,
        response: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userUid.isNullOrBlank() || !ownsHistory(userUid)) {
            onError("Inicia sesión con la cuenta que realizó la operación para guardar su historial.")
            return
        }
        if (mode !in listOf(CommunicationRecord.WRITE, CommunicationRecord.SPEAK) ||
            request.isBlank() || response.isBlank() ||
            request.length > CommunicationRecord.MAX_TEXT_LENGTH ||
            response.length > CommunicationRecord.MAX_TEXT_LENGTH
        ) {
            onError("El resultado no se pudo guardar: los textos deben tener entre 1 y 4000 caracteres.")
            return
        }

        try {
            firestore.collection("users").document(userUid)
                .collection("communicationHistory").document()
                .set(
                    mapOf(
                        "mode" to mode,
                        "request" to request,
                        "response" to response,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(historyError(it)) }
        } catch (exception: RuntimeException) {
            onError(historyError(exception))
        }
    }

    /** La consulta muestra los últimos 20 registros; no elimina registros anteriores. */
    fun listenRecords(
        userUid: String?,
        onRecordsChanged: (List<CommunicationRecord>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration? {
        if (userUid.isNullOrBlank() || !ownsHistory(userUid)) {
            onError("Inicia sesión para consultar tu historial.")
            return null
        }
        return firestore.collection("users").document(userUid)
            .collection("communicationHistory")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, exception ->
                if (!ownsHistory(userUid)) {
                    onError("La sesión cambió. Vuelve a abrir el historial con tu cuenta.")
                    return@addSnapshotListener
                }
                if (exception != null) {
                    onError(historyError(exception))
                    return@addSnapshotListener
                }
                try {
                    val records = snapshot?.documents.orEmpty().map { document ->
                        CommunicationRecord(
                            id = document.id,
                            mode = document.getString("mode").orEmpty(),
                            request = document.getString("request").orEmpty(),
                            response = document.getString("response").orEmpty(),
                            createdAt = document.getTimestamp("createdAt")?.toDate()
                        )
                    }
                    onRecordsChanged(records)
                } catch (_: RuntimeException) {
                    onError("No se pudieron leer los datos del historial.")
                }
            }
    }

    private fun historyError(exception: Exception): String =
        if (exception is FirebaseFirestoreException &&
            exception.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        ) {
            "No tienes permiso para acceder al historial."
        } else {
            "No se pudo acceder al historial. Revisa tu conexión e intenta nuevamente."
        }
}
