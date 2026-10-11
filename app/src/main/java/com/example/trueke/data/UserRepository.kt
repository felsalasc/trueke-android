package com.example.trueke.data

import com.example.trueke.model.User

object UserRepository {

    // Perfiles de ejemplo sin credenciales. La autenticación utiliza Firebase.
    val initialUsers: Array<User> = arrayOf(
        User(
            name = "Ana Pérez",
            email = "ana@trueke.cl",
            region = "Coquimbo",
            communicationPreference = "Texto"
        ),
        User(
            name = "Carlos Soto",
            email = "carlos@trueke.cl",
            region = "Metropolitana",
            communicationPreference = "Texto"
        ),
        User(
            name = "María López",
            email = "maria@trueke.cl",
            region = "Valparaíso",
            communicationPreference = "Visual"
        ),
        User(
            name = "Pedro Díaz",
            email = "pedro@trueke.cl",
            region = "Biobío",
            communicationPreference = "Texto"
        ),
        User(
            name = "Camila Rojas",
            email = "camila@trueke.cl",
            region = "Coquimbo",
            communicationPreference = "Visual"
        )
    )

    val users = initialUsers.toMutableList()
}
