package com.example.trueke.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import com.example.trueke.data.ProductRepository
import com.example.trueke.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val products = ProductRepository.products

    // ---------------------------
    // Estados de filtros
    // ---------------------------

    var expandedCategory by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf("Todas")
    }

    var onlyGoodCondition by remember {
        mutableStateOf(false)
    }

    var selectedDistance by remember {
        mutableStateOf(10)
    }

    val categories = listOf(
        "Todas",
        "Deportes",
        "Videojuegos",
        "Instrumentos",
        "Tecnología",
        "Fotografía"
    )

    // ---------------------------
    // Filtrado de productos
    // ---------------------------

    val filteredProducts = products.filter { product ->

        val categoryMatches =
            selectedCategory == "Todas" ||
                    product.category == selectedCategory

        val distanceMatches =
            product.distanceKm <= selectedDistance

        val conditionMatches =
            !onlyGoodCondition ||
                    product.condition.contains(
                        "Buen",
                        ignoreCase = true
                    ) ||
                    product.condition.contains(
                        "Excelente",
                        ignoreCase = true
                    )

        categoryMatches &&
                distanceMatches &&
                conditionMatches
    }

    // ---------------------------
    // Grilla principal
    // ---------------------------

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // TÍTULO

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            Column {

                Text(
                    text = "TRUEKE",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Productos cerca de ti",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }
        }

        // ---------------------------
        // COMBO BOX
        // ---------------------------

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            Column {

                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = {
                        expandedCategory = !expandedCategory
                    }
                ) {

                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Seleccionar categoría")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedCategory
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = {
                            expandedCategory = false
                        }
                    ) {

                        categories.forEach { category ->

                            DropdownMenuItem(
                                text = {
                                    Text(category)
                                },
                                onClick = {

                                    selectedCategory = category

                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // ---------------------------
        // CHECKBOX
        // ---------------------------

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = onlyGoodCondition,
                    onCheckedChange = {
                        onlyGoodCondition = it
                    }
                )

                Text(
                    text = "Mostrar solo productos en buen estado"
                )
            }
        }

        // ---------------------------
        // RADIO BUTTONS
        // ---------------------------

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            Column {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Distancia máxima",
                    style = MaterialTheme.typography.titleMedium
                )

                DistanceOption(
                    text = "2 km",
                    value = 2,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                DistanceOption(
                    text = "5 km",
                    value = 5,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                DistanceOption(
                    text = "10 km",
                    value = 10,
                    selectedDistance = selectedDistance,
                    onSelected = {
                        selectedDistance = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // ---------------------------
        // TABLA RESUMEN
        // ---------------------------

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            CatalogSummaryTable(
                totalProducts = products.size,
                visibleProducts = filteredProducts.size,
                category = selectedCategory,
                distance = selectedDistance
            )
        }

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(
                    maxLineSpan
                )
            }
        ) {

            Column {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "Productos disponibles",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "${filteredProducts.size} productos encontrados",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }

        // ---------------------------
        // PRODUCTOS EN GRILLA
        // ---------------------------

        items(filteredProducts) { product ->

            ProductCard(
                product = product
            )
        }

        // Sin resultados

        if (filteredProducts.isEmpty()) {

            item(
                span = {
                    androidx.compose.foundation.lazy.grid.GridItemSpan(
                        maxLineSpan
                    )
                }
            ) {

                Text(
                    text = "No encontramos productos con estos filtros.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}


@Composable
fun DistanceOption(
    text: String,
    value: Int,
    selectedDistance: Int,
    onSelected: (Int) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selectedDistance == value,
            onClick = {
                onSelected(value)
            }
        )

        Text(
            text = text
        )
    }
}


@Composable
fun CatalogSummaryTable(
    totalProducts: Int,
    visibleProducts: Int,
    category: String,
    distance: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Resumen del catálogo",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            TableRow(
                title = "Productos registrados",
                value = totalProducts.toString()
            )

            TableRow(
                title = "Productos encontrados",
                value = visibleProducts.toString()
            )

            TableRow(
                title = "Categoría",
                value = category
            )

            TableRow(
                title = "Distancia",
                value = "Hasta $distance km"
            )
        }
    }
}


@Composable
fun TableRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
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
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
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
                text = product.description,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Estado: ${product.condition}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Valor: $${product.referenceValue}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${product.distanceKm} km",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Usuario: ${product.owner}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = {
                    // Próximamente descartar producto
                }
            ) {

                Text("Pasar")
            }

            Button(
                onClick = {
                    // Próximamente generar match
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Me interesa")
            }
        }
    }
}