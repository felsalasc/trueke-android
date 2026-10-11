package com.example.trueke.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.trueke.data.CommunicationRepository
import com.example.trueke.model.CommunicationRecord
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

/** Comunicación presencial: escritura a voz y reconocimiento de voz a texto. */
@Composable
fun CommunicationDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf("") }
    var engine by remember { mutableStateOf<TextToSpeech?>(null) }
    var ready by remember { mutableStateOf(false) }
    var speaking by remember { mutableStateOf(false) }
    var recognizing by rememberSaveable { mutableStateOf(false) }
    var status by remember { mutableStateOf("Preparando la voz…") }
    var hasError by remember { mutableStateOf(false) }
    var activeUtterance by remember { mutableStateOf<String?>(null) }
    var utteranceNumber by remember { mutableStateOf(0) }
    var utteranceRequest by remember { mutableStateOf("") }
    val userUid = FirebaseAuth.getInstance().currentUser?.uid
    var pendingHistorySaves by remember { mutableStateOf(0) }
    var saveStatus by remember { mutableStateOf("") }
    var saveFailed by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var historyRecords by remember(userUid) { mutableStateOf<List<CommunicationRecord>>(emptyList()) }
    var historyLoading by remember { mutableStateOf(false) }
    var historyError by remember { mutableStateOf("") }
    val maxCharacters = minOf(TextToSpeech.getMaxSpeechInputLength(), CommunicationRecord.MAX_TEXT_LENGTH)

    fun saveInteraction(mode: String, request: String, response: String) {
        if (pendingHistorySaves == 0) saveFailed = false
        pendingHistorySaves++
        if (!saveFailed) saveStatus = "Guardando resultados en tu historial…"
        CommunicationRepository.saveRecord(
            userUid = userUid,
            mode = mode,
            request = request,
            response = response,
            onSuccess = {
                pendingHistorySaves--
                if (pendingHistorySaves == 0 && !saveFailed) {
                    saveStatus = "Resultados guardados en tu historial."
                }
            },
            onError = { error ->
                pendingHistorySaves--
                saveFailed = true
                saveStatus = "El resultado no se guardó. $error"
            }
        )
    }

    DisposableEffect(showHistory, userUid) {
        val subscription = if (showHistory) {
            historyLoading = true
            historyError = ""
            CommunicationRepository.listenRecords(
                userUid = userUid,
                onRecordsChanged = {
                    historyRecords = it
                    historyLoading = false
                },
                onError = {
                    historyRecords = emptyList()
                    historyError = it
                    historyLoading = false
                }
            )
        } else {
            null
        }
        onDispose { subscription?.remove() }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        recognizing = false
        val recognizedText = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.trim()?.takeIf { it.isNotBlank() }
        } else {
            null
        }
        when {
            recognizedText != null -> {
                message = recognizedText
                hasError = false
                status = "Voz convertida a texto. Revisa el mensaje y corrígelo si es necesario."
                saveInteraction(CommunicationRecord.SPEAK, "Convertir voz a texto", recognizedText)
            }
            result.resultCode == Activity.RESULT_CANCELED -> {
                hasError = false
                status = "Reconocimiento cancelado o sin resultado. Se conserva el mensaje anterior."
            }
            else -> {
                hasError = true
                status = "No se obtuvo texto. Se conserva el mensaje anterior. Intenta nuevamente."
            }
        }
    }

    DisposableEffect(context) {
        val handler = Handler(Looper.getMainLooper())
        var disposed = false
        var speech: TextToSpeech? = null

        fun updateUtterance(
            id: String?, text: String, playing: Boolean, error: Boolean,
            completed: Boolean = false
        ) {
            handler.post {
                if (!disposed && id == activeUtterance && id != null) {
                    status = text
                    speaking = playing
                    hasError = error
                    if (!playing) activeUtterance = null
                    if (completed) {
                        saveInteraction(CommunicationRecord.WRITE, utteranceRequest, text)
                    }
                }
            }
        }

        try {
            speech = TextToSpeech(context.applicationContext) { result ->
                // El callback puede llegar antes de que el constructor devuelva la instancia.
                handler.post {
                    if (!disposed) {
                        val initializedSpeech = speech
                        if (result == TextToSpeech.SUCCESS && initializedSpeech != null) {
                            try {
                                val languageResult = initializedSpeech.setLanguage(Locale.forLanguageTag("es-CL"))
                                ready = languageResult >= TextToSpeech.LANG_AVAILABLE
                                hasError = !ready
                                status = if (ready) {
                                    "Voz lista. Escribe un mensaje y presiona Reproducir en voz alta."
                                } else {
                                    "La voz en español no está disponible. Revisa la configuración " +
                                        "de texto a voz de tu dispositivo. Puedes mostrar el mensaje escrito."
                                }
                            } catch (_: RuntimeException) {
                                hasError = true
                                status = "No se pudo configurar la voz. Puedes mostrar el mensaje escrito."
                            }
                        } else {
                            hasError = true
                            status = "No se pudo iniciar la voz. Revisa el servicio de texto a voz " +
                                "del dispositivo. Puedes mostrar el mensaje escrito."
                        }
                    }
                }
            }
            speech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    updateUtterance(utteranceId, "Reproduciendo mensaje…", true, false)
                }

                override fun onDone(utteranceId: String?) {
                    updateUtterance(utteranceId, "Reproducción finalizada.", false, false, completed = true)
                }

                @Deprecated("Callback requerido por UtteranceProgressListener")
                override fun onError(utteranceId: String?) {
                    updateUtterance(utteranceId, "No se pudo reproducir el mensaje.", false, true)
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    updateUtterance(utteranceId, "No se pudo reproducir el mensaje.", false, true)
                }

                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    updateUtterance(utteranceId, "Reproducción detenida.", false, false)
                }
            })
            engine = speech
        } catch (_: RuntimeException) {
            hasError = true
            status = "El servicio de voz no está disponible. Puedes mostrar el mensaje escrito."
        }

        onDispose {
            disposed = true
            handler.removeCallbacksAndMessages(null)
            try {
                speech?.stop()
            } finally {
                speech?.shutdown()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (showHistory) "Mi historial" else "Comunicación accesible") },
        text = {
            if (showHistory) {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Los últimos 20 resultados de tu cuenta. Solo tú puedes consultar este historial.")
                    Spacer(Modifier.height(12.dp))
                    when {
                        historyLoading -> Text("Cargando historial…")
                        historyError.isNotBlank() -> Text(historyError, color = MaterialTheme.colorScheme.error)
                        historyRecords.isEmpty() -> Text("Todavía no tienes resultados guardados.")
                        else -> historyRecords.forEach { record ->
                            Text(
                                if (record.mode == CommunicationRecord.WRITE) "Escribir → voz" else "Hablar → texto",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                record.createdAt?.let {
                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(it)
                                } ?: "Fecha pendiente de confirmación",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text("Solicitud: ${record.request}")
                            Text("Respuesta: ${record.response}")
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            } else {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Escribe lo que quieras comunicar. Muestra el texto a la otra persona " +
                        "o reprodúcelo en voz alta. También puedes pedirle que hable: " +
                        "el texto reconocido reemplazará el mensaje actual. Revisa el resultado. " +
                        "Para escuchar la voz, revisa el volumen multimedia. " +
                        "Los resultados exitosos se guardan en tu historial privado."
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    enabled = !speaking && !recognizing,
                    label = { Text("Tu mensaje") },
                    textStyle = MaterialTheme.typography.titleLarge,
                    minLines = 3,
                    maxLines = 5,
                    supportingText = { Text("${message.length}/$maxCharacters caracteres") },
                    isError = message.length > maxCharacters,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = status,
                    color = if (hasError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                )
                if (saveStatus.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        saveStatus,
                        color = if (saveFailed) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    enabled = !speaking && !recognizing,
                    onClick = {
                        val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
                            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla para convertir tu mensaje en texto")
                        }
                        recognizing = true
                        hasError = false
                        status = "Abriendo reconocimiento de voz…"
                        try {
                            speechLauncher.launch(recognitionIntent)
                        } catch (_: ActivityNotFoundException) {
                            recognizing = false
                            hasError = true
                            status = "No hay una aplicación de reconocimiento de voz compatible. " +
                                "Puedes seguir escribiendo el mensaje."
                        } catch (_: SecurityException) {
                            recognizing = false
                            hasError = true
                            status = "No se pudo abrir el reconocimiento. Revisa los permisos " +
                                "de la aplicación de voz del dispositivo."
                        } catch (_: IllegalStateException) {
                            recognizing = false
                            hasError = true
                            status = "No se pudo iniciar el reconocimiento. Intenta nuevamente."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Hablar y convertir a texto") }
                Spacer(Modifier.height(8.dp))
                Button(
                    enabled = ready && !speaking && !recognizing && message.isNotBlank() &&
                        message.length <= maxCharacters,
                    onClick = {
                        val id = "trueke-message-${++utteranceNumber}"
                        activeUtterance = id
                        utteranceRequest = message.trim()
                        speaking = true
                        hasError = false
                        status = "Preparando reproducción…"
                        try {
                            val result = engine?.speak(utteranceRequest, TextToSpeech.QUEUE_FLUSH, null, id)
                            if (result != TextToSpeech.SUCCESS) {
                                activeUtterance = null
                                speaking = false
                                hasError = true
                                status = "No se pudo reproducir el mensaje."
                            }
                        } catch (_: RuntimeException) {
                            activeUtterance = null
                            speaking = false
                            hasError = true
                            status = "No se pudo reproducir el mensaje."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reproducir en voz alta") }
                TextButton(
                    enabled = speaking,
                    onClick = {
                        activeUtterance = null
                        try {
                            val result = engine?.stop()
                            hasError = result != TextToSpeech.SUCCESS
                            status = if (hasError) "No se pudo detener la voz. Cierra este diálogo."
                                else "Reproducción detenida."
                        } catch (_: RuntimeException) {
                            hasError = true
                            status = "No se pudo detener la voz. Cierra este diálogo."
                        } finally {
                            speaking = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Detener") }
            }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showHistory = !showHistory },
                enabled = !speaking && !recognizing
            ) { Text(if (showHistory) "Volver al mensaje" else "Ver historial") }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = !recognizing) { Text("Volver") }
        }
    )
}
