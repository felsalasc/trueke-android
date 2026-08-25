package com.example.trueke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.trueke.data.UserRepository
import com.example.trueke.model.User
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var communicationPreference by remember {
        mutableStateOf("Texto")
    }

    var acceptedTerms by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    var region by remember { mutableStateOf("") }
    var expandedRegion by remember { mutableStateOf(false) }


    val regions = listOf(
        "Arica y Parinacota",
        "Tarapacá",
        "Antofagasta",
        "Atacama",
        "Coquimbo",
        "Valparaíso",
        "Metropolitana",
        "O'Higgins",
        "Maule",
        "Ñuble",
        "Biobío",
        "La Araucanía",
        "Los Ríos",
        "Los Lagos",
        "Aysén",
        "Magallanes"
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = ""
            },
            label = {
                Text("Nombre")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Correo
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = {
                Text("Correo electrónico")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expandedRegion,
            onExpandedChange = {
                expandedRegion = !expandedRegion
            }
        ) {

            OutlinedTextField(
                value = region,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Región")
                },
                placeholder = {
                    Text("Selecciona una región")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedRegion
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = {
                    expandedRegion = false
                }
            ) {

                regions.forEach { option ->

                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            region = option
                            expandedRegion = false
                            errorMessage = ""
                        }
                    )
                }
            }
        }

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = {
                Text("Contraseña")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            label = {
                Text("Confirmar contraseña")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Preferencia de comunicación
        Text(
            text = "Preferencia de comunicación",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = communicationPreference == "Texto",
                onClick = {
                    communicationPreference = "Texto"
                }
            )

            Text("Texto")

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(
                selected = communicationPreference == "Visual",
                onClick = {
                    communicationPreference = "Visual"
                }
            )

            Text("Visual")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Términos y condiciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = acceptedTerms,
                onCheckedChange = {
                    acceptedTerms = it
                    errorMessage = ""
                }
            )

            Text(
                text = "Acepto los términos y condiciones"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Mensaje de éxito
        if (successMessage.isNotEmpty()) {

            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón registrar
        Button(
            onClick = {

                errorMessage = ""
                successMessage = ""

                when {

                    name.isBlank() ||
                            email.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank() -> {

                        errorMessage = "Completa todos los campos"
                    }

                    region.isBlank() -> {
                        errorMessage = "Selecciona una región"
                    }


                    password.length < 6 -> {

                        errorMessage =
                            "La contraseña debe tener al menos 6 caracteres"
                    }

                    password != confirmPassword -> {

                        errorMessage =
                            "Las contraseñas no coinciden"
                    }

                    !acceptedTerms -> {

                        errorMessage =
                            "Debes aceptar los términos y condiciones"
                    }

                    UserRepository.users.any {
                        it.email.equals(
                            email.trim(),
                            ignoreCase = true
                        )
                    } -> {

                        errorMessage =
                            "El correo ya se encuentra registrado"
                    }

                    else -> {

                        UserRepository.users.add(
                            User(
                                name = name.trim(),
                                email = email.trim(),
                                password = password,
                                region = region,
                                communicationPreference = communicationPreference
                            )
                        )

                        successMessage =
                            "Usuario registrado correctamente"

                        // Limpiar formulario
                        name = ""
                        email = ""
                        password = ""
                        region = ""
                        confirmPassword = ""
                        communicationPreference = "Texto"
                        acceptedTerms = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackToLogin
        ) {

            Text("Ya tengo una cuenta")
        }
    }
}