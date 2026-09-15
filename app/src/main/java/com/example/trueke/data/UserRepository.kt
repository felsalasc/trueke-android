package com.example.trueke.data

import com.example.trueke.model.User

object UserRepository {

    val initialUsers: Array<User> = arrayOf(
        User(
            name = "Ana Pérez",
            email = "ana@trueke.cl",
            password = "123456",
            region = "Coquimbo",
            communicationPreference = "Texto"
        ),
        User(
            name = "Carlos Soto",
            email = "carlos@trueke.cl",
            password = "123456",
            region = "Metropolitana",
            communicationPreference = "Texto"
        ),
        User(
            name = "María López",
            email = "maria@trueke.cl",
            password = "123456",
            region = "Valparaíso",
            communicationPreference = "Visual"
        ),
        User(
            name = "Pedro Díaz",
            email = "pedro@trueke.cl",
            password = "123456",
            region = "Biobío",
            communicationPreference = "Texto"
        ),
        User(
            name = "Camila Rojas",
            email = "camila@trueke.cl",
            password = "123456",
            region = "Coquimbo",
            communicationPreference = "Visual"
        )
    )

    val users = initialUsers.toMutableList()
}