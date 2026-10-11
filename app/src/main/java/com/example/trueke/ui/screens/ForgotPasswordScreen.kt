package com.example.trueke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trueke.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit
) {

    val auth = FirebaseAuth.getInstance()

    var email by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }

    var isError by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Ingresa tu correo y te enviaremos instrucciones para recuperar tu cuenta.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                message = ""
                isError = false
            },
            label = {
                Text("Correo electrónico")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = isError,
            enabled = !isLoading
        )

        if (message.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                val cleanEmail = email.trim()

                when {

                    cleanEmail.isEmpty() -> {
                        isError = true
                        message = "Debes ingresar tu correo electrónico."
                    }

                    !cleanEmail.isValidEmail() -> {

                        isError = true
                        message = "Ingresa un correo electrónico válido."
                    }

                    else -> {
                        isError = false
                        message = ""
                        isLoading = true

                        auth.sendPasswordResetEmail(cleanEmail)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                isError = !task.isSuccessful
                                message = if (task.isSuccessful) {
                                    "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña."
                                } else {
                                    "No fue posible solicitar la recuperación. Inténtalo nuevamente."
                                }
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {

            Text(if (isLoading) "Enviando..." else "Recuperar contraseña")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onBackToLogin,
            enabled = !isLoading
        ) {

            Text("Volver al inicio de sesión")
        }
    }
}
