package com.example.trueke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trueke.data.ProductRepository
import com.example.trueke.model.Product

@Composable
fun HomeScreen() {

    val products = ProductRepository.products

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "TRUEKE",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Productos cerca de ti",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(products) { product ->

                ProductCard(
                    product = product
                )

            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = product.category,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = product.description
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Estado: ${product.condition}"
            )

            Text(
                text = "Valor referencial: $${product.referenceValue}"
            )

            Text(
                text = "A ${product.distanceKm} km"
            )

            Text(
                text = "Publicado por: ${product.owner}"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                TextButton(
                    onClick = {
                        // Más adelante: descartar producto
                    }
                ) {
                    Text("Pasar")
                }

                Button(
                    onClick = {
                        // Más adelante: generar interés / match
                    }
                ) {
                    Text("Me interesa")
                }
            }
        }
    }
}